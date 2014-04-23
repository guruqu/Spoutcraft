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

import org.spoutcraft.api.gui.GenericComboBox;
import org.spoutcraft.api.gui.Widget;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketComboBox implements SpoutPacket {
	private GenericComboBox box;
	private UUID uuid;
	private boolean open;
	private int selection;

	public PacketComboBox() {
	}

	public PacketComboBox(GenericComboBox box) {
		this.box = box;
		this.uuid = box.getId();
		this.open = box.isOpen();
		this.selection = box.getSelectedRow();
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		uuid = buf.getUUID();
		open = buf.getBoolean();
		selection = buf.getInt();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putUUID(uuid);
		buf.putBoolean(open);
		buf.putInt(selection);
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (SpoutClient.getInstance().getActivePlayer().getMainScreen().getActivePopup() != null) {
			Widget w = SpoutClient.getInstance().getActivePlayer().getMainScreen().getActivePopup().getWidget(uuid);
			if (w != null && w instanceof GenericComboBox) {
				box = (GenericComboBox) w;
				box.setOpen(open);
				box.setSelection(selection);
			}
		}
	}
}
