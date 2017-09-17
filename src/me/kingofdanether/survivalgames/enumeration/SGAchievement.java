package me.kingofdanether.survivalgames.enumeration;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.util.StringUtils;

public enum SGAchievement {

	CLASSIC_FIRST_PLAY(GameStyle.CLASSIC, "classic-first-play",
	"Experimenter", 
	"&dPlay your first Classic game!",
	new ItemStack(Material.COMMAND)),
	
	ZDUBY_FIRST_PLAY(GameStyle.ZDUBY, "zduby-first-play",
	"Experimenter",
	"&dPlay your first Zduby game!",
	new ItemStack(Material.COMMAND)),
	
	CLASSIC_FIRST_WIN
	(GameStyle.CLASSIC, "classic-game-first-win", 
	"Warrior", 
	"&dWin your first game playing the Classic gamemode!",
	new ItemStack(Material.PAPER)),
	
	ZDUBY_FIRST_WIN(GameStyle.ZDUBY, "zduby-first-win", 
	"Warrior", 
	"&dWin your first game playing the Zduby gamemode!",
	new ItemStack(Material.PAPER)),
	
	CLASSIC_FIRST_KILL(GameStyle.CLASSIC, "classic-first-kill",
	"Blood Lust",
	"&dKill someone for the first time in the Classic gamemode!",
	new ItemStack(Material.GOLD_SWORD)),
	
	ZDUBY_FIRST_KILL(GameStyle.ZDUBY, "zduby-first-kill",
	"Blood Lust",
	"&dKill someone for the first time in the Zduby Gamemode!",
	new ItemStack(Material.DIAMOND_SWORD)),
	
	CLASSIC_FIRST_BOW_KILL(GameStyle.CLASSIC, "classic-first-bow-kill",
	"Marksman",
	"&dKill someone with a projectile for the first time in the Classic gamemode!",
	new ItemStack(Material.BOW)),
	
	ZDUBY_FIRST_BOW_KILL(GameStyle.ZDUBY, "zduby-first-bow-kill",
	"Marksman",
	"&dKill someone with a projectile for the first time in the Zduby gamemode!",
	new ItemStack(Material.BOW)),
	
	CLASSIC_500_DEATHS(GameStyle.CLASSIC, "classic-500-deaths",
	"Noob I",
	"&dDie 500 times in the classic gamemode!",
	new ItemStack(Material.SPONGE)),
	
	ZDUBY_500_DEATHS(GameStyle.ZDUBY, "zduby-500-deaths",
	"Noob I",
	"&dDie 500 times in the Zduby Gamemode!",
	new ItemStack(Material.SPONGE)),
	CLASSIC_FIRST_SUPPLY_DROP_LOOT(GameStyle.CLASSIC, "classic-supply-drop-loot",
	"Looter",
	"&dBe the first to locate the supply drop in the Classic gamemode!",
	new ItemStack(Material.BEACON)),
	ZDUBY_FIRST_SUPPLY_DROP_LOOT(GameStyle.ZDUBY, "zduby-supply-drop-loot",
	"Looter",
	"&dBe the first to locate the supply drop in the Zduby gamemode!",
	new ItemStack(Material.BEACON));
	
	private GameStyle style;
	private String name;
	private String message;
	private String tooltip;
	private ItemStack item;
	
	private SGAchievement(GameStyle style, String name, String message, String tooltip, ItemStack item) {
		this.style = style;
		this.name = name;
		this.message = StringUtils.colorize("&a&kl&a&kl&a> Achievement Unlocked: &6" + message + " &a<&kl&a&kl");
		this.tooltip = StringUtils.colorize(tooltip);
		this.item = item;
	}
	
	public GameStyle getGameStyle() {return style;}
	public String getName() {return name;}
	public String getMessage() {return message;}
	public String getToolTip() {return tooltip;}
	public ItemStack getItem() {return item;}
	
}
