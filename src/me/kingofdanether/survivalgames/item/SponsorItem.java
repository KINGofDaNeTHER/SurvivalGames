package me.kingofdanether.survivalgames.item;

import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;

public class SponsorItem {

	private Arena a;
	private ItemStack item;
	private double clout;
	
	public SponsorItem(Arena a, ItemStack item, double clout) {
		this.a = a;
		this.item = item;
		this.clout = clout;
	}
	
	public Arena getArena() {return a;}
	public ItemStack getItemStack() {return item;}
	public double getCloutCost() {return clout;}
	
	
}
