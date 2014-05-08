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

public class PacketNotification extends PacketShowAchievement {
	private String title;
	private String message;
	private int itemId;
	private short data;
	private int time;

	protected PacketNotification() {
	}

	public PacketNotification(String title, String message, int itemId, short data, int time) {
		super(title, message, itemId);
		this.title = title;
		this.message = message;
		this.itemId = itemId;
		this.data = data;
		this.time = time;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		super.decode(buf);
		this.data = buf.getShort();
		this.time = buf.getInt();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		super.encode(buf);
		buf.putShort(data);
		buf.putInt(time);
	}

	@Override
	public void handle(SpoutPlayer player) {
		SpoutClient.getInstance().getActivePlayer().showAchievement(title, message, itemId, data, time);
	}
}
