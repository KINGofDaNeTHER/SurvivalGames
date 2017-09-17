package me.kingofdanether.survivalgames.runnables;

import java.util.Iterator;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.enumeration.GameState;
import me.kingofdanether.survivalgames.enumeration.Rating;
import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.PlayerUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import mkremins.fanciful.FancyMessage;

public class EndGameTask extends BukkitRunnable {

	private Arena a;
	private int count;
	private int maxCount;
	private Player winner;
	
	public EndGameTask(Arena a, Player winner) {
		this.a = a;
		this.count = 0;
		this.maxCount = 6;
		this.winner = winner;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (count <= 5) {
			Firework f = winner.getWorld().spawn(winner.getLocation(), Firework.class);
			FireworkMeta fm = f.getFireworkMeta();
			fm.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.fromRGB(NumberUtils.randInt(1, 255), NumberUtils.randInt(1, 255), NumberUtils.randInt(1, 255))).build());
			fm.setPower(1);
			f.setFireworkMeta(fm); 
			if (count == 1) {
				f.setPassenger(winner);
			}
		}
		if (count >= maxCount) {
			this.cancel();
			a.setScoreboard(Constants.BLANK_SCOREBOARD);
			a.destroyEntities();
			Iterator<SGPlayer> iter = a.getPlayers().iterator();
			SGPlayer sgWinner = PlayerManager.getOrCreate(winner.getPlayerListName());
			PlayerUtils.setName(winner, "", "", TeamAction.DESTROY);
			if (sgWinner != null) {
				double clout = sgWinner.getClout(a.getGameStyle()) + a.getCloutPerWin();
				sgWinner.setClout(clout, a.getGameStyle());
				sgWinner.sendActionBar("&6You gained +" + a.getCloutPerWin() + " clout!");
				sgWinner.setGameKills(0);
			}
			while (iter.hasNext()) {
				SGPlayer player = iter.next();
				//XXX:player.setInGame(false);
				player.setDead(false);
				SurvivalGames.getInstance().getGhostFactory().setGhost(player.getBukkitPlayer(), false);
				player.fullHeal();
				if (player.getAttackSpeed() != 4.0D) {
					player.setAttackSpeed(4.0D);
				}
				if (a.isCrossWorld()) {
					PlayerUtils.restoreInventory(player);
				}
				player.getBukkitPlayer().teleport(PlayerUtils.loadPreviousLocation(player));
				if (!a.isCrossWorld()) {
					PlayerUtils.restoreInventory(player);
				}
				if (player.getBukkitPlayer().getGameMode() != GameMode.SURVIVAL) {
					player.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);	
				}
				if (player.getBukkitPlayer().getAllowFlight()) {
					player.getBukkitPlayer().setAllowFlight(false);
				}
				winner.showPlayer(player.getBukkitPlayer());
				player.getBukkitPlayer().showPlayer(winner);
				if (!player.canRate(a)) {
					player.setRateable(a, true);
					new FancyMessage(StringUtils.colorize(Constants.PREFIX + " &aRate this arena: ")).then(Rating.SHIT.getScoreName())
					.tooltip(StringUtils.colorize("&4This arena is shit!")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.SHIT.getScore())
					.then(" " + Rating.BAD.getScoreName()).tooltip(StringUtils.colorize("&cThis arena is bad.")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.BAD.getScore())
					.then(" " + Rating.OKAY.getScoreName()).tooltip(StringUtils.colorize("&eThis arena is alright :/")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.OKAY.getScore())
					.then(" " + Rating.GOOD.getScoreName()).tooltip(StringUtils.colorize("&aThis arena is good!")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.GOOD.getScore())
					.then(" " + Rating.AMAZING.getScoreName()).tooltip(StringUtils.colorize("&2This is the best shit i've ever seen!")).command("/sg %^&rate%^& " + a.getName() + " " + Rating.AMAZING.getScore())
					.send(player.getBukkitPlayer());
				}
				iter.remove();
				PlayerUtils.setName(player.getBukkitPlayer(), "", "", TeamAction.DESTROY);
			}
			a.getWorld().getWorldBorder().reset();
			a.getPlayers().clear();
			a.getMineLocs().clear();
			a.getEntityIds().clear();
			a.setGameState(GameState.POSTGAME_REBUILDING);
			new ResetArenaBlocksTask(a).runTaskTimer(SurvivalGames.getInstance(), 0, 1);
		}
		count++;
	}
	
}
