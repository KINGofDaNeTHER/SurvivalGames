package me.kingofdanether.survivalgames.inventory;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.inventory.Inventory;

public class GuiManager {

	public static ArrayList<GUI> guis = new ArrayList<GUI>();
	
	public static ArrayList<GUI> getAllGuis() {return guis;}
	
	public static void addGui(GUI gui) {guis.add(gui);}
	public static void removeGui(GUI gui) {guis.remove(gui);}
	
	public static GUI getGui(UUID id) {
		for (GUI gui : guis) {
			if (gui.getUUID().equals(id)) {
				return gui;
			}
		}
		return null;
	}
	
	public static GUI getGui(String name) {
		for (GUI gui : guis) {
			if (gui.getInventory().getTitle().equals(name)) {
				return gui;
			}
		}
		return null;
	}
	
	public static GUI getGui(Inventory inv) {
		for (GUI gui : guis) {
			if (gui.getInventory().equals(inv)) {
				return gui;
			}
		}
		return null;
	}
	
}
