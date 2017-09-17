package me.kingofdanether.survivalgames.inventory;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.player.SGPlayer;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.NumberUtils;
import me.kingofdanether.survivalgames.util.StringUtils;

public class PlayerSpectateGui extends GUI {

	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7);
	
	private SGPlayer player;
	
	public PlayerSpectateGui(SGPlayer player) {
		super(27, "&6" + player.getName());
		this.player = player;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (slot == 13) {
			if (clickType == ClickType.LEFT) {
				this.playClickSound(clicker);
				clicker.closeInventory();
				clicker.teleport(player.getBukkitPlayer().getLocation());
			} else if (clickType == ClickType.RIGHT) {
				//OPTIONS GUI
			}
		}
	}

	@Override
	public void setItems() {
		ItemStack skull = ItemUtils.createSkull("&6" + player.getName() + " (&c❤  " + NumberUtils.getPercentage((int)Math.round(player.getBukkitPlayer().getHealth()), 20) + "%&6)", player.getName(), Arrays.asList(StringUtils.colorize("&7Left click to teleport!"), StringUtils.colorize("&7RIght click for more options!")));
		this.getInventory().setItem(13, skull);
		for (int i = 0; i < this.getSize(); i++) {
			if (i != 13) {
				this.getInventory().setItem(i, glass);
			}
		}
	}

}
