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

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.SoundManager;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.api.sound.Music;
import org.spoutcraft.api.sound.SoundEffect;
import org.spoutcraft.client.SpoutClient;

public class PacketPlaySound extends SpoutPacket {
	private short soundId;
	private boolean location = false;
	private int x, y, z;
	private int volume, distance;

	protected PacketPlaySound() {
	}

	public PacketPlaySound(SoundEffect sound, int distance, int volume) {
		soundId = (short) sound.getId();
		this.volume = volume;
		this.distance = distance;
	}

	public PacketPlaySound(SoundEffect sound, Location loc, int distance, int volume) {
		soundId = (short) sound.getId();
		location = true;
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		this.volume = volume;
		this.distance = distance;
	}

	public PacketPlaySound(Music music, int volume) {
		soundId = (short) (music.getId() + (1 + SoundEffect.getMaxId()));
		this.volume = volume;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		soundId = buf.getShort();
		location = buf.getBoolean();
		x = buf.getInt();
		y = buf.getInt();
		z = buf.getInt();
		distance = buf.getInt();
		volume = buf.getInt();
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putShort(soundId);
		buf.putBoolean(location);
		if (!location) {
			buf.putInt(-1);
			buf.putInt(-1);
			buf.putInt(-1);
			buf.putInt(-1);
		} else {
			buf.putInt(x);
			buf.putInt(y);
			buf.putInt(z);
			buf.putInt(distance);
		}
		buf.putInt(volume);
	}

	public void handle(SpoutPlayer player) {
		EntityPlayer e = SpoutClient.getInstance().getPlayerFromId(entityId);
		if (e != null) {
			SoundManager sndManager = SpoutClient.getHandle().sndManager;
			if (soundId > -1 && soundId <= SoundEffect.getMaxId()) {
				SoundEffect effect = SoundEffect.getSoundEffectFromId(soundId);
				if (!location) {
					sndManager.playSoundFX(effect.getName(), 0.5F, 0.7F, effect.getVariationId(), volume / 100F);
				} else {
					sndManager.playSound(effect.getName(), x, y, z, 0.5F, (distance / 16F), effect.getVariationId(), volume / 100F);
				}
			}
			soundId -= (1 + SoundEffect.getMaxId());
			if (soundId > -1 && soundId <= Music.getMaxId()) {
				Music music = Music.getMusicFromId(soundId);
				sndManager.playMusic(music.getName(), music.getSoundId(), volume / 100F);
			}
		}
	}
}
