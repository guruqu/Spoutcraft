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
import java.util.UUID;

import org.spoutcraft.api.gui.Slot;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketSlotClick extends SpoutPacket {
	private UUID screen;
	private UUID slot;
	private int mouseClick;
	private boolean holdingShift;

	protected PacketSlotClick() {
	}

	public PacketSlotClick(Slot slot, int mouseClick, boolean holdingShift) {
		screen = slot.getScreen().getId();
		this.slot = slot.getId();
		this.mouseClick = mouseClick;
		this.holdingShift = holdingShift;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		long msb = buf.getLong();
		long lsb = buf.getLong();
		screen = new UUID(msb, lsb);
		msb = buf.getLong();
		lsb = buf.getLong();
		slot = new UUID(msb, lsb);
		button = buf.getInt();
		holdingShift = buf.getBoolean();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putLong(screen.getMostSignificantBits());
		buf.putLong(screen.getLeastSignificantBits()); // 16
		buf.putLong(slot.getMostSignificantBits());
		buf.putLong(slot.getLeastSignificantBits()); // 32
		buf.putInt(button); // mouseClick will usually be 0 (left) or 1 (right) - so this is safe unless the mouse has... 257 buttons :P
		buf.putBoolean(holdingShift);//34
	}
	
	@Override
	public void handle(SpoutPlayer player) {
		// Nothing to do.
	}
}
