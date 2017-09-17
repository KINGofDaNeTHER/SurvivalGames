package me.kingofdanether.survivalgames.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import me.kingofdanether.survivalgames.enumeration.TeamAction;
import me.kingofdanether.survivalgames.item.ItemManager;
import me.kingofdanether.survivalgames.player.SGPlayer;

@SuppressWarnings("deprecation")
public class PlayerUtils {

	private static Team team;
	private static Scoreboard scoreboard;

	public static void saveInventoryToFile(SGPlayer player) {
		player.getData().set("previous-inventory", player.getBukkitPlayer().getInventory().getContents());
		player.saveData();
	}

	public static void restoreInventory(SGPlayer player) {
		ItemStack[] contents;
		contents = (ItemStack[]) player.getData().get("previous-inventory");
		player.getBukkitPlayer().getInventory().setContents(contents);
	}

	public static void savePreviousLocation(SGPlayer player) {
		player.getData().set("previous-loc",
				LocationUtils.locToSavableString(player.getBukkitPlayer().getLocation(), ";"));
		player.saveData();
	}

	public static Location loadPreviousLocation(SGPlayer player) {
		return LocationUtils.parseLocation(player.getData().getString("previous-loc"), ";");
	}

	public static void dropItems(SGPlayer player) {
		for (ItemStack item : player.getBukkitPlayer().getInventory()) {
			if (item == null || ItemManager.getItem(item) != null)
				continue;
			player.getBukkitPlayer().getWorld().dropItemNaturally(player.getBukkitPlayer().getLocation(), item);
		}
	}

	public static void setName(Player player, String prefix, String suffix, TeamAction action) {
		if (player.getScoreboard() == null || prefix == null || suffix == null || action == null) {
			return;
		}

		scoreboard = player.getScoreboard();

		if (scoreboard.getTeam(player.getName()) == null) {
			scoreboard.registerNewTeam(player.getName());
		}

		team = scoreboard.getTeam(player.getName());
		team.setPrefix(StringUtils.colorize(prefix));
		team.setSuffix(StringUtils.colorize(suffix));
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);

		switch (action) {
		case CREATE:
			team.addPlayer(player);
			break;
		case UPDATE:
			team.unregister();
			scoreboard.registerNewTeam(player.getName());
			team = scoreboard.getTeam(player.getName());
			team.setPrefix(StringUtils.colorize(prefix));
			team.setSuffix(StringUtils.colorize(suffix));
			team.setNameTagVisibility(NameTagVisibility.ALWAYS);
			team.addPlayer(player);
			break;
		case DESTROY:
			team.removePlayer(player);
			team.unregister();
			break;
		}
	}

}
