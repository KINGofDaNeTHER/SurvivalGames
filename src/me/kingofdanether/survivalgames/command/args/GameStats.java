package me.kingofdanether.survivalgames.command.args;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.inventory.PlayerStatsGui;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;

public class GameStats implements CommandExecutor {

	private static GameStats instance;
	
	public static GameStats getInstance() {
		if (instance == null) {
			return new GameStats();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.stats")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			final SGPlayer sgPlayer = PlayerManager.getOrCreate(p.getPlayerListName());
			if (args.length == 1) {
				new PlayerStatsGui(sgPlayer, sgPlayer).open(p);
				return true;
			} else {
				if (!sender.hasPermission("survivalgames.stats.others")) {
					sender.sendMessage(Constants.PREFIX + " &cYou can't view other player's stats!");
					return true;
				}
				SGPlayer target = PlayerManager.searchOffline(args[1]);
				if (target == null) {
					sgPlayer.sendMessage(Constants.PREFIX + " &cThat player is no longer online!");
					return true;
				}
				new PlayerStatsGui(sgPlayer, target).open(p);
				return true;
			}
		}
		return true;
	}
	
}
