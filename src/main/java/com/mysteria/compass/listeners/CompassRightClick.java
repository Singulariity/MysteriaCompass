package com.mysteria.compass.listeners;

import com.mysteria.compass.CompassPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CompassRightClick implements Listener {

	public CompassRightClick() {
		Bukkit.getPluginManager().registerEvents(this, CompassPlugin.getInstance());
	}

	@EventHandler
	private void onCompassRightClick(PlayerInteractEvent e) {

		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.COMPASS) return;

		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.LODESTONE) return;
		}

		e.setCancelled(true);

		CompassPlugin.getCompassManager().CompassGUI(e.getPlayer());

	}

}
