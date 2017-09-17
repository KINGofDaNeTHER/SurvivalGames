package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.GameState;

public class ResetArenaBlocksTask extends BukkitRunnable {


	private Arena a;
	private int count;
	private int maxCount;
	private int speed;
	
	public ResetArenaBlocksTask(Arena a) {
		this.a = a;
		this.count = 0;
		this.maxCount = (a.getBlocksToRemove().size() - 1);
		this.speed = 20;
	}
	
	@Override
	public void run() {
		for (int i = 0; i <= speed; i++) {
			if ((count > maxCount) || maxCount <= 0) {
				this.cancel();
				a.getBlocksToRemove().clear();
				if (a.getBlocksToRebuild().size() <= 0) {
					for (Entity en :a.getWorld().getEntities()) {
						if (en.getType() == EntityType.DROPPED_ITEM) {
							Item dropped = (Item)en;
							dropped.remove();
						}
					}
					a.setGameState(GameState.LOBBY_WAITING);
				} else {
					new RebuildArenaBlocksTask(a).runTaskTimer(SurvivalGames.getInstance(), 0, 1);
				}
				return;
			}
			BlockState bs = a.getBlocksToRemove().get(count);
			if (bs.getLocation().getBlock().getType() != Material.AIR) {
				bs.getLocation().getBlock().setType(Material.AIR);
			}
			count++;
		}
	}

}
