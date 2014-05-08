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
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.player.SpoutPlayer;

public class PacketCustomBlockOverride extends SpoutPacket {
	private int x;
	private short y;
	private int z;
	private short blockId;
	private byte data;

	protected PacketCustomBlockOverride() {
	}

	public PacketCustomBlockOverride(int x, int y, int z, Integer blockId, Byte data) {
		this.x = x;
		this.y = (short) (y & 0xFFFF);
		this.z = z;
		setBlockId(blockId);
		setBlockData(data);
	}

	private void setBlockId(Integer blockId) {
		if (blockId == null) {
			this.blockId = -1;
		} else {
			this.blockId = blockId.shortValue();
		}
	}

	protected Integer getBlockId() {
		return blockId == -1 ? null : (int) blockId;
	}

	private void setBlockData(Byte data) {
		if (data == null) {
			this.data = -1;
		} else {
			this.data = data;
		}
	}

	protected Byte getBlockDatas() {
		return data == -1 ? null : data;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		x = buf.getInt();
		y = buf.getShort();
		z = buf.getInt();
		setBlockId((int) buf.getShort());
		setBlockData(buf.get());
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(x);
		buf.putShort(y);
		buf.putInt(z);
		buf.putShort(blockId);
		buf.put(data);
	}

	@Override
	public void handle(SpoutPlayer player) {
		Spoutcraft.getChunkAt(SpoutClient.getInstance().getRawWorld(), x, y, z).setCustomBlockId(x, y, z, blockId);
		Spoutcraft.getChunkAt(SpoutClient.getInstance().getRawWorld(), x, y, z).setCustomBlockData(x, y, z, data);
	}

	public int getVersion() {
		return 0;
	}
}
