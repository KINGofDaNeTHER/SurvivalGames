package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;

public class PlayerMove implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		Arena a = ArenaManager.getArena(player);
		if (player == null || !player.inGame() || a == null) return;
		if (LocationUtils.locIsSimilar(from, to)) return;
		if (a.getGameState() == GameState.INGAME_WAITING || a.getGameState() == GameState.INGAME_COUNTDOWN 
				|| a.getGameState() == GameState.INGAME_DEATHMATCH_COUNTOWN) {
			e.setCancelled(true);
		}
		Location loc = player.getBukkitPlayer().getLocation();
		if (!a.hasMine(loc)) return;
		a.getBlocksToRemove().remove(loc.getBlock().getState());
		a.removeMine(loc);
		SGPlayer attacker = a.getMine(loc);
		if (attacker == null) return;
		if (player.getName().equalsIgnoreCase(attacker.getName())) {
			player.sendMessage(Constants.PREFIX + " &eYou have triggered your own mine!");
		} else {
			player.sendMessage(Constants.PREFIX + " &eYou have triggered " + attacker.getName() + "'s mine!");
			attacker.sendMessage(Constants.PREFIX + " &e" + player.getName() + " has triggered your mine!");
		}
		TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
		tnt.setYield(Constants.MINE_EXPLOSION_POWER);
		tnt.setFuseTicks(10);
	}

}
