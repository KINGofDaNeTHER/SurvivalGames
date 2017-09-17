package me.kingofdanether.survivalgames.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiClickHandler implements Listener {
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.isCancelled() || e.getClickedInventory() == null) return;
		ItemStack clicked = e.getCurrentItem() == null ? null : e.getCurrentItem();
//		GUI gui = e.getClickedInventory().getTitle() == null ? null : GuiManager.getGui(e.getClickedInventory().getTitle());
		GUI gui = e.getClickedInventory().getTitle() == null ? null : GuiManager.getGui(e.getClickedInventory());
		if (gui == null) return;
		e.setCancelled(true);
		gui.clickItem((Player)e.getWhoClicked(), e.getSlot(), clicked, e.getClick());
	}

}
