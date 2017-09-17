package me.kingofdanether.survivalgames.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.command.args.AddArena;
import me.kingofdanether.survivalgames.command.args.AddChest;
import me.kingofdanether.survivalgames.command.args.AddSpawn;
import me.kingofdanether.survivalgames.command.args.AdminToggle;
import me.kingofdanether.survivalgames.command.args.EditArena;
import me.kingofdanether.survivalgames.command.args.FindChest;
import me.kingofdanether.survivalgames.command.args.ForceStart;
import me.kingofdanether.survivalgames.command.args.GameStats;
import me.kingofdanether.survivalgames.command.args.JoinArena;
import me.kingofdanether.survivalgames.command.args.Leaderboard;
import me.kingofdanether.survivalgames.command.args.LeaveArena;
import me.kingofdanether.survivalgames.command.args.RateArena;
import me.kingofdanether.survivalgames.command.args.RemoveArena;
import me.kingofdanether.survivalgames.command.args.RemoveChest;
import me.kingofdanether.survivalgames.command.args.SGReload;
import me.kingofdanether.survivalgames.command.args.SpectateArena;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;
import mkremins.fanciful.FancyMessage;

public class SGCommand implements CommandExecutor {

	private AddArena addArena = AddArena.getInstance();
	private RemoveArena remArena = RemoveArena.getInstance();
	private EditArena editArena = EditArena.getInstance();
	private FindChest findChest = FindChest.getInstance();
	private AddChest addChest = AddChest.getInstance();
	private RemoveChest remChest = RemoveChest.getInstance();
	private JoinArena joinArena = JoinArena.getInstance();
	private LeaveArena leaveArena = LeaveArena.getInstance();
	private AddSpawn addSpawn = AddSpawn.getInstance();
	private GameStats stats = GameStats.getInstance();
	private Leaderboard leaderBoardArg = Leaderboard.getInstance();
	private AdminToggle adminToggle = AdminToggle.getInstance();
	private SGReload sgReload = SGReload.getInstance();
	private RateArena rateArena = RateArena.getInstance();
	private SpectateArena specArena = SpectateArena.getInstance();
	private ForceStart forceStart = ForceStart.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Only players can do this command!");
				return true;
			}
			Player p = (Player)sender;
			if (args.length == 0) {
				p.sendMessage(StringUtils.colorize("&6SurvivalGames by KINGofDaNeTHER &e(Hover/click on the commands!)"));
				if (p.hasPermission("survivalgames.addarena")) {
					new FancyMessage(StringUtils.colorize("&e/sg addarena <name>")).tooltip(StringUtils.colorize("&6Add an arena")).suggest("/sg addarena <name>").send(sender);
				}
				if (p.hasPermission("survivalgames.removearena")) {
					new FancyMessage(StringUtils.colorize("&e/sg removearena <name>")).tooltip(StringUtils.colorize("&6Remove an arena")).suggest("/sg removearena <name>").send(sender);
				}
				if (p.hasPermission("survivalgames.editarena")) {
					new FancyMessage(StringUtils.colorize("&e/sg editarena <name>")).tooltip(StringUtils.colorize("&6Edit an arena")).suggest("/sg editarena <name>").send(sender);					
				}
				if (p.hasPermission("survivalgames.listarenas")) {
					new FancyMessage(StringUtils.colorize("&e/sg listarenas")).tooltip(StringUtils.colorize("&6List all arenas.")).suggest("/sg listarenas").send(sender);
				}
				if (p.hasPermission("survivalgames.findchest")) {
					new FancyMessage(StringUtils.colorize("&e/sg findchest <arena> <number>")).tooltip(StringUtils.colorize("&6Find chests in arenas.")).suggest("/sg findchest <arena> <number>").send(sender);
				}
				if (p.hasPermission("survivalgames.addchest")) {
					new FancyMessage(StringUtils.colorize("&e/sg addchest <arena> <tier>")).tooltip(StringUtils.colorize("&6Add a specific chest in an arena.")).suggest("/sg addchest <arena> <tier>").send(sender);
				}
				if (p.hasPermission("survivalgames.removechest")) {
					new FancyMessage(StringUtils.colorize("&e/sg removechest <arena> <number>")).tooltip(StringUtils.colorize("&6Remove a specific chest in an arena.")).suggest("/sg removechest <arena> <number>").send(sender);
				}
				if (p.hasPermission("survivalgames.addspawn")) {
					new FancyMessage(StringUtils.colorize("&e/sg addspawn <arena>")).tooltip(StringUtils.colorize("&6Add a spawn to a certain arena.")).suggest("/sg addspawn <arena>").send(sender);
				}
				if (p.hasPermission("survivalgames.join")) {
					new FancyMessage(StringUtils.colorize("&e/sg join <arena>")).tooltip(StringUtils.colorize("&6Join an arena.")).suggest("/sg join <arena>").send(sender);
				}
				if (p.hasPermission("survivalgames.leave")) {
					new FancyMessage(StringUtils.colorize("&e/sg leave")).tooltip(StringUtils.colorize("&6Leave the arena.")).suggest("/sg leave").send(sender);
				}
				if (p.hasPermission("survivalgames.stats")) {
					new FancyMessage(StringUtils.colorize("&e/sg stats [player]")).tooltip(StringUtils.colorize("&6Shows player statistics.")).suggest("/sg stats").send(sender);
				}
				if (p.hasPermission("survivalgames.leaderboard")) {
					new FancyMessage(StringUtils.colorize("&e/sg leaderboard [category:gamestyle]")).tooltip(StringUtils.colorize("&6Shows leaderboards in a specific category. (Catergories = Kills, Deaths, KD, Wins, Clout)")).suggest("/sg leaderboard [category:gamestyle]").send(sender);
				}
				if (p.hasPermission("survivalgames.adminmode")) {
					new FancyMessage(StringUtils.colorize("&e/sg admin")).tooltip(StringUtils.colorize("&6Toggle override mode.")).suggest("/sg admin").send(sender);
				}
				if (p.hasPermission("survivalgames.reload")) {
					new FancyMessage(StringUtils.colorize("&e/sg reload")).tooltip(StringUtils.colorize("&6Reload arena files.")).suggest("/sg reload").send(sender);
				}
				if (p.hasPermission("survivalgames.spectate")) {
					new FancyMessage(StringUtils.colorize("&e/sg spectate <arena>")).tooltip(StringUtils.colorize("&6Spectate an arena.")).suggest("/sg spectate <arena>").send(sender);
				}
				if (p.hasPermission("survivalgames.forcestart")) {
					new FancyMessage(StringUtils.colorize("&e/sg forcestart <arena>")).tooltip(StringUtils.colorize("&6Force an arena to start.")).suggest("/sg forcestart <arena>").send(sender);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("addarena") || args[0].equalsIgnoreCase("create")) {
				return addArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("removearena") || args[0].equalsIgnoreCase("delarena") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
				return remArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("editarena") || args[0].equalsIgnoreCase("edit")) {
				return editArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("listarenas") || args[0].equalsIgnoreCase("list")) {
				if (!sender.hasPermission("survivalgames.listarenas")) {
					sender.sendMessage(Constants.NO_PERMS_MSG);
					return true;
				}
				StringBuilder sb = new StringBuilder();
				sb.append("&6Arenas: ");
				int count = 0;
				for (Arena a : ArenaManager.getAllArenas()) {
					count++;
					if (count < ArenaManager.getAllArenas().size()) {
						if (a.isEnabled()) {
							sb.append("&a" + a.getName() + "&6, ");
							continue;
						}
						sb.append("&c" + a.getName() + "&6, ");
					} else {
						if (a.isEnabled()) {
							sb.append("&a" + a.getName());
							continue;
						}
						sb.append("&c" + a.getName());
					}
				}
				if (count <= 0) {
					sb.append("&cNone");
				}
				sender.sendMessage(StringUtils.colorize(sb.toString()));
			} else if (args[0].equalsIgnoreCase("findchest") || args[0].equalsIgnoreCase("fc")) {
				return findChest.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("addchest") || args[0].equalsIgnoreCase("ac")) {
				return addChest.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("removechest") || args[0].equalsIgnoreCase("rc")) {
				return remChest.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("join")) {
				return joinArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("leave")) {
				return leaveArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("addspawn")) {
				return addSpawn.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("stats")) {
				return stats.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("top")) {
				return leaderBoardArg.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("override")) {
				return adminToggle.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("reload")) {
				return sgReload.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("%^&rate%^&")) {
				return rateArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("spectate") || args[0].equalsIgnoreCase("spec")) {
				return specArena.onCommand(sender, cmd, label, args);
			} else if (args[0].equalsIgnoreCase("forcestart") || args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("fs")) {
				return forceStart.onCommand(sender, cmd, label, args);
			} else {
				sender.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cUnknown argument \"" + args[0] + "\"!"));
			}
		}
		return true;
	}

}
