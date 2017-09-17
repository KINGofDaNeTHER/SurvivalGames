package me.kingofdanether.survivalgames.item;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class ClickableItem {

	protected ItemStack item;
	protected UUID uuid;	
	protected int inventorySlot;
	
	public ClickableItem(ItemStack item, int inventorySlot) {
		this.item = item;
		this.uuid = UUID.randomUUID();
		this.inventorySlot = inventorySlot;
		ItemManager.addItem(this);
	}
	
	public ItemStack getItem() {return item;}
	public UUID getUUID() {return uuid;}
	
	public void give(Player p) {
		p.getInventory().setItem(inventorySlot, this.getItem());
	}
	
	public abstract void rightClick(Player clicker, Action action, Block clicked);
	
	public abstract void leftClick(Player clicker, Action action, Block clicked);
	
	public abstract void onDrop(Item dropped, Player dropper);
	
	public abstract void onHandSwitch(Player switcher);
	
	public abstract void onInventoryClick(Player clicker, int slot, ClickType click);
	
}
