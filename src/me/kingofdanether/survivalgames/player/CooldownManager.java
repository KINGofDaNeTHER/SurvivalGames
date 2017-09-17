package me.kingofdanether.survivalgames.player;

import java.util.HashMap;

import me.kingofdanether.survivalgames.enumeration.TimeUnit;
import me.kingofdanether.survivalgames.util.MinecraftTimeUtils;

public class CooldownManager {
    
	private static HashMap<String, Cooldown> cooldownPlayers = new HashMap<String, Cooldown>();

    public static void add(String player, String ability, long seconds) {
    	 if(hasCooldown(player)) return;
        if(!cooldownPlayers.containsKey(player)) {
        	cooldownPlayers.put(player, new Cooldown(player, ability, seconds * 1000, System.currentTimeMillis()));
        }
    }
    
    public static void removeCooldown(String player) {
        if(!cooldownPlayers.containsKey(player)) return;
        cooldownPlayers.remove(player);
    }
    
    public static Cooldown getCooldown(String player) {
    	return cooldownPlayers.get(player);
    }
    
    public static boolean hasCooldown(String player) {
    	return cooldownPlayers.containsKey(player);
    }
    
    public static double getTimeRemaining(String player) {
        if(!cooldownPlayers.containsKey(player)) return 0.0;
        return MinecraftTimeUtils.convert((cooldownPlayers.get(player).getSeconds() + cooldownPlayers.get(player).getSysTime()) - System.currentTimeMillis(), TimeUnit.SECONDS, 1);
    }
    
    public static HashMap<String, Cooldown> getAllCooldowns() {return cooldownPlayers;}
    
}
