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

public class DetailedStatsGui extends GUI {
	
	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	
	private SGPlayer target;
	private SGPlayer viewer;
	
	private GameStyle style;
	
	private String styleName;
	
	public DetailedStatsGui(SGPlayer viewer, SGPlayer target, GameStyle style) {
		super(36, viewer.getName().equals(target.getName()) ? "&6Your " + style.toString().toLowerCase() + " stats" : "&6" + target.getName() + "'s " + style.toString().toLowerCase() + " stats");
		this.target = target;
		this.viewer = viewer;
		this.style = style;
		this.styleName = style.toString().toLowerCase();
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
		ItemStack deaths = ItemUtils.createItemStack(Material.SKULL_ITEM, StringUtils.colorize("&6Deaths: " + target.getData().getInt("stats." + styleName+  ".deaths")));
		this.getInventory().setItem(2, deaths);
		String killDeathRatio =  target.getData().getInt("stats." + styleName + ".deaths") <= 0 ? String.valueOf(target.getData().getDouble("stats." + styleName + ".kills") + " (PERFECT)") : String.valueOf(NumberUtils.nearestHundreth(target.getData().getDouble("stats." + styleName + ".kills")/target.getData().getDouble("stats." + styleName + ".deaths")));
		ItemStack wins = ItemUtils.createItemStack(Material.EMERALD, StringUtils.colorize("&6Wins: " + target.getData().getInt("stats." + styleName+  ".wins")), Arrays.asList(StringUtils.colorize("&7K/D: " + killDeathRatio)));
		this.getInventory().setItem(4, wins);
		ItemStack kills = ItemUtils.createItemStack(Material.DIAMOND_SWORD, StringUtils.colorize("&6Kills: " + target.getData().getInt("stats." + styleName+  ".kills")));
		this.getInventory().setItem(6, kills);
		ItemStack totalGames = ItemUtils.createItemStack(Material.CHEST, StringUtils.colorize("&6Total Games Played: " + target.getData().getInt("stats." + styleName+  ".games-played")));
		this.getInventory().setItem(12, totalGames);
		ItemStack clout = ItemUtils.createItemStack(Material.NETHER_STAR, StringUtils.colorize("&6Clout: " + target.getData().getDouble("stats." + styleName + ".clout")));
		this.getInventory().setItem(14, clout);
		ItemStack gameStyle = style == GameStyle.CLASSIC ? ItemUtils.createItemStack(Material.BOW, StringUtils.colorize("&6Game Style: Classic")) : ItemUtils.createSkull("&6Game Style: Zduby", "Zduby");
		this.getInventory().setItem(31, gameStyle);
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
