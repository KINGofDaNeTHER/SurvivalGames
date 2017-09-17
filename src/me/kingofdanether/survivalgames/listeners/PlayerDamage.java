package me.kingofdanether.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;

public class PlayerDamage implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (!(e.getEntity() instanceof Player)) return;
		SGPlayer player = PlayerManager.getOrCreate(((Player)e.getEntity()).getPlayerListName());
		Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null) return;
		if (player.isDead()) {
			e.setCancelled(true);
			return;
		}
		if (a.getGameState() == GameState.LOBBY_WAITING || a.getGameState() == GameState.LOBBY_COUNTDOWN || a.getGameState() == GameState.INGAME_WAITING
				|| a.getGameState() == GameState.INGAME_COUNTDOWN || a.getGameState() == GameState.POSTGAME_ENDING 
				|| a.getGameState() == GameState.INGAME_DEATHMATCH_COUNTOWN) {
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
		SGPlayer damager = PlayerManager.getOrCreate(((Player)e.getDamager()).getPlayerListName());
		SGPlayer damaged = PlayerManager.getOrCreate(((Player)e.getEntity()).getPlayerListName());
		Arena a = ArenaManager.getArena(damager);
		if (damager == null || damaged == null || a == null) return;
		damaged.setLastDamager(damager);
		if ((damaged.inGame() && (damager.inGame() && damager.isDead()) || damaged.isDead())) {
			e.setCancelled(true);
			return;
		}
		if (a != null) {
			if (damager.getBukkitPlayer().hasPotionEffect(PotionEffectType.SPEED) && a.hasSpeedEffectActive()) {
				damager.getBukkitPlayer().removePotionEffect(PotionEffectType.SPEED);
			}
		}
	}
	
}
