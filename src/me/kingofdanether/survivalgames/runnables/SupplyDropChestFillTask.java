package me.kingofdanether.survivalgames.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SupplyDropChestFillTask extends BukkitRunnable {
	
	@SuppressWarnings("unused")
	private SGChest chest;
	private ArrayList<ItemStack> items;
	private int invSize;
	private int count;
	private int maxCount;
	private Chest mcChest;
	private Arena a;
	
	public SupplyDropChestFillTask(SGChest chest) {
		this.chest = chest;
		this.a = chest.getArena();
		this.items = this.getFillItems();
		this.mcChest = chest.getLocation().getBlock().getState() instanceof Chest ? (Chest)chest.getLocation().getBlock().getState() : null;
		this.invSize = mcChest == null ? 0 : chest.getChest().getInventory().getSize();
		this.count = 0;
		this.maxCount = (items.size() - 1);
	}
	
	@Override
	public void run() {
		if (mcChest == null || !(mcChest.getBlock().getState() instanceof Chest)) {
			this.cancel();
			return;
		}
		if (count > maxCount) {
			this.cancel();
			return;
		}
		int pos = NumberUtils.randInt(0, (invSize-1));
		if (mcChest.getBlockInventory().getItem(pos) == null) {
			mcChest.getBlockInventory().setItem(pos, items.get(count));
			mcChest.update();
			count++;
		}
	}
	
	private ArrayList<ItemStack> getFillItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		//for (String tier : a.getData().getConfigurationSection("loot.tier").getKeys(false)) {}
		if (a.getData().get("supply-drop") == null) {
			return items;
		}
		for (String item : a.getData().getConfigurationSection("supply-drop.loot.items").getKeys(false)) {
			ItemStack itemStack = ItemUtils.createItemStackFromString(a.getData().getString("supply-drop.loot.items." + item + ".item"), false);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (a.getData().get("supply-drop.loot.items." + item + ".name") != null) {
				itemMeta.setDisplayName(StringUtils.colorize(a.getData().getString("supply-drop.loot.items." + item + ".name")));
			}
			if (a.getData().get("supply-drop.loot.items." + item + ".lore") != null) {
				List<String> temp = new ArrayList<String>();
				for (String lore : a.getData().getStringList("supply-drop.loot.items." + item + ".lore")) {
					temp.add(StringUtils.colorize(lore));
				}
				itemMeta.setLore(temp);
			}
			if (a.getData().get("supply-drop.loot.items." + item + ".enchantments") != null) {
				for (String enchant : a.getData().getStringList("supply-drop.loot.items." + item + ".enchantments")) {
					String[] finalEnchant = enchant.split(":");
					if (finalEnchant.length >= 2) {
						Enchantment ench;
						try {
							ench = Enchantment.getByName(finalEnchant[0].toUpperCase());
							itemMeta.addEnchant(ench, Integer.valueOf(finalEnchant[1]), true);
						} catch (Exception ex) {
							SurvivalGames.getInstance().getLogger().warning("Invalid enchant \"" + finalEnchant[0] + "\" for item " + item + " in arena " + a.getName() + "!");
							continue;
						}
					}
				}
			}
			if (a.getData().get("supply-drop.loot.items." + item + ".item-flags") != null) {
				for (String flag : a.getData().getStringList("supply-drop.loot.items." + item + ".item-flags")) {
					ItemFlag itemFlag;
					try {
						itemFlag = ItemFlag.valueOf(flag.toUpperCase());
					} catch (Exception ex) {
						SurvivalGames.getInstance().getLogger().warning("Invalid item flag \"" + flag + "\" for item " + item + " in arena " + a.getName() + "!");
						continue;
					}
					itemMeta.addItemFlags(itemFlag);
				
				}
			}
			if (a.getData().get("supply-drop.loot.items." + item + ".amount") != null) {
				itemStack.setAmount(Integer.valueOf(a.getData().getString("supply-drop.loot.items." + item + ".amount")));
			}
			if (a.getData().get("supply-drop.loot.items." + item + ".durability") != null) {
				itemStack.setDurability(Short.valueOf(a.getData().getString("supply-drop.loot.items." + item + ".durability")));
			}
			itemStack.setItemMeta(itemMeta);
			int chance = a.getData().getInt("supply-drop.loot.items." + item + ".spawn-chance");
			if (NumberUtils.randInt(0, 100) <= chance) {
				items.add(itemStack);
			}
		}
		return items;
	}
	
}
