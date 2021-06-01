package com.mysteria.compass.listeners;

import com.mysteria.compass.CompassManager;
import com.mysteria.compass.CompassPlugin;
import com.mysteria.compass.enums.SetNoteResult;
import com.mysteria.utils.MysteriaUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WriteMode implements Listener {

	public WriteMode() {
		Bukkit.getPluginManager().registerEvents(this, CompassPlugin.getInstance());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onChat(AsyncChatEvent e) {

		CompassManager compassManager = CompassPlugin.getCompassManager();
		Player p = e.getPlayer();

		if (compassManager.RequestWriteModes.contains(p.getUniqueId())) {

			e.setCancelled(true);
			compassManager.RequestWriteModes.remove(p.getUniqueId());

			String message = MysteriaUtils.translateToString(e.message());

			if (message.equals("exit")) {
				MysteriaUtils.sendMessageYellow(p, "Cancelled.");
				return;
			}

			Player target = Bukkit.getPlayer(message);

			if (target == null) {
				MysteriaUtils.sendMessageRed(p, "Player not found.");
				return;
			}

			if (p == target) {
				MysteriaUtils.sendMessageDarkRed(p, "You can't send request to yourself.");
				return;
			}

			compassManager.sendRequest(p, target);

		}
		else if (compassManager.NoteWriteModes.contains(p.getUniqueId())) {

			e.setCancelled(true);
			compassManager.NoteWriteModes.remove(p.getUniqueId());

			String message = MysteriaUtils.translateToString(e.message());

			if (message.equals("exit")) {
				MysteriaUtils.sendMessageYellow(p, "Cancelled.");
				return;
			}

			String note;
			if (message.equals("clear")) {
				note = null;
			} else {
				note = message;
			}

			SetNoteResult setNoteResult = compassManager.setCompassNote(p, note);
			compassManager.sendSetNoteResultMessage(p, setNoteResult);

		}




	}


}
