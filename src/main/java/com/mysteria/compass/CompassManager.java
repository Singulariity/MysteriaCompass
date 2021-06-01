package com.mysteria.compass;

import com.mysteria.compass.enums.ResetCompassResult;
import com.mysteria.compass.enums.SelectTargetResult;
import com.mysteria.compass.enums.SetNoteResult;
import com.mysteria.customapi.items.CustomItem;
import com.mysteria.utils.ItemBuilder;
import com.mysteria.utils.MysteriaUtils;
import com.mysteria.utils.NamedColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class CompassManager {

	public final Component name = Component.text("Compass", NamedTextColor.DARK_GRAY);
	public final List<UUID> RequestWriteModes = new ArrayList<>();
	public final List<UUID> NoteWriteModes = new ArrayList<>();
	public final HashMap<UUID, UUID> requests = new HashMap<>();
	private Location meteoriteLocation = null;

	public CompassManager() {
		if (CompassPlugin.getCompassManager() != null) {
			throw new IllegalStateException();
		}
	}


	public void CompassGUI(@Nonnull Player p) {

		Component name = getGUIName();
		Inventory inv = Bukkit.createInventory(null, 36, name);

		ItemStack empty = ItemBuilder.builder(CustomItem.EMPTY.getItemStack())
				.adaptGUI()
				.build();

		inv.setItem(10, ItemBuilder.builder(Material.PINK_BED)
				.name(Component.text("House Location", NamedTextColor.DARK_GREEN))
				.lore(
						Component.text(" "),
						Component.text("Set your bed location to", NamedTextColor.GRAY),
						Component.text("target of held compass.", NamedTextColor.GRAY)
				)
				.adaptGUI()
				.build());
		inv.setItem(12, ItemBuilder.builder(Material.GRASS_BLOCK)
				.name(Component.text("Current Location", NamedTextColor.GREEN))
				.lore(
						Component.text(" "),
						Component.text("Set current location to", NamedTextColor.GRAY),
						Component.text("target of held compass.", NamedTextColor.GRAY)
				)
				.adaptGUI()
				.build());
		inv.setItem(14, ItemBuilder.builder(Material.PLAYER_HEAD)
				.name(Component.text("Another Player's Location", NamedTextColor.YELLOW))
				.lore(
						Component.text(" "),
						Component.text("Set a player's location to", NamedTextColor.GRAY),
						Component.text("target of held compass.", NamedTextColor.GRAY),
						Component.text(" "),
						Component.text("You need to get other player confirm", NamedTextColor.GRAY),
						Component.text("tracking them due to this option being", NamedTextColor.GRAY),
						Component.text("used with requests.", NamedTextColor.GRAY)
				)
				.adaptGUI()
				.build());
		inv.setItem(16, ItemBuilder.builder(Material.BARRIER)
				.name(Component.text("Reset Compass", NamedTextColor.RED))
				.lore(
						Component.text(" "),
						Component.text("Click to reset compass settings.", NamedTextColor.GRAY),
						Component.text(" "),
						Component.text("This action cannot be undone!", NamedTextColor.DARK_RED)
								.decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED)
				)
				.adaptGUI()
				.build());
		inv.setItem(27, ItemBuilder.builder(Material.OAK_SIGN)
				.name(Component.text("Set Note", NamedTextColor.GRAY)
						.decorate(TextDecoration.BOLD))
				.lore(
						Component.text(" "),
						Component.text("Click to set compass note.", NamedTextColor.GRAY)
				)
				.adaptGUI()
				.build());
		if (meteoriteLocation != null) {
			inv.setItem(31, ItemBuilder.builder(CustomItem.METEORITE_FRAGMENT.getItemStack())
					.name(Component.text("Meteorite Location", NamedTextColor.GOLD))
					.lore(
							Component.text(" "),
							Component.text("Set the target of held compass", NamedTextColor.GRAY),
							Component.text("to the location where the meteorite", NamedTextColor.GRAY),
							Component.text("will land.", NamedTextColor.GRAY),
							Component.text(" "),
							Component.text("(This option only available for a", NamedTextColor.GREEN),
							Component.text("limited time)", NamedTextColor.GREEN)
					)
					.adaptGUI()
					.build());
		}

		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null) inv.setItem(i, empty);
		}

		p.openInventory(inv);

	}

	public Component getGUIName() {
		return name;
	}











	public SelectTargetResult setCompassTarget(@Nonnull Player p, @Nullable Location location) {
		if (p.getInventory().getItemInMainHand().getType() != Material.COMPASS) return SelectTargetResult.MISSING_COMPASS;
		if (location == null) return SelectTargetResult.MISSING_BED;
		if (!location.getWorld().getName().equals("world")) return SelectTargetResult.DENIED_WORLD;

		ItemStack compass = CustomItem.TRACKING_COMPASS.getItemStack(p.getInventory().getItemInMainHand().getAmount());
		CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
		compassMeta.setLodestoneTracked(false);
		compassMeta.setLodestone(location);
		compass.setItemMeta(compassMeta);
		p.getInventory().setItemInMainHand(compass);
		p.playSound(p.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 2, 1);

		return SelectTargetResult.SUCCESS;
	}

	public void sendSelectTargetResultMessage(@Nonnull Player p, @Nonnull SelectTargetResult result) {
		Component message;
		switch (result) {
			case MISSING_COMPASS:
				message = Component.text("Held compass is missing.", NamedColor.HARLEY_DAVIDSON_ORANGE);
				break;
			case MISSING_BED:
				message = Component.text("Bed is missing. Target location couldn't set.", NamedColor.CARMINE_PINK);
				break;
			case DENIED_WORLD:
				Style red = Style.style(NamedColor.CARMINE_PINK);
				Style yellow = Style.style(NamedColor.TURBO);
				message = Component.text("Target location must be in ", red)
						.append(Component.text("Overworld", yellow))
						.append(Component.text(". Target location couldn't set.", red));
				break;
			case SUCCESS:
				message = Component.text("Target location of held compass successfully set.", NamedColor.SKIRRET_GREEN);
				break;
			default:
				message = Component.text("Something went terribly terribly wrong. Please report this to the staffs.", NamedColor.HARLEY_DAVIDSON_ORANGE);
				break;
		}
		MysteriaUtils.sendMessage(p, message);
	}


	public ResetCompassResult clearCompassTarget(@Nonnull Player p) {
		if (p.getInventory().getItemInMainHand().getType() != Material.COMPASS) return ResetCompassResult.MISSING_COMPASS;

		p.getInventory().setItemInMainHand(new ItemStack(Material.COMPASS, p.getInventory().getItemInMainHand().getAmount()));

		return ResetCompassResult.SUCCESS;
	}

	public void sendClearResultMessage(@Nonnull Player p, @Nonnull ResetCompassResult result) {
		Component message;
		switch (result) {
			case MISSING_COMPASS:
				message = Component.text("Held compass is missing.", NamedColor.HARLEY_DAVIDSON_ORANGE);
				break;
			case SUCCESS:
				message = Component.text("Held compass has been reset.", NamedColor.SKIRRET_GREEN);
				break;
			default:
				message = Component.text("Something went terribly terribly wrong. Please report this to the staffs.", NamedColor.HARLEY_DAVIDSON_ORANGE);
				break;
		}
		MysteriaUtils.sendMessage(p, message);
	}


	public void noteWriteMode(@Nonnull Player p) {
		UUID uuid = p.getUniqueId();
		if (NoteWriteModes.contains(uuid)) return;

		NoteWriteModes.add(uuid);

		for (int i = 0; i < 4; i++) {
			p.sendMessage(" ");
		}
		p.sendMessage(MysteriaUtils.centeredComponent(
				Component.text("Type your note for the compass.", NamedColor.BEEKEEPER)
		));
		p.sendMessage(MysteriaUtils.centeredComponent(
				Component.text()
						.append(Component.text("Type ", NamedColor.BEEKEEPER))
						.append(Component.text("clear", NamedColor.CARMINE_PINK))
						.append(Component.text(" to clear compass note.", NamedColor.BEEKEEPER))
						.build()
		));
		p.sendMessage(MysteriaUtils.centeredComponent(
				Component.text()
						.append(Component.text("Type ", NamedColor.BEEKEEPER))
						.append(Component.text("exit", NamedColor.CARMINE_PINK))
						.append(Component.text(" to cancel.", NamedColor.BEEKEEPER))
						.build()
		));
		for (int i = 0; i < 3; i++) {
			p.sendMessage(" ");
		}
	}

	public SetNoteResult setCompassNote(@Nonnull Player p, @Nullable String note) {
		ItemStack item = p.getInventory().getItemInMainHand();

		if (item.getType() != Material.COMPASS) return SetNoteResult.MISSING_COMPASS;

		CompassMeta meta = (CompassMeta) item.getItemMeta();

		//Component displayName = meta.displayName();
		if (!CustomItem.checkCustomItem(item, CustomItem.TRACKING_COMPASS)) return SetNoteResult.NOT_TRACKING_COMPASS;

		if (note == null) {
			meta.lore(null);
		} else {
			if (note.length() > 30) return SetNoteResult.TEXT_LENGTH_ERROR;

			meta.lore(new ArrayList<>(Collections.singletonList(Component.text(note, NamedTextColor.GRAY))));
		}
		item.setItemMeta(meta);
		return SetNoteResult.SUCCESS;
	}

	public void sendSetNoteResultMessage(@Nonnull Player p, @Nonnull SetNoteResult result) {
		Component message;
		switch (result) {
			case MISSING_COMPASS:
				message = Component.text("Held compass is missing.", NamedColor.HARLEY_DAVIDSON_ORANGE);
				break;
			case NOT_TRACKING_COMPASS:
				message = Component.text("Before you set note, you have to set compass target.", NamedColor.CARMINE_PINK);
				break;
			case TEXT_LENGTH_ERROR:
				Style red = Style.style(NamedColor.CARMINE_PINK);
				Style yellow = Style.style(NamedColor.TURBO);
				message = Component.text("Note length cannot exceed ", red)
						.append(Component.text("30 ", yellow))
						.append(Component.text("characters.", red));
				break;
			case SUCCESS:
				message = Component.text("Compass note successfully set.", NamedColor.SKIRRET_GREEN);
				break;
			default:
				message = Component.text("Something went terribly terribly wrong. Please report this to the staffs.", NamedColor.HARLEY_DAVIDSON_ORANGE);
				break;
		}
		MysteriaUtils.sendMessage(p, message);
	}




	public void requestWriteMode(@Nonnull Player p) {
		UUID uuid = p.getUniqueId();
		if (RequestWriteModes.contains(uuid)) return;

		RequestWriteModes.add(uuid);

		for (int i = 0; i < 4; i++) {
			p.sendMessage(" ");
		}
		p.sendMessage(MysteriaUtils.centeredComponent(
				Component.text("Type the name of the player.", NamedColor.BEEKEEPER)
		));
		p.sendMessage(MysteriaUtils.centeredComponent(
				Component.text()
						.append(Component.text("Type ", NamedColor.BEEKEEPER))
						.append(Component.text("exit", NamedColor.CARMINE_PINK))
						.append(Component.text(" to cancel.", NamedColor.BEEKEEPER))
						.build()
		));
		for (int i = 0; i < 4; i++) {
			p.sendMessage(" ");
		}
	}

	public void sendRequest(@Nonnull Player requester, @Nonnull Player receiver) {
		requests.put(requester.getUniqueId(), receiver.getUniqueId());
		MysteriaUtils.sendMessage(requester,
				Component.text("Request sent. Please keep holding the compass in your main hand.", NamedColor.TURBO));

		MysteriaUtils.sendMessage(requester.identity(), receiver,
				Component.text()
						.append(Component.text(requester.getName(), NamedColor.QUINCE_JELLY))
						.append(Component.text(" wants to set their compass target to your current location.", NamedColor.TURBO))
						.build());
		MysteriaUtils.sendMessage(requester.identity(), receiver,
				Component.text()
						.append(Component.text("Type ", NamedColor.TURBO))
						.append(Component.text("/crequest accept " + requester.getName(), NamedColor.SKIRRET_GREEN))
						.append(Component.text(" to accept.", NamedColor.TURBO))
						.build());
		MysteriaUtils.sendMessage(requester.identity(), receiver,
				Component.text()
						.append(Component.text("Type ", NamedColor.TURBO))
						.append(Component.text("/crequest deny " + requester.getName(), NamedColor.CARMINE_PINK))
						.append(Component.text(" to deny.", NamedColor.TURBO))
						.build());
	}

	public void acceptRequest(@Nonnull Player requester, @Nonnull Player receiver) {
		requests.remove(requester.getUniqueId());
		MysteriaUtils.sendMessage(receiver, Component.text("Request accepted.", NamedColor.TURBO));
		MysteriaUtils.sendMessage(requester,
				Component.text()
						.append(Component.text(receiver.getName(), NamedColor.QUINCE_JELLY))
						.append(Component.text(" accepted your target request.", NamedColor.SKIRRET_GREEN))
						.build());
	}

	public void denyRequest(@Nonnull Player requester, @Nonnull Player receiver) {
		requests.remove(requester.getUniqueId());
		MysteriaUtils.sendMessage(receiver, Component.text("Request denied.", NamedColor.TURBO));
		MysteriaUtils.sendMessage(requester,
				Component.text()
						.append(Component.text(receiver.getName(), NamedColor.QUINCE_JELLY))
						.append(Component.text(" denied your target request.", NamedColor.HARLEY_DAVIDSON_ORANGE))
						.build());
	}




	public void setMeteoriteLocation(@Nullable Location location) {
		meteoriteLocation = location;
	}

	@Nullable
	public Location getMeteoriteLocation() {
		return meteoriteLocation;
	}


}
