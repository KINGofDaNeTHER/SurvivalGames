package me.kingofdanether.survivalgames.inventory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import me.kingofdanether.survivalgames.arena.Arena;
import me.kingofdanether.survivalgames.util.ItemUtils;
import me.kingofdanether.survivalgames.util.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EditMinPlayersGui extends GUI {

	private Arena a;
	private ItemStack addArrow = ItemUtils.createItemStack(Material.ARROW, StringUtils.colorize("&6&l+"));
	private ItemStack subArrow = ItemUtils.createItemStack(Material.ARROW, StringUtils.colorize("&6&l-"));
	private ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)5);
	private ItemStack goBack = ItemUtils.createItemStack(Material.STAINED_CLAY, (short)14, StringUtils.colorize("&6Go Back"));
	
	public EditMinPlayersGui(Arena a) {
		super(27, "&6" + a.getName() + ": min players");
		this.a = a;
	}

	@Override
	public void clickItem(Player clicker, int slot, ItemStack clicked, ClickType clickType) {
		if (slot == 9) {
			this.playClickSound(clicker);
			if ((a.getMinPlayers() - 1) >= 2) {
				a.setMinPlayers(a.getMinPlayers() - 1);
				clicker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.colorize("&6New min players: &e" + a.getMinPlayers())));
				clicker.updateInventory();
			} else {
				clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
				return;
			}
			this.updateValues();
		} else if (slot == 17) {
			this.playClickSound(clicker);
			if ((a.getMinPlayers() + 1) <= (a.getMaxPlayers()-1)) {
				a.setMinPlayers(a.getMinPlayers() + 1);
				clicker.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringUtils.colorize("&6New min players: &e" + a.getMinPlayers())));
				clicker.updateInventory();
			} else {
				clicker.playSound(clicker.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, 1.0F);
				return;
			}
			this.updateValues();
		} else if (slot == 22) {
			this.playClickSound(clicker);
			new ArenaEditGui(a).open(clicker);
		}
	}

	@Override
	public void setItems() {
		for (int i = 0; i < this.getSize(); i++) {
			if (i == 9) {
				this.getInventory().setItem(i, subArrow);
				continue;
			}
			if (i == 17) {
				this.getInventory().setItem(i, addArrow);
				continue;
			}
			if (i == 22) {
				this.getInventory().setItem(i, goBack);
				continue;
			}
			if (i > 9 && i < 17) {
				ItemStack temp = ItemUtils.createItemStack(Material.STAINED_GLASS_PANE, (short)7, StringUtils.colorize("&6Min players: " + a.getMinPlayers()));
				this.getInventory().setItem(i, temp);
				continue;
			}
			if ((i >= 0 && i <=8) || (i >= 18 && i <=26)) {
				this.getInventory().setItem(i, glass);
				continue;
			}
		}
	}
	
	private void updateValues() {
		for (int i = 0; i < getSize(); i++) {
			if (i > 9 && i < 17) {
				ItemStack temp = ItemUtils.createItemStack(Material.STAINED_GLASS_PANE, (short)7, StringUtils.colorize("&6Min players: " + a.getMinPlayers()));
				this.getInventory().setItem(i, temp);
				continue;
			}
		}
	}

}
