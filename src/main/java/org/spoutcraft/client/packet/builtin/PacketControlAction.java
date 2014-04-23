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

import org.spoutcraft.api.gui.Screen;
import org.spoutcraft.api.gui.Widget;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketControlAction extends SpoutPacket {
	protected UUID screen;
	protected UUID widget;
	protected float state;
	protected String data = "";


	protected PacketControlAction() {
	}

	public PacketControlAction(Screen screen, Widget widget, float state) {
		this.screen = screen.getId();
		this.widget = widget.getId();
		this.state = state;
	}

	public PacketControlAction(Screen screen, Widget widget, float state, String data) {
		this.screen = screen.getId();
		this.widget = widget.getId();
		this.state = state;
		this.data = data;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		this.screen = buf.getUUID();
		this.widget = buf.getUUID();
		this.state = buf.getFloat();
		this.data = buf.getUTF8();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putUUID(screen);
		buf.putUUID(widget);
		buf.putFloat(state);
		buf.putUTF8(data);
	}

	public void handle(SpoutPlayer player) {
		// Nothing to do
	}
}
