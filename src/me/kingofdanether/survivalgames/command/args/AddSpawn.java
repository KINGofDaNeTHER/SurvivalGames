package me.kingofdanether.survivalgames.command.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;

public class AddSpawn implements CommandExecutor {

	private static AddSpawn instance;
	
	public static AddSpawn getInstance() {
		if (instance == null) {
			return new AddSpawn();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			Player p = (Player)sender;
			if (!sender.hasPermission("survivalgames.addspawn")) {
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
				if (a.hasSpawnpoint(p.getLocation())) {
					p.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cA spawn point already exists at this location!"));
					return true;
				}
				a.addSpawnPoint(p.getLocation());
				p.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aAdded a spawn point at your current location!"));
			}
		}
		return true;
	}
	
}
