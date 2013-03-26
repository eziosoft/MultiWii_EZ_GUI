package com.ezio.multiwii.helpers;

public class LowPassFilter {
	/*
	 * time smoothing constant for low-pass filter 0 ≤ alpha ≤ 1 ; a smaller
	 * value basically means more smoothing See: http://en.wikipedia.org/wiki
	 * /Low-pass_filter#Discrete-time_realization
	 */
	float ALPHA = 0f;
	float lastOutput = 0;

	public LowPassFilter(float ALPHA) {
		this.ALPHA = ALPHA;
	}

	public float lowPass(float input) {
		if (Math.abs(input - lastOutput) > 170) {
			lastOutput = input;
			return lastOutput;
		}
		lastOutput = lastOutput + ALPHA * (input - lastOutput);
		return lastOutput;
	}
}
