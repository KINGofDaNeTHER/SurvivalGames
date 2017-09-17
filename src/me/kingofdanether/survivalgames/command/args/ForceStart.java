package me.kingofdanether.survivalgames.command.args;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;

public class ForceStart implements CommandExecutor {

	private static ForceStart instance;
	
	public static ForceStart getInstance() {
		if (instance == null) {
			return new ForceStart();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.forcestart")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			SGPlayer sgPlayer = PlayerManager.getOrCreate(p.getPlayerListName());
			if (sgPlayer == null) return true;
			
			if (args.length == 1 && !sgPlayer.inGame()) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cPlease enter an arena name!"));
				return true;
			} else if (sgPlayer.inGame()) {
				Arena a = ArenaManager.getArena(sgPlayer);
				if (a == null) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cAn error occurred while starting this arena!");
					return true;
				}
				if (!a.isEnabled()) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis arena was disabled by an admin!"));
					return true;
				}
				if (a.inGame()) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena has already started!");
					return true;
				}
				if (a.getLobbyTask() == null && a.getGameTask() == null) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena cannot be started at this time!");
					return true;
				}
				if (a.getPlayers().size() <= 1) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena must have at least 2 players to be started!");
					return true;
				}
				if (a.getLobbyTask() != null) {
					a.getLobbyTask().forceStart();
					sgPlayer.sendMessage(Constants.PREFIX + " &aYou have attempted to force start this arena!");
					a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				} else if (a.getGameTask() != null) {
					a.getGameTask().forceStart();
					sgPlayer.sendMessage(Constants.PREFIX + " &aYou have attempted to force start this arena!");
				}
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
				if (a.inGame()) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena has already started!");
					return true;
				}
				if (a.getLobbyTask() == null && a.getGameTask() == null) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena cannot be started at this time!");
					return true;
				}
				if (a.getPlayers().size() <= 1) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThis arena must have at least 2 players to be started!");
					return true;
				}
				if (a.getLobbyTask() != null) {
					a.getLobbyTask().forceStart();
					sgPlayer.sendMessage(Constants.PREFIX + " &aYou have attempted to force start this arena!");
					a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				} else if (a.getGameTask() != null) {
					a.getGameTask().forceStart();
					sgPlayer.sendMessage(Constants.PREFIX + " &aYou have attempted to force start this arena!");
				}
			}
		}
		return true;
	}
	
}
