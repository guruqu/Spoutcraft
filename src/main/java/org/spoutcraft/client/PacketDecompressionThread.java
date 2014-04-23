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
package org.spoutcraft.client;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import org.spoutcraft.client.packet.builtin.CompressiblePacket;
import org.spoutcraft.client.packet.builtin.SpoutPacket;

public class PacketDecompressionThread extends Thread {
	private static PacketDecompressionThread instance = null;

	private static final int QUEUE_CAPACITY = 1024 * 10;
	private final LinkedBlockingDeque<CompressiblePacket> queue = new LinkedBlockingDeque<>(QUEUE_CAPACITY);
	private final List<CompressiblePacket> decompressed = Collections.synchronizedList(new LinkedList<CompressiblePacket>());

	private PacketDecompressionThread() {
	}

	public static void startThread() {
		instance = new PacketDecompressionThread();
		instance.start();
	}

	public static void endThread() {
		if (instance == null) {
			return;
		}
		instance.interrupt();
		try {
			instance.join();
		} catch (InterruptedException ignored) {
		}
		instance = null;
	}

	public static PacketDecompressionThread getInstance() {
		return instance;
	}

	public static void add(CompressiblePacket packet) {
		if (instance != null) {
			instance.queue.add(packet);
		}
	}

	public void run() {
		while (!isInterrupted()) {
			try {
				CompressiblePacket packet = queue.take();
				packet.decompress();
				synchronized(decompressed) {
					decompressed.add(packet);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public static void onTick() {
		synchronized(instance.decompressed) {
			Iterator<CompressiblePacket> i = instance.decompressed.iterator();
			while (i.hasNext()) {
				SpoutPacket packet = i.next();
				try {
					SpoutClient.getHandle().mcProfiler.startSection("Protocol: Handle - CompressiblePacket_" + packet.getClass().getSimpleName());
					packet.handle(SpoutClient.getInstance().player);
					i.remove();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					SpoutClient.getHandle().mcProfiler.endSection();
				}
			}
		}
	}
}
