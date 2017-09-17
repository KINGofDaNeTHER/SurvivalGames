package me.kingofdanether.survivalgames.inventory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.enumeration.RandomType;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class OptionsGui extends GUI {

	private Arena a;
	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	private ItemStack goBack = ItemUtils.createItemStack(Material.STAINED_CLAY, (short)14, StringUtils.colorize("&6Go Back"));
	
	public OptionsGui(Arena a) {
		super(36, "&6Options for: " + a.getName());
		this.a = a;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (slot == 4) {
			this.playClickSound(clicker);
			clicker.closeInventory();
			if (a.getLobby() == null) {
				clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order to enable this arena, you need to set the lobby first!"));
				return;
			}
			if (a.getCenter() == null) {
				clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order to enable this arena, you need to set the center first!"));
				return;
			}
			if (a.getMinPlayers() <= 0) {
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order to enable this arena, you need to change the min players first"));
				return;
			}
			if (a.getMaxPlayers() <= 0) {
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order to enable this arena, you need to change the max players first"));
				return;
			}
			if ((a.getTpType() == RandomType.LIST_RANDOM)) {
				if (a.getSpawnPoints().size() < a.getMaxPlayers()) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order enable this arena, there must be at least " + a.getMaxPlayers() + " spawn points set!"));
					return;
				}
				if (a.isEnabled()) {
					a.setEnabled(false);
					clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aThis arena is now disabled!"));
					return;
				}
				a.setEnabled(true);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aThis arena is now enabled!"));
				return;
			} else {
				if (a.isEnabled()) {
					a.setEnabled(false);
					clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aThis arena is now disabled!"));
					return;
				}
				a.setEnabled(true);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aThis arena is now enabled!"));
			}
			return;
		} else if (slot == 12) {
			this.playClickSound(clicker);
			clicker.closeInventory();
			if (a.getTpType() == RandomType.WORLD_RANDOM) {
				if (a.getSpawnPoints().size() < a.getMaxPlayers()) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order to use list random, there must be at least " + a.getMaxPlayers() + " spawn points set!"));
					return;
				} 
				a.setTeleportType(RandomType.LIST_RANDOM);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aTeleport type set to list random!"));
				a.updateStatus();
			} else {
				if (a.getGameStyle() == GameStyle.CLASSIC) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThe classic gamestyle only supports specific spawn points set, not randomly generated spawns!"));
					return;
				}
				a.setTeleportType(RandomType.WORLD_RANDOM);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aTeleport type set to world random!"));
				a.updateStatus();
			}
			return;
		} else if (slot == 13) {
			this.playClickSound(clicker);
			clicker.closeInventory();
			if (a.isCrossWorld()) {
				a.setCrossWorld(false);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aDisabled cross world!"));
			} else {
				a.setCrossWorld(true);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aEnabled cross world!"));
			}
		} else if (slot == 14) {
			this.playClickSound(clicker);
			clicker.closeInventory();
			if (a.getSupplyDropType() == RandomType.WORLD_RANDOM) {
				if (a.getSupplyDropLocs().size() <= 0) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cSorry, but in order to use list random, there must be at least one supply drop points set!"));
					return;
				} 
				a.setSupplyDropType(RandomType.LIST_RANDOM);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aSupply drop type set to list random!"));
			} else {
				a.setSupplyDropType(RandomType.WORLD_RANDOM);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aSupply drop type set to world random!"));
			}
			return;
		} else if (slot == 22) {
			this.playClickSound(clicker);
			clicker.closeInventory();
			if (a.getGameStyle() == GameStyle.CLASSIC) {
				a.setGameStyle(GameStyle.ZDUBY);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aGame style set to \"Zduby\"!"));
				a.updateStatus();
			} else {
				a.setGameStyle(GameStyle.CLASSIC);
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aGame style set to classic!"));
				a.updateStatus();
			}
			return;
		} else if (slot == 35) {
			this.playClickSound(clicker);
			new ArenaEditGui(a).open(clicker);
			return;
		}
	}

	@Override
	public void setItems() {
		ItemStack enabled = a.isEnabled() ? ItemUtils.createItemStack(Material.STAINED_CLAY, (short)5, StringUtils.colorize("&6Status: &aEnabled &6(" + a.getGameState().toString().toUpperCase() + ")")) : ItemUtils.createItemStack(Material.STAINED_CLAY, (short)14, StringUtils.colorize("&6Status: &cDisabled"));
		ItemStack teleportType = ItemUtils.createItemStack(Material.ENDER_PEARL, StringUtils.colorize("&6Teleport Type: " + a.getTpType().toString()));
		ItemStack supplyDropType = ItemUtils.createItemStack(Material.BEACON, StringUtils.colorize("&6Supply Drop Type: " + a.getSupplyDropType().toString()));
		ItemStack gameStyle = a.getGameStyle() == GameStyle.CLASSIC ? ItemUtils.createItemStack(Material.BOW, StringUtils.colorize("&6GameStyle: CLASSIC")) : Constants.ZDUBY_HEAD;
		ItemStack crossWorld = a.isCrossWorld() ? Constants.CROSS_WORLD_ENABLED : Constants.CROSS_WORLD_DISABLED;
		this.getInventory().setItem(4, enabled);
		this.getInventory().setItem(12, teleportType);
		this.getInventory().setItem(13, crossWorld);
		this.getInventory().setItem(14, supplyDropType);
		this.getInventory().setItem(22, gameStyle);
		this.getInventory().setItem(35, goBack);
		for (int i = 0; i < this.getSize(); i++) {
			if (i != 4 && i != 12 && i != 13 && i != 14 && i != 22 && i != 35) {
				this.getInventory().setItem(i, glass);
			}
		}
	}

}
