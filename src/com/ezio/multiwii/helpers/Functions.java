/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.helpers;

public class Functions {

	
	public static long ConcatInt(int x, int y) {
		return (int) ((x * Math.pow(10, numberOfDigits(y))) + y);
	}

	public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	public static int numberOfDigits(long n) {
		// Guessing 4 digit numbers will be more probable.
		// They are set in the first branch.
		if (n < 10000L) { // from 1 to 4
			if (n < 100L) { // 1 or 2
				if (n < 10L) {
					return 1;
				} else {
					return 2;
				}
			} else { // 3 or 4
				if (n < 1000L) {
					return 3;
				} else {
					return 4;
				}
			}
		} else { // from 5 a 20 (albeit longs can't have more than 18 or 19)
			if (n < 1000000000000L) { // from 5 to 12
				if (n < 100000000L) { // from 5 to 8
					if (n < 1000000L) { // 5 or 6
						if (n < 100000L) {
							return 5;
						} else {
							return 6;
						}
					} else { // 7 u 8
						if (n < 10000000L) {
							return 7;
						} else {
							return 8;
						}
					}
				} else { // from 9 to 12
					if (n < 10000000000L) { // 9 or 10
						if (n < 1000000000L) {
							return 9;
						} else {
							return 10;
						}
					} else { // 11 or 12
						if (n < 100000000000L) {
							return 11;
						} else {
							return 12;
						}
					}
				}
			} else { // from 13 to ... (18 or 20)
				if (n < 10000000000000000L) { // from 13 to 16
					if (n < 100000000000000L) { // 13 or 14
						if (n < 10000000000000L) {
							return 13;
						} else {
							return 14;
						}
					} else { // 15 or 16
						if (n < 1000000000000000L) {
							return 15;
						} else {
							return 16;
						}
					}
				} else { // from 17 to ...¿20?
					if (n < 1000000000000000000L) { // 17 or 18
						if (n < 100000000000000000L) {
							return 17;
						} else {
							return 18;
						}
					} else { // 19? Can it be?
						// 10000000000000000000L is'nt a valid long.
						return 19;
					}
				}
			}
		}
	}
}
