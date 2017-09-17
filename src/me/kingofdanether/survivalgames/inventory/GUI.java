package me.kingofdanether.survivalgames.inventory;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.util.StringUtils;

public abstract class GUI {
	
	
	protected Inventory inventory;
	protected UUID uuid;	
	protected int size;
	protected String name;
	
	public GUI(int size, String name) {
		this.inventory = Bukkit.createInventory(null, size, StringUtils.colorize(name));
		this.uuid = UUID.randomUUID();
		this.size = size;
		this.name = name;
		GuiManager.addGui(this);
	}

	public Inventory getInventory() {return inventory;}
	public UUID getUUID() {return uuid;}
	public int getSize() {return size;}
	public String getName() {return name;}
	
	public void open(Player p) {
		setItems();
		p.openInventory(inventory);
	}
	
	public void playClickSound(Player p) {p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);} 
	
	public abstract void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType);
	public abstract void setItems();
	
}
