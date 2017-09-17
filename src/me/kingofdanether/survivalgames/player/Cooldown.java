package me.kingofdanether.survivalgames.player;

public class Cooldown {
	
	private String ability = "";
	private String player = "";
	private long seconds;
	private long systime;

	public Cooldown(String player, String ability, long seconds, long systime) {
        this.player = player;
        this.ability = ability;
        this.seconds = seconds;
        this.systime = systime;
    }
	
	public String getAbility() {return ability;}
	public String getPlayerName() {return player;}
	public long getSeconds() {return seconds;}
	public int getNormalSeconds() {return (int)(seconds/1000);}
	public long getSysTime() {return systime;}
	
}
