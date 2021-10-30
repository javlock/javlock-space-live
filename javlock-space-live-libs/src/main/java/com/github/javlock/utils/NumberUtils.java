package com.github.javlock.utils;

import com.github.javlock.games.space.StaticData;

public final class NumberUtils {

	public static double randomDoubleInRange(double min, double max) {
		return min + (max - min) * StaticData.secureRandomObj.nextDouble();
	}

	public static float randomFloatInRange(float min, float max) {
		return min + (max - min) * StaticData.secureRandomObj.nextFloat();
	}

	public static int randomIntInRange(int min, int max) {
		return StaticData.secureRandomObj.nextInt((max - min) + 1) + min;
	}

	private NumberUtils() {
	}
}
