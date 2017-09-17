package me.kingofdanether.survivalgames.util;

import org.bukkit.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;


public class StringUtils {

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
    public static String capitalize(String input) {
    	return  input.substring(0,1).toUpperCase() + input.substring(1).toLowerCase();
    }
	
	public static String getTimeString(int days, int hours, int minutes, int seconds) {
		String output = "";
		if (seconds > 0) output = convertIntToTime(seconds) + " sec" + output;
		if (minutes > 0) {
			if (seconds > 0) {
				output = convertIntToTime(minutes) + " min : " + output;
			} else {
				output = convertIntToTime(minutes) + " min" + output;
			}
		}
		if (hours > 0) {
			if (minutes > 0) {
				output =   convertIntToTime(hours) + " hrs : " + output;
			} else {
				output =   convertIntToTime(hours) + " hrs" + output;
			}
		}
		if (days > 0) {
			if (hours > 0) {
				output = convertIntToTime(days) + " days : " + output; 
			} else {
				output = convertIntToTime(days) + " days" + output; 
			}
		}
		if (output.equals("")) output = "a moment";
		return output;
	}
	
	public static String convertIntToTime(int input) {
		String output = String.valueOf(input);
		//if (output.length() == 1) output = "0" + input;
		if (output.length() == 1) output = String.valueOf(input);
		return output;
	}
	
