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

import net.minecraft.src.GuiYesNo;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.gui.CustomScreen;
import org.spoutcraft.client.gui.precache.GuiPrecache;
import org.spoutcraft.client.io.FileDownloadThread;

public class PacketPreCacheCompleted extends SpoutPacket {
	public PacketPreCacheCompleted() {
		System.out.println("[Spoutcraft Cache Manager] Completed: " + System.currentTimeMillis());
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
	}

	public void handle(SpoutPlayer player) {
		FileDownloadThread.preCacheCompleted.set(System.currentTimeMillis());
		SpoutClient.getInstance().getPacketManager().sendSpoutPacket(this);
		if (!(SpoutClient.getHandle().currentScreen instanceof CustomScreen) && !(SpoutClient.getHandle().currentScreen instanceof GuiYesNo)) {
			// Closes downloading terrain
			SpoutClient.getHandle().displayGuiScreen(null, false);
			// Prevent closing a plugin created menu from opening the downloading terrain
			SpoutClient.getHandle().clearPreviousScreen();
		}
		if (SpoutClient.getHandle().currentScreen instanceof GuiPrecache) {
			// Closes downloading terrain
			SpoutClient.getHandle().displayGuiScreen(null, false);
			// Prevent closing a plugin created menu from opening the downloading terrain
			SpoutClient.getHandle().clearPreviousScreen();
		}
	}
}
