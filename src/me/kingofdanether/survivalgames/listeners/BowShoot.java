package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.player.Cooldown;
import me.kingofdanether.survivalgames.player.CooldownManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.runnables.HomingTask;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.StringUtils;

public class BowShoot implements Listener {
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		if (((e.getEntity() instanceof LivingEntity)) && ((e.getProjectile() instanceof Arrow))) {
			if (!(e.getEntity() instanceof Player)) return;
			SGPlayer player = PlayerManager.getOrCreate(((Player) e.getEntity()).getPlayerListName());
			Arena a = ArenaManager.getArena(player);
			if (!player.inGame() || player.isDead() || a == null) return;
			if (!player.getBukkitPlayer().getInventory().contains(Constants.TRACKING_BOW.getType())) return;
			ItemStack inHand = player.getBukkitPlayer().getInventory().getItemInMainHand();
			if (inHand == null || inHand.getItemMeta() == null || inHand.getItemMeta().getDisplayName() == null) return;
			if (!inHand.getItemMeta().getDisplayName().equalsIgnoreCase(Constants.TRACKING_BOW.getItemMeta().getDisplayName())) return;
			double minAngle = 6.283185307179586D;
			Entity entity = this.getNearestEntity(player);
			if (entity == null) return;
			if (CooldownManager.hasCooldown(player.getName())) {
				e.setCancelled(true);
				player.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
				Cooldown cooldown = CooldownManager.getCooldown(player.getName());
				if (!cooldown.getAbility().equalsIgnoreCase("&fTracking Bow")) {
					player.sendMessage(Constants.PREFIX + " &ePlease wait until &c" + cooldown.getAbility() + " &eis done cooling down!");
					return;
				}
				player.sendMessage(Constants.PREFIX + " &eYou cannot use &cTracking Bow &efor another &c" + CooldownManager.getTimeRemaining(player.getName()) + " &eseconds!");
				return;
			}
			Vector toTarget = entity.getLocation().toVector().clone().subtract(player.getBukkitPlayer().getLocation().toVector());
			double angle = e.getProjectile().getVelocity().angle(toTarget);
			if ((angle < minAngle)) {
				minAngle = angle;
				//minEntity = entity;
				//if (CooldownManager.hasCooldown(player.getName())) return;
				if (entity instanceof Player) {
					player.sendMessage("&6[SG] &c[Tracking-Bow] &aLocked on to " + ((Player)entity).getPlayerListName() + "!");
				} else {
					player.sendMessage("&6[SG] &c[Tracking-Bow] &aLocked on to " + StringUtils.capitalize(entity.getType().toString().replace("_", " ")) + "!");
				}
			}
			CooldownManager.add(player.getName(), "&fTracking Bow", 3);
			inHand.setDurability((short)(inHand.getDurability() + 65));
			new HomingTask((Arrow)e.getProjectile(), (LivingEntity)entity, SurvivalGames.getInstance());
		}
	}
	
	private Entity getNearestEntity(SGPlayer player) {
		int distance = 1000000000;
		Entity nearest = null;
		for (Entity entity : player.getBukkitPlayer().getNearbyEntities(64.0D, 64.0D, 64.0D)) {
			if ((player.getBukkitPlayer().hasLineOfSight(entity)) && ((entity instanceof LivingEntity)) && (!entity.isDead())) {
				if (!(entity instanceof Player)) continue;
				SGPlayer target = PlayerManager.getOrCreate(((Player)entity).getPlayerListName());
				if (target == null || !target.inGame() || target.isDead()) continue;
				int dist = (int)player.getBukkitPlayer().getLocation().distance(entity.getLocation());
				if (dist < distance) {
					distance = dist;
					nearest = entity;
				}
			}
		}
		return nearest;
	}
}
