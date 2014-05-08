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

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Packet0KeepAlive;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.io.CustomTextureManager;
import org.spoutcraft.client.io.FileUtil;

public class PacketDownloadFile extends CompressiblePacket {
	private String plugin;
	private byte[] fileData;
	private String fileName;
	private boolean compressed = false;

	protected PacketDownloadFile() {
	}

	public PacketDownloadFile(String plugin, File file) {
		this.plugin = plugin;
		try {
			this.fileData = FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.fileName = FileUtil.getFileName(file.getPath());
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		fileName = buf.getUTF8();
		plugin = buf.getUTF8();
		compressed = buf.getBoolean();
		int size = buf.getInt();
		this.fileData = new byte[size];
		buf.get(fileData);
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The server should not receive a PacketDownloadFile from the client (hack?)!");
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
	public void handle(SpoutPlayer player) {
		this.fileName = FileUtil.getFileName(this.fileName);
		if (!FileUtil.canCache(fileName)) {
			System.out.println("WARNING, " + plugin + " tried to cache an invalid file type: " + fileName);
			return;
		}
		File directory = new File(FileUtil.getCacheDir(), plugin);
		if (!directory.exists()) {
			directory.mkdir();
		}
		File cache = new File(directory, fileName);
		try {
			FileUtils.writeByteArrayToFile(cache, fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (cache.exists() && FileUtil.isImageFile(fileName)) {
			CustomTextureManager.getTextureFromUrl(plugin, fileName);
		}
		(Minecraft.getMinecraft().thePlayer).sendQueue.addToSendQueue(new Packet0KeepAlive());
	}
}
