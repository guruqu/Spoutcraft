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
import org.spoutcraft.client.entity.EntityText;

public class PacketSpawnTextEntity extends SpoutPacket {
	private String text;
	private double posX, posY, posZ, moveX, moveY, moveZ;
	private int duration;
	private float scale;

	protected PacketSpawnTextEntity() {
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		text = buf.getUTF8();
		posX = buf.getDouble();
		posY = buf.getDouble();
		posZ = buf.getDouble();
		scale = buf.getFloat();
		duration = buf.getInt();
		moveX = buf.getDouble();
		moveY = buf.getDouble();
		moveZ = buf.getDouble();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The client cant spawn text entities (hack?)!");
	}

	@Override
	public void handle(SpoutPlayer player) {
		EntityText entity = new EntityText(Minecraft.getMinecraft().theWorld);
		entity.setPosition(posX, posY, posZ);
		entity.setScale(scale);
		entity.setText(text);
		entity.setRotateWithPlayer(true);
		entity.motionX = moveX;
		entity.motionY = moveY;
		entity.motionZ = moveZ;
		entity.setDuration(duration);
		Minecraft.getMinecraft().theWorld.spawnEntityInWorld(entity);
	}
}
