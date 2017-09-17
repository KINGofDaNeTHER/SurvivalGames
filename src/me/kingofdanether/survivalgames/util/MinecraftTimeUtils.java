package me.kingofdanether.survivalgames.util;

import me.kingofdanether.survivalgames.enumeration.TimeUnit;

public class MinecraftTimeUtils {

	public static int secondsSince(long timeStamp) {
		return (((int) System.currentTimeMillis() / 1000) - (int) timeStamp / 1000);
	}

	public static String getTimeString(int time) {
		int hours = time / 3600;
		int minutes = (time % 3600) / 60;
		int seconds = (time % 3600) % 60;
		return StringUtils.getTimeString(0, hours, minutes, seconds);
	}

	public static double convert(long time, TimeUnit unit, int decPoint) {
		if (unit == TimeUnit.BEST) {
			if (time < 60000L) {
				unit = TimeUnit.SECONDS;
			} else if (time < 3600000L) {
				unit = TimeUnit.MINUTES;
			} else if (time < 86400000L) {
				unit = TimeUnit.HOURS;
			} else {
				unit = TimeUnit.DAYS;
			}
		}
		if (unit == TimeUnit.SECONDS)
			return NumberUtils.trim(time / 1000.0D, decPoint);
		if (unit == TimeUnit.MINUTES)
			return NumberUtils.trim(time / 60000.0D, decPoint);
		if (unit == TimeUnit.HOURS)
			return NumberUtils.trim(time / 3600000.0D, decPoint);
		if (unit == TimeUnit.DAYS)
			return NumberUtils.trim(time / 86400000.0D, decPoint);
		return NumberUtils.trim(time, decPoint);
	}
}
