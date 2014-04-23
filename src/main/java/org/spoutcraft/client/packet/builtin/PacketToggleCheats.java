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
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketToggleCheats implements SpoutPacket {
	private boolean cheatsky = false;
	private boolean forcesky = false;
	private boolean showsky = false;
	private boolean cheatclearwater = false;
	private boolean forceclearwater = false;
	private boolean showclearwater = false;
	private boolean cheatstars = false;
	private boolean forcestars = false;
	private boolean showstars = false;
	private boolean cheatweather = false;
	private boolean forceweather = false;
	private boolean showweather = false;
	private boolean time = false;
	private boolean coords = false;
	private boolean entitylabel = false;
	private boolean cheatvoidfog = false;
	private boolean forcevoidfog = false;
	private boolean showvoidfog = false;
	private boolean flightspeed = false;

	protected PacketToggleCheats() {
	}

	public PacketToggleCheats(boolean tsky, boolean fsky, boolean ssky, boolean tclearwater, boolean fclearwater, boolean sclearwater, boolean tstars, boolean fstars, boolean sstars, boolean tweather, boolean fweather, boolean sweather, boolean ttime, boolean tcoords, boolean tentitylabel, boolean tvoidfog, boolean fvoidfog, boolean svoidfog, boolean tflightspeed) {
		this.cheatsky = tsky;
		this.forcesky = fsky;
		this.showsky = ssky;
		this.cheatclearwater = tclearwater;
		this.forceclearwater = fclearwater;
		this.showclearwater = sclearwater;
		this.cheatstars = tstars;
		this.forcestars = fstars;
		this.showstars = sstars;
		this.cheatweather = tweather;
		this.forceweather = fweather;
		this.showweather = sweather;
		this.time = ttime;
		this.coords = tcoords;
		this.entitylabel = tentitylabel;
		this.cheatvoidfog = tvoidfog;
		this.forcevoidfog = fvoidfog;
		this.showvoidfog = svoidfog;
		this.flightspeed = tflightspeed;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		throw new IOException("The server should not receive a PacketToggleCheats from the client (hack?)!");
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putBoolean(cheatsky);
		buf.putBoolean(forcesky);
		buf.putBoolean(showsky);
		buf.putBoolean(cheatclearwater);
		buf.putBoolean(forceclearwater);
		buf.putBoolean(showclearwater);
		buf.putBoolean(cheatstars);
		buf.putBoolean(forcestars);
		buf.putBoolean(showstars);
		buf.putBoolean(cheatweather);
		buf.putBoolean(forceweather);
		buf.putBoolean(showweather);
		buf.putBoolean(time);
		buf.putBoolean(coords);
		buf.putBoolean(entitylabel);
		buf.putBoolean(cheatvoidfog);
		buf.putBoolean(forcevoidfog);
		buf.putBoolean(showvoidfog);
		buf.putBoolean(flightspeed);
	}

	public void handle(SpoutPlayer player) {
		SpoutClient.getInstance().setVisualCheats(cheatsky, forcesky, showsky, cheatclearwater, forceclearwater, showclearwater, cheatstars, forcestars, showstars, cheatweather, forceweather, showweather, time, coords, entitylabel, cheatvoidfog, forcevoidfog, showvoidfog, flyspeed);
	}	
}
