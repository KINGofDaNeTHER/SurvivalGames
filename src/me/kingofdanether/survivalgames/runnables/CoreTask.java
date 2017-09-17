package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.SGAchievement;
import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.sign.GameSign;
import me.kingofdanether.survivalgames.sign.SignManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import me.kingofdanether.survivalgames.util.WorldUtils;
import mkremins.fanciful.FancyMessage;

public class CoreTask extends BukkitRunnable {
	
	private SurvivalGames instance;
	private int count;
	private int fireCount;
	private int interval;
	
	public CoreTask(SurvivalGames instance, int interval) {
		this.instance = instance;
		this.count = 0;
		this.interval = interval;
	}
	
	@Override
	public void run() {
		this.handlePlayerData();
		for (Arena a : ArenaManager.getAllArenas()) {
			this.handleBorder(a);
			this.handleSgLobby(a);
			if (a.getPlayers().size() > 0) {
				this.updateScoreboard(a);
			}
			if (count >= (20/interval)) {
				this.updateSigns();
				this.handleSgWinner(a);
				this.handleFixSG(a);
				this.updateCompasses(a);
				this.updatePlayerHealths(a);
				this.updateRatings(a);
			}
			if (count >= 75/interval) {
				for (SGPlayer player : a.getPlayers()) { 
					player.setLastDamager(null);
				}
			}
			if (fireCount >= (45/interval)) {
				this.handleBorderFireballs(a);
			}
		}
		if (this.count >= (20/interval)) {
			this.count = 0;
		}
		if (this.fireCount >= (45/interval)) {
			this.fireCount = 0;
		}
		count++;
		fireCount++;
	}
	
	private void handleSgLobby(Arena a) {
		if ((a.getPlayers().size() >= a.getMinPlayers() && a.getGameState() == GameState.LOBBY_WAITING)) {
			a.setGameState(GameState.LOBBY_COUNTDOWN);
			LobbyCountdownTask lobbyTask = new LobbyCountdownTask(a, 10, 0);
			lobbyTask.runTaskTimer(SurvivalGames.getInstance(), 0, 20);
			a.setLobbyTask(lobbyTask);
			a.sendActionBar("&6Waiting for players.. &eComplete!");
			return;
		}
		for (SGPlayer player : a.getPlayers()) {
			if (a.getGameState() == GameState.LOBBY_WAITING) {
				player.sendActionBar("&6Waiting for players.. &e(" + NumberUtils.getPercentage(a.getPlayers().size(), a.getMinPlayers()) + "%)");
			}
		}
	}
	
	private void handleSgWinner(final Arena a) {
		if (a.getGameState() == GameState.LOBBY_COUNTDOWN || a.getGameState() == GameState.LOBBY_WAITING || a.getGameState() == GameState.POSTGAME_ENDING) {
			return;
		}
		if (a.hasWinner()) {
			final SGPlayer winner = a.getWinner();
			a.sendTitle("&6" + winner.getName(), "&6has won!", 10, 20, 20);
			a.sendMessage(Constants.PREFIX + " &a" + winner.getName() + " has won!");
			a.setGameState(GameState.POSTGAME_ENDING);
			if (!winner.hasAchievement(a.getGameStyle().toString() + "_FIRST_WIN")) {
				new BukkitRunnable() {
					@Override
					public void run() {
						SGAchievement achievement = null;
						try {
							achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_WIN");
						} catch (Exception ex) {
							return;
						}
						winner.playSound(Sound.ENTITY_PLAYER_LEVELUP);
						winner.addAchievement(achievement.toString());
						new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(winner.getBukkitPlayer());
					}
				}.runTaskLater(instance, 20 * 3);
			}
			winner.getData().set("stats." + a.getGameStyle().toString().toLowerCase() + ".wins", winner.getData().getInt("stats." + a.getGameStyle().toString().toLowerCase() + ".wins") + 1);
			winner.saveData();
			new EndGameTask(a, winner.getBukkitPlayer()).runTaskTimer(SurvivalGames.getInstance(), 0, 20);
		}
	}
	
	
	
	
	private void handlePlayerData() {
		for (Player op : Bukkit.getOnlinePlayers()) {
			if (!PlayerManager.hasPlayer(op.getUniqueId())) {
				PlayerManager.addPlayer(op.getName());
			} else if (!PlayerManager.getPlayer(op.getUniqueId()).getBukkitPlayer().equals(op)) {
				SGPlayer sgPlayer = PlayerManager.getPlayer(op.getUniqueId());
				sgPlayer.updatePlayer(op);
			}
			//SGPlayer sgPlayer = PlayerManager.getPlayer(op.getUniqueId());
		}
	}
	
