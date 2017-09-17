package me.kingofdanether.survivalgames.sign;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import me.kingofdanether.survivalgames.arena.Arena;

public abstract class GameSign {

	protected Sign sign;
	protected int id;
	protected Arena a;
	
	public GameSign(Sign sign, int id, Arena a) {
		this.sign = sign;
		this.id = id;
		this.a = a;
	}
	
	public Sign getSign() {return sign;}
	public int getID() {return id;}
	public Arena getArena() {return a;}
	public Location getLocation() {return sign.getLocation();}
	
	public abstract void update();
	
}
