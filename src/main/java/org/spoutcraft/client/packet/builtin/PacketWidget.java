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
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.spoutcraft.api.gui.GenericWidget;
import org.spoutcraft.api.gui.InGameHUD;
import org.spoutcraft.api.gui.OverlayScreen;
import org.spoutcraft.api.gui.PopupScreen;
import org.spoutcraft.api.gui.Screen;
import org.spoutcraft.api.gui.Widget;
import org.spoutcraft.api.gui.WidgetType;
import org.spoutcraft.api.io.MinecraftExpandableByteBuffer;
import org.spoutcraft.client.packet.CustomPacket;
import org.spoutcraft.client.player.SpoutPlayer;
import org.spoutcraft.client.SpoutClient;
import org.spoutcraft.client.gui.CustomScreen;

public class PacketWidget extends SpoutPacket { // TODO: Review class and update fully for MEBB
	private Byte widgetData;
	private WidgetType widgetType;
	private UUID widgetId;
	private int version;
	private Widget widget;
	private UUID screen;

	protected final static Map<UUID, Widget> ALL_WIDGETS = new HashMap<>();

	protected PacketWidget() {
	}

	public PacketWidget(Widget widget, UUID screen) {
		this.widget = widget;
		this.screen = screen;
	}

	@Override
	public void decode(MinecraftExpandableByteBuffer buf) throws IOException {
		final int id = buf.getInt();
		screen = buf.getUUID();

		final WidgetType widgetType = WidgetType.getWidgetFromId(id);
		if (widgetType != null) {
			try {
				widget = widgetType.getWidgetClass().newInstance();
				if (widget.getVersion() == buf.getInt()) {
					final byte[] data = new byte[buf.getInt()];
					buf.get(data);
					widget.decode(new MinecraftExpandableByteBuffer(data));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void encode(MinecraftExpandableByteBuffer buf) throws IOException {
		buf.putInt(widget.getType().getId());
		buf.putUUID(screen);
		buf.putUUID(widget.getId());
		buf.putShort((short) widget.getVersion());

		buf.mark();
		widget.encode(buf);
		buf.reset();
		buf.putInt(buf.remaining());
	}

	@Override
	public void handle(SpoutPlayer player) {
		try {
			if (ALL_WIDGETS.containsKey(widgetId)) {
				widget = ALL_WIDGETS.get(widgetId);
				if (widget.getVersion() == version) {
					widget.decode(new MinecraftExpandableByteBuffer(widgetData));
				}
			} else {
				widget = widgetType.getWidgetClass().newInstance();

				// Hackish way to set the ID without a setter
				((GenericWidget) widget).setId(widgetId);
				if (widget.getVersion() == version) {
					widget.decode(new MinecraftExpandableByteBuffer(widgetData));
				} else {
					widget = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (widget != null) {
			ALL_WIDGETS.put(widgetId, widget);
			InGameHUD mainScreen = SpoutClient.getInstance().getActivePlayer().getMainScreen();
			PopupScreen popup = mainScreen.getActivePopup();
			Screen overlay = null;

			if (SpoutClient.getHandle().currentScreen != null) {
				overlay = SpoutClient.getHandle().currentScreen.getScreen();
			}
			// Determine if this is a popup screen and if we need to update it
			if (widget instanceof PopupScreen) {
				if (popup != null) {
					if (widget.getId().equals(popup.getId())) {
						if (SpoutClient.getHandle().currentScreen instanceof CustomScreen) {
							(SpoutClient.getHandle().currentScreen).update((PopupScreen)widget);
						}
					} else {
						mainScreen.closePopup();
						mainScreen.attachPopupScreen((PopupScreen)widget);
					}
				} else {
					mainScreen.attachPopupScreen((PopupScreen)widget);
				}
			} else if (widget instanceof OverlayScreen) { // Determine if this screen overrides another screen
				if (SpoutClient.getHandle().currentScreen != null) {
					SpoutClient.getHandle().currentScreen.update((OverlayScreen)widget);
					overlay = (OverlayScreen)widget;
				}
			} else if (screen.equals(mainScreen.getId())) { // Determine if this is a widget on the main screen
				if (mainScreen.containsWidget(widget.getId())) {
					mainScreen.updateWidget(widget);
					widget.setScreen(mainScreen);
				} else {
					widget.setScreen(mainScreen);
					mainScreen.attachWidget(widget.getAddon(), widget);
				}
			} else if (popup != null && screen.equals(popup.getId())) { // Determine if this is a widget on the popup screen
				if (popup.containsWidget(widget.getId())) {
					popup.updateWidget(widget);
					widget.setScreen(popup);
				} else {
					widget.setScreen(popup);
					popup.attachWidget(widget.getAddon(), widget);
				}
			} else if (overlay != null && screen.equals(overlay.getId())) { // Determine if this is a widget on an overlay screen
				if (overlay.containsWidget(widget.getId())) {
					overlay.updateWidget(widget);
					widget.setScreen(overlay);
				} else {
					widget.setScreen(overlay);
					overlay.attachWidget(widget.getAddon(), widget);
				}
			}
		}
	}
}
