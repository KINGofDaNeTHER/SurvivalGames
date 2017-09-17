package me.kingofdanether.survivalgames.command.args;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class RemoveChest implements CommandExecutor {

	private static RemoveChest instance;
	
	public static RemoveChest getInstance() {
		if (instance == null) {
			return new RemoveChest();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.removechest")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
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
				Block b = p.getTargetBlock((Set<Material>) null, 10);
				if (b == null) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cNo block in sight!"));
					return true;
				}
				if (!(b.getState() instanceof Chest)) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThat block is not a chest!"));
					return true;
				}
				if (!a.hasChestAt(b.getLocation())) {
					sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cThis chest is not registered in this arena!"));
					return true;
				}
				a.removeChest(LocationUtils.locToSavableString(b.getLocation(), ","));
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aRemoved chest from arena \"" + a.getName() + "\" at " + LocationUtils.locToString(b.getLocation()) + "!"));
			}
		}
		return true;
	}

}
