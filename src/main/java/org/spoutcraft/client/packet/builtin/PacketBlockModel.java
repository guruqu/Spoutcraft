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

import org.spoutcraft.api.block.design.BlockDesign;
import org.spoutcraft.api.block.design.GenericBlockDesign;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.api.material.CustomBlock;
import org.spoutcraft.api.material.MaterialData;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketBlockModel extends SpoutPacket {
	private short customId;
	private byte data;
	private BlockDesign design;

	protected PacketBlockModel() {
	}

	public PacketBlockModel(short customId, BlockDesign design, byte data) {
		this.design = design;
		this.customId = customId;
		this.data = data;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		customId = buf.getShort();
		data = buf.get();
		design = new GenericBlockDesign();
		design.decode(buf);
		if (design.getReset()) {
			design = null;
		}
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putShort(customId);
		buf.put(data);
		if (design != null) {
			design.encode(buf);
		} else {
			buf.putUTF8(GenericBlockDesign.RESET_STRING);
		}
	}

	public void handle(SpoutPlayer player) {
		CustomBlock block = MaterialData.getCustomBlock(customId);
		if (block != null) {
			block.setBlockDesign(design, data);
		}
	}	
}
