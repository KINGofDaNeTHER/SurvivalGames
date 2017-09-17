package me.kingofdanether.survivalgames.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class PlayerStatsGui extends GUI {

	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	
	private SGPlayer target;
	private SGPlayer viewer;
	
	public PlayerStatsGui(SGPlayer viewer, SGPlayer target) {
		super(36, viewer.getName().equals(target.getName()) ? "&6Your stats" : "&6" + target.getName() + "'s stats");
		this.target = target;
		this.viewer = viewer;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (slot == 11) {
			this.playClickSound(clicker);
			new DetailedStatsGui(viewer, target, GameStyle.CLASSIC).open(clicker);
		} else if (slot == 13) {
			this.playClickSound(clicker);
			new GlobalStatsGui(viewer, target).open(clicker);
		} else if (slot == 15) {
			this.playClickSound(clicker);
			new DetailedStatsGui(viewer, target, GameStyle.ZDUBY).open(clicker);
		} else if (slot == 20) {
			this.playClickSound(clicker);
			new AchievementsGui(viewer, target, GameStyle.CLASSIC).open(clicker);
		} else if (slot == 22) {
			this.playClickSound(clicker);
			new GlobalAchievementsGui(viewer, target).open(clicker);
		} else if (slot == 24) {
			this.playClickSound(clicker);
			new AchievementsGui(viewer, target, GameStyle.ZDUBY).open(clicker);
		}
	}

	@Override
	public void setItems() {
		this.getInventory().setItem(11, ItemUtils.createItemStack(Material.BOW, StringUtils.colorize("&6Classic")));
		this.getInventory().setItem(13, ItemUtils.createSkull("&6Global", "Planet"));
		this.getInventory().setItem(15, ItemUtils.createSkull("&6Zduby", "Zduby"));
		this.getInventory().setItem(20, ItemUtils.createItemStack(Material.ENCHANTED_BOOK, StringUtils.colorize("&6Classic Achievements")));
		this.getInventory().setItem(22, ItemUtils.createItemStack(Material.ENCHANTED_BOOK, StringUtils.colorize("&6Global Achievements")));
		this.getInventory().setItem(24, ItemUtils.createItemStack(Material.ENCHANTED_BOOK, StringUtils.colorize("&6Zduby Achievements")));
		for (int i = 0; i < this.getSize(); i++) {
			if (i != 11 && i != 13 && i != 15 && i != 20 && i != 22 && i != 24) {
				this.getInventory().setItem(i, glass);
			}
		}
	}
	
	public SGPlayer getTarget() {return target;}
	public SGPlayer getViewer() {return viewer;}

}
