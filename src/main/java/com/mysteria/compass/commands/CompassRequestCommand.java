package com.mysteria.compass.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mysteria.compass.CompassManager;
import com.mysteria.compass.CompassPlugin;
import com.mysteria.compass.enums.SelectTargetResult;
import com.mysteria.utils.MysteriaUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("compassrequest|crequest")
public class CompassRequestCommand extends BaseCommand {

	@Default
	@CommandCompletion("@crequest @players @nothing")
	@Syntax("<accept|deny> <player>")
	@Description("Send a compass request.")
	public void onCommand(Player sender, String[] args) {

		if (args.length < 2) return;

		CompassManager compassManager = CompassPlugin.getCompassManager();

		Player find = Bukkit.getPlayer(args[1]);

		if (find == null || !compassManager.requests.containsKey(find.getUniqueId())) {
			MysteriaUtils.sendMessageRed(sender, "Request not found.");
			return;
		}

		switch (args[0].toLowerCase()) {
			case "accept":
				compassManager.acceptRequest(find, sender);
				SelectTargetResult result = compassManager.setCompassTarget(find, sender.getLocation());
				compassManager.sendSelectTargetResultMessage(find, result);
				break;
			case "deny":
				compassManager.denyRequest(find, sender);
				break;
		}

	}

}
