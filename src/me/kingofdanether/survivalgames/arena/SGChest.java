package me.kingofdanether.survivalgames.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SGChest {

	private Chest chest;
	private Arena a;
	private int tier;
	private Location location;
	private boolean fill;
	
	public SGChest(Chest chest, Arena a) {
		this.chest = chest;
		this.a = a;
		this.tier = 1;
		this.location = chest.getLocation();
		this.fill = false;
	}

	public Chest getChest() {return chest;}
	public Arena getArena() {return a;}
	public int getTier() {return tier;}
	public Location getLocation() {return location;}
	public boolean canBeFilled() {return fill;}
	
	public void setCanBeFilled(boolean fill) {
		this.fill = fill;
	}
	
	public void setTier(int tier) {
		this.tier = tier;
	}
	
	@Deprecated
	public void fill() {
		if (!a.getGameState().toString().startsWith("INGAME")) return;
		boolean clearInv = a.getGameState() == GameState.INGAME_WAITING || a.getGameState() == GameState.INGAME_COUNTDOWN;
		if (!this.canBeFilled()) return;
		this.fill(clearInv);
	}
	
	@SuppressWarnings("unused")
	public void fill(boolean clearInv) {
		if (!a.getGameState().toString().startsWith("INGAME")) return;
		Chest mcChest = chest.getLocation().getBlock().getState() instanceof Chest ? (Chest)chest.getLocation().getBlock().getState() : null;
		ArrayList<ItemStack> fillItems = this.getFillItems();
		if (fillItems.size() <= 0) fillItems = this.getFillItems();
		if (clearInv) {
			mcChest.getBlockInventory().clear();
			mcChest.update();
		}
		int count = 0;
		int tries = 0;
		int maxTries = 10;
		int maxCount = (fillItems.size() - 1);
		for (int i = 0; i <= fillItems.size() * 3; i++) {
			if (mcChest == null) break;
			if (!(mcChest.getBlock().getState() instanceof Chest)) break;
			if (mcChest.getInventory().firstEmpty() == -1) break;
			if (count > maxCount) break;
			//if (tries >= maxTries) break;
			//if (count >= maxTries) break;
			int pos = NumberUtils.randInt(0, (mcChest.getBlockInventory().getSize() - 1));
			if (mcChest.getBlockInventory().getItem(pos) == null) {
				mcChest.getBlockInventory().setItem(pos, fillItems.get(count));
				count++;
			}
			tries++;
		}
		if (fillItems.size() > 0) this.setCanBeFilled(false);
	}
	
	public ArrayList<ItemStack> getFillItems() {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		//for (String tier : a.getData().getConfigurationSection("loot.tier").getKeys(false)) {}
		if (a.getData().get("loot.tier." + tier + ".items") == null) {
			this.tier = 1;
			return items;
		}
		for (String item : a.getData().getConfigurationSection("loot.tier." + tier + ".items").getKeys(false)) {
			if (a.getData().get("loot.tier." + tier + ".items." + item + ".item") == null) {
				SurvivalGames.getInstance().getLogger().warning("Invalid loot item at arena \"" + a.getName() + "\" for item " + item + "!");
				continue;
			}
			ItemStack itemStack = ItemUtils.createItemStackFromString(a.getData().getString("loot.tier." + tier + ".items." + item + ".item"), false);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (a.getData().get("loot.tier." + tier + ".items." + item + ".name") != null) {
				itemMeta.setDisplayName(StringUtils.colorize(a.getData().getString("loot.tier." + tier + ".items." + item + ".name")));
			}
			if (a.getData().get("loot.tier." + tier + ".items." + item + ".lore") != null) {
				List<String> temp = new ArrayList<String>();
				for (String lore : a.getData().getStringList("loot.tier." + tier + ".items." + item + ".lore")) {
					temp.add(StringUtils.colorize(lore));
				}
				itemMeta.setLore(temp);
			}
			if (a.getData().get("loot.tier." + tier + ".items." + item + ".enchantments") != null) {
				for (String enchant : a.getData().getStringList("loot.tier." + tier + ".items." + item + ".enchantments")) {
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
			if (a.getData().get("loot.tier." + tier + ".items." + item + ".amount") != null) {
				itemStack.setAmount(Integer.valueOf(a.getData().getString("loot.tier." + tier + ".items." + item + ".amount")));
			}
			if (a.getData().get("loot.tier." + tier + ".items." + item + ".durability") != null) {
				itemStack.setDurability(Short.valueOf(a.getData().getString("loot.tier." + tier + ".items." + item + ".durability")));
			}
			itemStack.setItemMeta(itemMeta);
			int chance = a.getData().getInt("loot.tier." + tier + ".items." + item + ".spawn-chance");
			if (NumberUtils.randInt(0, 100) <= chance) {
				items.add(itemStack);
			}
		}
		return items;
	}
	
	
}
