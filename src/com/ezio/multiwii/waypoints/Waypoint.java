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

import com.google.android.gms.maps.model.LatLng;

public class Waypoint {

	// mission_step.number = read8();
	// mission_step.pos[LAT] = read32();
	// mission_step.pos[LON] = read32();
	// mission_step.action = read8();
	// mission_step.parameter = read16();
	// mission_step.altitude = read32();
	// mission_step.flag = read8();

	public int Heading = 0, TimeToStay = 0, NavFlag = 0; //old <=2.3

	//new >2.3
	public int Number = 0;
	public int Lat = 0;
	public int Lon = 0;
	public int Action = 0;
	public int Parameter = 0;
	public int Altitude = 0;
	public int Flag = 0;

	/**
	 * 
	 * @param number
	 * @param lat
	 * @param lon
	 * @param alt
	 *            altitude (cm)
	 * @param heading
	 *            heading (deg)
	 * @param timeToStay
	 *            time to stay (ms)
	 * @param navFlag
	 */
	public Waypoint(int number, int lat, int lon, int alt, int heading, int timeToStay, int navFlag) {
		Number = number;
		Lat = lat;
		Lon = lon;
		Altitude = alt; // to set altitude (cm)
		Heading = heading;// future: to set heading (deg)
		TimeToStay = timeToStay;// future: to set time to stay (ms)
		NavFlag = navFlag;

	}

	/**
	 * 
	 * @param number
	 * @param lat
	 * @param lon
	 * @param alt
	 *            altitude (cm)
	 * @param heading
	 *            heading (deg)
	 * @param timeToStay
	 *            time to stay (ms)
	 * @param navFlag
	 */
	public Waypoint(int number, LatLng latLng, int alt, int heading, int timeToStay, int navFlag) {
		Number = number;
		Lat = (int) (latLng.latitude * 1e7);
		Lon = (int) (latLng.longitude * 1e7);
		Heading = heading;
		TimeToStay = timeToStay;
		Altitude = alt;
		NavFlag = navFlag;
	}

	public Waypoint() {

	}

	public LatLng getLatLng() {
		return new LatLng(Lat / 1e7, Lon / 1e7);

	}
}
