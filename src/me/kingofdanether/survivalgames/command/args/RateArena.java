package me.kingofdanether.survivalgames.command.args;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.Rating;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;

public class RateArena implements CommandExecutor {

	private static RateArena instance;
	
	public static RateArena getInstance() {
		if (instance == null) {
			return new RateArena();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.rate")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			SGPlayer player = PlayerManager.getOrCreate(p.getPlayerListName());
			if (player == null) return true;
			if (!player.canRate()) {
				player.sendMessage(Constants.PREFIX + " &cYou can't do this right now!");
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
				if (args.length <= 2) {
					player.sendMessage(Constants.PREFIX + " &cPlease enter a rating (1-5)!");
					return true;
				}
				if (!player.canRate(a)) {
					player.sendMessage(Constants.PREFIX + " &cYou can't do this right now!");
					return true;
				}
				int score;
				try {
					score = Integer.valueOf(args[2]);
				} catch (Exception ex) {
					player.sendMessage(Constants.PREFIX + " &cInvalid number \"" + args[2] + "\" (1-5)!");
					return true;
				}
				if (score <= 0 || score > 5) {
					player.sendMessage(Constants.PREFIX + " &cInvalid number \"" + args[2] + "\" (1-5)!");
					return true;
				}
				Rating rating = Rating.getByScore(score);
				if (rating == null) {
					player.sendMessage(Constants.PREFIX + " &cError ocurred while rating this arena!");
					return true;
				}
				int current = a.getData().getInt("ratings." + rating.toString().toLowerCase());
				a.getData().set("ratings." + rating.toString().toLowerCase(), (current + 1));
				a.saveData();
				int timesRated = player.getData().getInt("ratings." + a.getName() + "." + rating.toString().toLowerCase());
				player.getData().set("ratings." + a.getName() + "." + rating.toString().toLowerCase(), (timesRated + 1));
				player.saveData();
				player.setRateable(a, false);
				player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				player.sendMessage(Constants.PREFIX + " &eYou rated the arena \"" + a.getName() + "\" as " + rating.getScoreName() + " " + StringUtils.capitalize(rating.toString()) + "&e!");
				player.sendMessage(Constants.PREFIX + " &aUpdated rating: " + a.getAverageRatingString() + " &a(" + a.getAverageRating() + "/5)!");
			}
		}
		return true;
	}

}
