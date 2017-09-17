package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class ListChestsGui extends GUI {

	private Arena a;
	private ItemStack goBack = ItemUtils.createItemStack(Material.STAINED_CLAY, (short)14, StringUtils.colorize("&6Go Back"));
	
	public ListChestsGui(Arena a) {
		super((Math.round(((a.getSpawnPoints().size() + 1) / 10) + 1) * 9) + 9, "&6" + a.getName() + ": chests");
		this.a = a;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (clicked == null || clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) return;
		if (slot == 0) {
			this.playClickSound(clicker);
			new ArenaEditGui(a).open(clicker);
			return;
		}
		String location = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
		if (clickType == ClickType.LEFT) {
			clicker.closeInventory();
			Location teleport = LocationUtils.parseLocation(location, ",");
			if (teleport == null) {
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis location may be corrupted or doesn't exist anymore, please delete it!"));
				return;
			}
			clicker.teleport(teleport);
		}
	}

	@Override
	public void setItems() {
		this.getInventory().setItem(0, goBack);
		for (int i = 0; i <= a.getChests().size(); i++) {
			if (i > 0) {
				Location l = a.getChests().get(i-1).getChest().getLocation();
				this.getInventory().setItem(i, ItemUtils.createItemStack(Material.CHEST, LocationUtils.locToString(l), Arrays.asList(StringUtils.colorize("&7Left click to teleport"))));
			}
		}
	}
	
}
