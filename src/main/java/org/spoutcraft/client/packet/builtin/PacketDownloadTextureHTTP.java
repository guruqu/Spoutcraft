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

import net.minecraft.src.AbstractClientPlayer;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketSkinURL extends SpoutPacket {
	public int entityId;
	public String skinURL;
	public String cloakURL;
	public boolean release = true;

	protected PacketDownloadTextureHTTP() {
	}

	public PacketDownloadTextureHTTP(int id, String skinURL, String cloakURL) {
		this.entityId = id;
		this.skinURL = skinURL;
		this.cloakURL = cloakURL;
		release = false;
	}

	public PacketDownloadTextureHTTP(int id, String skinURL) {
		this.entityId = id;
		this.skinURL = skinURL;
		this.cloakURL = "none";
	}

	public PacketDownloadTextureHTTP(String cloakURL, int id) {
		this.entityId = id;
		this.skinURL = "none";
		this.cloakURL = cloakURL;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		entityId = buf.getInt();
		skinURL = buf.getUTF8();
		cloakURL = buf.getUTF8();
		release = buf.getBoolean();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(entityId);
		buf.putUTF8(skinURL);
		buf.putUTF8(cloakURL);
		buf.putBoolean(release);
	}

	public void handle(SpoutPlayer player) {
		AbstractClientPlayer e = SpoutClient.getInstance().getAbstractPlayerFromId(entityId);
		if (e != null) {
			// Check if these are the Minecraft skin/cape, if so, use defaults instead
			String mcSkin = "http://s3.amazonaws.com/MinecraftSkins/" + e.username + ".png";
			String mcCape = "http://s3.amazonaws.com/MinecraftCloaks/" + e.username + ".png";
			if (!"none".equals(this.skinURL)) {
				//System.out.println(e.username + " is going to be sent skinURL: " + skinURL + " from SpoutPlugin's API.");
			}
			if (this.skinURL.equalsIgnoreCase(mcSkin)) {
				this.skinURL = "http://cdn.spout.org/game/vanilla/skin/" + e.username + ".png";
			}
			if (this.cloakURL.equalsIgnoreCase(mcCape)) {
				if (e.vip != null && e.vip.getCape() != null) {
					this.cloakURL = e.vip.getCape();
				} else {
					this.cloakURL = "http://cdn.spout.org/game/vanilla/cape/" + e.username + ".png";
				}
			}

			if (!"none".equals(this.skinURL)) {
				e.customSkinUrl = this.skinURL;
			}
			if (!"none".equals(this.cloakURL)) {
				e.customCapeUrl = this.cloakURL;
			}

			e.setupCustomSkin();

		}
	}
}
