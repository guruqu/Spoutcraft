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

import org.spoutcraft.api.gui.InGameHUD;
import org.spoutcraft.api.gui.PopupScreen;
import org.spoutcraft.api.gui.Screen;
import org.spoutcraft.api.gui.Widget;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketWidgetRemove extends SpoutPacket {	
	private UUID widget;

	protected PacketWidgetRemove() {
	}

	public PacketWidgetRemove(Widget widget, UUID screen) {
		this.widget = widget.getId();
	}

	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		widget = buf.getUUID();
	}

	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putUUID(widget);
	}

	public void handle(SpoutPlayer player) {
		InGameHUD mainScreen = SpoutClient.getInstance().getActivePlayer().getMainScreen();
		PopupScreen popup = mainScreen.getActivePopup();

		Widget w = PacketWidget.ALL_WIDGETS.get(widget);

		if (w != null && w.getScreen() != null && !(w instanceof Screen)) {
			w.getScreen().removeWidget(w);
		}

		if (w instanceof PopupScreen && popup.getId().equals(w.getId())) {
			// Determine if this is a popup screen and if we need to update it
			mainScreen.closePopup();
		}

		PacketWidget.ALL_WIDGETS.remove(widget);
	}	
}
