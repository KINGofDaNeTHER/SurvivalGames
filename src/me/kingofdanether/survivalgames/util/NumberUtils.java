package me.kingofdanether.survivalgames.util;

import java.text.DecimalFormat;
import java.util.List;

import io.netty.util.internal.ThreadLocalRandom;

public class NumberUtils {

	public static double getPercentage(double n, double total) {
		float proportion = ((float) n) / ((float) total);
		return nearestHundreth((proportion * 100));
	}

	public static double nearestHundreth(double d) {
		return (long) (d * 1e2) / 1e2;
	}

	public static int randInt(int min, int max) {
		// int randnum = ThreadLocalRandom.current().nextInt((max - min + 1) + min);
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static double trim(double untrimmeded, int decimal) {
		String format = "#.#";

		for (int i = 1; i < decimal; i++) {
			format = format + "#";
		}
		DecimalFormat twoDec = new DecimalFormat(format);
		return Double.valueOf(twoDec.format(untrimmeded)).doubleValue();
	}
	
	public static int getHighest(List<Integer> list) {
		int currentID = 1;
		boolean foundBiggest = false;
		for (int i : list) {
			if (i > currentID) {
				currentID = i;
				foundBiggest = true;
			}
		}
		if (foundBiggest) {
			return currentID + 1;
		} else {
			return list.size() + 1;
		}
	}

}
