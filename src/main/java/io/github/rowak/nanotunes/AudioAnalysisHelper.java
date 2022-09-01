package io.github.rowak.nanotunes;

public class AudioAnalysisHelper {
	private static final int DB_MIN = 35;
	private static final int DB_MAX = 120;
	
	// Normalizes an amplitude value to a percentage using decibels.
	public static float normalizeAmplitude(int amp) {
		float db = ampToDecibels(amp);
		return (db-DB_MAX)/(float)DB_MIN;
	}
	
	// Converts an amplitude value to decibels.
	private static float ampToDecibels(float amp) {
		return (float)(10*Math.log10(Math.pow(Math.abs(amp),2)));
	}
}
