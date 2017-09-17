package me.kingofdanether.survivalgames.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {
	
	public static boolean isArmorPiece(ItemStack stack) {
		if (stack.getType() == Material.LEATHER_HELMET) return true;
		if (stack.getType() == Material.LEATHER_CHESTPLATE) return true;
		if (stack.getType() == Material.LEATHER_LEGGINGS) return true;
		if (stack.getType() == Material.LEATHER_BOOTS) return true;
		if (stack.getType() == Material.IRON_HELMET) return true;
		if (stack.getType() == Material.IRON_CHESTPLATE) return true;
		if (stack.getType() == Material.IRON_LEGGINGS) return true;
		if (stack.getType() == Material.IRON_BOOTS) return true;
		if (stack.getType() == Material.GOLD_HELMET) return true;
		if (stack.getType() == Material.GOLD_CHESTPLATE) return true;
		if (stack.getType() == Material.GOLD_LEGGINGS) return true;
		if (stack.getType() == Material.GOLD_BOOTS) return true;
		if (stack.getType() == Material.DIAMOND_HELMET) return true;
		if (stack.getType() == Material.DIAMOND_CHESTPLATE) return true;
		if (stack.getType() == Material.DIAMOND_LEGGINGS) return true;
		if (stack.getType() == Material.DIAMOND_BOOTS) return true;
		if (stack.getType() == Material.CHAINMAIL_HELMET) return true;
		if (stack.getType() == Material.CHAINMAIL_CHESTPLATE) return true;
		if (stack.getType() == Material.CHAINMAIL_LEGGINGS) return true;
		if (stack.getType() == Material.CHAINMAIL_BOOTS) return true;
		return false;
	}
	
	public static void changeDisplayName(ItemStack stack, String name) {
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(StringUtils.colorize(name));
		stack.setItemMeta(meta);
	}
	
	public static void changeLore(ItemStack stack, List<String> lore) {
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(lore);
		stack.setItemMeta(meta);
	}
	
	public static ItemStack createItemStack(Material m, int amount) {
		ItemStack s = new ItemStack(m,amount);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStack(Material m, int amount, String displayName) {
		ItemStack s = new ItemStack(m,amount);
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStack(Material m, String displayName) {
		ItemStack s = new ItemStack(m);
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStack(Material m, short data, String displayName) {
		ItemStack s = new ItemStack(m,1,data);
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStack(Material m, String displayName, List<String> lore) {
		ItemStack s = new ItemStack(m);
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStack(Material m, short data, String displayName, List<String> lore) {
		ItemStack s = new ItemStack(m,1,data);
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}

	public static ItemStack createItemStackFromString(String itemStack, boolean itemFlags) {
		itemStack = itemStack.toUpperCase();
		ItemStack s;
		if (itemStack.contains(":")) {
			String[] args = itemStack.split(":");
			s = new ItemStack(Material.valueOf(args[0]),1,(short)Short.valueOf(args[1]));
		} else {
			s = new ItemStack(Material.valueOf(itemStack),1);
		}
		if (itemFlags) return applyItemFlags(s);
		return s;
	}
	
	public static ItemStack createItemStackFromString(String itemStack, String displayName) {
		itemStack = itemStack.toUpperCase();
		ItemStack s;
		if (itemStack.contains(":")) {
			String[] args = itemStack.split(":");
			s = new ItemStack(Material.valueOf(args[0]),1,(short)Short.valueOf(args[1]));
		} else {
			s = new ItemStack(Material.valueOf(itemStack),1);
		}
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}

	public static ItemStack createItemStackFromString(String itemStack, String displayName, List<String> lore) {
		itemStack = itemStack.toUpperCase();
		ItemStack s;
		if (itemStack.contains(":")) {
			String[] args = itemStack.split(":");
			s = new ItemStack(Material.valueOf(args[0]),1,(short)Short.valueOf(args[1]));
		} else {
			s = new ItemStack(Material.valueOf(itemStack),1);
		}
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStackFromString(String itemStack, int amount, String displayName, List<String> lore) {
		itemStack = itemStack.toUpperCase();
		ItemStack s;
		if (itemStack.contains(":")) {
			String[] args = itemStack.split(":");
			s = new ItemStack(Material.valueOf(args[0]),amount,(short)Short.valueOf(args[1]));
		} else {
			s = new ItemStack(Material.valueOf(itemStack),amount);
		}
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static ItemStack createItemStackFromString(String itemStack, int amount, String displayName) {
		itemStack = itemStack.toUpperCase();
		ItemStack s;
		if (itemStack.contains(":")) {
			String[] args = itemStack.split(":");
			s = new ItemStack(Material.valueOf(args[0]),amount,(short)Short.valueOf(args[1]));
		} else {
			s = new ItemStack(Material.valueOf(itemStack),amount);
		}
		ItemMeta meta = s.getItemMeta();
		meta.setDisplayName(displayName);
		s.setItemMeta(meta);
		return applyItemFlags(s);
	}
	
	public static boolean listContainsSimilarItemStack(ArrayList<ItemStack> items, ItemStack item) {
		if (item == null) return false;
		for (ItemStack is : items) {
			if (is.isSimilar(item) || is.getType() == item.getType()) {
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack applyItemFlags(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		for (ItemFlag flag : ItemFlag.values()) {
			meta.addItemFlags(flag);
		}
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack removeItemFlags(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		for (ItemFlag flag : ItemFlag.values()) {
			if (!meta.hasItemFlag(flag)) continue;
			meta.removeItemFlags(flag);
		}
		stack.setItemMeta(meta);
		return stack;
	}
	
	public static ItemStack createSkull(String displayName, String player) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1,(short)3);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwner(player);
        sm.setDisplayName(StringUtils.colorize(displayName));
        skull.setItemMeta(sm);
        return skull;
	}
	
	public static ItemStack createSkull(String displayName, String player, List<String> lore) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1,(short)3);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        sm.setOwner(player);
        sm.setDisplayName(StringUtils.colorize(displayName));
        sm.setLore(lore);
        skull.setItemMeta(sm);
        return skull;
	}
	
}
