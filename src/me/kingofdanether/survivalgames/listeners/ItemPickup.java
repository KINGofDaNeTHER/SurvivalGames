package me.kingofdanether.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;

@SuppressWarnings("deprecation")
public class ItemPickup implements Listener {
	
//	@EventHandler
//	public void onItemPickup(EntityPickupItemEvent e) {
//		Entity en = e.getEntity();
//		if (!(en instanceof Player)) return;
//		Player eventPlayer = (Player)en;
//		SGPlayer player = PlayerManager.getOrCreate(eventPlayer.getPlayerListName());
//		if (player == null || !player.inGame()) return;
//		if (!player.isDead()) return;
//		e.setCancelled(true);
//	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		Player eventPlayer = e.getPlayer();
		SGPlayer player = PlayerManager.getOrCreate(eventPlayer.getPlayerListName());
		if (player == null || !player.inGame()) return;
		if (!player.isDead()) return;
		e.setCancelled(true);
	}

}
