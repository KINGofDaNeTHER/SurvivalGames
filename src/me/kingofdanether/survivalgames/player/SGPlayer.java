package me.kingofdanether.survivalgames.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.enumeration.PlayerDirection;
import me.kingofdanether.survivalgames.enumeration.Rating;
import me.kingofdanether.survivalgames.enumeration.SGAchievement;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.FileUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SGPlayer {

	private Player p;
	private File dataFile;
	private YamlConfiguration data;
	private boolean dead;
	private boolean admin;
	private SGPlayer lastCompassTarget;
	private SGPlayer lastDamager;
	private ArrayList<SGAchievement> achievements;
	private ArrayList<String> ratings;
	private int gameKills;
	
	public SGPlayer(Player p) {
		if (p == null) return;
		this.p = p;
		this.dataFile = FileUtils.getAndCreateFile(SurvivalGames.getInstance().getDataFolder() + FileUtils.fs + "userdata" + FileUtils.fs + p.getUniqueId().toString() + ".yml");
		this.data = YamlConfiguration.loadConfiguration(dataFile);
		data.set("name", p.getPlayerListName());
		this.saveData();
		this.achievements = new ArrayList<SGAchievement>();
		this.ratings = new ArrayList<String>();
		for (String achievement : this.getData().getStringList("achievements")) {
			SGAchievement add = null;
			try {
				add = SGAchievement.valueOf(achievement.toUpperCase());
			} catch (Exception ex) {
				ex.printStackTrace();
				SurvivalGames.getInstance().getLogger().warning("Invalid achievement: " + achievement);
				continue;
			}
			achievements.add(add);
		}
	}
	
	public boolean inGame() {
		return ArenaManager.getArena(this) != null;
	}
	
	public void saveData() {
		try {
			data.save(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void updatePlayer(Player p) {
		this.p = p;
	}
	
	public void sendMessage(String s) {
		p.sendMessage(StringUtils.colorize(s));
	}
	
	public void sendActionBar(String s) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.colorize(s)));
	}
	
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		p.sendTitle(StringUtils.colorize(title), StringUtils.colorize(subtitle), fadeIn, stay, fadeOut);
	}
	
	public void playSound(Sound s) {
		p.playSound(p.getLocation(), s, 1.0F, 1.0F);
	}
	
	public void spawnParticle(Particle particle, Location loc, int amount) {
		p.spawnParticle(particle, loc, 25);
	}
	
	@Deprecated
	public void setInGame(boolean inGame) {}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public void fullHeal() {
		p.setHealth(20.0D);
		p.setFoodLevel(20);
		this.clearEffects();
	}
	
	public void respawn() {
		if (p.isDead()) p.spigot().respawn();
	}
	
	public void addHealth(double health) {
		double current = p.getHealth();
		@SuppressWarnings("deprecation")
		double add = p.getMaxHealth() < (current + health) ? ((current + health)-p.getMaxHealth()) : health;
		if (current >= 20) add = 0;
		p.setHealth(current + add);
	}
	
	public void addFood(int food) {
		int current = p.getFoodLevel();
		int add = 20 < (current + food) ? ((current + food)-20) : food;
		if (current >= 20) add = 0;
		p.setFoodLevel(current + add);
	}
	
	public void setSaturation(float f) {
		p.setSaturation(f);
	}
	
	public void addPotionEffect(PotionEffectType effect, int seconds, int level) {
		p.addPotionEffect(new PotionEffect(effect, (20 * seconds), level, true, true));
	}
	
	public void setLastCompassTarget(SGPlayer player) {
		this.lastCompassTarget = player;
	}
	
	public double getGlobalKD() {
		double deaths = this.getData().getDouble("stats." + GameStyle.CLASSIC.toString().toLowerCase() +  ".deaths") + this.getData().getDouble("stats." + GameStyle.ZDUBY.toString().toLowerCase() +  ".deaths");
		double kills = this.getData().getDouble("stats." + GameStyle.CLASSIC.toString().toLowerCase() +  ".kills") + this.getData().getDouble("stats." + GameStyle.ZDUBY.toString().toLowerCase() +  ".kills");
		return NumberUtils.nearestHundreth(kills/deaths);
	}
	
	public double getKD(GameStyle style) {
		return NumberUtils.nearestHundreth(this.getData().getDouble("stats." + style.toString().toLowerCase() + ".kills")/this.getData().getDouble("stats." + style.toString().toLowerCase() + ".deaths"));
	}
	
	public void addAchievement(String achievement) {
		SGAchievement add = null;
		try {
			add = SGAchievement.valueOf(achievement.toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			SurvivalGames.getInstance().getLogger().warning("Invalid achievement: " + achievement);
			return;
		}
		List<String> temp = this.getData().getStringList("achievements");
		if (!temp.contains(add.toString())) {
			temp.add(add.toString());
			this.getData().set("achievements", temp);
			this.saveData();
		}
		achievements.add(add);
	}
	
	public void addAchievement(SGAchievement add) {
		List<String> temp = this.getData().getStringList("achievements");
		if (!temp.contains(add.toString())) {
			temp.add(add.toString());
			this.getData().set("achievements", temp);
			this.saveData();
		}
		achievements.add(add);
	}
	
	public void removeAchievement(String achievement) {
		SGAchievement remove = null;
		try {
			remove = SGAchievement.valueOf(achievement.toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			SurvivalGames.getInstance().getLogger().warning("Invalid achievement: " + achievement);
			return;
		}
		List<String> temp = this.getData().getStringList("achievements");
		if (temp.contains(remove.toString())) {
			temp.remove(remove.toString());
			this.getData().set("achievements", temp);
			this.saveData();
		}
		achievements.remove(remove);
	}
	
	public void removeAchievement(SGAchievement remove) {
		List<String> temp = this.getData().getStringList("achievements");
		if (temp.contains(remove.toString())) {
			temp.remove(remove.toString());
			this.getData().set("achievements", temp);
			this.saveData();
		}
		achievements.remove(remove);
	}
	
	public boolean hasAchievement(String achievement) {
		SGAchievement a = null;
		try {
			a = SGAchievement.valueOf(achievement.toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			SurvivalGames.getInstance().getLogger().warning("Invalid achievement: " + achievement);
			return false;
		}
		return achievements.contains(a);
	}
	
	public boolean hasAchievement(SGAchievement a) {
		return achievements.contains(a);
	}
	
	public boolean canRate() {
		return ratings.size() >= 1;
	}
	
	public boolean canRate(Arena a) {
		return ratings.contains(a.getName());
	}
	
	public void setRateable(Arena a, boolean rate) {
		if (rate) {
			if (!ratings.contains(a.getName())) ratings.add(a.getName());
		} else {
			if (ratings.contains(a.getName())) ratings.remove(a.getName());
		}
	}
	
	public void sendRatingMessage(Arena a) {
		new FancyMessage(StringUtils.colorize(Constants.PREFIX + " &aRate this arena: ")).then(Rating.SHIT.getScoreName())
		.tooltip(StringUtils.colorize("&4This arena is shit!")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.SHIT.getScore())
		.then(" " + Rating.BAD.getScoreName()).tooltip(StringUtils.colorize("&cThis arena is bad.")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.BAD.getScore())
		.then(" " + Rating.OKAY.getScoreName()).tooltip(StringUtils.colorize("&eThis arena is alright :/")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.OKAY.getScore())
		.then(" " + Rating.GOOD.getScoreName()).tooltip(StringUtils.colorize("&aThis arena is good!")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.GOOD.getScore())
		.then(" " + Rating.AMAZING.getScoreName()).tooltip(StringUtils.colorize("&2This is the best shit i've ever seen!")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.AMAZING.getScore())
		.send(p);
		this.playSound(Sound.BLOCK_NOTE_PLING);
	}
	
	public PlayerDirection getCardinalDirection() {
		double rotation = (this.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return PlayerDirection.NORTH;
		} else if (22.5 <= rotation && rotation < 67.5) {
			return PlayerDirection.NORTH_EAST;
		} else if (67.5 <= rotation && rotation < 112.5) {
			return PlayerDirection.EAST;
		} else if (112.5 <= rotation && rotation < 157.5) {
			return PlayerDirection.SOUTH_EAST;
		} else if (157.5 <= rotation && rotation < 202.5) {
			return PlayerDirection.SOUTH;
		} else if (202.5 <= rotation && rotation < 247.5) {
			return PlayerDirection.SOUTH_WEST;
		} else if (247.5 <= rotation && rotation < 292.5) {
			return PlayerDirection.WEST;
		} else if (292.5 <= rotation && rotation < 337.5) {
			return PlayerDirection.NORTH_WEST;
		} else if (337.5 <= rotation && rotation < 360.0) {
			return PlayerDirection.NORTH;
		} else {
			return null;
		}
	}
	
	public void clearEffects() {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType());
		}
	}
	
	public void setLastDamager(SGPlayer player) {
		this.lastDamager = player;
	}
	
	public void setKills(int amount, GameStyle style) {
		this.getData().set("stats." + style.toString().toLowerCase() + ".kills", amount);
		this.saveData();
	}
	
	public int getKills(GameStyle style) {
		return this.getData().getInt("stats." + style.toString().toLowerCase() + ".kills");
	}
	
	public void setDeaths(int amount, GameStyle style) {
		this.getData().set("stats." + style.toString().toLowerCase() + ".deaths", amount);
		this.saveData();
	}
	
	public int getDeaths(GameStyle style) {
		return this.getData().getInt("stats." + style.toString().toLowerCase() + ".deaths");
	}
	
	public void setClout(double clout, GameStyle style) {
//		int current = this.getClout(style);
//		if (current - amount <= 0) {
//			amount = (amount - (amount - current));
//		}
		this.getData().set("stats." + style.toString().toLowerCase() + ".clout", clout);
		this.saveData();
	}
	
	public double getClout(GameStyle style) {
		return this.getData().getDouble("stats." + style.toString().toLowerCase() + ".clout");
	}
	
	public void setGameKills(int kills) {
		this.gameKills = kills;
	}
	
	public double getAttackSpeed() {
		return p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue();
	}
	
	public void setAttackSpeed(double speed) {
		p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(speed);
	}
	
	public Player getBukkitPlayer() {return p;}
	public String getName() {return p.getPlayerListName();}
	public File getDataFile() {return dataFile;}
	public YamlConfiguration getData() {return data;}
	public boolean isDead() {return dead;}
	public boolean adminEnabled() {return admin;}
	public SGPlayer getCompassTarget() {return lastCompassTarget;}
	public SGPlayer getLastDamager() {return lastDamager;}
	public ArrayList<SGAchievement> getAchievements() {return achievements;}
	public Location getLocation() {return p.getLocation();}
	public int getGameKills() {return gameKills;}
	
}
