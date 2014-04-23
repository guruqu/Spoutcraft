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
package org.spoutcraft.client.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.src.Packet;

import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.packet.builtin.SpoutPacket;

public class CustomPacket extends Packet {
	private SpoutPacket packet;

	protected CustomPacket() {
	}

	public CustomPacket(SpoutPacket packet) {
		this.packet = packet;
	}

	@Override
	public void readPacketData(DataInput input) throws IOException {
		packet = SpoutClient.getInstance().getPipeline().decode(input);
	}

	@Override
	public void writePacketData(DataOutput output) throws IOException {
		if (packet == null) {
			throw new IOException("Cannot send a CustomPacket with a null SpoutPacket instance!");
		}
		SpoutClient.getInstance().getPipeline().encode(packet, output);
	}

	@Override
	public void processPacket(NetHandler handler) {
		SpoutClient.getInstance().getPipeline().handle(packet);
	}

	@Override
	public int getPacketSize() {
		return 8;
	}
}