	private void handleFixSG(Arena a) {
		if ((a.getPlayers().size() <= 0) && (a.getGameState() != GameState.LOBBY_WAITING && a.getGameState() != GameState.POSTGAME_REBUILDING)) {
			a.setGameState(GameState.LOBBY_WAITING);
		}
	}
	
	private void handleBorder(final Arena a) {
		if (!a.inGame()) return;
		for (final SGPlayer player : a.getPlayers()) {
			if (player.isDead()) continue;
			if (!a.getWorld().getWorldBorder().isInside(player.getBukkitPlayer().getLocation())) {
				Vector push = WorldUtils.vectorPointingTo(a.getCenter(), player.getBukkitPlayer().getLocation());
				push.setY(push.getY()+  0.75);
				push.multiply(1.12);
				player.getBukkitPlayer().damage(a.getBorderDamage());
				player.getBukkitPlayer().setVelocity(push);
			}
		}
	}
	
	private void handleBorderFireballs(final Arena a) {
		if (a.getGameState() == GameState.INGAME_DEATHMATCH || a.getGameState().toString().startsWith("LOBBY")
			|| a.getGameState().toString().startsWith("POSTGAME")) return;
		for (final SGPlayer player : a.getPlayers()) {
			int distance = (int)player.getLocation().distance(a.getCenter());
			int borderSize = (((int)a.getWorld().getWorldBorder().getSize() - 10)/2);
			if (distance < borderSize) continue;
			Location temp = null;
			switch (player.getCardinalDirection()) {
			case NORTH:
				temp = player.getLocation().add(0.5,5,-7.5);
				break;
			case EAST:
				temp = player.getLocation().add(-7.5,5,0.5);
				break;
			case SOUTH:
				temp = player.getLocation().add(0.5,5,7.5);
				break;
			case WEST:
				temp = player.getLocation().add(7.5,5,0.5);
				break;
			case NORTH_EAST:
				temp = player.getLocation().add(0.5,5,-7.5);
				break;
			case NORTH_WEST:
				temp = player.getLocation().add(0.5,5,-7.5);
				break;
			case SOUTH_EAST:
				temp = player.getLocation().add(0.5,5,7.5);
				break;
			case SOUTH_WEST:
				temp = player.getLocation().add(0.5,5,7.5);
				break;
			default:
				break;
			}
			if (temp == null) continue;
			final Location fireSpawn = temp;
			if (player.isDead()) return;
			Fireball f = a.getWorld().spawn(fireSpawn, Fireball.class);
			f.setYield(1.50F);
			Vector dir = WorldUtils.vectorPointingTo(player.getBukkitPlayer().getLocation(), f.getLocation().multiply(0.75D));
			dir.setY(dir.getY() + (-0.724D));
			f.setVelocity(dir);
		}
	}
	
	private void updateCompasses(Arena a) {
		for (SGPlayer player  : a.getPlayers()) {
			if (player.isDead()) continue;
			if (player.getCompassTarget() == null) continue;
			if (player.getCompassTarget().isDead()) {
				player.sendMessage(Constants.PREFIX + " &cPlayer " + player.getCompassTarget().getName() + " has died! Your compass will no longer track that player!");
				player.setLastCompassTarget(null);
				player.getBukkitPlayer().setCompassTarget(a.getCenter());
				continue;
			}
			player.getBukkitPlayer().setCompassTarget(player.getCompassTarget().getBukkitPlayer().getLocation());
		}
	}
	
	private void updateScoreboard(Arena a) {
		a.setPlayersLeftSuffix(a.getPlayersAliveAmount() > 1 ? a.getPlayersAliveAmount() + " players" : a.getPlayersAliveAmount() + " player");
	}
	
	private void updateSigns() {
		for (GameSign s : SignManager.getAllSigns()) {
			s.update();
		}
	}
	
	private void updatePlayerHealths(Arena a) {
		if (a.getGameState().toString().startsWith("LOBBY")) return;
		for (SGPlayer player : a.getPlayers()) {
			if (player.isDead()) continue;
			PlayerUtils.setName(player.getBukkitPlayer(), "", StringUtils.colorize(" &f(" + NumberUtils.nearestHundreth(player.getBukkitPlayer().getHealth()) + " &c❤&f)"), TeamAction.CREATE);
		}
	}
	
	private void updateRatings(Arena a) {
		if (!a.getGameState().toString().startsWith("INGAME")) return;
		if (a.getAverageRating() <= 0.0D) {
			a.setRatingSuffix(a.getAverageRatingString());
			return;
		}
		a.setRatingSuffix(a.getAverageRatingString() + " &f(" + a.getAverageRating() + "/5)");
	}

}
