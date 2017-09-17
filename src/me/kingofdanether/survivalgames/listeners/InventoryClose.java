package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;

public class InventoryClose implements Listener {
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player closer = (Player)e.getPlayer();
		SGPlayer player = PlayerManager.getOrCreate(closer.getPlayerListName());
		if (player == null || !player.inGame()) return;
		Arena a = ArenaManager.getArena(player);
		if (a == null) return;
		Inventory closed = e.getInventory();
		if (closed == null || closed.getType() != InventoryType.CHEST) return;
		Location loc = e.getInventory().getLocation();
		if (a.getData().getString("last-supply-drop") == null) return;
		Location temp = LocationUtils.parseLocation(a.getData().getString("last-supply-drop"), ";");
		if (loc == null || temp == null) return;
		if (!loc.getWorld().getName().equals(temp.getWorld().getName()) || loc.getBlockX() != temp.getBlockX() || loc.getBlockZ() != temp.getBlockZ()) return;
		loc.getBlock().setType(Material.AIR);
		player.playSound(Sound.BLOCK_CHEST_CLOSE);
		loc.getWorld().playEffect(loc, Effect.STEP_SOUND, Material.CHEST);
		a.sendMessage(Constants.PREFIX + " &eThe supply drop has been looted by " + player.getName() + "!");
		a.playSound(Sound.BLOCK_NOTE_PLING);
	}

}
