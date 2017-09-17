package me.kingofdanether.survivalgames.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerManager {
	
	private static Map<UUID, SGPlayer> players = new HashMap<UUID, SGPlayer>();

	public static Map<UUID, SGPlayer> getAllPlayers() {return players;}

	public static SGPlayer getOrCreate(String player) {
		Player target = Bukkit.getPlayer(player);
		if (smartSearch(player) == null) {
			return new SGPlayer(target);
		}
		SGPlayer sgPlayer = players.get(target.getUniqueId());
		if (!checkObjects(sgPlayer, target)) {
			sgPlayer.updatePlayer(target);
		}
		return sgPlayer;
	}
	
	public static SGPlayer smartSearch(String player) {
		Player target = Bukkit.getPlayer(player);
		if (target == null) return null;
		if (!players.containsKey(target.getUniqueId())) return null;
		for (Entry<UUID, SGPlayer> entries : players.entrySet()) {
			if (target != null) {
				if ((entries.getKey().toString().equals(target.getUniqueId().toString()))) {
					return entries.getValue();
				}
			}
			if ((target.getName().equals(entries.getValue().getName()))) {
				return entries.getValue();
			}
		}
		return null;
	}
	
	public static SGPlayer searchOffline(String player) {
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(player);
		if (target == null) return null;
		if (!players.containsKey(target.getUniqueId())) return null;
		for (Entry<UUID, SGPlayer> entries : players.entrySet()) {
			if (target != null) {
				if ((entries.getKey().toString().equals(target.getUniqueId().toString()))) {
					return entries.getValue();
				}
			}
			if ((target.getName().equals(entries.getValue().getName()))) {
				return entries.getValue();
			}
		}
		return null;
	}
	
	public static void addPlayer(String player) {
		Player target = Bukkit.getPlayer(player);
		players.put(target.getUniqueId(), new SGPlayer(target));
	}
	
	public static void removePlayer(String player) {
		Player target = Bukkit.getPlayer(player);
		if (hasPlayer(target.getUniqueId())) {
			players.remove(target.getUniqueId());
		}
	}
	
	private static boolean checkObjects(SGPlayer player, Player p) {
		return player.getBukkitPlayer().equals(p);
	}
	
	public static boolean hasPlayer(UUID uuid) {
		return players.containsKey(uuid);
	}
	
	public static SGPlayer getPlayer(UUID uuid) {
		if (hasPlayer(uuid)) {
			return players.get(uuid);
		}
		return null;
	}
	
}
