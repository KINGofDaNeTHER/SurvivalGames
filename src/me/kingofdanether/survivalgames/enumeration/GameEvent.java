package me.kingofdanether.survivalgames.enumeration;

public enum GameEvent {
	CHEST_REFILL("Chest Refill"), SUPPLY_DROP("Supply Drop"), BORDER_SPAWN("Border Spawn"), BORDER_SHRINK("Border Shrinking"), DEATHMATCH("Deathmatch");
	
	private String name;
	
	private GameEvent(String name) {
		this.name = name;
	}
	
	public String getName() {return name;}
}
