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

public class PacketClipboardText extends SpoutPacket {
	private String text;

	protected PacketClipboardText() {
	}

	public PacketClipboardText(String text) {
		this.text = text;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		text = buf.getUTF8();
	}

	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putUTF8(text);
	}

	@Override
	public void handle(SpoutPlayer player) {
		// Possibility of setting client side clipboard via GuiScreen.setClipboard(text).
	}
}
