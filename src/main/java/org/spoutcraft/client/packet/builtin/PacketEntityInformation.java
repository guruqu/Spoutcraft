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
package org.spoutcraft.client.packet.builtin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.io.output.ByteArrayOutputStream;

import net.minecraft.src.Minecraft;
import net.minecraft.src.WorldClient;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.entity.CraftEntity;

public class PacketEntityInformation extends CompressiblePacket {
	private boolean compressed = false;
	private byte[] data = null;

	protected PacketEntityInformation() {
	}

	public PacketEntityInformation(List<LivingEntity> entities) {
		ByteBuffer tempbuffer = ByteBuffer.allocate(entities.size() * 20); //4 bytes for entity id, 16 for uuid
		for (Entity e : entities) {
			tempbuffer.putLong(e.getUniqueId().getLeastSignificantBits());
			tempbuffer.putLong(e.getUniqueId().getMostSignificantBits());
			tempbuffer.putInt(e.getEntityId());
		}
		data = tempbuffer.array();
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		int size = buf.getInt();
		if (size > 0) {
			data = new byte[size];
			buf.get(data);
		}
		compressed = buf.getBoolean();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		if (data != null) {
			buf.putInt(data.length);
			buf.put(data);
		} else {
			buf.putInt(0);
		}
		buf.putBoolean(compressed);
	}	

	@Override
	public void compress() {
		if (!compressed) {
			if (data != null) {
				Deflater deflater = new Deflater();
				deflater.setInput(data);
				deflater.setLevel(Deflater.BEST_COMPRESSION);
				deflater.finish();
				ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
				byte[] buffer = new byte[1024];
				while (!deflater.finished()) {
					int bytesCompressed = deflater.deflate(buffer);
					bos.write(buffer, 0, bytesCompressed);
				}
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				data = bos.toByteArray();
			}
			compressed = true;
		}
	}

	@Override
	public void decompress() {
		if (compressed) {
			Inflater decompressor = new Inflater();
			decompressor.setInput(data);

			ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

			byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				try {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				} catch (DataFormatException ignored) {
				}
			}
			try {
				bos.close();
			} catch (IOException ignored) {
			}

			data = bos.toByteArray();
		}
	}

	@Override
	public boolean isCompressed() {
		return compressed;
	}

	public void handle(SpoutPlayer player) {
		if (Minecraft.getMinecraft().theWorld instanceof WorldClient) {
			ByteBuffer rawData = ByteBuffer.allocate(data.length);
			rawData.put(data);
			for (int i = 0; i < data.length / 20; i++) {
				int index = i * 20;
				long lsb = rawData.getLong(index);
				long msb = rawData.getLong(index + 8);
				int id = rawData.getInt(index + 16);

				net.minecraft.src.Entity e = SpoutClient.getInstance().getEntityFromId(id);
				if (e != null) {
					e.uniqueId = new UUID(msb, lsb);
				}
			}
		}
	}
}
