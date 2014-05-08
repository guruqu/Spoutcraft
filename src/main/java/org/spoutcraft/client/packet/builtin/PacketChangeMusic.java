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

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketChangeMusic extends SpoutPacket {
	private int id;
	private int volumePercent;
	private boolean cancel = false;

	protected PacketChangeMusic() {
	}

	public PacketChangeMusic(int music, int volumePercent) {
		this.id = music;
		this.volumePercent = volumePercent;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		id = buf.getInt();
		volumePercent = buf.getInt();
		cancel = buf.getBoolean();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(id);
		buf.putInt(volumePercent);
		buf.putBoolean(cancel);
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (cancel) {
			SpoutClient.getHandle().sndManager.cancelled = true;
		} else {
			SpoutClient.getHandle().sndManager.allowed = true;
		}
	}	
}
