package com.ezio.multiwii.waypoints;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapHelperClass implements LocationSource {
	public GoogleMap map;
	private Polyline WPPathPolyLine;
	public Marker HomeMarker;
	public Marker PositionHoldMarker;
	public List<Marker> markers = new ArrayList<Marker>();
	public Circle CurrentWPCircle;
	private List<LatLng> FlyingPathPoints = new ArrayList<LatLng>();
	private int FlyingPathPointsCount = 20;
	private Polyline FlightPathPolyLine;

	private OnLocationChangedListener onLocationChangedListener;

	private int distanceWhenWPReached;

	public MapHelperClass(GoogleMap map, int distanceWhenWPReached) {

		this.map = map;
		this.distanceWhenWPReached = distanceWhenWPReached;

		map.setMyLocationEnabled(true);
		map.setLocationSource(this);
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		CleanMap();
	}

	public void RedrawLines() {
		if (WPPathPolyLine != null) {
			WPPathPolyLine.remove();
			WPPathPolyLine = null;
		}
		PolylineOptions polylineOptions = new PolylineOptions().color(Color.CYAN).width(4);
		for (Marker m : markers) {
			polylineOptions.add(m.getPosition());
		}
		WPPathPolyLine = map.addPolyline(polylineOptions);

	}

	public void DrawFlightPath(LatLng copterPositionLatLng) {
		if (FlyingPathPoints.size() == 0) {
			FlyingPathPoints.add(copterPositionLatLng);
			return;
		}

		if (gps2m(copterPositionLatLng.latitude, copterPositionLatLng.longitude, FlyingPathPoints.get(FlyingPathPoints.size() - 1).latitude, FlyingPathPoints.get(FlyingPathPoints.size() - 1).longitude) > 5) {
			FlyingPathPoints.add(copterPositionLatLng);

			if (FlyingPathPoints.size() > FlyingPathPointsCount) {
				FlyingPathPoints.remove(0);
			}

			if (FlightPathPolyLine != null) {
				FlightPathPolyLine.remove();
				FlightPathPolyLine = null;
			}

			PolylineOptions polylineOptions = new PolylineOptions().color(Color.argb(100, 0, 255, 255));
			for (LatLng p : FlyingPathPoints) {
				polylineOptions.add(p);
			}
			FlightPathPolyLine = map.addPolyline(polylineOptions);
		}
	}

	public void AddMarker() {
		LatLng mapCenter = map.getCameraPosition().target;
		Marker m = map.addMarker(new MarkerOptions().position(mapCenter).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		markers.add(m);

		int i = 1;
		for (Marker mm : markers) {
			mm.setTitle("WP#" + String.valueOf(i));
			i++;
		}
		RedrawLines();
	}

	public void AddMarker(LatLng position) {
		Marker m = map.addMarker(new MarkerOptions().position(position).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		markers.add(m);

		int i = 1;
		for (Marker mm : markers) {
			mm.setTitle("WP#" + String.valueOf(i));
			i++;
		}
		RedrawLines();
	}

	public void CleanMap() {
		markers = new ArrayList<Marker>();
		map.clear();
		addDefaultMarkersToMap();
	}

	public void RemoveMarker(int position) {
		markers.get(position).remove();
		markers.remove(position);
	}

	void addDefaultMarkersToMap() {
		LatLng mapCenter = map.getCameraPosition().target;
		HomeMarker = map.addMarker(new MarkerOptions().position(mapCenter).title("Home").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).draggable(true));
		PositionHoldMarker = map.addMarker(new MarkerOptions().position(mapCenter).title("PositionHold").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		CurrentWPCircle = map.addCircle(new CircleOptions().center(mapCenter).radius(distanceWhenWPReached).fillColor(Color.argb(50, 0, 255, 50)));
		CurrentWPCircle.setStrokeWidth(2);
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		this.onLocationChangedListener = onLocationChangedListener;
	}

	@Override
	public void deactivate() {
		this.onLocationChangedListener = null;
	}

	public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
		Location locationA = new Location("point A");
		locationA.setLatitude(lat_a);
		locationA.setLongitude(lng_a);

		Location locationB = new Location("point B");
		locationB.setLatitude(lat_b);
		locationB.setLongitude(lng_b);

		return locationA.distanceTo(locationB);
	}

	public void SetCopterLocation(LatLng copterPositionLatLng, float head, float alt) {
		if (onLocationChangedListener != null) {
			Location copterLocation = new Location("CustomCopterLocation");
			copterLocation.setLatitude(copterPositionLatLng.latitude);
			copterLocation.setLongitude(copterPositionLatLng.longitude);
			copterLocation.setBearing(head);
			copterLocation.setAccuracy(2);
			copterLocation.setAltitude(alt);
			onLocationChangedListener.onLocationChanged(copterLocation);
		}
	}

	static double FullCircleDegrees = 360d;
	static double HalfCircleDegrees = FullCircleDegrees / 2d;
	static double DegreesToRadians = Math.PI / HalfCircleDegrees;
	static double RadiansToDegrees = 1 / DegreesToRadians;

	public static LatLng GetPointGivenRadialAndDistance(LatLng center, double radius, double azimuth) {
		radius = radius * (1.56961231e-7);
		double lat1 = center.latitude * DegreesToRadians;
		double lng1 = center.longitude * DegreesToRadians;
		double lat = Math.asin((Math.sin(lat1) * Math.cos(radius)) + Math.cos(lat1) * Math.sin(radius) * Math.cos(azimuth * DegreesToRadians));
		double lng = 0d;
		if (Math.cos(lat) == 0) {
			lng = lng1;
		} else {
			lng = ((lng1 + Math.PI - Math.asin(Math.sin(azimuth * DegreesToRadians) * Math.sin(radius) / Math.cos(lat1))) % (2 * Math.PI)) - Math.PI;
		}
		return new LatLng(lat * RadiansToDegrees, lng * RadiansToDegrees);
	}

}
