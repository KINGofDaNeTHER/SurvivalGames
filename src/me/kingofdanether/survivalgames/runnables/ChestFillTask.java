package me.kingofdanether.survivalgames.runnables;

import java.util.ArrayList;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.util.NumberUtils;

public class ChestFillTask extends BukkitRunnable {
	
	@SuppressWarnings("unused")
	private SGChest chest;
	private ArrayList<ItemStack> items;
	private int invSize;
	private int count;
	private int maxCount;
	private Chest mcChest;
	private int tries;
	private int maxTries;
	
	public ChestFillTask(SGChest chest, boolean clearInv) {
		this.chest = chest;
		this.mcChest = chest.getLocation().getBlock().getState() instanceof Chest ? (Chest)chest.getLocation().getBlock().getState() : null;
		this.items = chest.getFillItems().size() <= 0 ? chest.getFillItems() : chest.getFillItems();
		this.invSize = mcChest == null ? 0 :  mcChest.getBlockInventory().getSize();
		this.count = 0;
		this.maxCount = (items.size() - 1);
		this.tries = 0;
		this.maxTries = 10;
		if (clearInv) {
			mcChest.getBlockInventory().clear();
			mcChest.update();
		}
	}
	
	@Override
	public void run() {
		if (mcChest == null) {
			this.cancel();
			return;
		}
		if (!(mcChest.getBlock().getState() instanceof Chest)) {
			this.cancel();
			return;
		}
		if (mcChest.getInventory().firstEmpty() == -1) {
			this.cancel();
			return;
		}
		if (tries >= maxTries) {
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
		tries++;
	}

}
