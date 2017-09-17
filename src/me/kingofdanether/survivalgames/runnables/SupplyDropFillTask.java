package me.kingofdanether.survivalgames.runnables;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.enumeration.SGAchievement;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import mkremins.fanciful.FancyMessage;

public class SupplyDropFillTask extends BukkitRunnable {

	private Arena a;
	private Location l;
	private int y;
	
	public SupplyDropFillTask(Arena a, Location l) {
		this.a = a;
		this.l = l;
		this.y = (l.getBlockY() + 100);
	}
	
	@Override
	public void run() {
		for (int i = 0; i <= 3; i++) {
			Location temp = new Location(l.getWorld(), l.getBlockX(), y, l.getBlockZ()).add(0.5,0,0.5);
			if (y <= l.getBlockY()) {
				this.cancel();
				if (l.getBlock().getState() instanceof Chest) {
					new SupplyDropChestFillTask(new SGChest((Chest)l.getBlock().getState(), a)).runTaskTimer(SurvivalGames.getInstance(), 0, 1);
					l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
					for (Entity en : l.getWorld().getNearbyEntities(l, 2.5, 2.5, 2.5)) {
						if (en instanceof Player) {
							SGPlayer player = PlayerManager.getOrCreate(((Player)en).getPlayerListName());
							if (player.isDead() || !player.inGame()) continue;
							SGAchievement achievement = null;
							try {
								achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_SUPPLY_DROP_LOOT");
							} catch (Exception ex) {
								break;
							}
							if (!player.hasAchievement(achievement)) {
								player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
								player.addAchievement(achievement);
								new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(player.getBukkitPlayer());
							}
							break;
						}
					}
				} else {
					l.getWorld().createExplosion(l, 1.5F);
				}
				return;
			}
			temp.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, temp, 25);
			temp.getWorld().playSound(temp, Sound.ENTITY_FIREWORK_LAUNCH, 1.0F, 1.0F);
			y--;
		}
	}
	
	
}
