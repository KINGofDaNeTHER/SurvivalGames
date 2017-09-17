package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.runnables.SupplyDropFillTask;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.WorldUtils;

public class BlockFall implements Listener {

	 @EventHandler
	 public void fallingChest(final EntityChangeBlockEvent event){
		 if (event.getEntity() instanceof FallingBlock){
			 FallingBlock fb = (FallingBlock) event.getEntity();
			 if (fb.getMaterial() == Material.CHEST){
				 event.setCancelled(true);
				 event.getBlock().setType(Material.CHEST);
				 final Location eventLoc = event.getBlock().getLocation();
				 final Location loc = event.getBlock().getLocation().add(0.5,-1,0.5);
				 new BukkitRunnable() {
					 double phi = 0;
					 @Override
					 public void run() {
						 phi += Math.PI/10;
						 for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI/40) {
							 double r = 1.5;
							 double x = r*Math.cos(theta)*Math.sin(phi);
							 double y = r*Math.cos(phi) + 1.5;
							 double z = r*Math.sin(theta)*Math.sin(phi);
							 loc.add(x, y, z);
							 WorldUtils.spawnParticle(loc, Particle.PORTAL, 1);
							 loc.subtract(x,y,z);
						 }
						 if (phi > Math.PI) {
							 this.cancel();
							 WorldUtils.createHelix(eventLoc, Particle.FIREWORKS_SPARK, 0.5);
							 Arena a = getArena(eventLoc);
							 if (a != null) {
								 new SupplyDropFillTask(a, event.getBlock().getLocation()).runTaskTimer(SurvivalGames.getInstance(), 0, 1);
								 a.getBlocksToRemove().add(eventLoc.getBlock().getState());
							 }
							 return;
						 }
					 }
				 }.runTaskTimer(SurvivalGames.getInstance(), 0, 1);
				 return;
			 }
			 event.setCancelled(true);
			 fb.getWorld().playEffect(fb.getLocation(), Effect.STEP_SOUND, fb.getMaterial());
			 fb.getWorld().playSound(fb.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0F, 1.0F);
		 }
	 }
	 
	 private Arena getArena(Location l) {
		 for (Arena a : ArenaManager.getAllArenas()) {
			 if (a.getSupplyDropLocs().contains(l)) {
				 return a;
			 }
			 if (a.getData().getString("last-supply-drop") != null) {
				 Location temp = LocationUtils.parseLocation(a.getData().getString("last-supply-drop"), ";");
				 if (l.getWorld().getName().equals(temp.getWorld().getName()) && l.getBlockX() == temp.getBlockX() && 
							l.getBlockZ() == temp.getBlockZ()) {
						return a;
				 }
			 }
		 }
		 return null;
	 }
	
}
