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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;;
import org.spoutcraft.client.gui.minimap.MinimapConfig;
import org.spoutcraft.client.gui.minimap.Waypoint;

public class PacketWaypoint extends SpoutPacket {
	private double x, y, z;
	private String name;
	private boolean death = false;

	protected PacketWaypoint() {
	}

	public PacketWaypoint(double x, double y, double z, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}

	public PacketWaypoint(double x, double y, double z, String name, boolean death) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
		this.death = death;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		x = buf.getDouble();
		y = buf.getDouble();
		z = buf.getDouble();
		name = buf.getUTF8();
		death = buf.getBoolean();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putDouble(x);
		buf.putDouble(y);
		buf.putDouble(z);
		buf.putUTF8(name);
		buf.putBoolean(death);
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (!death) {
			MinimapConfig.getInstance().addServerWaypoint(x, y, z, name);
		} else {
			Waypoint point = new Waypoint("Death " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()), (int)x, (int)y, (int)z, true);
			point.deathpoint = true;
			MinimapConfig.getInstance().addWaypoint(point);
		}
	}
}
