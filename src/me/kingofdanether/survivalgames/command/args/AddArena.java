package me.kingofdanether.survivalgames.command.args;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.FileUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class AddArena implements CommandExecutor {

	private static AddArena instance;
	
	public static AddArena getInstance() {
		if (instance == null) {
			return new AddArena();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.addarena")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			if (args.length == 1) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cPlease enter an arena name!"));
				return true;
			}
			String arenaName = args[1];
			if (ArenaManager.getArena(arenaName) != null) {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cAn arena by the name of \"" + arenaName + "\" already exists!"));
				return true;
			} else {
				if (arenaName.length() > 16) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThe name can't be longer than 16 characters!"));
					return true;
				}
				File file = FileUtils.getAndCreateFile(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "arenas" + FileUtils.fs + arenaName + ".arena");
				Arena a = new Arena(file);
				a.setName(arenaName);
				ArenaManager.addArena(a);
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aCreated a new arena called \"" + a.getName() + "\"!"));
			}
		}
		return true;
	}
}
