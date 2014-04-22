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
package org.getspout.spoutapi.io;

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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.src.Item;
import net.minecraft.src.NBTBase;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagEnd;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.util.Vector;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;

public class MinecraftExpandableByteBuffer extends ExpandableByteBuffer {
	public void putLocation(Location loc) {
		putUUID(loc.getWorld().getUID());
		putDouble(loc.getX());
		putDouble(loc.getY());
		putDouble(loc.getZ());
		putFloat(loc.getPitch());
		putFloat(loc.getYaw());
	}

	public Location getLocation() {
		final World world = Bukkit.getWorld(getUUID());
		if (world == null) {
			return null;
		}

		return new Location(world, getDouble(), getDouble(), getDouble(), getFloat(), getFloat());
	}

	public void putVector(Vector vector) {
		putDouble(vector.getX());
		putDouble(vector.getY());
		putDouble(vector.getZ());
	}

	public Vector getVector() {
		return new Vector(getDouble(), getDouble(), getDouble());
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
		final net.minecraft.server.v1_6_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
		putInt(nmsStack.getItem().id);
		put((byte) nmsStack.count);

		// TODO: In Bukkit 1.6.4, j is Item.getItemDamage
		putShort((short) nmsStack.j());

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

	private byte[] compress(NBTBase base) throws IOException {
		final ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
		final DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));

		try {
			dataoutputstream.writeByte(base.getTypeId());
			if (base.getTypeId() != 0) {
				dataoutputstream.writeUTF("");

				final Method nbtWrite = base.getClass().getDeclaredMethod("write", new Class[] {DataOutput.class});
				nbtWrite.setAccessible(true);
				nbtWrite.invoke(base, dataoutputstream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dataoutputstream.close();
		}

		return bytearrayoutputstream.toByteArray();
	}

	private NBTBase decompress(byte[] compressed) throws IOException {
		final DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(compressed))));

		try {
			final byte typeId = datainputstream.readByte();
			if (typeId == 0) {
				return new NBTTagEnd();
			}
			getUTF8();
			// TODO: Check this
			NBTBase found = NBTBase.createTag(typeId, "");
			if (found == null) {
				throw new IOException("NBTTag sent from client does not exist on this server (hack?)!");
			}
			final Method nbtLoad = found.getClass().getDeclaredMethod("load", new Class[] {DataInput.class, Integer.class});
			nbtLoad.setAccessible(true);
			nbtLoad.invoke(found, datainputstream, 0);
			return found;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {
			datainputstream.close();
		}
		return null;
	}
}