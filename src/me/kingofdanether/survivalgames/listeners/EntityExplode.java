package me.kingofdanether.survivalgames.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;

public class EntityExplode implements Listener {
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		List<Block> destroyed = e.blockList();
		//Iterator<Block> iter = destroyed.iterator();
		for (Block b : destroyed) {
			Location loc = b.getLocation();
			Arena a = this.getArena(loc);
			if (a == null) break;
			if (this.cancelEvent(loc)) {
				e.setCancelled(true);
				break;
			}
			if (a.getGameState() == GameState.POSTGAME_ENDING || a.getGameState() == GameState.POSTGAME_REBUILDING) {
				e.setCancelled(true);
				break;
			}
			if (!a.getGameState().toString().startsWith("INGAME")) {
				e.setCancelled(true);
				break;
			}
			//if (!a.getWorld().getWorldBorder().isInside(loc)) continue;
			a.getBlocksToRebuild().add(b.getState());
			float x = (float)-5 + (float) (Math.random() * ((5 - -5) + 1));
			float y = (float)-6 + (float) (Math.random() * ((6 - -6) + 1));
			float z = (float)-5 + (float) (Math.random() * ((5 - -5) + 1));
			@SuppressWarnings("deprecation")
			FallingBlock fb = a.getWorld().spawnFallingBlock(loc, b.getType(), b.getData());
			fb.setDropItem(false);
			fb.setVelocity(new Vector(x,y,z));
			//b.setType(Material.AIR);
		}
	}
	
	private boolean cancelEvent(Location l) {
		for (Arena a : ArenaManager.getAllArenas()) {
			if (a.getWorld().getName().equals(l.getWorld().getName())) {
				if (a.inGame()) return false;
				return true;
			}
		}
		return false;
	}
	
	private Arena getArena(Location l) {
		for (Arena a : ArenaManager.getAllArenas()) {
			if (a.getWorld().getWorldBorder().isInside(l)) {
				return a;
			}
		}
		return null;
	}

}
