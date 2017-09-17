package me.kingofdanether.survivalgames.command.args;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.WorldUtils;

public class LeaveArena implements CommandExecutor {

	private static LeaveArena instance;
	
	public static LeaveArena getInstance() {
		if (instance == null) {
			return new LeaveArena();
		}
		return instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("sg")) {
			if (!sender.hasPermission("survivalgames.join")) {
				sender.sendMessage(Constants.PREFIX + " " + Constants.NO_PERMS_MSG);
				return true;
			}
			Player p = (Player)sender;
			final SGPlayer sgPlayer = PlayerManager.getOrCreate(p.getPlayerListName());
			final Arena a = ArenaManager.getArena(sgPlayer);
			if (!sgPlayer.inGame() || a == null) {
				sgPlayer.sendMessage(Constants.PREFIX + " &cYou are not in a game!");
				return true;
			}
			if (a.getGameState().toString().startsWith("INGAME") && !sgPlayer.isDead()) {
				//sgPlayer.getBukkitPlayer().getWorld().strikeLightningEffect(sgPlayer.getBukkitPlayer().getLocation());
				a.playSound(Sound.ENTITY_PLAYER_LEVELUP);
				sgPlayer.getBukkitPlayer().getWorld().spawnParticle(Particle.SNOW_SHOVEL, sgPlayer.getBukkitPlayer().getLocation(), 25);
				for (int i = 0; i <= 10; i++) {
					final Item item = sgPlayer.getBukkitPlayer().getWorld().dropItemNaturally(sgPlayer.getBukkitPlayer().getLocation(), new ItemStack(Material.INK_SACK, 1, (short)1));
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
				WorldUtils.spawnFirework(sgPlayer.getBukkitPlayer().getLocation(), Type.BALL, Color.RED, Color.WHITE, 2);
				new BukkitRunnable() {
					@Override
					public void run() {
						WorldUtils.spawnCorpse(sgPlayer.getBukkitPlayer(), sgPlayer.getBukkitPlayer().getLocation(), a);
					}
				}.runTaskLater(SurvivalGames.getInstance(), 1);
				PlayerUtils.dropItems(sgPlayer);
			}
			a.removePlayer(sgPlayer);
			//XXX:sgPlayer.setInGame(false);
			sgPlayer.setDead(false);
			if (a.isCrossWorld()) {
				PlayerUtils.restoreInventory(sgPlayer);
			}
			sgPlayer.getBukkitPlayer().teleport(PlayerUtils.loadPreviousLocation(sgPlayer));
			//a.destroyEntities();
			sgPlayer.sendMessage(Constants.PREFIX + " &aYou left the arena \"" + a.getName() + "\"!");
			for (SGPlayer player : a.getPlayers()) {
				if (!player.equals(sgPlayer)) {
					player.sendMessage(Constants.PREFIX + " &a" + sgPlayer.getName() + " has left the game! &e(" + a.getPlayers().size() + "/" + a.getMaxPlayers() + ")");
				}
			}
			if (!a.isCrossWorld()) {
				PlayerUtils.restoreInventory(sgPlayer);
			} 
			if (sgPlayer.getBukkitPlayer().getGameMode() != GameMode.SURVIVAL) {
				sgPlayer.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);	
			}
			if (sgPlayer.getBukkitPlayer().getAllowFlight()) {
				sgPlayer.getBukkitPlayer().setAllowFlight(false);
			}
			a.showPlayer(sgPlayer);
			SurvivalGames.getInstance().getGhostFactory().setGhost(sgPlayer.getBukkitPlayer(), false);
			if (sgPlayer.getAttackSpeed() != 4.0D) {
				sgPlayer.setAttackSpeed(4.0D);
			}
			if (!sgPlayer.canRate(a) && (a.getGameState() != GameState.LOBBY_WAITING && a.getGameState() != GameState.LOBBY_COUNTDOWN)) {
				sgPlayer.setRateable(a, true);
				sgPlayer.sendRatingMessage(a);
			}
			sgPlayer.sendActionBar("");
		}
		return true;
	}
	
}
