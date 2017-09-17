package me.kingofdanether.survivalgames.runnables;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.MinecraftTimeUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;

public class InGameCountdownTask extends BukkitRunnable {
	
	private Arena a;
	
	private int seconds;
	private int sendMsgAt;
	private int count;
	
	private boolean forceStart;
	
	public InGameCountdownTask(Arena a, int seconds, int sendMsgAt) {
		this.a = a;
		this.seconds = seconds;
		this.sendMsgAt = sendMsgAt;
		this.count = 0;
		for (SGPlayer player : a.getPlayers()) {
			player.sendMessage(Constants.PREFIX + " &aThe game is starting in " + seconds + " seconds!");
			player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
			player.sendTitle("&6&lSurvival Games", "&c&l" + StringUtils.capitalize(a.getGameStyle().toString().toLowerCase()) + " Mode", 5, 20, 10);
		}
		a.setNextEventSuffix("Game Start");
	}
	
	@Override
	public void run() {
		if (a.getGameState() != GameState.INGAME_COUNTDOWN && a.getGameState() != GameState.INGAME_WAITING) {
			this.cancel();
			return;
		}
		if (forceStart || (count >= seconds)) {
			this.cancel();
			if (forceStart) {
				a.sendMessage(Constants.PREFIX + " &eThe game was force started by an admin!");
			}
			for (SGPlayer player : a.getPlayers()) {
				player.sendMessage(Constants.PREFIX + " &aThe game has started!");
				player.sendTitle("&6&lThe game has started!", "", 5, 10, 5);
				player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				if (a.getGameStyle() == GameStyle.CLASSIC) {
					player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * a.getSpeedDuration()), 1, true, true));
					player.sendMessage(Constants.PREFIX + " &2&lTIP: &aWhen the game starts, you gain " + a.getSpeedDuration() + " seconds of speed II for as long as you don't hit someone!");
					player.setAttackSpeed(a.getAttackSpeed());
				}
			}
			if (a.getGameStyle() == GameStyle.CLASSIC) {
				a.setSpeedEffectActive(true);
				new BukkitRunnable() {
					private int count = 0;
					@Override
					public void run() {
						if (!a.getGameState().toString().startsWith("INGAME")) {
							a.setSpeedEffectActive(false);
							this.cancel();
							return;
						}
						if (count >= a.getSpeedDuration() && a.hasSpeedEffectActive()) {
							a.setSpeedEffectActive(false);
							a.playSound(Sound.ENTITY_WOLF_HOWL);
							this.cancel();
							return;
						}
						count++;
					}
				}.runTaskTimer(SurvivalGames.getInstance(), 0, 20);
			}
			a.setGameState(GameState.INGAME_STARTED);
			a.setNextEventSuffix("Chest Refill");
			a.setTimerSuffix("????");
			a.setGameTask(null);
			new Countdown(a, NumberUtils.randInt(a.getMinWaitTime(), a.getMaxWaitTime())).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
			return;
		}
		if (count >= sendMsgAt) {
			if (a.getGameState() != GameState.INGAME_COUNTDOWN) {
				a.setGameState(GameState.INGAME_COUNTDOWN);
			}
			for (SGPlayer player : a.getPlayers()) {
				player.playSound(Sound.BLOCK_NOTE_PLING);
				player.sendTitle("&6&lStarting in..", "&a&l" + (seconds - count), 5, 20, 0);
			}
		}
		a.setTimerSuffix(MinecraftTimeUtils.getTimeString(seconds - count));
		count++;
	}
	
	public Arena getArena() {return a;}
	public int getSeconds() {return seconds;}
	public int getMsgStart() {return sendMsgAt;}

	public void forceStart() {
		this.forceStart = true;
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
				new ChestRefillTask(a).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
				return;
			}
			count++;
		}
	}
	
}
