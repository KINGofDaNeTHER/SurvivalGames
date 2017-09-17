package me.kingofdanether.survivalgames.command.args;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SpectateArena implements CommandExecutor {

	private static SpectateArena instance;
	
	public static SpectateArena getInstance() {
		if (instance == null) {
			return new SpectateArena();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.spectate")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			final SGPlayer player = PlayerManager.getOrCreate(p.getPlayerListName());
			if (player == null) return true;
			if (args.length == 1) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cPlease enter an arena name!"));
				return true;
			}
			String arenaName = args[1];
			final Arena a = ArenaManager.getArena(arenaName);
			if (a == null) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cAn arena by the name of \"" + arenaName + "\" doesn't exist!"));
				return true;
			} else {
				if (!a.isEnabled()) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis arena was disabled by an admin!"));
					return true;
				}
				if (player.inGame()) {
					player.sendMessage(Constants.PREFIX + " &cYou are already in a game!");
					return true;
				}
				if (a.getGameState() == GameState.POSTGAME_REBUILDING) {
					player.sendMessage(Constants.PREFIX + " &cThis arena is still rebuilding!");
					return true;
				}
				if (a.getGameState() == GameState.LOBBY_WAITING || a.getGameState() == GameState.LOBBY_COUNTDOWN) {
					player.sendMessage(Constants.PREFIX + " &cThis arena is not in a game!");
					return true;
				}
				a.addPlayer(player);
				a.hidePlayer(player);
				SurvivalGames.getInstance().getGhostFactory().setGhost(player.getBukkitPlayer(), true);
				if (!a.isCrossWorld()) {
					PlayerUtils.saveInventoryToFile(player);
					player.getBukkitPlayer().getInventory().clear();
				}
				PlayerUtils.savePreviousLocation(player);
				player.sendMessage(Constants.PREFIX + " &aSpectating arena \"" + a.getName() + "\"!");		
				for (SGPlayer sgp : a.getPlayers()) {
					if (!sgp.getName().equals(player.getName())) {
						sgp.sendMessage(Constants.PREFIX + " &a" + player.getName() + " is now spectating the game!");
					}
				}
				if (a.isCrossWorld()) {
					PlayerUtils.saveInventoryToFile(player);
					p.getInventory().clear();
				}
				player.getBukkitPlayer().teleport(a.getCenter());
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.getBukkitPlayer().getGameMode() != GameMode.ADVENTURE) {
							player.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
						}
						player.setDead(true);
						player.getBukkitPlayer().setAllowFlight(true);
						player.getBukkitPlayer().getInventory().clear();
						a.getLeaveItem().give(player.getBukkitPlayer());
						a.getSpectatorCompass().give(player.getBukkitPlayer());
						player.fullHeal();	
						player.getBukkitPlayer().setScoreboard(a.getScoreboard());
					}
				}.runTaskLater(SurvivalGames.getInstance(), 1);
			}
		}
		return true;
	}

}
