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

import net.minecraft.src.GuiEditSign;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntitySign;
import net.minecraft.src.World;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketOpenSignGUI extends SpoutPacket {
	private int x, y, z;

	protected PacketOpenSignGUI() {
	}

	public PacketOpenSignGUI(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		x = buf.getInt();
		y = buf.getInt();
		z = buf.getInt();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(x);
		buf.putInt(y);
		buf.putInt(z);
	}
	
	@Override
	public void handle(SpoutPlayer player) {
		World world = SpoutClient.getHandle().theWorld;
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)te;
			GuiEditSign gui = new GuiEditSign(sign);
			SpoutClient.getHandle().displayGuiScreen(gui);
		}
	}
}