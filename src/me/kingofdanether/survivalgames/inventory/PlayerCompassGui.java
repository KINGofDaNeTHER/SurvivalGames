package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class PlayerCompassGui extends GUI {

	private Arena a;
	
	public PlayerCompassGui(Arena a) {
		super((Math.round(((a.getPlayersAliveAmount() + 1) / 10) + 1) * 9) + 9, StringUtils.colorize("&6Players"));
		this.a = a;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (clicked == null || clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) return;
		String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
		if (clickType == ClickType.LEFT) {
			this.playClickSound(clicker);
			clicker.closeInventory();
			clicker.teleport(Bukkit.getPlayer(name));
		} else if (clickType == ClickType.RIGHT) {
			this.playClickSound(clicker);
			SGPlayer sponsor = PlayerManager.getOrCreate(clicker.getPlayerListName());
			SGPlayer sponsored = PlayerManager.getOrCreate(Bukkit.getPlayer(name).getPlayerListName());
			if (sponsor == null || sponsored == null) {
				clicker.sendMessage(StringUtils.colorize(Constants.PREFIX + " &cAn error occurred while grabbing data for " + name + "!"));
				clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
				return;
			}
			SponsorGui sponsorGui = new SponsorGui(sponsor, sponsored, a);
			sponsorGui.open(clicker);
		}
	}

	@Override
	public void setItems() {
		for (SGPlayer player : a.getPlayers()) {
			if (player.isDead()) continue;
			this.getInventory().addItem(ItemUtils.createSkull("&6" + player.getName(), player.getName(), Arrays.asList(StringUtils.colorize("&7Left click to teleport!"), StringUtils.colorize("&7Right click for more options!"), StringUtils.colorize("&7Health &c❤ &7: " + NumberUtils.getPercentage((int)Math.round(player.getBukkitPlayer().getHealth()), 20) + "%"))));
		}
	}

}
