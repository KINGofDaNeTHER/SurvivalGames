package me.kingofdanether.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;

public class PlayerTeleport implements Listener {

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if (e.getCause() != TeleportCause.SPECTATE) return;
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null) return;
		Player target = LocationUtils.nearLocation(e.getTo());
		if (target == null) return;
		SGPlayer sgTarget = PlayerManager.getOrCreate(target.getPlayerListName());
		Arena arenaTarget = ArenaManager.getArena(sgTarget);
		e.setCancelled(true);
		if (!sgTarget.inGame() || arenaTarget == null) {
			e.setCancelled(true);
			player.sendMessage(Constants.PREFIX + " &cThe player you want to teleport is not in a game!");
			return;
		}
		if (!a.getName().equals(arenaTarget.getName())) {
			e.setCancelled(true);
			player.sendMessage(Constants.PREFIX + " &cThe player you want to teleport to is not in the same game as you!");
			return;
		}
		//new PlayerSpectateGui(sgTarget).open(player.getBukkitPlayer());
 	}
	
}
