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
package org.spoutcraft.api.io;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagEnd;
import org.spoutcraft.api.Spoutcraft;
import org.spoutcraft.api.gui.Color;
import org.spoutcraft.api.inventory.ItemStack;
import org.spoutcraft.api.util.FixedLocation;
import org.spoutcraft.api.util.FixedVector;
import org.spoutcraft.api.util.Location;
import org.spoutcraft.api.util.MutableLocation;
import org.spoutcraft.api.util.MutableVector;

public class MinecraftExpandableByteBuffer extends ExpandableByteBuffer {
	public static final byte FLAG_COLORINVALID = 1;
	public static final byte FLAG_COLOROVERRIDE = 2;

	public MinecraftExpandableByteBuffer() {
	}

	public MinecraftExpandableByteBuffer(ByteBuffer buf) {
		super(buf);
	}

	public MinecraftExpandableByteBuffer(int initialSize) {
		super(initialSize);
	}

	public MinecraftExpandableByteBuffer(byte[] data) {
		super(data);
	}

	public MinecraftExpandableByteBuffer(byte[] data, int offset, int length) {
		super(data, offset, length);
	}

	public void putLocation(FixedLocation loc) {
		putUUID(Spoutcraft.getWorld().getUID());
		putDouble(loc.getX());
		putDouble(loc.getY());
		putDouble(loc.getZ());
		putDouble(loc.getPitch());
		putDouble(loc.getYaw());
	}

	public Location getLocation() {
		//Client has only one world and the server shouldn't be sending us locations for another world
		if (getUUID() != Spoutcraft.getWorld().getUID()) {
			return null;
		}

		return new MutableLocation(getDouble(), getDouble(), getDouble(), getDouble(), getDouble());
	}

	public void putVector(FixedVector vector) {
		putDouble(vector.getX());
		putDouble(vector.getY());
		putDouble(vector.getZ());
	}

	public FixedVector getVector() {
		return new MutableVector(getDouble(), getDouble(), getDouble());
	}

	public void putNBTTagCompound(NBTTagCompound compound) throws IOException {
		if (compound == null) {
			throw new IOException("Attempt made to send null NBTTagCompound to the client!");
		}
		final byte[] compressed = compress(compound);
		if (compressed.length > Short.MAX_VALUE) {
			throw new IOException("NBTTagCompound is too large to be sent to the client!");
		}
		if (compressed.length == 0) {
			throw new IOException("Attempt made to send zero length NBTTagCompound to the client!");
		}
		putShort((short) compressed.length);
		put(compressed);
	}

	public NBTTagCompound getNBTTagCompound() throws IOException {
		final short len = getShort();
		final byte[] compressed = new byte[len];
		get(compressed);
		final NBTBase tag = decompress(compressed);
		if (tag instanceof NBTTagCompound) {
			return (NBTTagCompound) tag;
		} else {
			throw new IOException("Attempt to get NBTTagCompound but the tag's class is " + tag.getClass().getSimpleName());
		}
	}

	public void putItemStack(ItemStack stack) throws IOException {
		if (stack == null) {
			throw new IOException("Attempt made to send null ItemStack to the client!");
		}
		putInt(stack.getTypeId());
		put((byte) stack.getAmount());
		//TODO Dockter, is this equivalent to the server's getItemDamage?
		putShort(stack.getDurability());

		// TODO: In Bukkit 1.6.4, n is Item.isDamageable
		// TODO: In Bukkit 1.6.4, s is Item.getShareTag
		if (nmsStack.getItem().n() || nmsStack.getItem().s()) {
			putNBTTagCompound(nmsStack.getTag());
		}
	}

	public net.minecraft.server.v1_6_R3.ItemStack getItemStack() throws IOException {
		final int id = getInt();
		net.minecraft.server.v1_6_R3.ItemStack nmsStack = null;

		if (id >= 0) {
			final byte amount = get();
			final short damage = getShort();
			nmsStack = CraftItemStack.asNMSCopy(new ItemStack(id, amount, damage));
			nmsStack.setTag(getNBTTagCompound());
		}

		return nmsStack;
	}

	public void putSpoutMaterial(Material material) {
		putInt(material.getRawId());
		putShort((short) material.getRawData());
	}

	public Material getSpoutMaterial() {
		return MaterialData.getMaterial(getInt(), getShort());
	}

	public Color getColor() {
		byte flags = get();
		int argb = getInt();
		if ((flags & FLAG_COLORINVALID) > 0) {
			return Color.ignore();
		}
		if ((flags & FLAG_COLOROVERRIDE) > 0) {
			return Color.remove();
		}
		return new Color(argb);
	}

	public void putColor(Color c) {
		byte flags = 0x0;

		if (c.getRedF() == -1F) {
			flags |= FLAG_COLORINVALID;
		} else if (c.getRedF() == -2F) {
			flags |= FLAG_COLOROVERRIDE;
		}

		put(flags);
		putInt(c.toInt());
	}

	private byte[] compress(NBTBase base) throws IOException {
		final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

		try (DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream))) {
			dataoutputstream.writeByte(base.getId());
			if (base.getId() != 0) {
				dataoutputstream.writeUTF("");

				final Method nbtWrite = base.getClass().getDeclaredMethod("write", new Class[] {DataOutput.class});
				nbtWrite.setAccessible(true);
				nbtWrite.invoke(base, dataoutputstream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bytearrayoutputstream.toByteArray();
	}

	private NBTBase decompress(byte[] compressed) throws IOException {
		try (DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(compressed))))) {
			final byte typeId = datainputstream.readByte();
			if (typeId == 0) {
				return new NBTTagEnd();
			}
			getUTF8();
			// TODO: Check this
			NBTBase found = NBTBase.newTag(typeId, "");
			if (found == null) {
				throw new IOException("NBTTag sent from client does not exist on this server (hack?)!");
			}
			final Method nbtLoad = found.getClass().getDeclaredMethod("load", new Class[] {DataInput.class, Integer.class});
			nbtLoad.setAccessible(true);
			nbtLoad.invoke(found, datainputstream, 0);
			return found;
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
