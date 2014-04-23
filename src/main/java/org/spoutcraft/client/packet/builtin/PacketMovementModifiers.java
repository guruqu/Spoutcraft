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

import net.minecraft.src.Minecraft;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketMovementModifiers extends SpoutPacket {
	double gravityMod = 1;
	double walkingMod = 1;
	double swimmingMod = 1;
	double jumpingMod = 1;
	double airspeedMod = 1;

	protected PacketMovementModifiers() {
	}

	public PacketMovementModifiers(double gravity, double walking, double swimming, double jumping, double airspeed) {
		gravityMod = gravity;
		walkingMod = walking;
		swimmingMod = swimming;
		jumpingMod = jumping;
		airspeedMod = airspeed;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		gravityMod = buf.getDouble();
		walkingMod = buf.getDouble();
		swimmingMod = buf.getDouble();
		jumpingMod = buf.getDouble();
		airspeedMod = buf.getDouble();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putDouble(gravityMod);
		buf.putDouble(walkingMod);
		buf.putDouble(swimmingMod);
		buf.putDouble(jumpingMod);
		buf.putDouble(airspeedMod);
	}

	public void handle(SpoutPlayer player) {		
		Minecraft.getMinecraft().thePlayer.getData().setGravityMod(gravityMod);
		Minecraft.getMinecraft().thePlayer.getData().setWalkingMod(walkingMod);
		Minecraft.getMinecraft().thePlayer.getData().setSwimmingMod(swimmingMod);
		Minecraft.getMinecraft().thePlayer.getData().setJumpingMod(jumpingMod);
		Minecraft.getMinecraft().thePlayer.getData().setAirspeedMod(airspeedMod);
	}
}
