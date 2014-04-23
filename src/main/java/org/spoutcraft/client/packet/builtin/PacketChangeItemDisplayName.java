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
import org.spoutcraft.api.material.Material;
import org.spoutcraft.api.material.MaterialData;

public class PacketChangeItemDisplayName implements SpoutPacket {
	private int id;
	private short data;
	private String name;

	protected PacketChangeItemDisplayName() {
	}

	public PacketChangeItemDisplayName(int id, short data, String name) {
		this.id = id;
		this.data = data;
		this.name = name;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(id);
		buf.putShort(data);
		buf.putUTF8(name);		
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The server should not receive a PacketChangeItemDisplayName from the client (hack?)!");
	}

	public void handle(SpoutPlayer player) {
		Material material = MaterialData.getOrCreateMaterial(id, data);
		if (material == null) {
			material = MaterialData.getCustomItem(data);
		}
		if (name.equals("[resetall]")) {
			MaterialData.reset();
		} 
		if (material != null) {
			if (name.equals("[reset]")) {
				material.setName(material.getNotchianName());
			} else {
				material.setName(name);
			}
		} else {
			// Debug Client Code
			//System.out.println("Tried to set item name to [" + name + "] for unknown material (" + id + ", " + data + ")");
		}
	}
}
