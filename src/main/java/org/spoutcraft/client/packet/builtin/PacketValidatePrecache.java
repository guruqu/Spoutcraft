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

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.precache.PrecacheManager;
import org.spoutcraft.client.precache.PrecacheTuple;

public class PacketValidatePrecache extends SpoutPacket {
	int count;
	PrecacheTuple[] plugins;

	protected PacketValidatePrecache() {
	}

	public PacketValidatePrecache(HashMap<Plugin, Long> pluginMap) {
		count = pluginMap.size();
		plugins = new PrecacheTuple[count];
		int i = 0;
		for (Entry entry : pluginMap.entrySet()) {
			Plugin p = (Plugin) entry.getKey();
			plugins[i] = new PrecacheTuple(
					p.getDescription().getName(),
					p.getDescription().getVersion(),
					(Long) entry.getValue()
					);
			i++;
		}
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		count = buf.getInt();
		if (count > 0) {
			plugins = new PrecacheTuple[count];
			for (int i = 0; i < count; i++) {
				String plugin = buf.getUTF8();
				String version = buf.getUTF8();
				long crc = buf.getLong();
				plugins[i] = new PrecacheTuple(plugin, version, crc);
			}
		}
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(count);
		for (int i = 0; i < count; i++) {
			buf.putUTF8(plugins[i].getPlugin());
			buf.putUTF8(plugins[i].getVersion());
			buf.putLong(plugins[i].getCrc());
		}
	}

	@Override
	public void handle(SpoutPlayer player) {
		PrecacheManager.reset();
		// Build the precache list
		for (PrecacheTuple plugin : plugins) {
			PrecacheManager.addPlugin(plugin);
		}

		PrecacheManager.doNextCache();
	}	
}
