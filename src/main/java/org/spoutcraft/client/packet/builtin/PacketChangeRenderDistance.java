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

import net.minecraft.src.GameSettings;
import net.minecraft.src.Minecraft;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.api.player.RenderDistance;
import org.spoutcraft.client.SpoutClient;

public class PacketChangeRenderDistance extends SpoutPacket {
	private byte view = -1;
	private byte max = -1;
	private byte min = -1;

	protected PacketChangeRenderDistance() {
	}

	public PacketChangeRenderDistance(byte view) {
		this.view = view;
	}

	public int getNumBytes() {
		return 3;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		view = buf.get();
		max = buf.get();
		min = buf.get();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.put(view);
		buf.put(max);
		buf.put(min);
	}

	@Override
	public void handle(SpoutPlayer player) {
		Minecraft game = SpoutClient.getHandle();
		if (game != null) {
			GameSettings settings = game.gameSettings;
			if (view > -1 && view < 4) {
				settings.renderDistance = view;
			}
		}
		if (min > -1 && min < 4) {
			SpoutClient.getInstance().getActivePlayer().setMinimumView(RenderDistance.getRenderDistanceFromValue(min));
		}
		if (max > -1 && max < 4) {
			SpoutClient.getInstance().getActivePlayer().setMaximumView(RenderDistance.getRenderDistanceFromValue(max));
		}
		if (min == -2) {
			SpoutClient.getInstance().getActivePlayer().setMinimumView(RenderDistance.TINY);
		}
		if (max == -2) {
			SpoutClient.getInstance().getActivePlayer().setMinimumView(RenderDistance.FAR);
		}
	}
}
