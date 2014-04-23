/*
 * This file is part of Spoutcraft.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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

import java.io.IOException;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketAlert extends SpoutPacket {
	String message;
	String title;
	int itemId;

	protected PacketAlert() {
	}

	public PacketAlert(String title, String message, int itemId) {
		this.title = title;
		this.message = message;
		this.itemId = itemId;
	}

	public void readData(SpoutInputStream input) throws IOException {
		title = input.readString();
		message = input.readString();
		itemId = input.readInt();
	}

	public void writeData(SpoutOutputStream output) throws IOException {
		output.writeString(title);
		output.writeString(message);
		output.writeInt(itemId);
	}

	@Override
	public void handle(SpoutPlayer player) {
		SpoutClient.getInstance().getActivePlayer().showAchievement(title, message, itemId);
	}
}
