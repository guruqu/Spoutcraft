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
package org.spoutcraft.client.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.PacketDecompressionThread;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.packet.builtin.CompressiblePacket;
import org.spoutcraft.client.packet.builtin.PacketAccessory;
import org.spoutcraft.client.packet.builtin.SpoutPacket;

public class CustomPacketPipeline {
	private final SpoutClient game;
	private final LinkedList<Class<? extends SpoutPacket>> packets = new LinkedList<Class<? extends SpoutPacket>>();

	public CustomPacketPipeline(SpoutClient game) {
		this.game = game;
	}

	public void register(Class<? extends SpoutPacket> clazz) {
		if (packets.size() > Short.MAX_VALUE) {
			game.getLogger().info("A maximum of Short.MAX_VALUE messages can only be registered at one time!");
			return;
		}
		if (packets.contains(clazz)) {
			game.getLogger().severe("Attempt made to register " + clazz + " twice!");
			return;
		}
		packets.add(clazz);
	}

	protected SpoutPacket decode(DataInput input) throws IOException {
		SpoutClient.getInstance().setSpoutActive(true);
		final int packetid = input.readInt();
		final Class<? extends SpoutPacket> clazz = packets.get(packetid);
		if (clazz == null) {
			throw new IOException("Unknown custom packet with SpoutPacket id [" + packetid + "]!");
		}
		final short version = input.readShort();
		final int length = input.readInt();
		try {
			final SpoutPacket packet = clazz.newInstance();
			//TODO Dockter, should client disconnect in either of these cases?
			if (packet.getVersion() > version) {
				SpoutClient.getInstance().getActivePlayer().showAchievement("Update Available!", "New Spoutcraft update!", 323 /*Sign*/);
				throw new IOException("Server sent packet that is a newer version than what is on the client!");
			} else if (packet.getVersion() < version) {
				throw new IOException("Server sent packet that is an older version than what is on the client!");
			}
			final byte[] data = new byte[length];
			input.readFully(data);

			packet.decode(new MinecraftExpandableByteBuffer(data));
			return packet;
		} catch (IllegalAccessException | InstantiationException ex) {
			throw new IOException("Failed to create SpoutPacket instance for packet id [" + packetid + "] with a length of [" + length + "] and version [" + version + "]!");
		}
	}

	protected void encode(SpoutPacket packet, DataOutput output) throws IOException {
		final int packetid = getId(packet.getClass());
		if (packetid == -1) {
			throw new IOException("Attempt to send custom packet with SpoutPacket [" + packet.getClass().getSimpleName() + "] that is not registered!");
		}
		output.writeInt(packetid);
		output.writeShort(packet.getVersion());
		final MinecraftExpandableByteBuffer buf = new MinecraftExpandableByteBuffer();
		packet.encode(buf);
		buf.flip();
		output.writeInt(buf.remaining());
		output.write(buf.array());
	}

	protected void handle(SpoutPacket packet) {
		if (packet instanceof CompressiblePacket) {
			PacketDecompressionThread.add((CompressiblePacket) packet);
			return;
		}
		SpoutClient.getHandle().mcProfiler.startSection("Protocol: Handle - SpoutPacket_" + packet.getClass().getSimpleName());
		packet.handle(SpoutClient.getInstance().player);
		SpoutClient.getHandle().mcProfiler.endSection();
	}

	public void onEnable() {
		registerSpoutPackets();
		Collections.sort(packets, new Comparator<Class<? extends SpoutPacket>>() {
			@Override
			public int compare(Class<? extends SpoutPacket> clazz1, Class<? extends SpoutPacket> clazz2) {
				int com = String.CASE_INSENSITIVE_ORDER.compare(clazz1.getCanonicalName(), clazz2.getCanonicalName());
				if (com == 0) {
					com = clazz1.getCanonicalName().compareTo(clazz2.getCanonicalName());
				}

				return com;
			}
		});
	}

	private int getId(Class<? extends SpoutPacket> clazz) {
		for (int i = 0; i < packets.size(); i++) {
			if (packets.get(i) == clazz) {
				return i;
			}
		}
		return -1;
	}

	private void registerSpoutPackets() {
		register(PacketAccessory.class);
	}
}
