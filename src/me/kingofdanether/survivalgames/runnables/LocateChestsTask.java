package me.kingofdanether.survivalgames.runnables;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class LocateChestsTask extends BukkitRunnable {

	private Player executer;
	private Arena a;
	private List<Block> queue;
	private int count;
	private int maxCount;
	private int chestCount;
	private int speed;
	
	public LocateChestsTask(Player executer, Arena a, List<Block> queue) {
//		Iterator<Block> iter = queue.iterator();
//		while (iter.hasNext()) {
//			Block b = iter.next();
//			if ((b.getType() == Material.AIR) || !(b.getState() instanceof Chest)) {
//				iter.remove();
//			}
//		}
		this.executer = executer;
		this.a = a;
		this.queue = queue;
		this.count = 0;
		this.maxCount = (queue.size() - 1);
		this.chestCount = 0;
		this.speed = 20;
	}
	
	@Override
	public void run() {
		for (int i = 0; i <= speed; i++) {
			if (count > maxCount) {
				this.cancel();
				executer.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aFound " + chestCount + " new chest(s) in total!"));
				return;
			}
			final BlockState bs = queue.get(count).getState();
			if (bs.getBlock().getType() != Material.AIR && bs instanceof Chest) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!a.asyncHasChestAt(bs.getLocation())) {
							chestCount++;
							a.addChest(bs.getLocation());
							executer.sendMessage(StringUtils.colorize(Constants.PREFIX + " &aFound new chest at: " + LocationUtils.locToString(bs.getLocation())));
						}
					}
				}.runTask(SurvivalGames.getInstance());
			}
			count++;
			executer.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.colorize("&6Searching for chests: &e" + NumberUtils.getPercentage(count, queue.size()) + "% (" + (speed * 20) + " BPS)")));
		}
	}
	
}
