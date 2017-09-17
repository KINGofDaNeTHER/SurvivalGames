package me.kingofdanether.survivalgames.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ClickableItemHandler implements Listener {
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.OFF_HAND) return;
		if (e.getItem() == null) return;
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			ClickableItem item = ItemManager.getItem(e.getItem());
			if (item == null) return;
			e.setCancelled(true);
			item.leftClick(e.getPlayer(), e.getAction(), e.getClickedBlock());
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ClickableItem item = ItemManager.getItem(e.getItem());
			if (item == null) return;
			e.setCancelled(true);
			item.rightClick(e.getPlayer(), e.getAction(), e.getClickedBlock());
			return;
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (e.isCancelled()) return;
		if (e.getItemDrop().getItemStack() == null) return;
		ClickableItem item = ItemManager.getItem(e.getItemDrop().getItemStack());
		if (item == null) return;
		e.setCancelled(true);
		item.onDrop(e.getItemDrop(), e.getPlayer());
	}
	
	@EventHandler
	public void onHandSwitch(PlayerSwapHandItemsEvent e) {
		ItemStack eventItem = e.getOffHandItem();
		if (eventItem == null) return;
		ClickableItem item = ItemManager.getItem(eventItem);
		if (item == null) return;
		e.setCancelled(true);
		item.onHandSwitch(e.getPlayer());
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.isCancelled() || e.getClickedInventory() == null || e.getCurrentItem() == null) return;
		ItemStack clicked = e.getCurrentItem();
		ClickableItem item = ItemManager.getItem(clicked);
		if (item == null) return;
		e.setCancelled(true);
		item.onInventoryClick((Player)e.getWhoClicked(), e.getSlot(), e.getClick());
	}

}
