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

import org.spoutcraft.api.gui.Color;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.api.player.SkyManager;
import org.spoutcraft.client.SpoutClient;

public class PacketChangeSkybox extends SpoutPacket {
	private int cloudY, stars, sunPercent, moonPercent;
	private Color skyColor, fogColor, cloudColor;
	String sun = "";
	String moon = "";
	public PacketChangeSky() {
	}

	public PacketChangeSkybox(int cloudY, int stars, int sunPercent, int moonPercent, Color skyColor) {
		this.cloudY = cloudY;
		this.stars = stars;
		this.sunPercent = sunPercent;
		this.moonPercent = moonPercent;
		this.skyColor = skyColor.clone();
	}

	public PacketChangeSkybox(String sunUrl, String moonUrl) {
		this.cloudY = 0;
		this.stars = 0;
		this.sunPercent = 0;
		this.moonPercent = 0;
		this.sun = sunUrl;
		this.moon = moonUrl;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		cloudY = buf.getInt();
		stars = buf.getInt();
		sunPercent = buf.getInt();
		moonPercent = buf.getInt();
		sun = buf.getUTF8();
		moon = buf.getUTF8();
		skyColor = buf.getColor();
		fogColor = buf.getColor();
		cloudColor = buf.getColor();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(cloudY);
		buf.putInt(stars);
		buf.putInt(sunPercent);
		buf.putInt(moonPercent);
		buf.putUTF8(sun);
		buf.putUTF8(moon);
		buf.putColor(skyColor);
		buf.putColor(fogColor);
		buf.putColor(cloudColor);
	}

	public void handle(SpoutPlayer player) {
		if (cloudY != 0) {
			SpoutClient.getInstance().getSkyManager().setCloudHeight(cloudY);
		}
		if (stars != 0) {
			SpoutClient.getInstance().getSkyManager().setStarFrequency(stars);
		}
		if (sunPercent != 0) {
			SpoutClient.getInstance().getSkyManager().setSunSizePercent(sunPercent);
		}
		if (moonPercent != 0) {
			SpoutClient.getInstance().getSkyManager().setMoonSizePercent(moonPercent);
		}
		if (sun != null) {
			if (sun.equals("[reset]")) {
				SpoutClient.getInstance().getSkyManager().setSunTextureUrl(null);
			} else if (sun.length() > 5) {
				SpoutClient.getInstance().getSkyManager().setSunTextureUrl(sun);
			}
		}
		if (moon != null) {
			if (moon.equals("[reset]")) {
				SpoutClient.getInstance().getSkyManager().setMoonTextureUrl(null);
			} else if (moon.length() > 5) {
				SpoutClient.getInstance().getSkyManager().setMoonTextureUrl(moon);
			}
		}
		SkyManager sky = SpoutClient.getInstance().getSkyManager();

		// Sky
		if (skyColor.isOverride()) {
			sky.setSkyColor(null);
		} else if (!skyColor.isInvalid()) {
			sky.setSkyColor(skyColor);
		}

		// Fog
		if (fogColor.isOverride()) {
			sky.setFogColor(null);
		} else if (!fogColor.isInvalid()) {
			sky.setFogColor(fogColor);
		}

		// Cloud
		if (cloudColor.isOverride()) {
			sky.setCloudColor(null);
		} else if (!cloudColor.isInvalid()) {
			sky.setCloudColor(cloudColor);
		}
	}
}
