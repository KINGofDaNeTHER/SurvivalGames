package me.kingofdanether.survivalgames.item;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class ItemManager {

	public static ArrayList<ClickableItem> items = new ArrayList<ClickableItem>();
	
	public static ArrayList<ClickableItem> getAllItems() {return items;}
	
	public static void addItem(ClickableItem item) {items.add(item);}
	public static void removeItem(ClickableItem item) {items.remove(item);}
	
	public static ClickableItem getItem(UUID id) {
		for (ClickableItem item : items) {
			if (item.getUUID().equals(id)) {
				return item;
			}
		}
		return null;
	}
	
	public static ClickableItem getItem(ItemStack item) {
		for (ClickableItem clickItem : items) {
			if (clickItem.getItem().equals(item)) {
				return clickItem;
			}
		}
		return null;
	}
}
