package me.kingofdanether.survivalgames.sign;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.kingofdanether.survivalgames.SurvivalGames;
import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.arena.ArenaManager;
import me.kingofdanether.survivalgames.arena.ArenaSign;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.LocationUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SignHandler implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if (e.getHand() == EquipmentSlot.OFF_HAND) return;
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (e.getClickedBlock() == null) return;
		if (!(e.getClickedBlock().getState() instanceof Sign)) return;
		Sign clicked = (Sign)e.getClickedBlock().getState();
		SGPlayer clicker = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		if (clicker == null) return;
		GameSign sign = SignManager.getSign(clicked);
		if (sign == null) return;
		if (sign instanceof ArenaSign) {
			clicker.getBukkitPlayer().performCommand("sg join " + sign.getArena().getName());
		}
		return;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignEdit(SignChangeEvent e) {
		if (e.isCancelled()) return;
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		if (player == null) return;
		String[] lines = e.getLines();
		if (lines[0].equals("[" + SurvivalGames.getInstance().getName() + "]")) {
			if (!player.getBukkitPlayer().hasPermission("survivalgames.signs.create")) {
				e.setLine(0, StringUtils.colorize("&4[" + SurvivalGames.getInstance().getName() + "]"));
				e.setLine(1, StringUtils.colorize("&4&o(Error) No perms!"));
				player.sendMessage(Constants.PREFIX + " &cYou aren't allowed to create an arena sign!");
				return;
			}
			if (e.getLine(1).equals("") || e.getLine(1).equals(" ")) {
				e.setLine(0, StringUtils.colorize("&4[" + SurvivalGames.getInstance().getName() + "]"));
				player.sendMessage(Constants.PREFIX + " &cPlease enter an arena name on the second line!");
				return;
			} else if (lines.length >= 2) {
				Arena a = ArenaManager.getArena(lines[1]);
				if (a == null) {
					e.setLine(0, StringUtils.colorize("&4[" + SurvivalGames.getInstance().getName() + "]"));
					e.setLine(1, StringUtils.colorize("&c" + lines[1]));
					player.sendMessage(Constants.PREFIX + " &c\"" + lines[1] + "\" is not a valid arena!");
					return;
				}
				e.setLine(0, StringUtils.colorize("&6[&aNCCSG&6]"));
				e.setLine(1, StringUtils.colorize("&2&oLoading Data."));
				int id = 0;
				if (SurvivalGames.getInstance().getSignsYml().get("signs") == null) {
					id = 1;
				} else {
					List<Integer> ids = new ArrayList<Integer>();
					for (String s : SurvivalGames.getInstance().getSignsYml().getConfigurationSection("signs").getKeys(false)) {
						ids.add(Integer.valueOf(s));
					}
					id = NumberUtils.getHighest(ids);
				}
				ArenaSign sign = new ArenaSign((Sign)e.getBlock().getState(), id, a);
				SurvivalGames.getInstance().getSignsYml().set("signs." + id + ".arena", a.getName());
				SurvivalGames.getInstance().getSignsYml().set("signs." + id + ".location", LocationUtils.locToSavableString(e.getBlock().getLocation(), ";"));
				SurvivalGames.getInstance().getSignsYml().set("signs." + id + ".creator", player.getName());
				SurvivalGames.getInstance().getSignsYml().saveConfig();
				sign.update();
				SignManager.addSign(sign);
				player.sendMessage(Constants.PREFIX + " &aCreated a sign for arena \"" + a.getName() + "\"!");
				player.getBukkitPlayer().playEffect(e.getBlock().getLocation(), Effect.SMOKE, 0);
				player.playSound(Sound.BLOCK_NOTE_PLING);
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignBreak(BlockBreakEvent e) {
		if (e.isCancelled()) return;
		Block b = e.getBlock();
		if (!(b.getState() instanceof Sign)) return;
		SGPlayer player = PlayerManager.getOrCreate(e.getPlayer().getPlayerListName());
		if (player == null) return;
		GameSign sign = SignManager.getSign(b.getLocation());
		if (sign == null) return;
		if (!player.getBukkitPlayer().hasPermission("survivalgames.signs.break")) {
			e.setCancelled(true);
			sign.getSign().setLine(3, StringUtils.colorize("&4&o(Error) No perms!"));
			player.sendMessage(Constants.PREFIX + " &cYou aren't allowed to break an arena sign!");
			return;
		}
		if (SurvivalGames.getInstance().getSignsYml().get("signs." + sign.getID()) == null) return;
		SurvivalGames.getInstance().getSignsYml().set("signs." + sign.getID(), null);
		SurvivalGames.getInstance().getSignsYml().saveConfig();
		sign.getSign().setLine(0, StringUtils.colorize("&6[&a" + SurvivalGames.getInstance().getName() + "&6]"));
		sign.getSign().setLine(1, StringUtils.colorize("&4&oErasing Data."));
		sign.getSign().setLine(2, "");
		sign.getSign().setLine(3, "");
		SignManager.removeSign(sign);
		player.sendMessage(Constants.PREFIX + " &cYou broke a sign for arena \"" + sign.getArena().getName() + "\"!");
		player.getBukkitPlayer().playEffect(e.getBlock().getLocation(), Effect.SMOKE, 0);
		player.playSound(Sound.ENTITY_GENERIC_EXPLODE);
	}
}
