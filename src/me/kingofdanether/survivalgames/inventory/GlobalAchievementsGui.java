package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kingofdanether.survivalgames.enumeration.SGAchievement;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class GlobalAchievementsGui extends GUI {


	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	
	private SGPlayer target;
	private SGPlayer viewer;
	
	public GlobalAchievementsGui(SGPlayer viewer, SGPlayer target) {
		super(36, viewer.getName().equals(target.getName()) ? "&6Your global achivements" : "&6" + target.getName() + "'s global achievements");
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
		for (SGAchievement achievement : SGAchievement.values()) {
			if (!target.hasAchievement(achievement)) {
				//this.getInventory().addItem(ItemUtils.createItemStack(Material.BOOK, StringUtils.colorize("&d&k") +  ChatColor.stripColor(achievement.getToolTip())));
				continue;
			}
			ItemStack item = achievement.getItem();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(achievement.getMessage());
			meta.setLore(Arrays.asList(achievement.getToolTip()));
			item.setItemMeta(meta);
			this.getInventory().addItem(item);
		}
		this.getInventory().setItem(35, ItemUtils.createItemStack(Material.STAINED_CLAY, (short)14, StringUtils.colorize("&cGo back")));
		for (int i = 0; i < this.getSize(); i++) {
			if (i <= 26 && i != 35 && this.getInventory().getItem(i) == null) {
				this.getInventory().setItem(i, glass);
			}
		}
	}
	
	public SGPlayer getTarget() {return target;}
	public SGPlayer getViewer() {return viewer;}

}
