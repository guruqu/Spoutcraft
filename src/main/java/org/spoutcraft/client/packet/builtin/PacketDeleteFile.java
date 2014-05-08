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

public class PacketDeleteFile extends SpoutPacket {
	private String plugin;
	private String fileName;

	protected PacketDeleteFile() {
	}

	public PacketDeleteFile(String plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		plugin = buf.getUTF8();
		fileName = buf.getUTF8();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The client should not send a PacketDeleteFile to the server (hack?)!");
	}

	@Override
	public void handle(SpoutPlayer player) {
		// TODO Fix security vulnerability: http://pastie.org/private/qdmx5veidnood1ectllkcq
		/*File file = FileUtil.findFile(plugin, fileName);
		if (file != null) {
			file.delete();
		}*/
	}	
}
