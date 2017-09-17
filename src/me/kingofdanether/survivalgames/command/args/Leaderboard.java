package me.kingofdanether.survivalgames.command.args;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.FileUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class Leaderboard implements CommandExecutor {
	
	private static Leaderboard instance;
	
	public static Leaderboard getInstance() {
		if (instance == null) {
			return new Leaderboard();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.leaderboard")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			final SGPlayer sgPlayer = PlayerManager.getOrCreate(p.getPlayerListName());
			if (args.length == 1) {
				sgPlayer.sendMessage(Constants.PREFIX +  " &aPlease specify a category!: Kills, Deaths, KD, Wins, Clout");
				return true;
			} else {
				if (args[1].equalsIgnoreCase("kills") || args[1].startsWith("kills:")) {
					HashMap<String, Integer> values = new HashMap<String, Integer>();
					if (args[1].contains(":")) {
						String[] array = args[1].split(":");
						String style = array.length >= 2 ? array[1] : null;
						if (style == null) {
							for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
								if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
								String uuid = f.getName().replace(".yml", "");
								YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
								values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".kills") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".kills"));
							}
							sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Killers: ");
							this.getLeaderboardPlayers(values, sgPlayer, "kills");
							return true;
						}
						for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
							if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
							String uuid = f.getName().replace(".yml", "");
							YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
							values.put(player.getName(), data.getInt("stats." + array[1].toLowerCase() + ".kills"));
						}
						sgPlayer.sendMessage(Constants.PREFIX + " &a" + StringUtils.capitalize(array[1]) + " Top Killers: ");
						this.getLeaderboardPlayers(values, sgPlayer, "kills");
						return true;
					}
					for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
						if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
						String uuid = f.getName().replace(".yml", "");
						YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
						OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
						values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".kills") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".kills"));
					}
					sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Killers: ");
					this.getLeaderboardPlayers(values, sgPlayer, "kills");
					return true;
				} else if (args[1].equalsIgnoreCase("deaths") || args[1].startsWith("deaths:")) {
					HashMap<String, Integer> values = new HashMap<String, Integer>();
					if (args[1].contains(":")) {
						String[] array = args[1].split(":");
						String style = array.length >= 2 ? array[1] : null;
						if (style == null) {
							for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
								if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
								String uuid = f.getName().replace(".yml", "");
								YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
								values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".deaths") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".deaths"));
							}
							sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Deaths: ");
							this.getLeaderboardPlayers(values, sgPlayer, "deaths");
							return true;
						}
						for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
							if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
							String uuid = f.getName().replace(".yml", "");
							YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
							values.put(player.getName(), data.getInt("stats." + array[1].toLowerCase() + ".deaths"));
						}
						sgPlayer.sendMessage(Constants.PREFIX + " &a" + StringUtils.capitalize(array[1]) + " Top Deaths: ");
						this.getLeaderboardPlayers(values, sgPlayer, "deaths");
						return true;
					}
					for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
						if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
						String uuid = f.getName().replace(".yml", "");
						YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
						OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
						values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".deaths") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".deaths"));
					}
					sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Deaths: ");
					this.getLeaderboardPlayers(values, sgPlayer, "deaths");
					return true;
				} else if (args[1].equalsIgnoreCase("kd") || args[1].equalsIgnoreCase("k/d") || args[1].startsWith("kd:") || args[1].startsWith("k/d:")) {
					HashMap<String, Double> values = new HashMap<String, Double>();
					if (args[1].contains(":")) {
						String[] array = args[1].split(":");
						String style = array.length >= 2 ? array[1] : null;
						if (style == null) {
							for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
								if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
								String uuid = f.getName().replace(".yml", "");
								YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
								double kills = data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".kills") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".kills");
								double deaths = data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".deaths") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".deaths");
								values.put(player.getName(), deaths <= 0 ? kills : NumberUtils.nearestHundreth(kills/deaths));
							}
							sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top K/D: ");
							this.getTopKD(values, sgPlayer);
							return true;
						}
						for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
							if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
							String uuid = f.getName().replace(".yml", "");
							YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
							double kills = data.getInt("stats." + array[1].toLowerCase() + ".kills") + data.getInt("stats." + array[1].toLowerCase() + ".kills");
							double deaths = data.getInt("stats." + array[1].toLowerCase() + ".deaths") + data.getInt("stats." + array[1].toLowerCase() + ".deaths");
							values.put(player.getName(), deaths <= 0 ? kills : NumberUtils.nearestHundreth(kills/deaths));
						}
						sgPlayer.sendMessage(Constants.PREFIX + " &a" + StringUtils.capitalize(array[1]) + " Top K/D: ");
						this.getTopKD(values, sgPlayer);
						return true;
					}
					for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
						if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
						String uuid = f.getName().replace(".yml", "");
						YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
						OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
						double kills = data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".kills") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".kills");
						double deaths = data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".deaths") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".deaths");
						values.put(player.getName(), deaths <= 0 ? kills : NumberUtils.nearestHundreth(kills/deaths));
					}
					sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top K/D: ");
					this.getTopKD(values, sgPlayer);
					return true;
				} else if (args[1].equalsIgnoreCase("wins") || args[1].startsWith("wins:")) {
					HashMap<String, Integer> values = new HashMap<String, Integer>();
					if (args[1].contains(":")) {
						String[] array = args[1].split(":");
						String style = array.length >= 2 ? array[1] : null;
						if (style == null) {
							for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
								if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
								String uuid = f.getName().replace(".yml", "");
								YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
								values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".wins") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".wins"));
							}
							sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Winners: ");
							this.getLeaderboardPlayers(values, sgPlayer, "wins");
							return true;
						}
						for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
							if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
							String uuid = f.getName().replace(".yml", "");
							YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
							values.put(player.getName(), data.getInt("stats." + array[1].toLowerCase() + ".wins"));
						}
						sgPlayer.sendMessage(Constants.PREFIX + " &a" + StringUtils.capitalize(array[1]) + " Top Winners: ");
						this.getLeaderboardPlayers(values, sgPlayer, "wins");
						return true;
					}
					for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
						if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
						String uuid = f.getName().replace(".yml", "");
						YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
						OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
						values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".wins") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".wins"));
					}
					sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Winners: ");
					this.getLeaderboardPlayers(values, sgPlayer, "wins");
					return true;
				} else if (args[1].equalsIgnoreCase("clout") || args[1].equalsIgnoreCase("clout:")) {
					HashMap<String, Integer> values = new HashMap<String, Integer>();
					if (args[1].contains(":")) {
						String[] array = args[1].split(":");
						String style = array.length >= 2 ? array[1] : null;
						if (style == null) {
							for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
								if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
								String uuid = f.getName().replace(".yml", "");
								YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
								OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
								values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".clout") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".clout"));
							}
							sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Clout: ");
							this.getLeaderboardPlayers(values, sgPlayer, "clout");
							return true;
						}
						for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
							if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
							String uuid = f.getName().replace(".yml", "");
							YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
							OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
							values.put(player.getName(), data.getInt("stats." + array[1].toLowerCase() + ".clout"));
						}
						sgPlayer.sendMessage(Constants.PREFIX + " &a" + StringUtils.capitalize(array[1]) + " Top Clout: ");
						this.getLeaderboardPlayers(values, sgPlayer, "clout");
						return true;
					}
					for (File f : FileUtils.getAndCreateFolder(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata").listFiles()) {
						if (f == null || f.isDirectory() || !f.getName().endsWith(".yml")) continue;
						String uuid = f.getName().replace(".yml", "");
						YamlConfiguration data = YamlConfiguration.loadConfiguration(f);
						OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
						values.put(player.getName(), data.getInt("stats." + GameStyle.CLASSIC.toString().toLowerCase() + ".clout") + data.getInt("stats." + GameStyle.ZDUBY.toString().toLowerCase() + ".clout"));
					}
					sgPlayer.sendMessage(Constants.PREFIX + " &aGlobal Top Clout: ");
					this.getLeaderboardPlayers(values, sgPlayer, "clout");
					return true;
				} else {
					sgPlayer.sendMessage(Constants.PREFIX + " &cUnknown category \"" + args[1] + "\"!");
				}
				return true;
			}
		}
		return true;
	}
	
	private void getLeaderboardPlayers(HashMap<String, Integer> values, SGPlayer player, String category) {
		String top = "";
		int num = 0;
		for (int i = 1; i <= SurvivalGames.getInstance().getConfig().getInt("leaderboard.amount"); i++) {
			for (Entry<String, Integer> temp : values.entrySet()) {
				if (temp.getValue() > num) {
					top = temp.getKey();
					num = temp.getValue();
				}
			}
			if (num > 0) {
				player.sendMessage("&a(" + i + ") &6" + top + " &7" + Constants.RIGHT_ARROW + " &a" + num + " " + String.valueOf(num > 1 ? category : category.replace("s", "")));
			} else {
				player.sendMessage("&a(" + i + ") &6" + top);
			}
			values.remove(top);
			top = StringUtils.colorize("&aNo leaderboards exist for this rank yet.");
			num = 0;
		}
	}
	
	private void getTopKD(HashMap<String, Double> values, SGPlayer player) {
		String top = "";
		double num = 0;
		for (int i = 1; i <= SurvivalGames.getInstance().getConfig().getInt("leaderboard.amount"); i++) {
			for (Entry<String, Double> temp : values.entrySet()) {
				if (temp.getValue() > num) {
					top = temp.getKey();
					num = temp.getValue();
				}
			}
			if (num > 0) {
				player.sendMessage("&a(" + i + ") &6" + top + " &7" + Constants.RIGHT_ARROW + " &a" + NumberUtils.nearestHundreth(num) + " k/d");
			} else {
				player.sendMessage("&a(" + i + ") &6" + top);
			}
			values.remove(top);
			top = StringUtils.colorize("&aNo leaderboards exist for this rank yet.");
			num = 0;
		}
	}
	
}
