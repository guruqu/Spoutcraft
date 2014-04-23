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

import net.minecraft.src.MovementInputFromOptions;

import org.spoutcraft.api.gui.ScreenType;
import org.spoutcraft.api.io.SpoutInputStream;
import org.spoutcraft.api.io.SpoutOutputStream;

public class PacketKeyPress implements SpoutPacket {
	public boolean pressDown;
	public int key;
	public byte settingKeys[] = new byte[10];
	public int screenType = -1;

	protected PacketKeyPress() {
	}

	public PacketKeyPress(int key, boolean pressDown) {
		this.key = key;
		this.pressDown = pressDown;
	}

	public PacketKeyPress(int key, boolean pressDown, MovementInputFromOptions input) {
		this.key = key;
		this.pressDown = pressDown;
		this.settingKeys[0] = (byte)input.gameSettings.keyBindForward.keyCode;
		this.settingKeys[1] = (byte)input.gameSettings.keyBindLeft.keyCode;
		this.settingKeys[2] = (byte)input.gameSettings.keyBindBack.keyCode;
		this.settingKeys[3] = (byte)input.gameSettings.keyBindRight.keyCode;
		this.settingKeys[4] = (byte)input.gameSettings.keyBindJump.keyCode;
		this.settingKeys[5] = (byte)input.gameSettings.keyBindInventory.keyCode;
		this.settingKeys[6] = (byte)input.gameSettings.keyBindDrop.keyCode;
		this.settingKeys[7] = (byte)input.gameSettings.keyBindChat.keyCode;
		this.settingKeys[8] = (byte)input.gameSettings.keyBindToggleFog.keyCode;
		this.settingKeys[9] = (byte)input.gameSettings.keyBindSneak.keyCode;
	}

	public PacketKeyPress(int key, boolean pressDown, MovementInputFromOptions input, ScreenType type) {
		this.key = key;
		this.pressDown = pressDown;
		this.settingKeys[0] = (byte)input.gameSettings.keyBindForward.keyCode;
		this.settingKeys[1] = (byte)input.gameSettings.keyBindLeft.keyCode;
		this.settingKeys[2] = (byte)input.gameSettings.keyBindBack.keyCode;
		this.settingKeys[3] = (byte)input.gameSettings.keyBindRight.keyCode;
		this.settingKeys[4] = (byte)input.gameSettings.keyBindJump.keyCode;
		this.settingKeys[5] = (byte)input.gameSettings.keyBindInventory.keyCode;
		this.settingKeys[6] = (byte)input.gameSettings.keyBindDrop.keyCode;
		this.settingKeys[7] = (byte)input.gameSettings.keyBindChat.keyCode;
		this.settingKeys[8] = (byte)input.gameSettings.keyBindToggleFog.keyCode;
		this.settingKeys[9] = (byte)input.gameSettings.keyBindSneak.keyCode;
		this.screenType = type.getCode();
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		key = buf.getInt();
		pressDown = buf.getBoolean();
		screenType = buf.getInt();
		for (int i = 0; i < 10; i++) {
			settingKeys[i] = buf.get();
		}
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(key);
		buf.putBoolean(pressDown);
		buf.putInt(screenType);
		for (int i = 0; i < 10; i++) {
			buf.put(settingKeys[i]);
		}
	}

	@Override
	public void handle(SpoutPlayer player) {
	}	
}
