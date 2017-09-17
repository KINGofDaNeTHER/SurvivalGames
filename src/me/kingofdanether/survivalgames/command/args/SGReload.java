package me.kingofdanether.survivalgames.command.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SGReload implements CommandExecutor {
	
	private static SGReload instance;
	
	public static SGReload getInstance() {
		if (instance == null) {
			return new SGReload();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.reload")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			SurvivalGames.getInstance().reloadConfig();
			SurvivalGames.getInstance().getSignsYml().reloadConfig();
			int count = 2;
			for (Arena a : ArenaManager.getAllArenas()) {
				a.reloadData();
				count++;
			}
			p.sendMessage(Constants.PREFIX + StringUtils.colorize(" &aReloaded " + count + " files!"));
			p.sendMessage(Constants.PREFIX + StringUtils.colorize(" &cWarning: &eReloading files this way may cause unknown bugs, the best way to do this is by restarting the server!"));
		}
		return true;
	}
	
}
