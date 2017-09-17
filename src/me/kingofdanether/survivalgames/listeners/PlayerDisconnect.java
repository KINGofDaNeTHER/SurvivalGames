package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.WorldUtils;

public class PlayerDisconnect implements Listener {
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		final SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		final Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null) return;
		a.removePlayer(player);
		if (player.getAttackSpeed() != 4.0D) player.setAttackSpeed(4.0D);
		for (SGPlayer sgPlayer : a.getPlayers()) {
			if (!sgPlayer.equals(player)) {
				if (a.getGameState() == GameState.LOBBY_WAITING || a.getGameState() == GameState.LOBBY_COUNTDOWN) {
					sgPlayer.sendMessage(Constants.PREFIX + " &a" + player.getName() + " has logged out! &e(" + a.getPlayers().size() + "/" + a.getMaxPlayers() + ")");
				} else if (a.getGameState() != GameState.POSTGAME_ENDING) {
					sgPlayer.sendMessage(Constants.PREFIX + " &a" + player.getName() + " has logged out!");
				}
			}
		}
		if (a.getGameState() != GameState.LOBBY_COUNTDOWN && a.getGameState() != GameState.LOBBY_WAITING && a.getGameState() != GameState.POSTGAME_ENDING) {
			//player.getBukkitPlayer().getWorld().strikeLightningEffect(player.getBukkitPlayer().getLocation());
			a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
			player.getBukkitPlayer().getWorld().spawnParticle(Particle.SNOW_SHOVEL, player.getBukkitPlayer().getLocation(), 25);
			for (int i = 0; i <= 10; i++) {
				final Item item = player.getBukkitPlayer().getWorld().dropItemNaturally(player.getBukkitPlayer().getLocation(), new ItemStack(Material.INK_SACK, 1, (short)1));
				item.setVelocity(WorldUtils.randVector(2.5, 3, 2.5));
				item.setPickupDelay(1000);
				new BukkitRunnable() {
					@Override
					public void run() {
						if (item != null) {
							item.remove();
						}
					}
				}.runTaskLater(SurvivalGames.getInstance(), (20 * 3));
			}
			WorldUtils.spawnFirework(player.getBukkitPlayer().getLocation(), Type.BALL, Color.RED, Color.WHITE, 2);
			PlayerUtils.setName(player.getBukkitPlayer(), "", "", TeamAction.DESTROY);
			new BukkitRunnable() {
				@Override
				public void run() {
					WorldUtils.spawnCorpse(player.getBukkitPlayer(), player.getBukkitPlayer().getLocation(), a);
				}
			}.runTaskLater(SurvivalGames.getInstance(), 1);
			PlayerUtils.dropItems(player);
		}
		//XXX:player.setInGame(false);
		if (a.isCrossWorld()) {
			PlayerUtils.restoreInventory(player);
		}
		player.getBukkitPlayer().teleport(PlayerUtils.loadPreviousLocation(player));
		if (!a.isCrossWorld()) {
			PlayerUtils.restoreInventory(player);
		} 
		a.showPlayer(player);
	}

}
