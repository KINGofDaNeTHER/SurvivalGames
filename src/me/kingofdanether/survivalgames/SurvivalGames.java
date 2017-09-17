package me.kingofdanether.survivalgames;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.arena.ArenaSign;
import me.kingofdanether.survivalgames.command.SGCommand;
import me.kingofdanether.survivalgames.configuration.ConfigFile;
import me.kingofdanether.survivalgames.configuration.ConfigFileManager;
import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.inventory.GuiClickHandler;
import me.kingofdanether.survivalgames.item.ClickableItemHandler;
import me.kingofdanether.survivalgames.listeners.BlockBreak;
import me.kingofdanether.survivalgames.listeners.BlockFall;
import me.kingofdanether.survivalgames.listeners.BlockPlace;
import me.kingofdanether.survivalgames.listeners.BowShoot;
import me.kingofdanether.survivalgames.listeners.CommandPreprocess;
import me.kingofdanether.survivalgames.listeners.EntityExplode;
import me.kingofdanether.survivalgames.listeners.HandleSGPlayerDeath;
import me.kingofdanether.survivalgames.listeners.InventoryClose;
import me.kingofdanether.survivalgames.listeners.ItemPickup;
import me.kingofdanether.survivalgames.listeners.PlayerConsumeItem;
import me.kingofdanether.survivalgames.listeners.PlayerDamage;
import me.kingofdanether.survivalgames.listeners.PlayerDisconnect;
import me.kingofdanether.survivalgames.listeners.PlayerFoodLevelChange;
import me.kingofdanether.survivalgames.listeners.PlayerInteract;
import me.kingofdanether.survivalgames.listeners.PlayerMove;
import me.kingofdanether.survivalgames.listeners.PlayerTeleport;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.runnables.CooldownHandler;
import me.kingofdanether.survivalgames.runnables.CoreTask;
import me.kingofdanether.survivalgames.sign.SignHandler;
import me.kingofdanether.survivalgames.sign.SignManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.FileUtils;
import me.kingofdanether.survivalgames.util.GhostFactory;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.PlayerUtils;

public class SurvivalGames extends JavaPlugin {
	
	private static SurvivalGames instance;
	private static ScoreboardManager boardManager = Bukkit.getScoreboardManager();
	private ConfigFileManager configManager;
	private ConfigFile signsFile;
	private GhostFactory ghostFactory;
	
