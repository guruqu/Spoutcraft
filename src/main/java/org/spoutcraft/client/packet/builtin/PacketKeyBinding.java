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

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.api.keyboard.KeyBinding;
import org.spoutcraft.client.SpoutClient;

public class PacketKeyBinding implements SpoutPacket {
	private String id;
	private String plugin;
	private String description;
	private int key;
	private boolean pressed;
	private UUID uniqueId;

	protected PacketKeyBinding() {
	}

	public PacketKeyBinding(KeyBinding binding, int key, boolean pressed, int screen) {
		this.key = key;
		this.pressed = pressed;
		this.uniqueId = binding.getUniqueId();
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		id = buf.getInt();
		description = buf.getUTF8();
		plugin = buf.getUTF8();
		key = buf.getInt();
        uniqueId = buf.getUUID();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(key);
		buf.putBoolean(pressed);
		buf.putUUID(uniqueId);
	}

	public void handle(SpoutPlayer player) {
		KeyBinding binding = new KeyBinding(key, plugin, id, description);
		binding.setUniqueId(uniqueId);
		SpoutClient.getInstance().getKeyBindingManager().registerControl(binding);
	}
}
