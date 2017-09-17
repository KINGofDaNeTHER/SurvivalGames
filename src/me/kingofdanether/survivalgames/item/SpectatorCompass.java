package me.kingofdanether.survivalgames.item;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.inventory.PlayerCompassGui;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class SpectatorCompass extends ClickableItem {

	private Arena a;
	
	public SpectatorCompass(Arena a) {
		super(ItemUtils.createItemStack(Material.COMPASS, StringUtils.colorize("&6Player Finder")), 4);
		this.a = a;
	}

	@Override
	public void rightClick(Player clicker, Action action, Block clicked) {
		new PlayerCompassGui(a).open(clicker);
	}

	@Override
	public void leftClick(Player clicker, Action action, Block clicked) {}

	@Override
	public void onDrop(Item dropped, Player dropper) {}

	@Override
	public void onHandSwitch(Player switcher) {}

	@Override
	public void onInventoryClick(Player clicker, int slot, ClickType click) {}

}
