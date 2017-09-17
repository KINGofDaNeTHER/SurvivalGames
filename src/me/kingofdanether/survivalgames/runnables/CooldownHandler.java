package me.kingofdanether.survivalgames.runnables;

import java.util.Map.Entry;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.player.Cooldown;
import me.kingofdanether.survivalgames.player.CooldownManager;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class CooldownHandler extends BukkitRunnable {

	@Override
	public void run() {
		if (CooldownManager.getAllCooldowns().size() <= 0) return;
		for (Entry<String, Cooldown> key : CooldownManager.getAllCooldowns().entrySet()) {
            Cooldown cooldown = key.getValue();
			String name = key.getKey();
            SGPlayer player = PlayerManager.getOrCreate(name);
            if (player == null || player.isDead() || !player.inGame()) continue;
			if(CooldownManager.getTimeRemaining(name) <= 0.0) {
				player.sendActionBar("&f" + cooldown.getAbility() + " " + StringUtils.getTimerBar((100.0D - NumberUtils.getPercentage(CooldownManager.getTimeRemaining(name), cooldown.getNormalSeconds()))) + " &fReady");
				player.playSound(Sound.BLOCK_NOTE_PLING);
				CooldownManager.removeCooldown(name);
                continue;
            }
            player.sendActionBar("&f" + cooldown.getAbility() + " " + StringUtils.getTimerBar((100.0D - NumberUtils.getPercentage(CooldownManager.getTimeRemaining(name), cooldown.getNormalSeconds()))) + " &f" + CooldownManager.getTimeRemaining(name) + "s");
		}
	}

}
