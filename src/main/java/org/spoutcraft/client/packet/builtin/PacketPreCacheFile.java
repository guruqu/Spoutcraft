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

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.io.CRCManager;
import org.spoutcraft.client.io.CustomTextureManager;
import org.spoutcraft.client.io.Download;
import org.spoutcraft.client.io.FileDownloadThread;
import org.spoutcraft.client.io.FileUtil;

public class PacketPreCacheFile extends SpoutPacket {	
	private boolean cached = false;
	private boolean url = false;
	private long expectedCRC;
	private String file;
	private String plugin;

	public PacketPreCacheFile() {
	}

	public PacketPreCacheFile(String plugin, String file, long expectedCRC, boolean url) {
		this.file = file;
		this.plugin = plugin;
		this.expectedCRC = expectedCRC;
		this.url = url;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		cached = buf.getBoolean();
		url = buf.getBoolean();
		expectedCRC = buf.getLong();
		file = buf.getUTF8();
		plugin = buf.getUTF8();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putBoolean(this.cached);
		buf.putBoolean(this.url);
		buf.putLong(this.expectedCRC);
		buf.putUTF8(this.file);
		buf.putUTF8(this.plugin);
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (!FileUtil.canCache(file)) {
			System.out.println("WARNING, " + plugin + " tried to cache an invalid file type: " + file);
			return;
		}
		final File directory = new File(FileUtil.getCacheDir(), plugin);
		if (!directory.exists()) {
			directory.mkdir();
		}
		final String fileName = FileUtil.getFileName(file);
		final File expected = new File(directory, fileName);
		this.cached = expected.exists();
		System.out.println("Received Precache for [" + fileName + "]. File " + (expected.exists() ? ("exists" + (cached ? " and is valid" : "and is invalid")) : "does not exist"));
		if (!cached) {
			final long finalCRC = expectedCRC;
			CRCManager.setCRC(fileName, finalCRC);
			if (expected.exists()) {
				expected.delete();
			}
			// Request copy of file
			if (!url) {
				SpoutClient.getInstance().getPacketManager().sendSpoutPacket(this);
			} else { // Begin download
				Runnable queued = null;
				if (FileUtil.isImageFile(fileName)) {
					queued = new Runnable() {
						public void run() {
							long crc = FileUtil.getCRC(expected, new byte[16384]);
							System.out.println("Downloaded File " + fileName + "'s CRC " + crc + ", expected CRC: " + expectedCRC);
							if (crc == finalCRC) {
								CustomTextureManager.getTextureFromUrl(plugin, fileName);
							} else {
								System.out.println("WARNING, Downloaded File " + fileName + "'s CRC " + crc + " did not match the expected CRC: " + finalCRC);
							}
						}
					};
				}
				Download data = new Download(fileName, directory, file, queued);
				FileDownloadThread.getInstance().addToDownloadQueue(data);
			}
		} else {
			if (FileUtil.isImageFile(fileName)) {
				CustomTextureManager.getTextureFromUrl(plugin, fileName);
			}
		}
	}	
}
