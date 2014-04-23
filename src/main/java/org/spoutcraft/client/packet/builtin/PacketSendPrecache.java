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

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.precache.PrecacheManager;
import org.spoutcraft.client.precache.PrecacheTuple;

public class PacketSendPrecache extends CompressablePacket {
	private byte[] fileData;
	private String plugin;
	private String version;
	private boolean compressed = false;

	protected PacketSendPrecache() {
	}

	public PacketSendPrecache(Plugin plugin, File file) {
		try {
			this.fileData = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.plugin = plugin.getDescription().getName();
		this.version = plugin.getDescription().getVersion();
	}

	@Override
	public void compress() {
		if (!compressed) {
			Deflater deflater = new Deflater();
			deflater.setInput(fileData);
			deflater.setLevel(Deflater.BEST_COMPRESSION);
			deflater.finish();
			ByteArrayOutputStream bos = new ByteArrayOutputStream(fileData.length);
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
			fileData = bos.toByteArray();
			compressed = true;
		}
	}

	@Override
	public void decompress() {
		if (compressed) {
			Inflater decompressor = new Inflater();
			decompressor.setInput(fileData);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(fileData.length);
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
			fileData = bos.toByteArray();
		}
	}

	@Override
	public boolean isCompressed() {
		return compressed;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		this.plugin = buf.getUTF8();
		this.version = buf.getUTF8();
		compressed = buf.getBoolean();
		int size = buf.getInt();
		this.fileData = new byte[size];
		buf.get(fileData);
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putUTF8(plugin);
		buf.putUTF8(version);
		buf.putBoolean(compressed);
		buf.putInt(fileData.length);
		buf.put(fileData);
	}
	
	@Override
	public void handle(SpoutPlayer player) {
		// Packet recieved, grabbing the zip file
		File zip = PrecacheManager.getPluginPreCacheFile(plugin, version);
		if (zip.exists()) {
			zip.delete();
		}

		try {
			FileUtils.writeByteArrayToFile(zip, fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PrecacheTuple plugin = PrecacheManager.getPrecacheTuple(this.plugin, version);
		if (plugin != null) {
			PrecacheManager.setCached(plugin);
		}
		PrecacheManager.doNextCache();
	}
}
