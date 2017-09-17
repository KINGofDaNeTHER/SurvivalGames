package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class GlobalStatsGui extends GUI {
	
	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	
	private SGPlayer target;
	private SGPlayer viewer;
	
	public GlobalStatsGui(SGPlayer viewer, SGPlayer target) {
		super(36, viewer.getName().equals(target.getName()) ? "&6Your global stats" : "&6" + target.getName() + "'s global stats");
		this.target = target;
		this.viewer = viewer;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		this.playClickSound(clicker);
		if (slot == 35) {
			new PlayerStatsGui(viewer, target).open(clicker);
		}
	}

	@Override
	public void setItems() {
		int numDeaths = target.getData().getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() +  ".deaths") + target.getData().getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() +  ".deaths");
		int numKills = target.getData().getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() +  ".kills") + target.getData().getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() +  ".kills");
		int numWins = target.getData().getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() +  ".wins") + target.getData().getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() +  ".wins");
		double numClout = target.getData().getDouble("stats." + GameStyle.CLASSIC.toString().toLowerCase() +  ".clout") + target.getData().getDouble("stats." + GameStyle.ZDUBY.toString().toLowerCase() +  ".clout");
		int totalGamesPlayed = target.getData().getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".games-played") + target.getData().getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".games-played");
		
		ItemStack deaths = ItemUtils.createItemStack(Material.SKULL_ITEM, StringUtils.colorize("&6Deaths: " + numDeaths));
		this.getInventory().setItem(2, deaths);
		String killDeathRatio =  numDeaths <= 0 ? String.valueOf((double)numKills + " (PERFECT)") : String.valueOf(NumberUtils.nearestHundreth((double)numKills/(double)numDeaths));
		ItemStack wins = ItemUtils.createItemStack(Material.EMERALD, StringUtils.colorize("&6Wins: " + numWins), Arrays.asList(StringUtils.colorize("&7K/D: " + killDeathRatio)));
		this.getInventory().setItem(4, wins);
		ItemStack kills = ItemUtils.createItemStack(Material.DIAMOND_SWORD, StringUtils.colorize("&6Kills: " + numKills));
		this.getInventory().setItem(6, kills);
		ItemStack totalGames = ItemUtils.createItemStack(Material.CHEST, StringUtils.colorize("&6Total Games Played: " + totalGamesPlayed));
		this.getInventory().setItem(12, totalGames);
		ItemStack clout = ItemUtils.createItemStack(Material.NETHER_STAR, StringUtils.colorize("&6Clout: " + numClout));
		this.getInventory().setItem(14, clout);
		this.getInventory().setItem(31, ItemUtils.createSkull("&6Global Stats", "Planet"));
		this.getInventory().setItem(35, ItemUtils.createItemStack(Material.STAINED_CLAY, (short)14, StringUtils.colorize("&cGo back")));
		for (int i = 0; i < this.getSize(); i++) {
			if (i != 2 && i != 4 && i != 6 && i != 12 && i != 14 && i != 31 && i != 35) {
				this.getInventory().setItem(i, glass);
			}
		}
	}
	
	public SGPlayer getTarget() {return target;}
	public SGPlayer getViewer() {return viewer;}
	
}
