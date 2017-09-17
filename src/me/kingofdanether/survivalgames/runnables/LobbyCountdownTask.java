package me.kingofdanether.survivalgames.runnables;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.enumeration.RandomType;
import me.kingofdanether.survivalgames.enumeration.SGAchievement;
import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import mkremins.fanciful.FancyMessage;

public class LobbyCountdownTask extends BukkitRunnable {
	
	private Arena a;
	
	private int seconds;
	private int sendMsgAt;
	private int count;
	
	private boolean forceStart;
	
	public LobbyCountdownTask(Arena a, int seconds, int sendMsgAt) {
		this.a = a;
		this.seconds = seconds;
		this.sendMsgAt = sendMsgAt;
		this.count = 0;
	}
	
	@Override
	public void run() {
		if (a.getPlayers().size() < a.getMinPlayers()) {
			this.cancel();
			for (SGPlayer player : a.getPlayers()) {
				player.playSound(Sound.BLOCK_ANVIL_LAND);
				player.sendTitle("&6&lWaiting for players..", "", 5, 20, 10);
			}
			a.setGameState(GameState.LOBBY_WAITING);
			return;
		}
		if (forceStart || (count >= seconds)) {
			this.cancel();
			if (forceStart) {
				a.sendMessage(Constants.PREFIX + " &eThe game was force started by an admin!");
			}
			a.initScoreboard();
			if (a.getGameStyle() == GameStyle.CLASSIC || a.getTpType() == RandomType.LIST_RANDOM) {
				int count = 0;
				for (int i = 0; i < a.getPlayers().size(); i++) {
					if (count >= a.getSpawnPoints().size()) count = 0;
					Location spawn = a.getSpawnPoints().get(i);
					a.getPlayers().get(i).getBukkitPlayer().getInventory().clear();
					a.getPlayers().get(i).getBukkitPlayer().teleport(spawn);
					a.getPlayers().get(i).getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
					a.getPlayers().get(i).getBukkitPlayer().setHealth(20.0D);
					a.getPlayers().get(i).getBukkitPlayer().setFoodLevel(20);
					a.getPlayers().get(i).getBukkitPlayer().setAllowFlight(false);
					PlayerUtils.setName(a.getPlayers().get(i).getBukkitPlayer(), "", StringUtils.colorize(" &f(" + NumberUtils.nearestHundreth(a.getPlayers().get(i).getBukkitPlayer().getHealth()) + " &c❤&f)"), TeamAction.CREATE);
					a.showPlayer(a.getPlayers().get(i));
					a.getPlayers().get(i).getData().set("stats." + a.getGameStyle().toString().toLowerCase() + ".games-played", a.getPlayers().get(i).getData().getInt("stats." + a.getGameStyle().toString().toLowerCase() + ".games-played") + 1);
					a.getPlayers().get(i).saveData();
					SGAchievement achievement = null;
					try {
						achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_PLAY");
					} catch (Exception ex) {}
					if (!a.getPlayers().get(i).hasAchievement(achievement)) {
						a.getPlayers().get(i).playSound(Sound.ENTITY_PLAYER_LEVELUP);
						a.getPlayers().get(i).addAchievement(achievement);
						new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(a.getPlayers().get(i).getBukkitPlayer());
					}		
					count++;
				}
			} else {
				for (SGPlayer player : a.getPlayers()) {
					Location spawn = LocationUtils.randomLocation(a.getCorner1(), a.getCorner2());
					spawn.setY(spawn.getWorld().getHighestBlockYAt(spawn));
					player.getBukkitPlayer().getInventory().clear();
					player.getBukkitPlayer().teleport(spawn.add(0.5,0.5,0.5));
					player.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);
					player.fullHeal();
					if (player.getBukkitPlayer().getActivePotionEffects().size() > 0) player.clearEffects();
					player.getBukkitPlayer().setAllowFlight(false);
					PlayerUtils.setName(player.getBukkitPlayer(), "", StringUtils.colorize(" &f(" + NumberUtils.nearestHundreth(player.getBukkitPlayer().getHealth()) + " &c❤&f)"), TeamAction.CREATE);
					a.showPlayer(player);
					player.getData().set("stats." + a.getGameStyle().toString().toLowerCase() + ".games-played", player.getData().getInt("stats." + a.getGameStyle().toString().toLowerCase() + ".games-played") + 1);
					player.saveData();
					SGAchievement achievement = null;
					try {
						achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_PLAY");
					} catch (Exception ex) {}
					if (!player.hasAchievement(achievement)) {
						player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
						player.addAchievement(achievement);
						new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(player.getBukkitPlayer());
					}				
				}
			}
			a.setGameState(GameState.INGAME_WAITING);
			a.setScoreboard(a.getScoreboard());
			a.getWorld().setTime(a.getTimeOfDay());
			InGameCountdownTask gameTask = new InGameCountdownTask(a, 20, 10);
			gameTask.runTaskTimer(SurvivalGames.getInstance(), 15, 20);
			a.setLobbyTask(null);
			a.setGameTask(gameTask);
			for (SGChest chest : a.getChests()) {
				chest.setCanBeFilled(true);
			}
			return;
		}
		if (count >= sendMsgAt) {
			for (SGPlayer player : a.getPlayers()) {
				player.playSound(Sound.BLOCK_NOTE_PLING);
				player.sendTitle("&6&lStarting in..", "&a&l" + (seconds - count), 5, 20, 0);
			}
		}
		count++;
	}
	
	public Arena getArena() {return a;}
	public int getSeconds() {return seconds;}
	public int getMsgStart() {return sendMsgAt;}
	
	public void forceStart() {
		this.forceStart = true;
	}

}
