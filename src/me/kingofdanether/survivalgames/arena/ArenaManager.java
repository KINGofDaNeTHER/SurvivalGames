package me.kingofdanether.survivalgames.arena;

import java.util.ArrayList;

import me.kingofdanether.survivalgames.player.SGPlayer;

public class ArenaManager {
	
	private static ArrayList<Arena> arenas = new ArrayList<Arena>();
	
	public static ArrayList<Arena> getAllArenas() {return arenas;}
	
	public static void addArena(Arena a) {arenas.add(a);}
	public static void removeArena(Arena a) {arenas.remove(a);}
	
	public boolean hasArena(Arena a) {return arenas.contains(a);}
	
	public static Arena getArena(String name) {
		for (Arena a : arenas) {
			if (a.getName() != null && a.getName().equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}
	
	public static Arena getArena(SGPlayer player) {
		for (Arena a : arenas) {
			if (a.getPlayers().contains(player)) {
				return a;
			}
		}
		return null;
	}

}
