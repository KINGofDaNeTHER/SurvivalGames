package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.runnables.LocateChestsTask;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class ArenaEditGui extends GUI {

	private Arena a;
	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	
	public ArenaEditGui(Arena a) {
		super(36, "&6Editing: " + a.getName());
		this.a = a;
	}
	
	public Arena getArena() {return a;}

	@Override
	public void clickItem(final Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (slot == 3) {
			//edit corner1
			if (clickType == ClickType.LEFT) {
				clicker.closeInventory();
				if (a.getCorner1() == null) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cCorner 1 is not set!"));
					return;
				} 
				clicker.teleport(a.getCorner1());
			} else if (clickType == ClickType.RIGHT) {
				a.setCorner1(clicker.getLocation());
				clicker.closeInventory();
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aSet corner 1 to your current location!"));
			}
		} else if (slot == 5) {
			//edit corner2
			if (clickType == ClickType.LEFT) {
				clicker.closeInventory();
				if (a.getCorner2() == null) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cCorner 2 is not set!"));
					return;
				} 
				clicker.teleport(a.getCorner2());
			} else if (clickType == ClickType.RIGHT) {
				a.setCorner2(clicker.getLocation());
				clicker.closeInventory();
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aSet corner 2 to your current location!"));
			}
		} else if (slot == 4) {
			//edit lobby
			if (clickType == ClickType.LEFT) {
				clicker.closeInventory();
				if (a.getLobby() == null) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cLobby is not set!"));
					return;
				} 
				clicker.teleport(a.getLobby());
			} else if (clickType == ClickType.RIGHT) {
				clicker.closeInventory();
				if (a.getCorner1() == null || a.getCorner2() == null) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cPlease set corner 1 and corner 2 before doing this!"));
					return;
				}
				a.setLobby(clicker.getLocation());
				clicker.closeInventory();
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aSet the lobby to your current location!"));
			}
		} else if (slot == 11) {
			//edit min players
			this.playClickSound(clicker);
			new EditMinPlayersGui(a).open(clicker);
		} else if (slot == 13) {
			//edit center
			if (clickType == ClickType.LEFT) {
				clicker.closeInventory();
				if (a.getCenter() == null) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cCenter is not set!"));
					return;
				} 
				clicker.teleport(a.getCenter());
			} else if (clickType == ClickType.RIGHT) {
				a.setCenter(clicker.getLocation());
				clicker.closeInventory();
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aSet the center to your current location!"));
			}
		} else if (slot == 15) {
			//edit max players
			this.playClickSound(clicker);
			new EditMaxPlayersGui(a).open(clicker);
		} else if (slot == 20) {
			this.playClickSound(clicker);
			a.updateStatus();
			new OptionsGui(a).open(clicker);
		} else if (slot == 24) {
			//spawn points
			if (clickType == ClickType.LEFT) {
				this.playClickSound(clicker);
				new EditSpawnsGui(a).open(clicker);
			} else if (clickType == ClickType.RIGHT) {
				clicker.closeInventory();
				if (a.hasSpawnpoint(clicker.getLocation())) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cA spawn point already exists at this location!"));
					return;
				}
				a.addSpawnPoint(clicker.getLocation());
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aAdded a spawn point at your current location!"));
			}
		} else if (slot == 30) {
			//supply drops
			if (clickType == ClickType.LEFT) {
				this.playClickSound(clicker);
				new EditSupplyDropsGui(a).open(clicker);
			} else if (clickType == ClickType.RIGHT) {
				clicker.closeInventory();
				if (a.hasSupplyDropAt(clicker.getLocation())) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cA supply drop already exists at this location!"));
					return;
				}
				a.addSupplyDropLoc(clicker.getLocation());
				clicker.playSound(clicker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aAdded a supply drop point at your current location!"));
			}
		} else if (slot == 32) {
			if (clickType == ClickType.LEFT) {
				this.playClickSound(clicker);
				clicker.closeInventory();
				new BukkitRunnable() {
					int deleteCount = 0;
					@Override
					public void run() {
						Iterator<SGChest> chests = a.getChests().iterator();
						while (chests.hasNext()) {
							SGChest chest = chests.next();
							if (!(chest.getChest().getBlock().getState() instanceof Chest)) {
								chests.remove();
								deleteCount++;
							}
						}
						if (deleteCount > 0) {
							clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aDeleted " + deleteCount + " chest(s) that no longer exist!"));
						} else {
							clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aNo old chests found! It's all good!"));
						}
					}
				}.runTask(SurvivalGames.getInstance());
			} else if (clickType == ClickType.RIGHT) {
				clicker.closeInventory();
				if (a.getCorner1() == null || a.getCorner2() == null) {
					clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
					clicker.sendMessage(StringUtils.capitalize(Constants.PREFIX + "&cPlease set corner 1 and corner 2 before doing this!"));
					return;
				}
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &eSearching for chests between corner 1 and corner 2!"));
				new LocateChestsTask(clicker, a, LocationUtils.chestsFromTwoPoints(a.getCorner1(), a.getCorner2())).runTaskTimerAsynchronously(SurvivalGames.getInstance(), 0, 1);
			}
		}
	} 

	@Override
	public void setItems() {
		ItemStack corner1 = a.getCorner1() == null ? ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Corner 1: &7NOT SET"), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")))
		: ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Corner 1: " + LocationUtils.locToString(a.getCorner1())), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")));
		ItemStack corner2 = a.getCorner2() == null ? ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Corner 2: &7NOT SET"), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")))
		: ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Corner 2: " + LocationUtils.locToString(a.getCorner2())), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")));
		this.getInventory().setItem(3, corner1);
		ItemStack lobby = a.getLobby() == null ? ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Lobby: &7NOT SET"), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")))
		: ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Lobby: " + LocationUtils.locToString(a.getCorner2())), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")));
		this.getInventory().setItem(4, lobby);
		this.getInventory().setItem(5, corner2);
		this.getInventory().setItem(11, ItemUtils.createItemStack(Material.SKULL_ITEM, (short)3, StringUtils.colorize("&6Min players: " + a.getMinPlayers()), Arrays.asList(StringUtils.colorize("&7Click to edit!"))));
		ItemStack center = a.getCenter() == null ? ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Center: &7NOT SET"), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")))
		: ItemUtils.createItemStack(Material.EMPTY_MAP, StringUtils.colorize("&6Center: " + LocationUtils.locToString(a.getCenter())), Arrays.asList(StringUtils.colorize("&7Left click to teleport"), StringUtils.colorize("&7Right click to edit")));
		this.getInventory().setItem(13, center);
		this.getInventory().setItem(15, ItemUtils.createItemStack(Material.SKULL_ITEM, (short)3, StringUtils.colorize("&6Max players: " + a.getMaxPlayers()), Arrays.asList(StringUtils.colorize("&7Click to edit!"))));
		ItemStack spawnpoints = a.getSpawnPoints().size() <= 0 ? ItemUtils.createItemStack(Material.DIRT, StringUtils.colorize("&6Spawn points: &7NONE SET"), Arrays.asList(StringUtils.colorize("&7Left click to list spawn-points"), StringUtils.colorize("&7Right click to add spawn-point")))
		: ItemUtils.createItemStack(Material.GRASS, StringUtils.colorize("&6Spawn points: " + a.getSpawnPoints().size()), Arrays.asList(StringUtils.colorize("&7Left click to list spawn-points"), StringUtils.colorize("&7Right click to add spawn-point")));
		this.getInventory().setItem(20, ItemUtils.createItemStack(Material.COMMAND, StringUtils.colorize("&6Options")));
		this.getInventory().setItem(24, spawnpoints);
		this.getInventory().setItem(30, ItemUtils.createItemStack(Material.BEACON, StringUtils.colorize("&6Supply drops: " + a.getSupplyDropLocs().size()), Arrays.asList(StringUtils.colorize("&7Left click to list supply drop points"), StringUtils.colorize("&7Right click to add supply drop point"))));
		this.getInventory().setItem(32, ItemUtils.createItemStack(Material.CHEST, StringUtils.colorize("&6Chests: " + a.getChests().size()), Arrays.asList(StringUtils.colorize("&7Left click to purge chests that no longer exist!"), StringUtils.colorize("&7Right click to find new chests between corner 1 and corner 2!"))));
		for (int i = 0; i < this.getSize(); i++) {
			if (i != 3 && i != 4 && i != 5 && i != 11  && i != 13 && i != 15 && i != 20 && i != 24 && i != 30 && i != 32) {
				this.getInventory().setItem(i, glass);
			}
		}
	}

}
