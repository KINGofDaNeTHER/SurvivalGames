package me.kingofdanether.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;

public class PlayerFoodLevelChange implements Listener {

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (e.isCancelled()) return;
		SGPlayer player = PlayerManager.getOrCreate(((Player)e.getEntity()).getPlayerListName());
		Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null) return;
		if (player.isDead()) {
			e.setCancelled(true);
			return;
		}
		if (a.getGameState() == GameState.LOBBY_WAITING || a.getGameState() == GameState.LOBBY_COUNTDOWN || a.getGameState() == GameState.INGAME_WAITING
				|| a.getGameState() == GameState.INGAME_COUNTDOWN || a.getGameState() == GameState.POSTGAME_ENDING) {
			e.setCancelled(true);
			return;
		}
	}
	
}
