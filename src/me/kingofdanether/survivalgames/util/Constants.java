package me.kingofdanether.survivalgames.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import me.kingofdanether.survivalgames.SurvivalGames;

public class Constants {

	public static final String PREFIX = StringUtils.colorize(SurvivalGames.getInstance().getConfig().getString("prefix"));
	public static final String NO_PERMS_MSG = StringUtils.colorize(SurvivalGames.getInstance().getConfig().getString("no-perm-msg"));
	public static final String MINE_NAME = StringUtils.colorize(SurvivalGames.getInstance().getConfig().getString("explosive-mine.name"));
	
	public static final String RIGHT_ARROW = "»";
	public static final String LEFT_ARROW = "«";
	public static final String COOLDOWN_BAR = "╎";
	
	public static final ItemStack ZDUBY_HEAD = ItemUtils.createSkull("&6GameStyle: ZDUBY", "Zduby");
	public static final ItemStack CROSS_WORLD_ENABLED = ItemUtils.createSkull("&6Cross World: &aEnabled", "Planet");
	public static final ItemStack CROSS_WORLD_DISABLED = ItemUtils.createSkull("&6Cross World: &cDisabled", "Planet");
	public static final ItemStack TRACKING_BOW = ItemUtils.createItemStackFromString(SurvivalGames.getInstance().getConfig().getString("tracking-bow.item"), StringUtils.colorize(SurvivalGames.getInstance().getConfig().getString("tracking-bow.name")));
	public static final ItemStack EMPTY_BOWL = new ItemStack(Material.BOWL);
	
	public static final int MINE_EXPLOSION_POWER = SurvivalGames.getInstance().getConfig().getInt("explosive-mine.explosion-power");

	public static final Material MINE_MATERIAL = Material.valueOf(SurvivalGames.getInstance().getConfig().getString("explosive-mine.material").toUpperCase());

	public static final Scoreboard BLANK_SCOREBOARD = Bukkit.getScoreboardManager().getNewScoreboard();
	
	public static final double ATTACK_SPEED = SurvivalGames.getInstance().getConfig().get("gamemode.classic.attack-speed") == null ? 16 : SurvivalGames.getInstance().getConfig().getDouble("gamemode.classic.attack-speed");
}

