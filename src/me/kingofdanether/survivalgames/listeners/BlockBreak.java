package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;

public class BlockBreak implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null) return;
		if (player.isDead()) {
			e.setCancelled(true);
			return;
		}
		if (a.getGameState().toString().startsWith("INGAME") && a.getGameState() != GameState.INGAME_COUNTDOWN && a.getGameState() != GameState.INGAME_WAITING) {
			Block b = e.getBlock();
			if (a.getGameStyle() == GameStyle.CLASSIC) {
				if (b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2) {
					a.getBlocksToRebuild().add(b.getState());
					return;
				}
				e.setCancelled(true);
				return;
			} else if (a.getGameStyle() == GameStyle.ZDUBY) {
				a.getBlocksToRebuild().add(b.getState());
				return;
			}
		} else { 
			e.setCancelled(true);
		}
	}

}
