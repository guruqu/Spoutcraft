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

import org.spoutcraft.api.gui.ScreenType;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.packet.ScreenAction;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketScreenAction extends SpoutPacket {
	private byte action = -1;
	private byte screen = -1; // UnknownScreen

	protected PacketScreenAction() {
	}

	public PacketScreenAction(ScreenAction action, ScreenType screen) {
		this.action = (byte)action.getId();
		this.screen = (byte)screen.getCode();
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		action = buf.get();
		screen = buf.get();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.put(action);
		buf.put(screen);
	}

	@Override
	public void handle(SpoutPlayer player) {
		switch(ScreenAction.getScreenActionFromId(action)) {
		case Open:
			SpoutClient.getHandle().displayPreviousScreen();
			break;
		case Close:
			SpoutClient.getHandle().displayPreviousScreen();
			break;
		case Force_Close:
			SpoutClient.getHandle().displayGuiScreen(null, false);
			break;
		}
	}
}
