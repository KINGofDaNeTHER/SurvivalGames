package me.kingofdanether.survivalgames.command.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;

public class AdminToggle implements CommandExecutor {
	
	private static AdminToggle instance;
	
	public static AdminToggle getInstance() {
		if (instance == null) {
			return new AdminToggle();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.adminmode")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			SGPlayer sgPlayer = PlayerManager.getOrCreate(p.getPlayerListName());
			if (sgPlayer == null) return true;
			sgPlayer.setAdmin(sgPlayer.adminEnabled() ? false : true);
			String value = sgPlayer.adminEnabled() ? "&aenabled" : "&cdisabled";
			sgPlayer.sendMessage(Constants.PREFIX + " " + value + " &6override mode!");
			return true;
		}
		return true;
	}
	
}
