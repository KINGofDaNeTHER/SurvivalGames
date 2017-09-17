package me.kingofdanether.survivalgames.command.args;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class JoinArena implements CommandExecutor {

	private static JoinArena instance;
	
	public static JoinArena getInstance() {
		if (instance == null) {
			return new JoinArena();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.join")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			if (!sender.hasPermission("survivalgames.join.bypass")) {
				String world = p.getWorld().getName();
				if (!SurvivalGames.getInstance().getConfig().getStringList("whitelisted-worlds").contains(world)) {
					p.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cYou cannot join a game from this world!"));
					return true;
				}
			}
			if (args.length == 1) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cPlease enter an arena name!"));
				return true;
			}
			String arenaName = args[1];
			Arena a = ArenaManager.getArena(arenaName);
			if (a == null) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cAn arena by the name of \"" + arenaName + "\" doesn't exist!"));
				return true;
			} else {
				if (!a.isEnabled()) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis arena was disabled by an admin!"));
					return true;
				}
				SGPlayer sgPlayer = PlayerManager.getOrCreate(p.getPlayerListName());
				if (sgPlayer.inGame()) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cYou are already in a game!");
					return true;
				}
				if (a.getPlayers().size() >= a.getMaxPlayers()) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena is full!");
					return true;
				}
				if (a.getGameState() == GameState.POSTGAME_REBUILDING) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena is still rebuilding!");
					return true;
				}
				if (a.getGameState() != GameState.LOBBY_WAITING && a.getGameState() != GameState.LOBBY_COUNTDOWN) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena is already in a game!");
					return true;
				}
				a.addPlayer(sgPlayer);
				//XXX:sgPlayer.setInGame(true);
				if (!a.isCrossWorld()) {
					PlayerUtils.saveInventoryToFile(sgPlayer);
					sgPlayer.getBukkitPlayer().getInventory().clear();
				}
				PlayerUtils.savePreviousLocation(sgPlayer);
				sgPlayer.getBukkitPlayer().teleport(a.getLobby());
				//WorldUtils.spawnCorpse(p, a);
				sgPlayer.sendMessage(Constants.PREFIX + " &aJoined arena \"" + a.getName() + "\"! &e(" + a.getPlayers().size() + "/" + a.getMaxPlayers() + ")");
				sgPlayer.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
				sgPlayer.fullHeal();
				//if (sgPlayer.getBukkitPlayer().getActivePotionEffects().size() > 0) sgPlayer.clearEffects();
				for (SGPlayer player : a.getPlayers()) {
					if (!player.equals(sgPlayer)) {
						player.sendMessage(Constants.PREFIX + " &a" + sgPlayer.getName() + " has joined the game! &e(" + a.getPlayers().size() + "/" + a.getMaxPlayers() + ")");
					}
				}
				if (a.isCrossWorld()) {
					PlayerUtils.saveInventoryToFile(sgPlayer);
					p.getInventory().clear();
				}
				a.getLeaveItem().give(sgPlayer.getBukkitPlayer());
				a.showPlayer(sgPlayer);
			}
		}
		return true;
	}
	
}
