package me.kingofdanether.survivalgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.GameStyle;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;

public class BlockPlace implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.isCancelled()) return;
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null) return;
		if (player.isDead()) {
			e.setCancelled(true);
			return;
		}
		if (a.getGameState().toString().startsWith("INGAME") && a.getGameState() != GameState.INGAME_COUNTDOWN && a.getGameState() != GameState.INGAME_WAITING) {
			ItemStack placed = e.getItemInHand();
			if (a.getGameStyle() == GameStyle.CLASSIC) {
				if (placed == null || placed.getItemMeta() == null || placed.getItemMeta().getDisplayName() == null) {
					e.setCancelled(true);
					return;
				}
				if (!placed.getItemMeta().getDisplayName().equals(Constants.MINE_NAME) || placed.getType() != Constants.MINE_MATERIAL) {
					e.setCancelled(true);
					return;
				} else if (placed.getType() == Constants.MINE_MATERIAL){
					a.getBlocksToRemove().add(e.getBlockPlaced().getState());
					a.addMine(e.getBlock().getLocation(), player);
					player.sendMessage(Constants.PREFIX + " &eYou placed a mine! Be careful to not trigger it on yourself!");
				}
				a.getBlocksToRemove().add(e.getBlockPlaced().getState());
				return;
			} else if (a.getGameStyle() == GameStyle.ZDUBY) {
				if (placed == null || placed.getItemMeta() == null || placed.getItemMeta().getDisplayName() == null) return;
				if (!placed.getItemMeta().getDisplayName().equals(Constants.MINE_NAME) || placed.getType() != Constants.MINE_MATERIAL) {
					return;
				} else if (placed.getType() == Constants.MINE_MATERIAL){
					a.getBlocksToRemove().add(e.getBlockPlaced().getState());
					a.addMine(e.getBlock().getLocation(), player);
					player.sendMessage(Constants.PREFIX + " &eYou placed a mine! Be careful to not trigger it on yourself!");
				}
				a.getBlocksToRemove().add(e.getBlockPlaced().getState());
				return;
			}
		} else { 
			e.setCancelled(true);
		}
	}
	
}
