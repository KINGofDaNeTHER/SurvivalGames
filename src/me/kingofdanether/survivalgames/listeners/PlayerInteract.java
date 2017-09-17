package me.kingofdanether.survivalgames.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.arena.SGChest;
import me.kingofdanether.survivalgames.player.Cooldown;
import me.kingofdanether.survivalgames.player.CooldownManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.OFF_HAND) return;
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			SGPlayer clicker = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
			Arena a = ArenaManager.getArena(clicker);
			if (clicker == null || !clicker.inGame() || a == null) return;
			if (clicker.inGame() && clicker.isDead()) {
				e.setCancelled(true);
				return;
			}
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null && e.getClickedBlock().getState() instanceof Chest) {
				Chest clicked = (Chest)e.getClickedBlock().getState();
				SGChest chestClicked = a.getChest(clicked.getLocation());
				if (chestClicked == null || !chestClicked.canBeFilled()) return;
				chestClicked.fill(true);
				return;
			} else if (e.getItem() != null && e.getItem().getType() == Material.MUSHROOM_SOUP)  {
				if (clicker.getBukkitPlayer().getGameMode() == GameMode.CREATIVE) return;
				if (CooldownManager.hasCooldown(clicker.getName())) {
					e.setCancelled(true);
					clicker.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
					Cooldown cooldown = CooldownManager.getCooldown(clicker.getName());
					if (!cooldown.getAbility().equalsIgnoreCase("&fSoup")) {
						clicker.sendMessage(Constants.PREFIX + " &ePlease wait until &c" + cooldown.getAbility() + " &eis done cooling down!");
						return;
					}
					clicker.sendMessage(Constants.PREFIX + " &eYou cannot use &cSoup &efor another &c" + CooldownManager.getTimeRemaining(clicker.getName()) + " &eseconds!");
					return;
				}
				clicker.addHealth(4.0D);
				clicker.addFood(6);
				clicker.setSaturation(7.2F);
				clicker.addPotionEffect(PotionEffectType.REGENERATION, 6, 0);
				a.spawnParticle(Particle.HEART, clicker.getLocation().add(0.5,1,0.5), 25);
				a.playSound(Sound.ENTITY_PLAYER_BURP);
				if (e.getItem().getAmount() <= 1) {
					e.getItem().setType(Material.BOWL);
				} else {
					e.getItem().setAmount(e.getItem().getAmount() - 1);
					if (clicker.getBukkitPlayer().getInventory().firstEmpty() == -1) {
						clicker.getLocation().getWorld().dropItemNaturally(clicker.getLocation(), Constants.EMPTY_BOWL);
						CooldownManager.add(clicker.getName(), "&fSoup", 2);
						return;
					} 
					clicker.getBukkitPlayer().getInventory().addItem(Constants.EMPTY_BOWL);
				}
				CooldownManager.add(clicker.getName(), "&fSoup", 2);
			}
		} 	
	}

	public SGPlayer getNearestPlayer(SGPlayer player) {
		if (player.isDead()) return null;
		Arena a = ArenaManager.getArena(player);
		if (a == null) return null;
		if (!a.getGameState().toString().startsWith("INGAME")) return null;
		SGPlayer target = null;
		int temp = 1000000000;
		for (SGPlayer sgp : a.getPlayers()) {
			if (sgp.isDead()) continue;
			if (sgp.getName().equalsIgnoreCase(player.getName())) continue;
			if (!sgp.getBukkitPlayer().getWorld().getName().equals(player.getBukkitPlayer().getWorld().getName())) continue;
			int distance = (int) sgp.getBukkitPlayer().getLocation().distance(player.getBukkitPlayer().getLocation());
			if (distance < temp) {
				target = sgp;
				temp = distance;
			}
		}
		return target;
	}
}
