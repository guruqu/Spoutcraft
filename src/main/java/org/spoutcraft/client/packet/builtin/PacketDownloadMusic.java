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
import org.spoutcraft.client.io.Download;
import org.spoutcraft.client.io.FileDownloadThread;
import org.spoutcraft.client.io.FileUtil;
import org.spoutcraft.client.sound.QueuedSound;

public class PacketDownloadMusic extends SpoutPacket {
	private int x, y, z;
	private int volume, distance;
	private boolean soundEffect, notify;
	private String url, plugin;

	protected PacketDownloadMusic() {
	}

	public PacketDownloadMusic(String plugin, String URL, Location loc, int distance, int volume, boolean soundEffect, boolean notify) {
		this.plugin = plugin;
		this.url = URL;
		this.volume = volume;
		this.soundEffect = soundEffect;
		this.notify = notify;
		if (loc != null) {
			x = loc.getBlockX();
			y = loc.getBlockY();
			z = loc.getBlockZ();
			this.distance = distance;
		} else {
			this.distance = -1;
		}
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		url = buf.getUTF8();
		plugin = buf.getUTF8();
		distance = buf.getInt();
		x = buf.getInt();
		y = buf.getInt();
		z = buf.getInt();
		volume = buf.getInt();
		soundEffect = buf.getBoolean();
		notify = buf.getBoolean();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putUTF8(url);
		buf.putUTF8(plugin);
		buf.putInt(distance);
		buf.putInt(x);
		buf.putInt(y);
		buf.putInt(z);
		buf.putInt(volume);
		buf.putBoolean(soundEffect);
		buf.putBoolean(notify);
	}

	@Override
	public void handle(SpoutPlayer player) {
		File directory = new File(FileUtil.getTempDir(), plugin);
		if (!directory.exists()) {
			directory.mkdir();
		}
		String fileName = FileUtil.getFileName(url);
		if (!FileUtil.isAudioFile(fileName)) {
			System.out.println("Rejecting download of invalid audio: " + fileName);
			return;
		}
		File song = FileUtil.findFile(plugin, fileName);
		if (song != null && song.exists()) {
			QueuedSound action = new QueuedSound(song, x, y, z, volume, distance, soundEffect);
			action.run();
			return;
		} else {
			song = new File(directory, fileName);
		}

		QueuedSound action = new QueuedSound(song, x, y, z, volume, distance, soundEffect);
		Download download = new Download(fileName, directory, url, action);
		action.setNotify(!download.isDownloaded() && notify);
		if (!download.isDownloaded() && notify) {
			SpoutClient.getInstance().getActivePlayer().showAchievement("Downloading Music...", fileName, 2256 /*Gold Record*/);
		}
		FileDownloadThread.getInstance().addToDownloadQueue(download);
	}
}