	public static String getTimerBar(double amount) {
		amount = Math.round(amount);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= 100; i++) {
			if (i <= amount) {
				sb.append("&a" + Constants.COOLDOWN_BAR);
				continue;
			}
			sb.append("&c" + Constants.COOLDOWN_BAR);
		}
		return StringUtils.colorize(sb.toString());
	}

	public static String getDeathMessage(SGPlayer player, DamageCause cause) {
		switch (cause) {
		case BLOCK_EXPLOSION:
			return Constants.PREFIX + " &e" + player.getName() + " has died by &6Suffocation&e!";
		case CONTACT:
			break;
		case CRAMMING:
			return Constants.PREFIX + " &e" + player.getName() + " was squished to death!";
		case CUSTOM:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by a &6Magical force&e!";
		case DRAGON_BREATH:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by the &6Dragons breath&e!";
		case DROWNING:
			return Constants.PREFIX + " &e" + player.getName() + " drowned to death!";
		case ENTITY_ATTACK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by his awful pvp skills!";
		case ENTITY_EXPLOSION:
			return Constants.PREFIX + " &e" + player.getName() + " blew up!";
		case ENTITY_SWEEP_ATTACK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by &6Sweep attack&e!";
		case FALL:
			return Constants.PREFIX + " &e" + player.getName() + " thought he could survive his fall!";
		case FALLING_BLOCK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by a falling block!";
		case FIRE:
			return Constants.PREFIX + " &e" + player.getName() + " was burned alive!";
		case FIRE_TICK:
			return Constants.PREFIX + " &e" + player.getName() + " was burned alive!";
		case FLY_INTO_WALL:
			return Constants.PREFIX + " &e" + player.getName() + " flew into a wall!";
		case HOT_FLOOR:
			return Constants.PREFIX + " &e" + player.getName() + " died while walking on a hot floor!";
		case LAVA:
			return Constants.PREFIX + " &e" + player.getName() + " confused water with lava and died!";
		case LIGHTNING:
			return Constants.PREFIX + " &e" + player.getName() + " was struck by Herobrine!";
		case MAGIC:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by a &6Magical force&e!";
		case MELTING:
			return Constants.PREFIX + " &e" + player.getName() + " was melted to death!";
		case POISON:
			return Constants.PREFIX + " &e" + player.getName() + " died by poisoning!";
		case PROJECTILE:
			return Constants.PREFIX + " &e" + player.getName() + " was shot!";
		case STARVATION:
			return Constants.PREFIX + " &e" + player.getName() + " starved to death!";
		case SUFFOCATION:
			return Constants.PREFIX + " &e" + player.getName() + " died by &6Suffocation&e!";
		case SUICIDE:
			return Constants.PREFIX + " &e" + player.getName() + " killed himself!";
		case THORNS:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by &6Thorns&e!";
		case VOID:
			return Constants.PREFIX + " &e" + player.getName() + " fell into the void!";
		case WITHER:
			return Constants.PREFIX + " &e" + player.getName() + " withered to death!";
		default:
			break;
		}
		return Constants.PREFIX + " &e" + player.getName() + " has died by &e" + StringUtils.capitalize(cause.toString().toLowerCase()).replace("_", " ") + "!";
	}
	
	public static String getDeathMessageWithKiller(SGPlayer player, DamageCause cause) {
		if (player.getBukkitPlayer().getKiller() == null) {
			return getDeathMessage(player, cause);
		}
		SGPlayer killer = PlayerManager.getOrCreate(player.getBukkitPlayer().getKiller().getPlayerListName());
		if (killer == null) return getDeathMessage(player, cause);
		switch (cause) {
		case BLOCK_EXPLOSION:
			return Constants.PREFIX + " &e" + player.getName() + " was doomed to &6Suffocation &eby &6" + killer.getName() + "!";
		case CONTACT:
			break;
		case CRAMMING:
			return Constants.PREFIX + " &e" + player.getName() + " was squished &eby " + killer.getName() + "!";
		case CUSTOM:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Magical force&e!";
		case DRAGON_BREATH:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by the &6Dragons breath&e!";
		case DROWNING:
			return Constants.PREFIX + " &e" + player.getName() + " doomed to drowning by " + killer.getName() + "!";
		case ENTITY_ATTACK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by his awful pvp skills against " + killer.getName() + "!";
		case ENTITY_EXPLOSION:
			return Constants.PREFIX + " &e" + player.getName() + " was blown apart by " + killer.getName() + "!";
		case ENTITY_SWEEP_ATTACK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Sweep attack&e!";
		case FALL:
			return Constants.PREFIX + " &e" + player.getName() + " was doomed to &6Fall &eby " + killer.getName() + "!";
		case FALLING_BLOCK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by a falling block!";
		case FIRE:
			return Constants.PREFIX + " &e" + player.getName() + " was burned alive by " + killer.getName() + "!";
		case FIRE_TICK:
			return Constants.PREFIX + " &e" + player.getName() + " was burned alive by " + killer.getName() + "!";
		case FLY_INTO_WALL:
			return Constants.PREFIX + " &e" + player.getName() + " flew into a wall while escaping " + killer.getName() + "!";
		case HOT_FLOOR:
			return Constants.PREFIX + " &e" + player.getName() + " died while walking on a hot floor!";
		case LAVA:
			return Constants.PREFIX + " &e" + player.getName() + " confused water with lava and died while escaping " + killer.getName() + "!";
		case LIGHTNING:
			return Constants.PREFIX + " &e" + player.getName() + " was struck by Herobrine!";
		case MAGIC:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Magical force&e!";
		case MELTING:
			return Constants.PREFIX + " &e" + player.getName() + " was melted to death!";
		case POISON:
			return Constants.PREFIX + " &e" + player.getName() + " was poisoned by " + killer.getName() + "!";
		case PROJECTILE:
			return Constants.PREFIX + " &e" + player.getName() + " was shot by " + killer.getName() + "!";
		case STARVATION:
			return Constants.PREFIX + " &e" + player.getName() + " starved to death!";
		case SUFFOCATION:
			return Constants.PREFIX + " &e" + player.getName() + " died by &6Suffocation&e!";
		case SUICIDE:
			return Constants.PREFIX + " &e" + player.getName() + " killed himself!";
		case THORNS:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Thorns&e!";
		case VOID:
			return Constants.PREFIX + " &e" + player.getName() + " fell into the void while escaping " + killer.getName() + "!";
		case WITHER:
			return Constants.PREFIX + " &e" + player.getName() + " withered to death!";
		default:
			break;
		}
		return Constants.PREFIX + " &e" + player.getName() + " has died by &e" + StringUtils.capitalize(cause.toString().toLowerCase()).replace("_", " ") + "!";
	}
	
	public static String getDeathMessageWithKiller(SGPlayer player, SGPlayer killer, DamageCause cause) {
		if (killer == null) return getDeathMessage(player, cause);
		switch (cause) {
		case BLOCK_EXPLOSION:
			return Constants.PREFIX + " &e" + player.getName() + " was doomed to &6Suffocation &eby &6" + killer.getName() + "!";
		case CONTACT:
			break;
		case CRAMMING:
			return Constants.PREFIX + " &e" + player.getName() + " was squished &eby " + killer.getName() + "!";
		case CUSTOM:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Magical force&e!";
		case DRAGON_BREATH:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by the &6Dragons breath&e!";
		case DROWNING:
			return Constants.PREFIX + " &e" + player.getName() + " doomed to drowning by " + killer.getName() + "!";
		case ENTITY_ATTACK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by his awful pvp skills against " + killer.getName() + "!";
		case ENTITY_EXPLOSION:
			return Constants.PREFIX + " &e" + player.getName() + " was blown apart by " + killer.getName() + "!";
		case ENTITY_SWEEP_ATTACK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Sweep attack&e!";
		case FALL:
			return Constants.PREFIX + " &e" + player.getName() + " was doomed to &6Fall &eby " + killer.getName() + "!";
		case FALLING_BLOCK:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by a falling block!";
		case FIRE:
			return Constants.PREFIX + " &e" + player.getName() + " was burned alive by " + killer.getName() + "!";
		case FIRE_TICK:
			return Constants.PREFIX + " &e" + player.getName() + " was burned alive by " + killer.getName() + "!";
		case FLY_INTO_WALL:
			return Constants.PREFIX + " &e" + player.getName() + " flew into a wall while escaping " + killer.getName() + "!";
		case HOT_FLOOR:
			return Constants.PREFIX + " &e" + player.getName() + " died while walking on a hot floor!";
		case LAVA:
			return Constants.PREFIX + " &e" + player.getName() + " confused water with lava and died while escaping " + killer.getName() + "!";
		case LIGHTNING:
			return Constants.PREFIX + " &e" + player.getName() + " was struck by Herobrine!";
		case MAGIC:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Magical force&e!";
		case MELTING:
			return Constants.PREFIX + " &e" + player.getName() + " was melted to death!";
		case POISON:
			return Constants.PREFIX + " &e" + player.getName() + " was poisoned by " + killer.getName() + "!";
		case PROJECTILE:
			return Constants.PREFIX + " &e" + player.getName() + " was shot by " + killer.getName() + "!";
		case STARVATION:
			return Constants.PREFIX + " &e" + player.getName() + " starved to death!";
		case SUFFOCATION:
			return Constants.PREFIX + " &e" + player.getName() + " died by &6Suffocation&e!";
		case SUICIDE:
			return Constants.PREFIX + " &e" + player.getName() + " killed himself!";
		case THORNS:
			return Constants.PREFIX + " &e" + player.getName() + " was killed by " + killer.getName() + "'s &6Thorns&e!";
		case VOID:
			return Constants.PREFIX + " &e" + player.getName() + " fell into the void while escaping " + killer.getName() + "!";
		case WITHER:
			return Constants.PREFIX + " &e" + player.getName() + " withered to death!";
		default:
			break;
		}
		return Constants.PREFIX + " &e" + player.getName() + " has died by &e" + StringUtils.capitalize(cause.toString().toLowerCase()).replace("_", " ") + "!";
	}
	
}
