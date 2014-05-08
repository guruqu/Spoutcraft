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

import net.minecraft.src.Entity;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketSetVelocity extends SpoutPacket {
	private double motX = 0;
	private double motY = 0;
	private double motZ = 0;
	private int entityId = 0;

	protected PacketSetVelocity() {
	}

	public PacketSetVelocity(int entityId, double motX, double motY, double motZ) {
		this.entityId = entityId;
		this.motX = motX;
		this.motY = motY;
		this.motZ = motZ;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		entityId = buf.getInt();
		motX = buf.getDouble();
		motY = buf.getDouble();
		motZ = buf.getDouble();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(entityId);
		buf.putDouble(motX);
		buf.putDouble(motY);
		buf.putDouble(motZ);
	}

	@Override
	public void handle(SpoutPlayer player) {
		Entity e = SpoutClient.getInstance().getEntityFromId(entityId);
		if (e != null && !Double.isNaN(motX) && !Double.isNaN(motY) && !Double.isNaN(motZ)) {
			e.motionX = motX;
			e.motionY = motY;
			e.motionZ = motZ;
		}
	}
}
