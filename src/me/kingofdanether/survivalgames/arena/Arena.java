package me.kingofdanether.survivalgames.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.enumeration.GameEvent;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.enumeration.RandomType;
import me.kingofdanether.survivalgames.enumeration.Rating;
import me.kingofdanether.survivalgames.item.LeaveGameItem;
import me.kingofdanether.survivalgames.item.SpectatorCompass;
import me.kingofdanether.survivalgames.item.SponsorItem;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.runnables.InGameCountdownTask;
import me.kingofdanether.survivalgames.runnables.LobbyCountdownTask;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;

public class Arena {

	private File data;
	
	private YamlConfiguration dataYml;
	
	private String name;
	
	private int maxPlayers;
	private int minPlayers;
	
	private Location corner1;
	private Location corner2;
	private Location lobby;
	private Location center;
	
	private GameState gameState;
	
	private RandomType teleportType;
	private RandomType supplyDropType;
	
	private GameStyle gameStyle;
	
	private ArrayList<Location> spawns;
	private ArrayList<Location> supplyDrops;
	private ArrayList<SGChest> chests;
	private ArrayList<SGPlayer> players;
	private ArrayList<BlockState> blocksToRemove;
	private ArrayList<BlockState> blocksToRebuild;
	private ArrayList<SponsorItem> sponsorItems;
	
	private ArrayList<Integer> entityIds;
	
	private HashMap<Location, SGPlayer> mineLocations;
	
	private boolean enabled;
	private boolean crossWorld;
	private boolean speedEffect;
	
	private LeaveGameItem leaveItem;
	private SpectatorCompass spectatorCompass;
	
	private GameEvent nextGameEvent;
	
	private int chestRefillTime;
	private int supplyDropTime;
	private int borderTime;
	private int deathMatchTime;
	
	private int borderDamageBuffer;
	private int borderDecreaseTime;
	
	private int minWaitTime;
	private int maxWaitTime;
	
	private double borderSize;
	private double borderDamage;
	private double borderDecrease;
	private int borderSpeed;
	private double minBorderSize;
	private double cloutMultiplier;
	private double cloutPerWin;
	private double attackSpeed;
	
	private int speedDuration;
	private int maxCompassUses;
	
	private int timeOfDay;
	
	private boolean dataLoaded;
	
	private Scoreboard scoreboard;
	private Objective obj;
	
	private Team playersLeftCount;
	private Team nextEvent;
	private Team timer;
	private Team gamemode;
	private Team rating;
	
	private LobbyCountdownTask lastLobbyTask;
	private InGameCountdownTask lastGameTask;
	
