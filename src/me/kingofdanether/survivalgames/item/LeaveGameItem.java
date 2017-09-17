package me.kingofdanether.survivalgames.item;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.player.PlayerManager;
import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.sign.SignManager;
import me.kingofdanether.survivalgames.util.Constants;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class LeaveGameItem extends ClickableItem {

	private Arena a;
	
	public LeaveGameItem(Arena a) {
		super(ItemUtils.createItemStack(Material.INK_SACK, (short)1, StringUtils.colorize("&cQuit")), 8);
		this.a = a;
	}

	@Override
	public void rightClick(Player clicker, Action action, Block clicked) {
		SGPlayer sgPlayer = PlayerManager.getOrCreate(clicker.getPlayerListName());
		if (!sgPlayer.inGame()) {
			sgPlayer.sendMessage(Constants.PREFIX + " &cYou are not in a game!");
			return;
		}
		if (clicked != null) {
			if (clicked.getState() instanceof Sign) {
				Sign sign = (Sign)clicked.getState();
				if (SignManager.getSign(sign) != null) return;
			}
		}
		clicker.performCommand("sg leave");
	}

	@Override
	public void leftClick(Player clicker, Action action, Block clicked) {}

	@Override
	public void onDrop(Item dropped, Player dropper) {}

	@Override
	public void onHandSwitch(Player switcher) {}
	
	public Arena getArena() {return a;}

	@Override
	public void onInventoryClick(Player clicker, int slot, ClickType click) {}

}
