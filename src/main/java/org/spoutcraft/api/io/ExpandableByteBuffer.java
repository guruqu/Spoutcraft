/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011 SpoutcraftDev <http://spoutcraft.org/>
 * Spoutcraft is licensed under the GNU Lesser General Public License.
 *
 * Spoutcraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spoutcraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spoutcraft.api.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ExpandableByteBuffer {
	private static final int INITIAL_SIZE = 256;
	private ByteBuffer buf;

	public ExpandableByteBuffer() {
		buf = ByteBuffer.allocate(INITIAL_SIZE);
	}

	public ExpandableByteBuffer(ByteBuffer buf) {
		this.buf = buf;
	}

	public ExpandableByteBuffer(int initialSize) {
		buf = ByteBuffer.allocate(initialSize);
	}

	public ExpandableByteBuffer(byte[] data) {
		this(data, 0, data.length);
	}

	public ExpandableByteBuffer(byte[] data, int offset, int length) {
		buf = ByteBuffer.wrap(data, offset, length);
	}

	public void put(byte b) {
		expandIfNeeded(1);
		buf.put(b);
	}

	public byte get() {
		return buf.get();
	}

	public void put(byte[] bytes) {
		expandIfNeeded(bytes.length);
		buf.put(bytes);
	}

	public ByteBuffer get(byte[] dst) {
		return buf.get(dst);
	}

	public void put(byte[] bytes, int offset, int length) {
		expandIfNeeded(length - offset);
		buf.put(bytes, offset, length);
	}

	public ByteBuffer get(byte[] dst, int offset, int length) {
		return buf.get(dst, offset, length);
	}

	public void put(ByteBuffer from) {
		expandIfNeeded(from.capacity() - from.remaining());
		buf.put(from);
	}

	public void putBoolean(boolean b) {
		expandIfNeeded(1);
		buf.put(b ? (byte) 1 : 0);
	}

	public boolean getBoolean() {
		return buf.get() == 1;
	}

	public void putChar(char c) {
		expandIfNeeded(2);
		buf.putChar(c);
	}

	public char getChar() {
		return buf.getChar();
	}

	public void putShort(short s) {
		expandIfNeeded(2);
		buf.putShort(s);
	}

	public short getShort() {
		return buf.getShort();
	}

	public void putInt(int i) {
		expandIfNeeded(4);
		buf.putInt(i);
	}

	public int getInt() {
		return buf.getInt();
	}

	public void putFloat(float f) {
		expandIfNeeded(4);
		buf.putFloat(f);
	}

	public float getFloat() {
		return buf.getFloat();
	}

	public void putDouble(double d) {
		expandIfNeeded(8);
		buf.putDouble(d);
	}

	public double getDouble() {
		return buf.getDouble();
	}

	public void putLong(long l) {
		expandIfNeeded(8);
		buf.putLong(l);
	}

	public long getLong() {
		return buf.getLong();
	}

	public void putUTF8(String s) throws IOException {
		final byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
		if (bytes.length >= Short.MAX_VALUE) {
			throw new IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuffer!");
		}
		expandIfNeeded(2 + bytes.length);
		buf.putShort((short) bytes.length);
		buf.put(bytes);
	}

	public String getUTF8() throws IOException {
		final short len = buf.getShort();
		final byte[] bytes = new byte[len];
		buf.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public void putUUID(UUID uuid) {
		putLong(uuid.getMostSignificantBits());
		putLong(uuid.getLeastSignificantBits());
	}

	public UUID getUUID() {
		return new UUID(getLong(), getLong());
	}

	public int[] getInts() {
		int length = getInt();
		int[] newArray = new int[length];
		for (int i = 0; i < length; i++) {
			newArray[i] = getInt();
		}
		return newArray;
	}

	public void putInts(int[] ints) {
		putInt(ints.length);
		for (int i : ints) {
			putInt(ints[i]);
		}
	}

	public float[] getFloats() {
		int length = getInt();
		float[] newArray = new float[length];
		for (int i = 0; i < length; i++) {
			newArray[i] = getFloat();
		}
		return newArray;
	}

	public void putFloats(float[] floats) {
		putInt(floats.length);
		for (float f : floats) {
			putFloat(f);
		}
	}

	public float[][] get2DFloats() throws IOException {
		final int length = getInt();
		final int depth = getInt();
		final float[][] newArray = new float[length][depth];
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < depth; j++) {
				newArray[i][j] = buf.getFloat();
			}
		}
		return newArray;
	}

	public void put2DFloats(float[][] floats, int depth) throws IOException {
		putInt(floats.length);
		putInt(depth);
		for (float[] aFloat : floats) {
			for (int j = 0; j < depth; j++) {
				putFloat(aFloat[j]);
			}
		}
	}

	public ByteBuffer asReadOnlyBuffer() {
		return buf.asReadOnlyBuffer();
	}

	public int position() {
		return buf.position();
	}

	public ExpandableByteBuffer position(int position) {
		buf.position(position);
		return this;
	}

	public int limit() {
		return buf.limit();
	}

	public ExpandableByteBuffer limit(int limit) {
		buf.limit(limit);
		return this;
	}

	public ExpandableByteBuffer flip() {
		buf.flip();
		return this;
	}

	public ExpandableByteBuffer mark() {
		buf.mark();
		return this;
	}

	public ExpandableByteBuffer reset() {
		buf.reset();
		return this;
	}

	public int remaining() {
		return buf.remaining();
	}

	public byte[] array() {
		return buf.array();
	}

	private void expandIfNeeded(int amount) {
		if (buf.remaining() > amount) {
			return;
		}
		final ByteBuffer temp = ByteBuffer.allocate(buf.capacity() + ++amount);
		temp.put(buf);
		temp.position(buf.position());
		buf = temp;
	}
}