	public Arena(File data) {
		this.data = data;
		this.dataYml = YamlConfiguration.loadConfiguration(data);
		this.name = dataYml.get("name") == null ? data.getName().replace(".arena", "") : dataYml.getString("name");
		this.maxPlayers = dataYml.get("max-players") == null ? 0 : dataYml.getInt("max-players");
		this.minPlayers = dataYml.get("min-players") == null ? 0 : dataYml.getInt("min-players");
		this.corner1 = dataYml.get("corner-1") == null ? null : LocationUtils.parseLocation(dataYml.getString("corner-1"), ";").add(0.5,0,0.5);
		this.corner2 = dataYml.get("corner-2") == null ? null : LocationUtils.parseLocation(dataYml.getString("corner-2"), ";").add(0.5,0,0.5);
		this.lobby = dataYml.get("lobby") == null ? null : LocationUtils.parseLocation(dataYml.getString("lobby"), ";").add(0.5,0,0.5);
		this.center = dataYml.get("center") == null ? null : LocationUtils.parseLocation(dataYml.getString("center"), ";").add(0.5,0,0.5);
		this.gameState = GameState.LOBBY_WAITING;
		this.spawns = new ArrayList<Location>();
		this.enabled = dataYml.get("enabled") == null ? false : dataYml.getBoolean("enabled");
		this.crossWorld = dataYml.get("cross-world") == null ? false : dataYml.getBoolean("cross-world");
		this.teleportType = dataYml.get("teleport-type") == null ? RandomType.WORLD_RANDOM : RandomType.valueOf(dataYml.getString("teleport-type").toUpperCase());
		this.supplyDropType = dataYml.get("supply-drop-type") == null ? RandomType.WORLD_RANDOM : RandomType.valueOf(dataYml.getString("supply-drop-type").toUpperCase());
		this.gameStyle = dataYml.get("game-style") == null ? GameStyle.CLASSIC : GameStyle.valueOf(dataYml.getString("game-style").toUpperCase());
		if (dataYml.get("teleport-type") != null) {
			if ((this.getGameStyle() == GameStyle.CLASSIC) && this.getTpType() != RandomType.LIST_RANDOM) {
				this.setTeleportType(RandomType.LIST_RANDOM);
			} else {
				this.setTeleportType(RandomType.valueOf(dataYml.getString("teleport-type").toUpperCase()));
			}
		} else if (this.getGameStyle() == GameStyle.CLASSIC) {
			this.setTeleportType(RandomType.LIST_RANDOM);
		}
		
		if (dataYml.get("spawn-points") != null) {
			for (String loc : dataYml.getStringList("spawn-points")) {
				spawns.add(LocationUtils.parseLocation(loc, ";").add(0.5,0,0.5));
			}
		}
		this.supplyDrops = new ArrayList<Location>();
		if (dataYml.get("supply-drops") != null) {
			for (String loc : dataYml.getStringList("supply-drops")) {
				supplyDrops.add(LocationUtils.parseLocation(loc, ";"));
			}
		}
		this.mineLocations = new HashMap<Location, SGPlayer>();
		this.players = new ArrayList<SGPlayer>();
		this.blocksToRemove = new ArrayList<BlockState>();
		this.blocksToRebuild = new ArrayList<BlockState>();
		this.sponsorItems = this.loadSponsorItems();
		this.entityIds = new ArrayList<Integer>();
		this.leaveItem = new LeaveGameItem(this);
		this.spectatorCompass = new SpectatorCompass(this);
		this.nextGameEvent = GameEvent.CHEST_REFILL;
		this.chestRefillTime = dataYml.get("seconds-to-chest-refill") == null ? 300 : dataYml.getInt("seconds-to-chest-refill");
		this.supplyDropTime =  dataYml.get("seconds-to-supply-drop") == null ? 330 : dataYml.getInt("seconds-to-supply-drop");
		this.borderTime = dataYml.get("seconds-to-border-spawn") == null ? 400 : dataYml.getInt("seconds-to-border-spawn");
		this.borderSize = dataYml.get("border-size") == null ? 400 : dataYml.getDouble("border-size");
		this.borderDamage = dataYml.get("border-damage") == null ? 2 : dataYml.getDouble("border-damage");
		this.borderDecrease = dataYml.get("border-decrease") == null ? 5 : dataYml.getDouble("border-decrease");
		this.borderDamageBuffer = dataYml.get("border-damage-buffer") == null ? 1 : dataYml.getInt("border-damage-buffer");
		this.minBorderSize = dataYml.get("min-border-size") == null ? 45 : dataYml.getInt("min-border-size");
		this.cloutMultiplier = dataYml.get("clout-multiplier") == null ? 2.5 : dataYml.getDouble("clout-multiplier");
		this.cloutPerWin = dataYml.get("clout-per-win") == null ? 25 : dataYml.getDouble("clout-per-win");
		this.attackSpeed = dataYml.get("attack-speed") == null ? 4.0D : dataYml.getDouble("attack-speed");
		this.borderDecreaseTime = dataYml.get("border-decrease-time") == null ? 3 : dataYml.getInt("border-decrease-time");
		this.borderSpeed = dataYml.get("border-speed") == null ? 1 : dataYml.getInt("border-speed");
		this.minWaitTime = dataYml.get("min-wait-time") == null ? 120 : dataYml.getInt("min-wait-time");
		this.maxWaitTime = dataYml.get("max-wait-time") == null ? 240 : dataYml.getInt("max-wait-time");
		this.speedDuration = dataYml.get("speed-duration") == null ? 25 : dataYml.getInt("speed-duration");
		this.maxCompassUses = dataYml.get("max-compass-uses") == null ? 4 : dataYml.getInt("max-compass-uses");
		this.timeOfDay = dataYml.get("time-of-day") == null ? 0 : dataYml.getInt("time-of-day");
		this.chests = new ArrayList<SGChest>();
		if (dataYml.get("chests") != null) {
			int count = 0;
			for (String loc : dataYml.getStringList("chests")) {
				try {
					Block b = LocationUtils.parseLocation(loc, ";").getBlock();
					if (b.getState() instanceof Chest) {
						chests.add(this.parseChest(loc, ";"));
					}
				} catch (Exception ex) {
					SurvivalGames.getInstance().getLogger().warning("Could not load chest "  + count + " in arena " + this.getName() + "!");
				}
				count++;
			}
		}
		
		//setup scoreboard
		this.initScoreboard();
		if (this.getMinPlayers() <= 0) this.setMinPlayers(2);
		if (this.getMaxPlayers() <= 0) this.setMaxPlayers(3);
		//ArenaManager.addArena(this);
	}
	
	
	public void initScoreboard() {
		//1// TITLE: NCC SG
		//2// EMPTY
		//3// Players Left: 
		//4// <amount>
		//5// EMPTY
		//6// Next Event:
		//7// <timer>
		//8// EMPTY
		//9// Gamemode:
		//10// <gamemode>
		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.obj = scoreboard.registerNewObjective(this.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(StringUtils.colorize(this.getData().get("scoreboard-name") != null ? this.getData().getString("scoreboard-name") : "&6&lNCC SG"));
		Score playersLeft = obj.getScore(StringUtils.colorize("&6" + Constants.RIGHT_ARROW + " &aPlayers Left &6" + Constants.LEFT_ARROW));
		playersLeft.setScore(10);
		this.playersLeftCount = scoreboard.registerNewTeam("playersLeft");
		playersLeftCount.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE + "");
		this.setLine(playersLeftCount, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " " + this.getPlayersAliveAmount() + " players"));
		obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE + "").setScore(9);
		obj.getScore("  ").setScore(8);
		this.nextEvent = scoreboard.registerNewTeam("nextEvent");
		nextEvent.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE + "");
		this.setLine(nextEvent, StringUtils.colorize("&6" + Constants.RIGHT_ARROW + " &aNext Event &6" + Constants.LEFT_ARROW));
		obj.getScore(ChatColor.BLUE + "" + ChatColor.WHITE + "").setScore(7);
		this.timer = scoreboard.registerNewTeam("eventTimer");
		timer.addEntry(ChatColor.DARK_AQUA + "" + ChatColor.WHITE + "");
		this.setLine(timer, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6????"));
		obj.getScore(ChatColor.DARK_AQUA + "" + ChatColor.WHITE + "").setScore(6);
		obj.getScore("    ").setScore(5);
		Score gamemode = obj.getScore(StringUtils.colorize("&6" + Constants.RIGHT_ARROW + " &aGame Style &6" + Constants.LEFT_ARROW));
		gamemode.setScore(4);
		this.gamemode = scoreboard.registerNewTeam("gamemode");
		this.gamemode.addEntry(ChatColor.DARK_BLUE + "" + ChatColor.WHITE + "");
		this.setLine(this.gamemode, this.getGameStyle() == GameStyle.CLASSIC ? StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6Classic") : StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6Zduby"));
		obj.getScore(ChatColor.DARK_BLUE + "" + ChatColor.WHITE + "").setScore(3);
		obj.getScore("     ").setScore(2);
		Score ratingScore = obj.getScore(StringUtils.colorize("&6" + Constants.RIGHT_ARROW + " &aAverage Rating"));
		ratingScore.setScore(1);
		this.rating = scoreboard.registerNewTeam("rating");
		this.rating.addEntry(ChatColor.DARK_GRAY + "" + ChatColor.WHITE + "");
		if (this.getAverageRating() <= 0.0D) {
			this.setLine(rating, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " " + this.getAverageRatingString()));
		} else {
			this.setLine(rating, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " " + this.getAverageRatingString() + " &f(" + this.getAverageRating() + "/5)"));
		}
		obj.getScore(ChatColor.DARK_GRAY + "" + ChatColor.WHITE + "").setScore(0);
	}
	
	private void setLine(Team team, String s) {
		if (s.length() < 16) {
			team.setPrefix(s);
			team.setSuffix("");
		} else {
			String pre;
			String suf;
			if (s.lastIndexOf(ChatColor.COLOR_CHAR) == 15) {
				pre = s.substring(0, 15);
				suf = s.substring(15, Math.min(30, s.length()));
			} else {
				pre = s.substring(0,16);
				suf = ChatColor.getLastColors(pre) + s.substring(16, Math.min(30, s.length()));
			}
			team.setPrefix(pre);
			team.setSuffix(suf);
		}
	}
	
	public void setNextEventSuffix(String s) {
		this.setLine(nextEvent, StringUtils.colorize( "&6" + Constants.RIGHT_ARROW + " &a" + s + " &6" + Constants.LEFT_ARROW));
	}
	
	public void setTimerSuffix(String s) {
		this.setLine(timer, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6" + s));
	}
	
	public void setGameStyleSuffix(String s) {
		this.setLine(gamemode, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6" + s));
	}
	
	public void setPlayersLeftSuffix(String s) {
		this.setLine(playersLeftCount, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " &6" + s));
	}
	
	public void setRatingSuffix(String s) {
		this.setLine(rating, StringUtils.colorize("&7" + Constants.RIGHT_ARROW + " " + s));
	}
	
	public void saveData() {
		try {
			dataYml.save(data);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void reloadData() {
		this.dataYml = YamlConfiguration.loadConfiguration(data);
		this.name = dataYml.get("name") == null ? data.getName() : dataYml.getString("name");
		this.maxPlayers = dataYml.get("max-players") == null ? 0 : dataYml.getInt("max-players");
		this.minPlayers = dataYml.get("min-players") == null ? 0 : dataYml.getInt("min-players");
		this.corner1 = dataYml.get("corner-1") == null ? null : LocationUtils.parseLocation(dataYml.getString("corner-1"), ";").add(0.5,0,0.5);
		this.corner2 = dataYml.get("corner-2") == null ? null : LocationUtils.parseLocation(dataYml.getString("corner-2"), ";").add(0.5,0,0.5);
		this.lobby = dataYml.get("lobby") == null ? null : LocationUtils.parseLocation(dataYml.getString("lobby"), ";").add(0.5,0,0.5);
		this.center = dataYml.get("center") == null ? null : LocationUtils.parseLocation(dataYml.getString("center"), ";").add(0.5,0,0.5);
		this.gameState = GameState.LOBBY_WAITING;
		this.spawns = new ArrayList<Location>();
		this.enabled = dataYml.get("enabled") == null ? false : dataYml.getBoolean("enabled");
		this.crossWorld = dataYml.get("cross-world") == null ? false : dataYml.getBoolean("cross-world");
		this.teleportType = dataYml.get("teleport-type") == null ? RandomType.WORLD_RANDOM : RandomType.valueOf(dataYml.getString("teleport-type").toUpperCase());
		this.supplyDropType = dataYml.get("supply-drop-type") == null ? RandomType.WORLD_RANDOM : RandomType.valueOf(dataYml.getString("supply-drop-type").toUpperCase());
		this.gameStyle = dataYml.get("game-style") == null ? GameStyle.CLASSIC : GameStyle.valueOf(dataYml.getString("game-style").toUpperCase());
		if (dataYml.get("teleport-type") != null) {
			if ((this.getGameStyle() == GameStyle.CLASSIC) && this.getTpType() != RandomType.LIST_RANDOM) {
				this.setTeleportType(RandomType.LIST_RANDOM);
			} else {
				this.setTeleportType(RandomType.valueOf(dataYml.getString("teleport-type").toUpperCase()));
			}
		} else if (this.getGameStyle() == GameStyle.CLASSIC) {
			this.setTeleportType(RandomType.LIST_RANDOM);
		}
		
		if (dataYml.get("spawn-points") != null) {
			for (String loc : dataYml.getStringList("spawn-points")) {
				spawns.add(LocationUtils.parseLocation(loc, ";").add(0.5,0,0.5));
			}
		}
		this.supplyDrops = new ArrayList<Location>();
		if (dataYml.get("supply-drops") != null) {
			for (String loc : dataYml.getStringList("supply-drops")) {
				supplyDrops.add(LocationUtils.parseLocation(loc, ";"));
			}
		}
		this.mineLocations = new HashMap<Location, SGPlayer>();
		this.players = new ArrayList<SGPlayer>();
		this.blocksToRemove = new ArrayList<BlockState>();
		this.blocksToRebuild = new ArrayList<BlockState>();
		this.sponsorItems = this.loadSponsorItems();
		this.entityIds = new ArrayList<Integer>();
		this.leaveItem = new LeaveGameItem(this);
		this.spectatorCompass = new SpectatorCompass(this);
		this.nextGameEvent = GameEvent.CHEST_REFILL;
		this.chestRefillTime = dataYml.get("seconds-to-chest-refill") == null ? 300 : dataYml.getInt("seconds-to-chest-refill");
		this.supplyDropTime =  dataYml.get("seconds-to-supply-drop") == null ? 330 : dataYml.getInt("seconds-to-supply-drop");
		this.borderTime = dataYml.get("seconds-to-border-spawn") == null ? 400 : dataYml.getInt("seconds-to-border-spawn");
		this.borderSize = dataYml.get("border-size") == null ? 400 : dataYml.getDouble("border-size");
		this.borderDamage = dataYml.get("border-damage") == null ? 2 : dataYml.getDouble("border-damage");
		this.borderDecrease = dataYml.get("border-decrease") == null ? 5 : dataYml.getDouble("border-decrease");
		this.borderDamageBuffer = dataYml.get("border-damage-buffer") == null ? 1 : dataYml.getInt("border-damage-buffer");
		this.minBorderSize = dataYml.get("min-border-size") == null ? 45 : dataYml.getInt("min-border-size");
		this.cloutMultiplier = dataYml.get("clout-multiplier") == null ? 2.5 : dataYml.getDouble("clout-multiplier");
		this.cloutPerWin = dataYml.get("clout-per-win") == null ? 25 : dataYml.getDouble("clout-per-win");
		this.attackSpeed = dataYml.get("attack-speed") == null ? 4.0D : dataYml.getDouble("attack-speed");
		this.borderDecreaseTime = dataYml.get("border-decrease-time") == null ? 3 : dataYml.getInt("border-decrease-time");
		this.borderSpeed = dataYml.get("border-speed") == null ? 1 : dataYml.getInt("border-speed");
		this.minWaitTime = dataYml.get("min-wait-time") == null ? 120 : dataYml.getInt("min-wait-time");
		this.maxWaitTime = dataYml.get("max-wait-time") == null ? 240 : dataYml.getInt("max-wait-time");
		this.speedDuration = dataYml.get("speed-duration") == null ? 25 : dataYml.getInt("speed-duration");
		this.maxCompassUses = dataYml.get("max-compass-uses") == null ? 4 : dataYml.getInt("max-compass-uses");
		this.timeOfDay = dataYml.get("time-of-day") == null ? 0 : dataYml.getInt("time-of-day");
		this.chests = new ArrayList<SGChest>();
		if (dataYml.get("chests") != null) {
			int count = 0;
			for (String loc : dataYml.getStringList("chests")) {
				try {
					Block b = LocationUtils.parseLocation(loc, ";").getBlock();
					if (b.getState() instanceof Chest) {
						chests.add(this.parseChest(loc, ";"));
					}
				} catch (Exception ex) {
					SurvivalGames.getInstance().getLogger().warning("Could not load chest "  + count + " in arena " + this.getName() + "!");
				}
				count++;
			}
		}
		
		//setup scoreboard
		this.initScoreboard();
		if (this.getMinPlayers() <= 0) this.setMinPlayers(2);
		if (this.getMaxPlayers() <= 0) this.setMaxPlayers(3);
		//ArenaManager.addArena(this);
	}
	
	public void setName(String name) {
		this.name = name;
		dataYml.set("name", name);
		this.saveData();
	}
	
	public void setMinPlayers(int min) {
		this.minPlayers = min;
		dataYml.set("min-players", min);
		this.saveData();
	}
	
	public void setMaxPlayers(int max) {
		this.maxPlayers = max;
		dataYml.set("max-players", max);
		this.saveData();
	}
	
	public void setCorner1(Location l) {
		this.corner1 = l;
		dataYml.set("corner-1", LocationUtils.locToSavableString(l, ";"));
		this.saveData();
	}
	
	public void setCorner2(Location l) {
		this.corner2 = l;
		dataYml.set("corner-2", LocationUtils.locToSavableString(l, ";"));
		this.saveData();
	}
	
	public void setLobby(Location l) {
		this.lobby = l;
		dataYml.set("lobby", LocationUtils.locToSavableString(l, ";"));
		this.saveData();
	}
	
	public void setCenter(Location l) {
		this.center = l;
		dataYml.set("center", LocationUtils.locToSavableString(l, ";"));
		this.saveData();
	}
	
	public void setGameState(GameState state) {
		this.gameState = state;
	}
	
	public void setTeleportType(RandomType type) {
		this.teleportType = type;
		dataYml.set("teleport-type", type.toString().toUpperCase());
		this.saveData();
	}
	
	public void setSupplyDropType(RandomType type) {
		this.supplyDropType = type;
		dataYml.set("supply-drop-type", type.toString().toUpperCase());
		this.saveData();
	}
	
	public void setGameStyle(GameStyle style) {
		this.gameStyle = style;
		dataYml.set("game-style", style.toString().toUpperCase());
		this.saveData();
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		dataYml.set("enabled", enabled);
		this.saveData();
	}
	
	public void setCrossWorld(boolean crossWorld) {
		this.crossWorld = crossWorld;
		dataYml.set("cross-world", crossWorld);
		this.saveData();
	}
	
	public void addMine(Location l, SGPlayer player) {
		mineLocations.put(l, player);
	}
	
	public void removeMine(Location l) {
		mineLocations.remove(l);
	}
	
	public boolean hasMine(Location l) {
		for (Entry<Location, SGPlayer> entry : mineLocations.entrySet()) {
			if (LocationUtils.locEqualsLoc(l, entry.getKey())) {
				return true;
			}
		}
		return false;
	}
	
	public SGPlayer getMine(Location l) {
		for (Entry<Location, SGPlayer> entry : mineLocations.entrySet()) {
			if (LocationUtils.locEqualsLoc(l, entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public void addSpawnPoint(Location l) {
		this.getSpawnPoints().add(l);
		List<String> spawns = dataYml.getStringList("spawn-points");
		spawns.add(LocationUtils.locToSavableString(l, ";"));
		dataYml.set("spawn-points", spawns);
		this.saveData();
	}
	
	public void removeSpawnPoint(String loc) {
		Location loc1 = LocationUtils.parseLocation(loc, ",");
		Iterator<Location> iterator = spawns.iterator();
		while (iterator.hasNext()) {
			Location l = iterator.next();
			if (l.getWorld().getName().equals(loc1.getWorld().getName()) && l.getBlockX() == loc1.getBlockX() && 
					l.getBlockY() == loc1.getBlockY() && l.getBlockZ() == loc1.getBlockZ()) {
				iterator.remove();
			}
		}
		List<String> ymlList = dataYml.getStringList("spawn-points");
		Iterator<String> iter = ymlList.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.startsWith(loc.replace(",", ";").replace(" ", ""))) {
				iter.remove();
			}
		}
		dataYml.set("spawn-points", ymlList);
		this.saveData();
	}
	
	public boolean hasSpawnpoint(Location l) {
		for (Location loc : this.getSpawnPoints()) {
			if (l.getWorld().getName().equals(loc.getWorld().getName()) && l.getBlockX() == loc.getBlockX() && 
					l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
				return true;
			}
		}
		return false;
	}
	
	public void setSpeedEffectActive(boolean speedEffect) {
		this.speedEffect = speedEffect;
	}
	
	public void addChest(Location l) {
		SGChest chest = new SGChest((Chest)l.getBlock().getState(), this);
		this.getChests().add(chest);
		List<String> chests = dataYml.getStringList("chests");
		chests.add(LocationUtils.chestToString(chest, chest.getTier(), ";"));
		dataYml.set("chests", chests);
		this.saveData();
	}
	
	public void addChest(SGChest chest) {
		this.getChests().add(chest);
		List<String> chests = dataYml.getStringList("chests");
		chests.add(LocationUtils.chestToString(chest, chest.getTier(), ";"));
		dataYml.set("chests", chests);
		this.saveData();
	}
	
	public void addChestAsync(Location l) {
		this.getChests().add(new SGChest((Chest)l.getBlock().getState(), this));
		List<String> chests = dataYml.getStringList("chests");
		chests.add(LocationUtils.locToSavableString(l, ";"));
		dataYml.set("chests", chests);
		this.saveData();
	}
	
	public void removeChest(String loc) {
		Location loc1 = LocationUtils.parseLocation(loc, ",");
		Iterator<SGChest> iterator = chests.iterator();
		while (iterator.hasNext()) {
			SGChest sgChest = iterator.next();
			Location l = sgChest.getChest().getLocation();
			if (l.getWorld().getName().equals(loc1.getWorld().getName()) && l.getBlockX() == loc1.getBlockX() && 
					l.getBlockY() == loc1.getBlockY() && l.getBlockZ() == loc1.getBlockZ()) {
				iterator.remove();
			}
		}
		List<String> ymlList = dataYml.getStringList("chests");
		Iterator<String> iter = ymlList.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.startsWith(loc.replace(",", ";").replace(" ", ""))) {
				iter.remove();
			}
		}
		dataYml.set("chests", ymlList);
		this.saveData();
	}
	
	public boolean hasChestAt(Location l) {
		for (SGChest chest : this.getChests()) {
			Location loc = chest.getChest().getLocation();
			if (l.getWorld().getName().equals(loc.getWorld().getName()) && l.getBlockX() == loc.getBlockX() && 
					l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
				return true;
			}
		}
		return false;
	}
	
	public SGChest getChest(Location l) {
		for (SGChest chest : this.getChests()) {
			Location loc = chest.getChest().getLocation();
			if (l.getWorld().getName().equals(loc.getWorld().getName()) && l.getBlockX() == loc.getBlockX() && 
					l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
				return chest;
			}
		}
		return null;
	}
	
	public boolean asyncHasChestAt(Location l) {
		Iterator<SGChest> iter = this.getChests().iterator();
		while (iter.hasNext()) {
			SGChest chest = iter.next();
			Location loc = chest.getChest().getLocation();
			if (l.getWorld().getName().equals(loc.getWorld().getName()) && l.getBlockX() == loc.getBlockX() && 
					l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
				return true;
			}
		}
		return false;
	}
	
	public void addPlayer(SGPlayer player) {
		players.add(player);	
	}
	
	public void removePlayer(SGPlayer player) {
		player.getBukkitPlayer().setScoreboard(Constants.BLANK_SCOREBOARD);
		players.remove(player);
	}
	
	public boolean hasPlayer(SGPlayer player) {
		return players.contains(player);
	}
	
	public void addSupplyDropLoc(Location l) {
		this.getSupplyDropLocs().add(l);
		List<String> supplyDropLocs = dataYml.getStringList("supply-drops");
		supplyDropLocs.add(LocationUtils.locToSavableString(l, ";"));
		dataYml.set("supply-drops", supplyDropLocs);
		this.saveData();
	}
	
	public void removeSupplyDropLoc(String loc) {
		Location loc1 = LocationUtils.parseLocation(loc, ",");
		Iterator<Location> iterator = supplyDrops.iterator();
		while (iterator.hasNext()) {
			Location l = iterator.next();
			if (l.getWorld().getName().equals(loc1.getWorld().getName()) && l.getBlockX() == loc1.getBlockX() && 
					l.getBlockY() == loc1.getBlockY() && l.getBlockZ() == loc1.getBlockZ()) {
				iterator.remove();
			}
		}
		List<String> ymlList = dataYml.getStringList("supply-drops");
		Iterator<String> iter = ymlList.iterator();
		while (iter.hasNext()) {
			String s = iter.next();
			if (s.startsWith(loc.replace(",", ";").replace(" ", ""))) {
				iter.remove();
			}
		}
		dataYml.set("supply-drops", ymlList);
		this.saveData();
	}
	
	public boolean hasSupplyDropAt(Location l) {
		for (Location loc : this.getSupplyDropLocs()) {
			if (l.getWorld().getName().equals(loc.getWorld().getName()) && l.getBlockX() == loc.getBlockX() && 
					l.getBlockY() == loc.getBlockY() && l.getBlockZ() == loc.getBlockZ()) {
				return true;
			}
		}
		return false;
	}
	
	public void updateStatus() {
		if (this.getLobby() == null) {
			this.setEnabled(false);
			return;
		}
		if (this.getCenter() == null) {
			this.setEnabled(false);
			return;
		}
		if (this.getGameStyle() == GameStyle.CLASSIC) {
			if (this.getTpType() == RandomType.WORLD_RANDOM) {
				this.setTeleportType(RandomType.LIST_RANDOM);
			}
		}
		if ((this.getTpType() == RandomType.LIST_RANDOM)) {
			if (this.getSpawnPoints().size() < this.getMaxPlayers()) {
				this.setEnabled(false);
			}
		}
	}
	
	public void sendMessage(String s) {
		for (SGPlayer player : players) {
			player.sendMessage(s);
		}
	}
	
	public void sendMessage(String s, SGPlayer exempt) {
		for (SGPlayer player : players) {
			if (player.getName().equals(exempt.getName())) continue;
			player.sendMessage(s);
		}
	}
	
	public void playSound(Sound s) {
		for (SGPlayer player : players) {
			player.playSound(s);
		}
	}
	
	public void spawnParticle(Particle particle, Location loc, int amount) {
		for (SGPlayer player : players) {
			player.spawnParticle(particle, loc, amount);
		}
	}
	
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		for (SGPlayer player : players) {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		}
	}
	
	public void sendActionBar(String msg) {
		for (SGPlayer player : players) {
			player.getBukkitPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.colorize(msg)));
		}
	}
	
	public boolean hasWinner() {
		int deadCount = 0;
		for (SGPlayer player : players) {
			if (player.isDead()) deadCount++;
		}
		return deadCount == (players.size() - 1);
	}
	
	public SGPlayer getWinner() {
		for (SGPlayer player : players) {
			if (!player.isDead()) {
				return player;
			}
		}
		return null;
	}
	
	public void hidePlayer(SGPlayer player) {
		for (SGPlayer sgp : players) {
			if (sgp.equals(player)) continue;
			if (sgp.isDead()) {
				sgp.getBukkitPlayer().showPlayer(player.getBukkitPlayer());
				continue;
			}
			sgp.getBukkitPlayer().hidePlayer(player.getBukkitPlayer());
		}
	}
	
	public void showPlayer(SGPlayer player) {
		for (SGPlayer sgp : players) {
			if (sgp.equals(player)) continue;
			sgp.getBukkitPlayer().showPlayer(player.getBukkitPlayer());
		}
	}
	
	public int getPlayersAliveAmount() {
		int deadCount = 0;
		for (SGPlayer player : players) {
			if (player.isDead()) deadCount++;
		}
		return (players.size() - deadCount);
	}
	
	public SGChest parseChest(String chest, String seperator) {
		String[] chestArray = chest.split(seperator);
		Location l = LocationUtils.parseLocation(chest, seperator);
		//return String.valueOf(w.getName() + seperator + x + seperator + y + seperator + z + seperator + yaw + seperator + pitch + seperator + tier);
		int tier = 1;
		if (chestArray.length >= 7) {
			try {
				tier = Integer.valueOf(chestArray[6]);
			} catch (Exception ex) {
				SurvivalGames.getInstance().getLogger().warning("Invalid tier \"" + tier + "\" for chest in arena " + this.getName());
			}
		}
		if (this.getData().get("loot.tier." + tier) == null) tier = 1;
		SGChest sgChest = new SGChest(((Chest)l.getBlock().getState()), this);
		sgChest.setTier(tier);
		return sgChest;
	}
	
	public void setNextEvent(GameEvent event) {
		this.nextGameEvent = event;
	}
	
	public void checkForChestFails() {
		if (dataYml.get("chests") != null) {
			if (this.chests.size() == dataYml.getStringList("chests").size()) return;
			SurvivalGames.getInstance().getLogger().info("Could not load all chests for arena " + this.getName() + " at startup.. attempting to load them now..");
			chests.clear();
			int count = 0;
			for (String loc : dataYml.getStringList("chests")) {
				try {
					Block b = LocationUtils.parseLocation(loc, ";").getBlock();
					if (b.getState() instanceof Chest) {
						chests.add(new SGChest((Chest)b.getState(), this));
					}
				} catch (Exception ex) {
					SurvivalGames.getInstance().getLogger().warning("Could not load chest "  + count + " in arena " + this.getName() + "!");
				}
				count++;
			}
			SurvivalGames.getInstance().getLogger().info("Loaded " + count + " chests in arena " + this.getName());
		}
	}
	
	public void setScoreboard(Scoreboard board) {
		for (SGPlayer player : this.getPlayers()) {
			player.getBukkitPlayer().setScoreboard(board);
		}
	}
	
	public void destroyEntities() {
		for (SGPlayer player : this.getPlayers()) {
			Player bukkitPlayer = player.getBukkitPlayer();
			for (int id : this.entityIds) {
				PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
				((CraftPlayer)bukkitPlayer).getHandle().playerConnection.sendPacket(packet);
			}
		}
	}
	
	public int getRatingAmount(Rating rating) {
		return this.getData().getInt("ratings." + rating.toString().toLowerCase());
	}
	
	public double getAverageRating() {
		int totalRatings = 0;
		for (Rating r : Rating.values()) {
			totalRatings = totalRatings + this.getData().getInt("ratings." + r.toString().toLowerCase());
		}
		int ratings = (this.getRatingAmount(Rating.SHIT) * 1 + this.getRatingAmount(Rating.BAD) * 2 + this.getRatingAmount(Rating.OKAY) * 3 + this.getRatingAmount(Rating.GOOD) * 4 + this.getRatingAmount(Rating.AMAZING) * 5);
		return NumberUtils.nearestHundreth((double)ratings/(double)totalRatings);
	}
	
	public String getAverageRatingString() {
		double rating = Math.round(this.getAverageRating());
		if (rating <= 0.0D) {
			return StringUtils.colorize("&6N/A");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 5; i++) {
			if (i <= rating) {
				sb.append("&e✰");
			} else {
				sb.append("&f✰");
			}
		}
		return StringUtils.colorize(sb.toString());
	}
	
	public void setLobbyTask(LobbyCountdownTask task) {
		this.lastLobbyTask = task;
	}
	
	public void setGameTask(InGameCountdownTask task) {
		this.lastGameTask = task;
	}
	
	public boolean inGame() {
		return this.getGameState() == GameState.INGAME_STARTED || this.getGameState() == GameState.INGAME_DEATHMATCH || this.getGameState() == GameState.INGAME_DEATHMATCH_COUNTOWN|| this.getGameState() == GameState.INGAME_DEATHMATCH_WAITING || this.getGameState() == GameState.POSTGAME_ENDING;
	}
	
	public ArrayList<SponsorItem> loadSponsorItems() {
		ArrayList<SponsorItem> items = new ArrayList<SponsorItem>();
		//for (String tier : a.getData().getConfigurationSection("loot.tier").getKeys(false)) {}
		if (this.getData().get("sponsor-gui") == null) {
			return items;
		}
		for (String item : this.getData().getConfigurationSection("sponsor-gui.items").getKeys(false)) {
			ItemStack itemStack = ItemUtils.createItemStackFromString(this.getData().getString("sponsor-gui.items." + item + ".item"), false);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (this.getData().get("sponsor-gui.items." + item + ".name") != null) {
				itemMeta.setDisplayName(StringUtils.colorize(this.getData().getString("sponsor-gui.items." + item + ".name")));
			}
			if (this.getData().get("sponsor-gui.items." + item + ".lore") != null) {
				List<String> temp = new ArrayList<String>();
				for (String lore : this.getData().getStringList("sponsor-gui.items." + item + ".lore")) {
					temp.add(StringUtils.colorize(lore));
				}
				itemMeta.setLore(temp);
			}
			if (this.getData().get("sponsor-gui.items." + item + ".enchantments") != null) {
				for (String enchant : this.getData().getStringList("sponsor-gui.items." + item + ".enchantments")) {
					String[] finalEnchant = enchant.split(":");
					if (finalEnchant.length >= 2) {
						Enchantment ench;
						try {
							ench = Enchantment.getByName(finalEnchant[0].toUpperCase());
							itemMeta.addEnchant(ench, Integer.valueOf(finalEnchant[1]), true);
						} catch (Exception ex) {
							SurvivalGames.getInstance().getLogger().warning("Invalid enchant \"" + finalEnchant[0] + "\" for item " + item + " in arena " + this.getName() + "!");
							continue;
						}
					}
				}
			}
			if (this.getData().get("sponsor-gui.items." + item + ".item-flags") != null) {
				for (String flag : this.getData().getStringList("sponsor-gui.items." + item + ".item-flags")) {
					ItemFlag itemFlag;
					try {
						itemFlag = ItemFlag.valueOf(flag.toUpperCase());
					} catch (Exception ex) {
						SurvivalGames.getInstance().getLogger().warning("Invalid item flag \"" + flag + "\" for item " + item + " in arena " + this.getName() + "!");
						continue;
					}
					itemMeta.addItemFlags(itemFlag);
				
				}
			}
			if (this.getData().get("sponsor-gui.items." + item + ".amount") != null) {
				itemStack.setAmount(Integer.valueOf(this.getData().getString("sponsor-gui.items." + item + ".amount")));
			}
			if (this.getData().get("sponsor-gui.items." + item + ".durability") != null) {
				itemStack.setDurability(Short.valueOf(this.getData().getString("sponsor-gui.items." + item + ".durability")));
			}
			itemStack.setItemMeta(itemMeta);
			if (this.getData().get("sponsor-gui.items." + item + ".clout-cost") != null) {
				double cost = this.getData().getDouble("sponsor-gui.items." + item + ".clout-cost");
				SponsorItem sponsorItem = new SponsorItem(this, itemStack, cost);
				items.add(sponsorItem);
			}
		}
		return items;
	}
	
	public File getDataFile() {return data;}
	public YamlConfiguration getData() {return dataYml;}
	public String getName() {return name;}
	public int getMaxPlayers() {return maxPlayers;}
	public int getMinPlayers() {return minPlayers;}
	public Location getCorner1() {return corner1;}
	public Location getCorner2() {return corner2;}
	public Location getLobby() {return lobby;}
	public Location getCenter() {return center;}
	public GameState getGameState() {return gameState;}
	public ArrayList<Location> getSpawnPoints() {return spawns;}
	public ArrayList<Location> getSupplyDropLocs() {return supplyDrops;}
	public ArrayList<SGChest> getChests() {return chests;}
	public ArrayList<SGPlayer> getPlayers() {return players;}
	public ArrayList<BlockState> getBlocksToRemove() {return blocksToRemove;}
	public ArrayList<BlockState> getBlocksToRebuild() {return blocksToRebuild;}
	public ArrayList<Integer> getEntityIds() {return entityIds;}
	public ArrayList<SponsorItem> getSponsorItems() {return sponsorItems;}
	public HashMap<Location, SGPlayer> getMineLocs() {return mineLocations;}
	public RandomType getTpType() {return teleportType;}
	public RandomType getSupplyDropType() {return supplyDropType;}
	public GameStyle getGameStyle() {return gameStyle;}
	public boolean isEnabled() {return enabled;}
	public boolean isCrossWorld() {return crossWorld;}
	public boolean hasSpeedEffectActive() {return speedEffect;}
	public LeaveGameItem getLeaveItem() {return leaveItem;}
	public SpectatorCompass getSpectatorCompass() {return spectatorCompass;}
	public GameEvent getNextEvent() {return nextGameEvent;}
	public int getRefillTime() {return chestRefillTime;}
	public int getSupplyDropTime() {return supplyDropTime;}
	public int getDeathmatchTime() {return deathMatchTime;}
	public int getBorderTime() {return borderTime;}
	public int getBorderDecreaseTime() {return borderDecreaseTime;}
	public int getBorderDamageBuffer() {return borderDamageBuffer;}
	public int getMinWaitTime() {return minWaitTime;}
	public int getMaxWaitTime() {return maxWaitTime;}
	public double getBorderSize() {return borderSize;}
	public double getMinBorderSize() {return minBorderSize;}
	public double getBorderDecrease() {return borderDecrease;}
	public double getCloutMult() {return cloutMultiplier;}
	public double getCloutPerWin() {return cloutPerWin;}
	public double getAttackSpeed() {return attackSpeed;}
	public int getBorderSpeed() {return borderSpeed;}
	public double getBorderDamage() {return borderDamage;}
	public int getSpeedDuration() {return speedDuration;}
	public int getMaxCompassUses() {return maxCompassUses;}
	public int getTimeOfDay() {return timeOfDay;}
	public boolean dataLoaded() {return dataLoaded;}
	public Scoreboard getScoreboard() {return scoreboard;}
	public Team getNextEventTeam() {return nextEvent;}
	public Team getTimerTeam() {return timer;}
	public World getWorld() {return center == null ? null : center.getWorld();}
	public LobbyCountdownTask getLobbyTask() {return lastLobbyTask;}
	public InGameCountdownTask getGameTask() {return lastGameTask;}
	
}
