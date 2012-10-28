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
package com.ezio.multiwii.waypoints;

public class Waypoint {

	public int Number = 0, Lat = 0, Lon = 0, Alt = 0, NavFlag = 0;

	public Waypoint(int number, int lat, int lon, int alt, int navFlag) {
		Number = number;
		Lat = lat;
		Lon = lon;
		Alt = alt;
		NavFlag = navFlag;

	}

	public Waypoint() {

	}
}
