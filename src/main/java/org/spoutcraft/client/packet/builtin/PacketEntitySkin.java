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

import org.spoutcraft.api.Spoutcraft;
import org.spoutcraft.api.entity.EntitySkinType;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.entity.CraftEntity;

public class PacketEntitySkin extends SpoutPacket {
	private String texture = "";
	private int entityId;
	private byte textureId = 0;

	protected PacketEntitySkin() {
	}
	
	public PacketEntitySkin(Entity entity, String texture, byte type) {
		this.entityId = entity.getEntityId();
		this.texture = texture;
		this.textureId = type;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		entityId = buf.getInt();
		textureId = buf.get();
		texture = buf.getUTF8();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(entityId);
		buf.put(textureId);
		buf.putUTF8(texture);
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (texture.equals("[reset]")) {
			texture = null;
		}
		final CraftEntity entity = Spoutcraft.getWorld().getEntityFromId(entityId);
		if (entity != null) {
			entity.setSkin(texture, EntitySkinType.getType(textureId));
		}
	}	
}
