package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.util.MinecraftTimeUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;

public class ChestRefillTask extends BukkitRunnable {

	private Arena a;
	private int count;
	private int maxCount;
	
	public ChestRefillTask(Arena a) {
		this.a = a;
		this.maxCount = a.getRefillTime();
		this.count = 0;
	}
	
	@Override
	public void run() {
		if (!a.getGameState().toString().startsWith("INGAME")) {
			this.cancel();
		}
		if (count >= maxCount) {
			this.cancel();
			for (SGChest chest : a.getChests()) {
				new ChestFillTask(chest, false).runTaskTimer(SurvivalGames.getInstance(), 0, 1);
			}
			a.sendTitle("&6&lChests", "&6&lhave been refilled", 5, 20, 10);
			a.playSound(Sound.BLOCK_CHEST_CLOSE);
			a.setNextEventSuffix("Supply Drop");
			a.setTimerSuffix("????");
			new Countdown(a, NumberUtils.randInt(a.getMinWaitTime(), a.getMaxWaitTime())).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
			return;
		}
		//a.sendActionBar("&6&lChest Refill: " + MinecraftTimeUtils.getTimeString(maxCount - count));
		a.setTimerSuffix(MinecraftTimeUtils.getTimeString(maxCount - count));
		count++;
	}
	
	private class Countdown extends BukkitRunnable {

		private Arena a;
		private int seconds;
		private int count;
		
		public Countdown(Arena a, int seconds) {
			this.a = a;
			this.seconds = seconds;
			this.count = 0;
		}
		
		@Override
		public void run() {
			if (!a.getGameState().toString().startsWith("INGAME")) {
				this.cancel();
			}
			if (count >= seconds) {
				this.cancel();
				new SupplyDropTask(a).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
				return;
			}
			count++;
		}
		
	}
}
