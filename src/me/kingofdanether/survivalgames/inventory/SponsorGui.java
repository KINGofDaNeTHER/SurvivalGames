package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.item.SponsorItem;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SponsorGui extends GUI {

	private SGPlayer player;
	private SGPlayer viewer;
	private Arena a;
	private HashMap<Integer, SponsorItem> itemLocations;
	
	public SponsorGui(SGPlayer viewer, SGPlayer player, Arena a) {
		super((Math.round(((a.getSponsorItems().size() + 1) / 10) + 1) * 9), "&6Sponsor " + player.getName());
		this.viewer = viewer;
		this.player = player;
		this.a = a;
		this.itemLocations = new HashMap<Integer, SponsorItem>();
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		SponsorItem sponsor = itemLocations.get(slot);
		if (sponsor == null) return;
		if (!a.inGame()) {
			clicker.closeInventory();
			viewer.sendMessage(Constants.PREFIX + " &cThis game is over, or hasn't started!");
			viewer.playSound(Sound.BLOCK_ANVIL_LAND);
			return;
		}
		if (viewer.getClout(a.getGameStyle()) < sponsor.getCloutCost()) {
			viewer.sendActionBar("&cYou can't afford to sponsor " + player.getName() + "!");
			viewer.playSound(Sound.BLOCK_ANVIL_LAND);
			return;
		}
		clicker.closeInventory();
		viewer.setClout(viewer.getClout(a.getGameStyle()) - sponsor.getCloutCost(), a.getGameStyle());
		viewer.playSound(Sound.ENTITY_PLAYER_LEVELUP);
		player.playSound(Sound.ENTITY_PLAYER_LEVELUP);
		for (SGPlayer sgp : a.getPlayers()) {
			if (player.getName().equals(sgp.getName())) {
				player.sendMessage(Constants.PREFIX + " &a" + viewer.getName() + " has sponsonred you with x" + sponsor.getItemStack().getAmount() + " " + StringUtils.capitalize(sponsor.getItemStack().getType().toString().replace("_", " ") + "!"));
				continue;
			}
			sgp.sendMessage(Constants.PREFIX + " &a" + viewer.getName() + " has sponsored " + player.getName() + " with x" + sponsor.getItemStack().getAmount() + " " + StringUtils.capitalize(sponsor.getItemStack().getType().toString().replace("_", " ") + "!"));
		}
		if (player.getBukkitPlayer().getInventory().firstEmpty() == -1) { 
			player.sendMessage(Constants.PREFIX + " &cThe item was dropped on the floor since you have no inventory space!");
			player.getLocation().getWorld().dropItemNaturally(player.getLocation(), sponsor.getItemStack());
		} else {
			player.getBukkitPlayer().getInventory().addItem(sponsor.getItemStack());
		}
	}

	@Override
	public void setItems() {
		int count = 0;
		for (SponsorItem sponsorItem : a.getSponsorItems()) {
			ItemStack item = sponsorItem.getItemStack().clone();
			ItemUtils.applyItemFlags(item);
			ItemUtils.changeDisplayName(item, "&6Click to sponsor " + player.getName() + "!");
			ItemUtils.changeLore(item, Arrays.asList(StringUtils.colorize("&6x" + item.getAmount() + " " + StringUtils.capitalize(item.getType().toString().replace("_", " "))),
				StringUtils.colorize((viewer.getClout(a.getGameStyle()) >= sponsorItem.getCloutCost() ? "&a" : "&c") + "Cost: " + sponsorItem.getCloutCost() + " clout")));
			itemLocations.put(count, sponsorItem);
			this.getInventory().setItem(count, item);
			count++;
		}
	}
	
	public SGPlayer getPlayer() {return player;}
	public Arena getArena() {return a;}

}
