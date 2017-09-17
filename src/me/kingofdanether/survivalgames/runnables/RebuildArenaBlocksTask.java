package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.GameState;

public class RebuildArenaBlocksTask extends BukkitRunnable {

	private Arena a;
	private int count;
	private int maxCount;
	private int speed;
	
	public RebuildArenaBlocksTask(Arena a) {
		this.a = a;
		this.count = 0;
		this.maxCount = (a.getBlocksToRebuild().size() - 1);
		this.speed = 20;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		for (int i = 0; i <= speed; i++) {
			if ((count > maxCount) || maxCount <= 0) {
				this.cancel();
				a.getBlocksToRebuild().clear();
				for (Entity en :a.getWorld().getEntities()) {
					if (en.getType() == EntityType.DROPPED_ITEM) {
						Item dropped = (Item)en;
						dropped.remove();
					}
				}
				a.setGameState(GameState.LOBBY_WAITING);
				return;
			}
			BlockState bs = a.getBlocksToRebuild().get(count);
			if (bs.getType() == Material.TNT) continue;
			bs.getLocation().getBlock().setType(bs.getType());
			bs.getLocation().getBlock().setData(bs.getRawData());
			count++;
		}
	}

}
