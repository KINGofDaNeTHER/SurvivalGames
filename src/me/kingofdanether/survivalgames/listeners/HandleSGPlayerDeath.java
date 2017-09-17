package me.kingofdanether.survivalgames.listeners;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.SGAchievement;
import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.player.CooldownManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import me.kingofdanether.survivalgames.util.WorldUtils;
import mkremins.fanciful.FancyMessage;

public class HandleSGPlayerDeath implements Listener {

	@EventHandler
	public void onRespawn(final PlayerRespawnEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
				final Arena a = ArenaManager.getArena(player);
				if (!player.inGame() || a == null || !player.isDead()) return;
				if (!a.getGameState().toString().startsWith("INGAME")) return;
				Location deadLoc = player.getData().get("dead-loc") == null ? null : LocationUtils.parseLocation(player.getData().getString("dead-loc"), ";");
				if (deadLoc == null) return;
				if (player.getBukkitPlayer().getWorld().getName().equals(deadLoc.getWorld().getName())) return;
				deadLoc.setY(WorldUtils.getHighestY(deadLoc));
				player.getBukkitPlayer().teleport(deadLoc.add(0.5, 2, 0.5));
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.getBukkitPlayer().getGameMode() != GameMode.ADVENTURE) {
							player.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
						}
						player.getBukkitPlayer().setAllowFlight(true);
						player.getBukkitPlayer().getInventory().clear();
						a.getLeaveItem().give(player.getBukkitPlayer());
						a.getSpectatorCompass().give(player.getBukkitPlayer());
						player.fullHeal();
					}
				}.runTaskLater(SurvivalGames.getInstance(), 2);
			}
		}.runTaskLater(SurvivalGames.getInstance(), (20 * 3));
	}

	@EventHandler
	public void onDeath(final PlayerDeathEvent e) {
		final SGPlayer player = PlayerManager.getOrCreate(e.getEntity().getPlayerListName());
		final Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null || player.isDead()) return;
		if (!a.getGameState().toString().startsWith("INGAME")) return;
		e.setDeathMessage(null);
		new BukkitRunnable() {
			@Override
			public void run() {
				Entity killer = player.getBukkitPlayer().getKiller();
				if (killer != null) {
					if (killer instanceof Player) {
						Player killr = ((Player)killer);
						ItemStack inHand = killr.getInventory().getItemInMainHand();
						String item;
						if (inHand == null || inHand.getType() == Material.AIR) item = "Fists";
						item = (inHand.getItemMeta() != null && inHand.getItemMeta().getDisplayName() != null) ? inHand.getItemMeta().getDisplayName() : StringUtils.capitalize(inHand.getType().toString().toLowerCase());
						a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " &6was killed by &e" + killr.getPlayerListName() + " &6with &e" + item.replace("_", " ") + "&6!");
						SGPlayer sgKiller = PlayerManager.getOrCreate(killr.getPlayerListName());
						int kills = sgKiller.getKills(a.getGameStyle());
						sgKiller.setKills((kills + 1), a.getGameStyle());
						sgKiller.setGameKills(sgKiller.getGameKills() + 1);
						double clout = (sgKiller.getGameKills() * a.getCloutMult());
						sgKiller.setClout(sgKiller.getClout(a.getGameStyle()) + clout, a.getGameStyle());
						if (!CooldownManager.hasCooldown(player.getName())) {
							sgKiller.sendActionBar("&6You gained +" + clout + " clout!");
						}
						SGAchievement achievement = null;
						try {
							achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_KILL");
						} catch (Exception ex) {}
						if (!sgKiller.hasAchievement(achievement)) {
							sgKiller.playSound(Sound.ENTITY_PLAYER_LEVELUP);
							sgKiller.addAchievement(achievement);
							new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(sgKiller.getBukkitPlayer());
						}
					} else {
						a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " &6was killed by &e" + StringUtils.capitalize(killer.getType().toString().toLowerCase()));
					}
				} else {
					DamageCause damageCause = player.getBukkitPlayer().getLastDamageCause().getCause();
					if (damageCause != null) {
						a.sendMessage(StringUtils.getDeathMessageWithKiller(player, player.getLastDamager(), damageCause));
					} else {
						a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " has died!");
					}
				}
				//player.getBukkitPlayer().getWorld().strikeLightningEffect(player.getBukkitPlayer().getLocation());
				a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				player.getBukkitPlayer().getWorld().spawnParticle(Particle.SNOW_SHOVEL, player.getBukkitPlayer().getLocation(), 25);
				for (int i = 0; i <= 10; i++) {
					final Item item = player.getBukkitPlayer().getWorld().dropItemNaturally(player.getBukkitPlayer().getLocation(), new ItemStack(Material.INK_SACK, 1, (short)1));
					item.setVelocity(WorldUtils.randVector(0.35, 0.45, 0.35));
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
				WorldUtils.spawnFirework(player.getBukkitPlayer().getLocation(), Type.CREEPER, Color.RED, Color.RED, 1);
				PlayerUtils.setName(player.getBukkitPlayer(), "",  StringUtils.colorize(" &4✘"), TeamAction.CREATE);
				SurvivalGames.getInstance().getGhostFactory().setGhost(player.getBukkitPlayer(), true);
				if (player.getAttackSpeed() != 4.0D) {
					player.setAttackSpeed(4.0D);
				}
				player.setDead(true);
				final Player dead = (Player)e.getEntity();
				player.getData().set("dead-loc", LocationUtils.locToSavableString(dead.getLocation(), ";"));
				int deaths = player.getDeaths(a.getGameStyle());
				player.setDeaths((deaths + 1), a.getGameStyle());
				player.setGameKills(0);
				if (player.getClout(a.getGameStyle()) > 0) {
					player.setClout((player.getClout(a.getGameStyle()) - 1), a.getGameStyle());
					if (!CooldownManager.hasCooldown(player.getName())) {
						player.sendActionBar("&6You gained -1 clout!");
					}
				}
				if (player.getDeaths(a.getGameStyle()) >= 500) {
					SGAchievement achievement = null;
					try {
						achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_500_DEATHS");
					} catch (Exception ex) {}
					if (!player.hasAchievement(achievement)) {
						player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
						player.addAchievement(achievement);
						new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(player.getBukkitPlayer());
					}
				}
				final Location deadLoc = dead.getLocation();
				player.respawn();
				deadLoc.setY(WorldUtils.getHighestY(deadLoc));
				player.getBukkitPlayer().teleport(deadLoc.add(0.5, 2, 0.5));
				player.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
				player.getBukkitPlayer().setAllowFlight(true);
				if (player.getBukkitPlayer().getWorld().getName().equals(deadLoc.getWorld().getName())) {
					player.getBukkitPlayer().getInventory().clear();
					a.getLeaveItem().give(player.getBukkitPlayer());
					a.getSpectatorCompass().give(player.getBukkitPlayer());
				}
				a.hidePlayer(player);
				player.fullHeal();
				new BukkitRunnable() {
					@Override
					public void run() {
						WorldUtils.spawnCorpse(player.getBukkitPlayer(), deadLoc.add(0,-2,0), a);
					}
				}.runTaskLater(SurvivalGames.getInstance(), 1);
				if (!player.canRate(a)) {
					player.setRateable(a, true);
					player.sendRatingMessage(a);
				}
			}
		}.runTaskLater(SurvivalGames.getInstance(), 20);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (e.isCancelled()) return;
		if (!(e.getEntity() instanceof Player)) return;
		final SGPlayer player = PlayerManager.getOrCreate(((Player)e.getEntity()).getPlayerListName());
		final Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null || player.isDead()) return;
		if (!a.getGameState().toString().startsWith("INGAME")) return;
		double currentHealth = ((Player)e.getEntity()).getHealth();
		double damage = e.getFinalDamage();
		if ((currentHealth - damage) > 0) return;
		Entity killer = e.getDamager();
		if (killer != null) {
			if (killer instanceof Player) {
				Player killr = ((Player)killer);
				ItemStack inHand = killr.getInventory().getItemInMainHand();
				String item;
				if (inHand == null || inHand.getType() == Material.AIR) item = "Fists";
				item = (inHand.getItemMeta() != null && inHand.getItemMeta().getDisplayName() != null) ? inHand.getItemMeta().getDisplayName() : StringUtils.capitalize(inHand.getType().toString().toLowerCase());
				a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " &6was killed by &e" + killr.getPlayerListName() + " &6with &e" + item.replace("_", " ") + "&6!");
				SGPlayer sgKiller = PlayerManager.getOrCreate(killr.getPlayerListName());
				int kills = sgKiller.getKills(a.getGameStyle());
				sgKiller.setKills((kills + 1), a.getGameStyle());
				sgKiller.setGameKills(sgKiller.getGameKills() + 1);
				double clout = (sgKiller.getGameKills() * a.getCloutMult());
				sgKiller.setClout(sgKiller.getClout(a.getGameStyle()) + 1, a.getGameStyle());
				if (!CooldownManager.hasCooldown(player.getName())) {
					sgKiller.sendActionBar("&6You gained +" + clout + " clout!");
				}
				SGAchievement achievement = null;
				try {
					achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_KILL");
				} catch (Exception ex) {}
				if (!sgKiller.hasAchievement(achievement)) {
					sgKiller.playSound(Sound.ENTITY_PLAYER_LEVELUP);
					sgKiller.addAchievement(achievement);
					new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(sgKiller.getBukkitPlayer());
				}
			} else if (killer instanceof Projectile) {
				Projectile proj = (Projectile)killer;
				if (proj.getShooter() == null || (!(proj.getShooter() instanceof Player))) return;
				Player killr = ((Player) ((Projectile)killer).getShooter());
				ItemStack inHand = killr.getInventory().getItemInMainHand();
				String item;
				if (inHand == null || inHand.getType() == Material.AIR) item = "Fists";
				item = (inHand.getItemMeta() != null && inHand.getItemMeta().getDisplayName() != null) ? inHand.getItemMeta().getDisplayName() : StringUtils.capitalize(inHand.getType().toString().toLowerCase());
				a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " &6was shot by &e" + killr.getPlayerListName() + " &6with &e" + item.replace("_", " ") + "&6!");
				SGPlayer sgKiller = PlayerManager.getOrCreate(killr.getPlayerListName());
				int kills = sgKiller.getKills(a.getGameStyle());
				sgKiller.setKills((kills + 1), a.getGameStyle());
				sgKiller.setGameKills(sgKiller.getGameKills() + 1);
				double clout = (sgKiller.getGameKills() * a.getCloutMult());
				sgKiller.setClout(sgKiller.getClout(a.getGameStyle()) + clout, a.getGameStyle());
				if (!CooldownManager.hasCooldown(player.getName())) {
					sgKiller.sendActionBar("&6You gained +" + clout + " clout!");
				}
				SGAchievement achievement = null;
				try {
					achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_FIRST_BOW_KILL");
				} catch (Exception ex) {}
				if (!sgKiller.hasAchievement(achievement)) {
					sgKiller.playSound(Sound.ENTITY_PLAYER_LEVELUP);
					sgKiller.addAchievement(achievement);
					new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(sgKiller.getBukkitPlayer());
				}
			} else {
				a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " &6was killed by &e" + StringUtils.capitalize(killer.getType().toString().toLowerCase()));
			}
		} else {
			DamageCause damageCause = player.getBukkitPlayer().getLastDamageCause().getCause();
			if (damageCause != null) {
				a.sendMessage(StringUtils.getDeathMessageWithKiller(player, player.getLastDamager(), damageCause));
			} else {
				a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " has died!");
			}
		}
		e.setCancelled(true);
		//player.getBukkitPlayer().getWorld().strikeLightningEffect(player.getBukkitPlayer().getLocation());
		PlayerUtils.dropItems(player);
		a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
		player.getBukkitPlayer().getWorld().spawnParticle(Particle.SNOW_SHOVEL, player.getBukkitPlayer().getLocation(), 25);
		for (int i = 0; i <= 10; i++) {
			final Item item = player.getBukkitPlayer().getWorld().dropItemNaturally(player.getBukkitPlayer().getLocation(), new ItemStack(Material.INK_SACK, 1, (short)1));
			item.setVelocity(WorldUtils.randVector(0.35, 0.45, 0.35));
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
		final Location npcLoc = player.getBukkitPlayer().getLocation();
		new BukkitRunnable() {
			@Override
			public void run() {
				WorldUtils.spawnCorpse(player.getBukkitPlayer(), npcLoc, a);
			}
		}.runTaskLater(SurvivalGames.getInstance(), 1);
		WorldUtils.spawnFirework(player.getBukkitPlayer().getLocation(), Type.CREEPER, Color.RED, Color.RED, 1);
		PlayerUtils.setName(player.getBukkitPlayer(), "",  StringUtils.colorize(" &4✘"), TeamAction.CREATE);
		SurvivalGames.getInstance().getGhostFactory().setGhost(player.getBukkitPlayer(), true);
		if (player.getAttackSpeed() != 4.0D) {
			player.setAttackSpeed(4.0D);
		}
		player.setDead(true);
		player.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
		player.getBukkitPlayer().setAllowFlight(true);
		Location deadLoc = killer.getLocation();
		deadLoc.setY(WorldUtils.getHighestY(deadLoc));
		player.getBukkitPlayer().teleport(deadLoc.add(0.5,2,0.5));
		player.getBukkitPlayer().getInventory().clear();
		a.getLeaveItem().give(player.getBukkitPlayer());
		a.getSpectatorCompass().give(player.getBukkitPlayer());
		a.hidePlayer(player);
		player.fullHeal();
		int deaths = player.getDeaths(a.getGameStyle());
		player.setDeaths((deaths + 1), a.getGameStyle());
		player.setGameKills(0);
		if (player.getClout(a.getGameStyle()) > 0) {
			player.setClout((player.getClout(a.getGameStyle()) - 1), a.getGameStyle());
			if (!CooldownManager.hasCooldown(player.getName())) {
				player.sendActionBar("&6You gained -1 clout!");
			}
		}
		if (player.getDeaths(a.getGameStyle()) >= 500) {
			SGAchievement achievement = null;
			try {
				achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_500_DEATHS");
			} catch (Exception ex) {}
			if (!player.hasAchievement(achievement)) {
				player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				player.addAchievement(achievement);
				new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(player.getBukkitPlayer());
			}
		}
		if (!player.canRate(a)) {
			player.setRateable(a, true);
			player.sendRatingMessage(a);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (!(e.getEntity() instanceof Player)) return;
		final SGPlayer player = PlayerManager.getOrCreate(((Player)e.getEntity()).getPlayerListName());
		final Arena a = ArenaManager.getArena(player);
		if (!player.inGame() || a == null || player.isDead()) return;
		if (!a.getGameState().toString().startsWith("INGAME")) return;
		double currentHealth = ((Player)e.getEntity()).getHealth();
		double damage = e.getFinalDamage();
		if ((currentHealth - damage) > 0) return;
		DamageCause damageCause = player.getBukkitPlayer().getLastDamageCause() == null ? null : player.getBukkitPlayer().getLastDamageCause().getCause();
		if (damageCause == DamageCause.ENTITY_ATTACK || damageCause == DamageCause.PROJECTILE) return;
		if (damageCause != null) {
			a.sendMessage(StringUtils.getDeathMessageWithKiller(player, player.getLastDamager(), damageCause));
		} else {
			a.sendMessage(Constants.PREFIX + " &e" + player.getName() + " has died!");
		}
		e.setCancelled(true);
		PlayerUtils.dropItems(player);
		//player.getBukkitPlayer().getWorld().strikeLightningEffect(player.getBukkitPlayer().getLocation());
		a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
		player.getBukkitPlayer().getWorld().spawnParticle(Particle.SNOW_SHOVEL, player.getBukkitPlayer().getLocation(), 25);
		for (int i = 0; i <= 10; i++) {
			final Item item = player.getBukkitPlayer().getWorld().dropItemNaturally(player.getBukkitPlayer().getLocation(), new ItemStack(Material.INK_SACK, 1, (short)1));
			item.setVelocity(WorldUtils.randVector(0.35, 0.45, 0.35));
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
		WorldUtils.spawnFirework(player.getBukkitPlayer().getLocation(), Type.CREEPER, Color.RED, Color.RED, 1);
		PlayerUtils.setName(player.getBukkitPlayer(), "", StringUtils.colorize(" &4✘"), TeamAction.CREATE); 
		SurvivalGames.getInstance().getGhostFactory().setGhost(player.getBukkitPlayer(), true);
		if (player.getAttackSpeed() != 4.0D) {
			player.setAttackSpeed(4.0D);
		}
		player.setDead(true);
		player.getBukkitPlayer().setGameMode(GameMode.ADVENTURE);
		player.getBukkitPlayer().setAllowFlight(true);
		final Location deadLoc = player.getBukkitPlayer().getLocation();
		deadLoc.setY(WorldUtils.getHighestY(deadLoc));
		player.getBukkitPlayer().teleport(deadLoc.add(0.5, 2, 0.5));
		player.getBukkitPlayer().getInventory().clear();
		a.getLeaveItem().give(player.getBukkitPlayer());
		a.getSpectatorCompass().give(player.getBukkitPlayer());
		a.hidePlayer(player);
		player.fullHeal();
		new BukkitRunnable() {
			@Override
			public void run() {
				WorldUtils.spawnCorpse(player.getBukkitPlayer(), deadLoc, a);
			}
		}.runTaskLater(SurvivalGames.getInstance(), 1);
		int deaths = player.getDeaths(a.getGameStyle());
		player.setDeaths((deaths + 1), a.getGameStyle());
		player.setGameKills(0);
		if (player.getClout(a.getGameStyle()) > 0) {
			player.setClout((player.getClout(a.getGameStyle()) - 1), a.getGameStyle());
			if (!CooldownManager.hasCooldown(player.getName())) {
				player.sendActionBar("&6You gained -1 clout!");
			}
		}
		if (player.getDeaths(a.getGameStyle()) >= 500) {
			SGAchievement achievement = null;
			try {
				achievement = SGAchievement.valueOf(a.getGameStyle().toString() + "_500_DEATHS");
			} catch (Exception ex) {}
			if (!player.hasAchievement(achievement)) {
				player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				player.addAchievement(achievement);
				new FancyMessage(achievement.getMessage()).tooltip(achievement.getToolTip()).send(player.getBukkitPlayer());
			}
		}
		if (!player.canRate(a)) {
			player.setRateable(a, true);
			player.sendRatingMessage(a);
		}
	}
	
}
