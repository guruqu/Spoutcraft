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

public abstract class SpoutPacket {
	/**
	 * Reads the incoming data from the client. <p> Note: Data should be read in exactly the same order as it was written.
	 *
	 * @param buf The byte buffer to read data from
	 */
	public abstract void decode(MinecraftExpandableByteBuffer buf) throws IOException;

	/**
	 * Writes the outgoing data to the output stream.
	 *
	 * @param buf The buffer to write data to
	 */
	public abstract void encode(MinecraftExpandableByteBuffer buf) throws IOException;

	/**
	 * Performs any tasks for the packet after data has been successfully read into the packet.
	 *
	 * @param player The player of this packet
	 */
	public abstract void handle(SpoutPlayer player);

	/**
	 * Version of the packet this represents. Version numbers should start with 0. Versions should be incremented any time the member variables or serialization of the packet changes, to prevent
	 * crashing.
	 *
	 * @return version
	 */
	public int getVersion() {
		return 0;
	}
}
