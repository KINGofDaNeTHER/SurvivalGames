package me.kingofdanether.survivalgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;

public class CommandPreprocess implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if (e.isCancelled()) return;
		String eventMsg = e.getMessage().replace("/", "");
		if (eventMsg.startsWith("sg") || eventMsg.startsWith("survivalgames") || eventMsg.startsWith(SurvivalGames.getInstance().getName())) return;
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		if (player == null) return;
		String command = eventMsg.split(" ")[0];
		if (!player.inGame() || player.adminEnabled() || SurvivalGames.getInstance().getConfig().getStringList("whitelisted-commands").contains(command)) return;
		e.setCancelled(true);
		player.sendMessage(Constants.PREFIX + " &cYou cannot use this command while in game!");
		if (player.getBukkitPlayer().hasPermission("survivalgames.adminmode")) {
			player.sendMessage(Constants.PREFIX + " &eYou can bypass by using &6/sg admin&e!");
			return;
		}
	}
	
}
