package me.kingofdanether.survivalgames.enumeration;

import me.kingofdanether.survivalgames.util.StringUtils;

public enum Rating {
	
	SHIT(1, "&4[1]"), BAD(2, "&c[2]"), OKAY(3, "&e[3]"), GOOD(4, "&a[4]"), AMAZING(5, "&2[5]");
	
	private int score;
	private String s;
	
	private Rating(int score, String s) {
		this.score = score;
		this.s = StringUtils.colorize(s);
	}
	
	public int getScore() {return score;}
	public String getScoreName() {return s;}
	
	public static Rating getByScore(int score) {
		for (Rating r : Rating.values()) {
			if (r.getScore() == score) {
				return r;
			}
		}
		return null;
	}
}