	@Override
	public void onLoad() {
		this.getLogger().info("Loading SurvivalGames by KINGofDaNeTHER..");
	}
	
	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		this.configManager = new ConfigFileManager(this);
		this.signsFile = configManager.getNewConfig("signs.yml", new String[] {"Arena Signs"});
		this.ghostFactory = new GhostFactory(this);
		FileUtils.createFolder(this.getDataFolder() + FileUtils.fs + "userdata");
		File arenasFolder = FileUtils.getAndCreateFolder(this.getDataFolder() + FileUtils.fs + "arenas");
		if (arenasFolder.isDirectory()) {
			int count = 0;
			for (File file : arenasFolder.listFiles()) {
				ArenaManager.addArena(new Arena(file));
				count++;
			}
			this.getLogger().info("Loaded " + count + " arenas!");
		}
		if (signsFile.get("signs") != null) {
			int count = 0;
			for (String s : signsFile.getConfigurationSection("signs").getKeys(false)) {
				Arena a = ArenaManager.getArena(signsFile.getString("signs." + s + ".arena"));
				if (a == null) {
					this.getLogger().warning("Invalid arena \"" + signsFile.getString("signs." + s + ".arena") + "\" for sign " + s + ", removing it!");
					signsFile.set("signs." + s, null);
					continue;
				}
				Location loc = LocationUtils.parseLocation(signsFile.getString("signs." + s + ".location"), ";");
				Block b = loc.getBlock();
				if (!(b.getState() instanceof Sign)) {
					this.getLogger().warning("Block for sign " + s + " is no longer a sign, removing it!");
					signsFile.set("signs." + s, null);
					continue;
				}
				int id;
				try {
					id = Integer.valueOf(s);
				} catch (Exception ex) {
					this.getLogger().warning("Invalid id for sign " + s + ", removing it!");
					signsFile.set("signs." + s, null);
					continue;
				}
				ArenaSign sign = new ArenaSign((Sign)b.getState(), id, a);
				SignManager.addSign(sign);
				sign.update();
				count++;
			}
			signsFile.saveConfig();
			this.getLogger().info("Loaded " + count + " arena signs!");
		}
		this.registerListeners(this.getServer().getPluginManager());
		this.registerCommands();
		new CoreTask(this, 10).runTaskTimer(this, 0, 10);
		new CooldownHandler().runTaskTimerAsynchronously(this, 0, 1);
	}
	
	@Override
	public void onDisable() {
		for (Arena a : ArenaManager.getAllArenas()) {
			if (a.getPlayers().size() <= 0) continue;
			a.setScoreboard(Constants.BLANK_SCOREBOARD);
			a.destroyEntities();
			Player winner = a.getWinner().getBukkitPlayer();
			PlayerUtils.setName(winner, "", "", TeamAction.DESTROY);
			for (SGPlayer player : a.getPlayers()) {
				if (a.isCrossWorld()) {
					PlayerUtils.restoreInventory(player);
				}
				player.getBukkitPlayer().teleport(PlayerUtils.loadPreviousLocation(player));
				if (!a.isCrossWorld()) {
					PlayerUtils.restoreInventory(player);
				}
				if (player.getBukkitPlayer().getGameMode() != GameMode.SURVIVAL) {
					player.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);	
				}
				if (player.getBukkitPlayer().getAllowFlight()) {
					player.getBukkitPlayer().setAllowFlight(false);
				}
				player.fullHeal();
				winner.showPlayer(player.getBukkitPlayer());
				player.getBukkitPlayer().showPlayer(winner);
				PlayerUtils.setName(player.getBukkitPlayer(), "", "", TeamAction.DESTROY);
				player.sendMessage(Constants.PREFIX + " &cThe plugin was disabled, so the game was stopped!");
				player.playSound(Sound.BLOCK_ANVIL_LAND);
			}
			a.getWorld().getWorldBorder().reset();
		}
	}
	
	public static SurvivalGames getInstance() {return instance;}
	public static ScoreboardManager getScoreboardManager() {return boardManager;}
	public ConfigFile getSignsYml() {return signsFile;}
	public GhostFactory getGhostFactory() {return ghostFactory;}
	
	public void disable() {this.setEnabled(false);}
	
	private void registerListeners(PluginManager pm) {
		pm.registerEvents(new GuiClickHandler(), this);
		pm.registerEvents(new ClickableItemHandler(), this);
		pm.registerEvents(new PlayerDisconnect(), this);
		pm.registerEvents(new PlayerMove(), this);
		pm.registerEvents(new HandleSGPlayerDeath(), this);
		pm.registerEvents(new PlayerDamage(), this);
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new BlockFall(), this);
		pm.registerEvents(new PlayerFoodLevelChange(), this);
		pm.registerEvents(new PlayerTeleport(), this);
		pm.registerEvents(new PlayerInteract(), this);
		pm.registerEvents(new EntityExplode(), this);
		pm.registerEvents(new ItemPickup(), this);
		pm.registerEvents(new BowShoot(), this);
		pm.registerEvents(new SignHandler(), this);
		pm.registerEvents(new CommandPreprocess(), this);
		pm.registerEvents(new PlayerConsumeItem(), this);
		pm.registerEvents(new InventoryClose(), this);
	}
	
	private void registerCommands() {
		this.getCommand("sg").setExecutor(new SGCommand());
	}
 	
}
