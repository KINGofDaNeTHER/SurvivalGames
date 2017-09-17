package me.kingofdanether.survivalgames.sign;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import me.kingofdanether.survivalgames.arena.ArenaSign;
import me.kingofdanether.survivalgames.util.LocationUtils;

public class SignManager {

	private static ArrayList<GameSign> signs = new ArrayList<GameSign>();
	
	public static ArrayList<GameSign> getAllSigns() {return signs;}
	
	public static void addSign(GameSign s) {signs.add(s);}
	public static void removeSign(GameSign s) {signs.remove(s);}
	
	public boolean hasSign(ArenaSign s) {return signs.contains(s);}
	
	public static GameSign getSign(int id) {
		for (GameSign s : signs) {
			if (s.getID() == id) {
				return s;
			}
		}
		return null;
	}
	
	public static GameSign getSign(Location l) {
		for (GameSign s : signs) {
			if (LocationUtils.locEqualsLoc(s.getLocation(), l)) {
				return s;
			}
		}
		return null;
	}
	
	public static GameSign getSign(Sign sign) {
		for (GameSign s : signs) {
			if (s.getSign().equals(sign)) {
				return s;
			}
		}
		return null;
	}
	
}
