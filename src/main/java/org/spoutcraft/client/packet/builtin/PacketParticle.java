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
import java.util.Random;

import net.minecraft.src.Minecraft;
import net.minecraft.src.EntityFX;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.api.util.Location;
import org.spoutcraft.api.util.Vector;

public class PacketParticle extends SpoutPacket {
	private String name;
	private Location location;
	private Vector motion;
	private float scale, gravity, particleRed, particleBlue, particleGreen;
	private int maxAge, amount;

	protected PacketParticle() {
	}

	public PacketParticle(Particle particle) {
		this.particle = particle;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		name = buf.getUTF8();
		location = buf.getLocation();
		motion = buf.getVector();
		scale = buf.getFloat();
		gravity = buf.getFloat();
		particleRed = buf.getFloat();
		particleBlue = buf.getFloat();
		particleGreen = buf.getFloat();
		maxAge = buf.getInt();
		amount = Math.min(1000, buf.getInt());
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The client should not send a PacketDownloadFile to the server (hack?)!");
	}

	@Override
	public void handle(SpoutPlayer player) {
		Random r = new Random();
		for (int i = 0; i < amount; i++) {
			double x = location.getX();
			double y = location.getY();
			double z = location.getZ();
			if (amount > 1)	{
				x += (r.nextBoolean() ? 2 : -2) * r.nextFloat();
				y += (r.nextBoolean() ? 2 : -2) * r.nextFloat();
				z += (r.nextBoolean() ? 2 : -2) * r.nextFloat();
			}

			EntityFX particle = Minecraft.getMinecraft().renderGlobal.doSpawnParticle(name, x, y, z, motion.getX(), motion.getY(), motion.getZ());
			if (particle != null) {
				if (scale > 0) {
					particle.particleScale = scale;
				}
				particle.particleGravity = gravity;
				if (particleRed >= 0F && particleRed <= 1F) {
					particle.particleRed = particleRed;
				}
				if (particleBlue >= 0F && particleBlue <= 1F) {
					particle.particleBlue = particleBlue;
				}
				if (particleGreen >= 0F && particleGreen <= 1F) {
					particle.particleGreen = particleGreen;
				}
				particle.particleMaxAge = maxAge;
			}
		}
	}
}
