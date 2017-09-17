package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.RandomType;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.MinecraftTimeUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;

public class SupplyDropTask extends BukkitRunnable {


	private Arena a;
	private int count;
	private int maxCount;
	private Location dropLoc;
	
	public SupplyDropTask(Arena a) {
		this.a = a;
		this.maxCount = a.getSupplyDropTime();
		this.count = 0;
		if (a.getSupplyDropType() == RandomType.LIST_RANDOM) {
			this.dropLoc = a.getSupplyDropLocs().get(NumberUtils.randInt(0, (a.getSupplyDropLocs().size() - 1)));
		} else if (a.getSupplyDropType() == RandomType.WORLD_RANDOM) {
			Location dropLocation = LocationUtils.randomLocation(a.getCorner1(), a.getCorner2());
			dropLocation.setY(dropLocation.getWorld().getHighestBlockYAt(dropLocation));
			this.dropLoc = dropLocation.add(0.5,0,0.5);
		}
		a.sendMessage(Constants.PREFIX + " &aA supply drop is dropping in " + MinecraftTimeUtils.getTimeString(maxCount) + " at " + LocationUtils.locToString(dropLoc));
		a.playSound(Sound.BLOCK_NOTE_PLING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (!a.getGameState().toString().startsWith("INGAME")) {
			this.cancel();
		}
		if (count >= maxCount) {
			this.cancel();
			dropLoc.getWorld().strikeLightningEffect(dropLoc);
			a.getData().set("last-supply-drop", LocationUtils.locToSavableString(dropLoc, ";"));
			a.saveData();
			new BukkitRunnable() {
				@Override
				public void run() {
					dropLoc.getWorld().spawnFallingBlock(dropLoc, Material.CHEST, (byte)0x0);
					a.sendMessage(Constants.PREFIX + " &aThe supply drop has spawned at " + LocationUtils.locToString(dropLoc));
					a.playSound(Sound.BLOCK_NOTE_PLING);
					new Countdown(a, NumberUtils.randInt(a.getMinWaitTime(), a.getMaxWaitTime())).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
					a.setNextEventSuffix("Border Spawn");
					a.setTimerSuffix("????");
				}
			}.runTaskLater(SurvivalGames.getInstance(), (20 * 2));
			return;
		}
		a.setTimerSuffix(MinecraftTimeUtils.getTimeString(maxCount - count));
//		for (SGPlayer player : a.getPlayers()) {
//			if (CooldownManager.hasCooldown(player.getName())) continue;
//			player.sendActionBar("&6&l" + Constants.LEFT_ARROW + "Supply Drop" + Constants.RIGHT_ARROW + " " + (int)player.getLocation().distance(dropLoc) + " blocks");
//		}
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
				new WorldBorderTask(a).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
				return;
			}
			count++;
		}
		
	}

}
