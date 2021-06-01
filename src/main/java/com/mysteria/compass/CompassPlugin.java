package com.mysteria.compass;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.mysteria.compass.commands.CompassRequestCommand;
import com.mysteria.compass.listeners.CompassGUIClick;
import com.mysteria.compass.listeners.CompassRightClick;
import com.mysteria.compass.listeners.WriteMode;
import org.bukkit.command.defaults.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CompassPlugin extends JavaPlugin {

	private static CompassPlugin instance;
	private static CompassManager compassManager;
	private static PaperCommandManager commandManager;

	public CompassPlugin() {
		if (instance != null) throw new IllegalStateException();
		instance = this;
	}

	@Override
	public void onEnable() {
		compassManager = new CompassManager();
		commandManager = new PaperCommandManager(getInstance());

		registerListeners();
		registerCommands();
	}

	private void registerCommandCompletions() {
		getCommandManager().getCommandCompletions().registerAsyncCompletion("crequest", c ->
				ImmutableList.of("accept", "deny"));
	}

	private void registerCommands() {
		registerCommandCompletions();
		getCommandManager().registerCommand(new CompassRequestCommand());
	}

	private void registerListeners() {
		new CompassRightClick();
		new CompassGUIClick();
		new WriteMode();
	}

	public static PaperCommandManager getCommandManager() {
		return commandManager;
	}

	public static CompassManager getCompassManager() {
		return compassManager;
	}

	public static CompassPlugin getInstance() {
		if (instance == null) throw new IllegalStateException();
		return instance;
	}

}
