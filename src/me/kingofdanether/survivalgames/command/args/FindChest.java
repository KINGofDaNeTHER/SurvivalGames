package me.kingofdanether.survivalgames.command.args;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;

public class FindChest implements CommandExecutor {

	private static FindChest instance;
	
	public static FindChest getInstance() {
		if (instance == null) {
			return new FindChest();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.findchest")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
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
				if (args.length == 2) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cPlease enter a number (1-" + a.getChests().size() + ")!"));
					return true;
				}
				int chestNum;
				try {
					chestNum = Integer.valueOf(args[2]);
				} catch (Exception ex) {
					ex.printStackTrace();
					return true;
				}
				if (chestNum > a.getChests().size()) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis arena does not have that many chests! It has " + a.getChests().size() + " chest(s)"));
					return true;
				}
				if ((chestNum - 1) <= 0) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cEnter a number greater than 0! This arena has " + a.getChests().size() + " chest(s)"));
					return true;
				}
				Location teleport = a.getChests().get(chestNum - 1).getChest().getLocation();
				if (teleport == null) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis location may be corrupted or doesn't exist anymore, please delete it!"));
					return true;
				}
				((Player)sender).teleport(teleport);
			}
		}
		return true;
	}

}
