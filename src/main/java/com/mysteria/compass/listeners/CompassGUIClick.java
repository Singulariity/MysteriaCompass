package com.mysteria.compass.listeners;

import com.mysteria.compass.CompassManager;
import com.mysteria.compass.CompassPlugin;
import com.mysteria.compass.enums.ResetCompassResult;
import com.mysteria.compass.enums.SelectTargetResult;
import com.mysteria.customapi.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CompassGUIClick implements Listener {

	public CompassGUIClick() {
		Bukkit.getPluginManager().registerEvents(this, CompassPlugin.getInstance());
	}


	@EventHandler(ignoreCancelled = true)
	private void onGUIClick(InventoryClickEvent e) {

		CompassManager compassManager = CompassPlugin.getCompassManager();

		if (!e.getView().title().equals(compassManager.getGUIName())) return;

		e.setCancelled(true);

		// Filled slot click check
		if (e.getCurrentItem() == null || CustomItem.checkCustomItem(e.getCurrentItem(), CustomItem.EMPTY)) return;

		Player p = (Player) e.getWhoClicked();
		SelectTargetResult result;

		switch (e.getRawSlot()) {
			case 10:
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

				result = compassManager.setCompassTarget(p, p.getBedSpawnLocation());
				compassManager.sendSelectTargetResultMessage(p, result);
				break;

			case 12:
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

				result = compassManager.setCompassTarget(p, p.getLocation());
				compassManager.sendSelectTargetResultMessage(p, result);
				break;

			case 14:
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

				compassManager.requestWriteMode(p);
				break;

			case 16:
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

				ResetCompassResult resetCompassResult = compassManager.clearCompassTarget(p);
				compassManager.sendClearResultMessage(p, resetCompassResult);
				break;

			case 27:
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

				compassManager.noteWriteMode(p);
				break;

			case 31:
				p.closeInventory();
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

				if (compassManager.getMeteoriteLocation() == null) return;

				result = compassManager.setCompassTarget(p, compassManager.getMeteoriteLocation());
				compassManager.sendSelectTargetResultMessage(p, result);
				break;

		}




	}





}
