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

import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiConfirmOpenLink;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;

import java.io.IOException;
import java.net.URL;


public class PacketSendLink extends SpoutPacket {
	protected URL link;

	public PacketSendLink() {
		link = null;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		link = buf.getUTF8();		
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The client cannot send a link from the server!");
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (link != null) {
			try {
				Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink(Minecraft.getMinecraft().currentScreen, link.toString(), 0, false));
			} catch (Exception e) { }
		}
	}
}
