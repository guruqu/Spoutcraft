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
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.io.output.ByteArrayOutputStream;

import net.minecraft.src.EnumSkyBlock;
import net.minecraft.src.World;

import org.spoutcraft.api.Spoutcraft;
import org.spoutcraft.api.io.ExpandableByteBuffer;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.api.io.SpoutInputStream;
import org.spoutcraft.api.io.SpoutOutputStream;
import org.spoutcraft.api.material.CustomBlock;
import org.spoutcraft.api.material.MaterialData;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.block.SpoutcraftChunk;
import org.spoutcraft.client.packet.LightingThread.LightingData;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketOverrideChunk extends CompressiblePacket {
	private static LightingThread thread;
	private int chunkX;
	private int chunkZ;
	private boolean hasData = false;
	private byte[] data;
	private boolean compressed = true;

	protected PacketOverrideChunk() {
	}

	public PacketOverrideChunk(int x, int z) {
		chunkX = x;
		chunkZ = z;
		compressed = false;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		chunkX = buf.getInt();
		chunkZ = buf.getInt();
		hasData = buf.getBoolean();
		if (hasData) {
			data = new byte[buf.getInt()];
			buf.get(data);
		}
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(chunkX);
		buf.putInt(chunkZ);
		buf.putBoolean(hasData);
		if (hasData) {
			buf.putInt(data.length);
			buf.put(data);
		}
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (hasData) {
			if (!SpoutClient.getInstance().getRawWorld().chunkProvider.chunkExists(chunkX, chunkZ)) {
				return;
			}

			if (thread == null) {
				thread = new LightingThread();
				thread.start();
			}

			ByteBuffer buffer = ByteBuffer.allocate(data.length);
			buffer.put(data);
			short[] customIds = new short[16*16*256];
			byte[] customData = new byte[16*16*256];
			for (int i = 0; i < customIds.length; i++) {
				customIds[i] = buffer.getShort(i * 3);
				customData[i] = buffer.get((i * 3) + 2);
			}
			final SpoutcraftChunk chunk = Spoutcraft.getChunk(SpoutClient.getInstance().getRawWorld(), chunkX, chunkZ);
			chunk.setCustomBlockIds(customIds);
			chunk.setCustomBlockData(customData);
			thread.queue.add(new LightingData(chunkX, chunkZ, customIds));
		}
	}

	@Override
	public void compress() {
		if (!compressed && hasData) {
			if (data != null) {
				Deflater deflater = new Deflater();
				deflater.setInput(data);
				deflater.setLevel(Deflater.BEST_COMPRESSION);
				deflater.finish();
				final ExpandableByteBuffer buf = new ExpandableByteBuffer(data.length);
				while (!deflater.finished()) {
					deflater.deflate()
					int bytesCompressed = deflater.deflate(buffer);
					bos.write(buffer, 0, bytesCompressed);
				}
				data = bos.toByteArray();
			}
			compressed = true;
		}
	}

	@Override
	public void decompress() {
		if (compressed && hasData) {
			Inflater decompressor = new Inflater();
			decompressor.setInput(data);

			ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

			byte[] buf = new byte[1024];
			while (!decompressor.finished()) {
				try {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				} catch (DataFormatException e) {
				}
			}
			try {
				bos.close();
			} catch (IOException e) {
			}

			data = bos.toByteArray();
		}
	}

	@Override
	public boolean isCompressed() {
		return compressed;
	}
}

class LightingThread extends Thread {
	final LinkedBlockingQueue<LightingData> queue = new LinkedBlockingQueue<LightingData>();
	final int[] lightingBlockList = new int[32768 * 4];

	LightingThread() {
		super("Lighting Thread");
		setDaemon(true);
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			try {
				LightingData data = queue.take();
				World world = SpoutClient.getHandle().theWorld;
				if (world != null && world.chunkProvider.chunkExists(data.cx, data.cz)) {
					for (int i = 0; i < data.ids.length; i++) {
						CustomBlock cb = MaterialData.getCustomBlock(data.ids[i]);
						if (cb != null && cb.getLightLevel() != 0) {
							int bx = (i >> 12) & 0xF;
							int by = i & 0xFF;
							int bz = (i >> 8) & 0xF;
							world.updateLightByType(lightingBlockList, EnumSkyBlock.Sky, data.cx * 16 + bx, by, data.cz * 16 + bz);
							world.updateLightByType(lightingBlockList, EnumSkyBlock.Block, data.cx * 16 + bx, by, data.cz * 16 + bz);
						}
					}
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	static class LightingData {
		final int cx, cz;
		final short[] ids;
		LightingData(int cx, int cz, short[] ids) {
			this.cx = cx;
			this.cz = cz;
			this.ids = ids;
		}
	}
}
