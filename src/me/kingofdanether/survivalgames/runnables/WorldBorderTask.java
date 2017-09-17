package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.MinecraftTimeUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class WorldBorderTask extends BukkitRunnable {

	private Arena a;
	private WorldBorder worldBorder;
	private int tempCount;
	private int count;
	private int maxCount;
	private boolean sentMsg;
	private boolean border;
	
	public WorldBorderTask(Arena a) {
		this.a = a;
		this.worldBorder = a.getWorld().getWorldBorder();
//		worldBorder.setSize(a.getBorderSize());
//		worldBorder.setCenter(a.getCenter());
//		worldBorder.setDamageAmount(0);
//		worldBorder.setDamageBuffer(a.getBorderDamageBuffer());
		this.tempCount = 0;
		this.count = 0;
		this.maxCount = a.getBorderTime();
		a.sendMessage(Constants.PREFIX + " &aThe border is spawning in " + MinecraftTimeUtils.getTimeString(maxCount));
		a.playSound(Sound.BLOCK_NOTE_PLING);
	}
	
	@Override
	public void run() {
		if (!a.getGameState().toString().startsWith("INGAME")) {
			this.cancel();
			return;
		}
		if (count >= maxCount) {
			if (worldBorder.getSize() <= a.getMinBorderSize()) {
				this.cancel();
				worldBorder.setDamageBuffer(0);
				a.sendTitle("&6&lThe border", "&6&lhas stopped moving", 15, 20, 10);
				a.sendMessage(Constants.PREFIX + " &aThe border has stopped moving!");
				a.setNextEventSuffix("Death Match");
				a.setTimerSuffix("END THE GAME");
				a.playSound(Sound.ENTITY_GENERIC_EXPLODE);
				a.setGameState(GameState.INGAME_DEATHMATCH);
				return;
			}
			if (!this.sentMsg) {
				worldBorder.setSize(a.getBorderSize());
				worldBorder.setCenter(a.getCenter());
				worldBorder.setDamageAmount(0);
				worldBorder.setDamageBuffer(a.getBorderDamageBuffer());
				a.sendTitle("&6&lThe border", "&6&lhas spawned!", 5, 20, 20);
				a.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aThe border has spawned!"));
				a.playSound(Sound.ENTITY_GENERIC_EXPLODE);
				this.sentMsg = true;
				this.border = true;
			}
			if (tempCount >= a.getBorderDecreaseTime()) {
				this.tempCount = 0;
				worldBorder.setSize((worldBorder.getSize() - a.getBorderDecrease()), Long.valueOf(String.valueOf(a.getBorderSpeed())));
				a.getNextEventTeam().setPrefix(StringUtils.colorize("&6" + Constants.RIGHT_ARROW + " &aBorder "));
				a.getNextEventTeam().setSuffix(StringUtils.colorize("&aShrink &6" + Constants.LEFT_ARROW));
				a.getTimerTeam().setPrefix(StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6Size: "));
				a.getTimerTeam().setSuffix(StringUtils.colorize("&6" + NumberUtils.nearestHundreth((worldBorder.getSize()))));
			}
			tempCount++;
		}
		if (!border) {
			//a.sendActionBar("&6&lBorder spawn: " + MinecraftTimeUtils.getTimeString(maxCount - count));
			a.setTimerSuffix(MinecraftTimeUtils.getTimeString(maxCount - count));
			count++;
		}
	}

}
