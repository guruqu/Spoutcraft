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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ScreenShotHelper;

import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;

public class PacketScreenshot extends SpoutPacket {
	private byte[] ssAsPng = null;
	private boolean isRequest = false;

	protected PacketScreenshot() {
		isRequest = true;
	}

	public PacketScreenshot(BufferedImage ss) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(ss, "png", baos);
		baos.flush();
		ssAsPng = baos.toByteArray();
		baos.close();
	}

	public int getNumBytes() {
		if (ssAsPng == null) {
			return 1;
		}
		return ssAsPng.length + 5;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		isRequest = buf.getBoolean();
		if (!isRequest) {
			int ssLen = buf.getInt();
			ssAsPng = new byte[ssLen];
			buf.get(ssAsPng);
		}
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		if (ssAsPng == null) {
			buf.putBoolean(true);
		} else {
			buf.putBoolean(false);
			buf.putInt(ssAsPng.length);
			buf.put(ssAsPng);
		}
	}

	@Override
	public void handle(SpoutPlayer player) {
		if (!isRequest) {
			return; // we can't do anything!
		}
		try {
			SpoutClient.getInstance().getActivePlayer().showAchievement("Sending screenshot...", "Screenshot requested", 321);
			BufferedImage screenshot = ScreenShotHelper.getScreenshot(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
			PacketScreenshot packet = new PacketScreenshot(screenshot);
			SpoutClient.getInstance().getPacketManager().sendSpoutPacket(packet); // TODO: Fix this
		} catch (IOException ioe) {
			ioe.printStackTrace();
			SpoutClient.getInstance().getActivePlayer().showAchievement("Sending screenshot...", "Failed!", 321);
		}
	}
}
