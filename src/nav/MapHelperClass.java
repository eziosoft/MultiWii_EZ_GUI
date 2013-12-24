package nav;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.ezio.multiwii.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapHelperClass implements LocationSource {
	private final Context context;
	public final GoogleMap map;
	private Polyline WPPathPolyLine;
	public Marker HomeMarker;
	public Marker PositionHoldMarker;
	public Marker ModelMarker;
	final private float markerAnchorX = 0.17f;
	final private float markerAnchorY = 1f;

	public List<Marker> markers = new ArrayList<Marker>();
	// public Circle CurrentWPCircle;
	private List<LatLng> FlyingPathPoints = new ArrayList<LatLng>();
	private int FlyingPathPointsCount = 20;
	private Polyline FlightPathPolyLine;

	private OnLocationChangedListener onLocationChangedListener;

	private final int CircleAroundWPinMeters;

	private BitmapDescriptor ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_quadx);

	public MapHelperClass(Context context, GoogleMap map, int CircleAroundWPinMeters, int modelType) {

		this.context = context;
		this.map = map;
		this.CircleAroundWPinMeters = CircleAroundWPinMeters;

		setModelType(modelType);
		map.setMyLocationEnabled(true);
		map.setLocationSource(this);
		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		CleanMap();

	}

	private void setModelType(int modelType) {
		final int TRI = 1;
		final int QUADP = 2;
		final int QUADX = 3;
		final int BI = 4;
		final int GIMBAL = 5;
		final int Y6 = 6;
		final int HEX6 = 7;
		final int FLYING_WING = 8;
		final int Y4 = 9;
		final int HEX6X = 10;
		final int OCTOX8 = 11;
		final int OCTOFLATX = 12;
		final int OCTOFLATP = 13;
		final int AIRPLANE = 14;
		final int HELI_120_CCPM = 15;
		final int HELI_90_DEG = 16;
		final int VTAIL4 = 17;
		final int HEX6H = 18;
		final int PPM_TO_SERVO = 19;
		final int DUALCOPTER = 20;
		final int SINGLECOPTER = 21;

		switch (modelType) {
		case TRI:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_tri);
			break;
		case QUADP:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_quadp);
			break;
		case QUADX:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_quadx);
			break;
		case BI:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bi);
			break;
		case GIMBAL:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_quadx);
			break;
		case Y6:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_y6);
			break;
		case HEX6:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_hex6p);
			break;
		case FLYING_WING:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_fwing);
			break;
		case Y4:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_y4);
			break;
		case HEX6X:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_hex6x);
			break;
		case OCTOX8:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_octox8);
			break;
		case OCTOFLATX:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_octoflatx);
			break;
		case OCTOFLATP:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_oktoflatp);
			break;
		case AIRPLANE:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_airplane);
			break;
		case HELI_120_CCPM:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_heli);
			break;
		case HELI_90_DEG:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_heli);
			break;
		case VTAIL4:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_vtail4);
			break;
		case HEX6H:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_hex6p);
			break;
		case DUALCOPTER:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_bi);
			break;
		case SINGLECOPTER:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_heli);
			break;

		default:
			ModelIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_quadx);
			break;
		}
	}

	public void RedrawLines() {
		if (WPPathPolyLine != null) {
			WPPathPolyLine.remove();
			WPPathPolyLine = null;
		}
		PolylineOptions polylineOptions = new PolylineOptions().color(Color.CYAN).width(4);
		for (Marker m : markers) {
			if (!m.getSnippet().contains("SET_POI")) {
				polylineOptions.add(m.getPosition());
			}
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

	/**
	 * 
	 * @return marker ID
	 */

	// public String AddMarkerOnCenter() {
	// LatLng mapCenter = map.getCameraPosition().target;
	//
	//
	//
	// Marker m = map.addMarker(new
	// MarkerOptions().position(mapCenter).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
	// m.showInfoWindow();
	// markers.add(m);
	//
	// int i = 1;
	// for (Marker mm : markers) {
	// mm.setTitle("WP#" + String.valueOf(i));
	// i++;
	// }
	// RedrawLines();
	// return m.getId();
	// }

	/**
	 * 
	 * @return marker ID
	 */

	public String AddMarker(LatLng position, String title, String snippet, int action) {

		BitmapDescriptor IconColor;// =
									// BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
		switch (action) {
		case WaypointNav.WP_ACTION_WAYPOINT:
			IconColor = BitmapDescriptorFactory.fromResource(R.drawable.waypoint);
			break;

		case WaypointNav.WP_ACTION_POSHOLD_TIME:
			IconColor = BitmapDescriptorFactory.fromResource(R.drawable.poshold_time);
			break;

		case WaypointNav.WP_ACTION_POSHOLD_UNLIM:
			IconColor = BitmapDescriptorFactory.fromResource(R.drawable.poshold_unlim);
			break;

		// case WaypointNav.WP_ACTION_RTH:
		// IconColor =
		// BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
		// break;
		// case WaypointNav.WP_ACTION_JUMP:
		// IconColor =
		// BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
		// break;

		case WaypointNav.WP_ACTION_SET_POI:
			IconColor = BitmapDescriptorFactory.fromResource(R.drawable.poi);
			break;
		default:
			IconColor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
			break;
		}
		Marker m = map.addMarker(new MarkerOptions().position(position).draggable(true).icon(IconColor).anchor(markerAnchorX, markerAnchorY));
		markers.add(m);

		m.setTitle(title);
		m.setSnippet(snippet);
		RedrawLines();
		return m.getId();
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
		HomeMarker = map.addMarker(new MarkerOptions().position(mapCenter).title("Home").icon(BitmapDescriptorFactory.fromResource(R.drawable.home)).draggable(false).anchor(markerAnchorX, markerAnchorY));
		ModelMarker = map.addMarker(new MarkerOptions().position(mapCenter).title("Model").icon(ModelIcon).draggable(false).anchor(0.5f, 0.5f).flat(true));

		PositionHoldMarker = map.addMarker(new MarkerOptions().position(mapCenter).title("PositionHold").icon(BitmapDescriptorFactory.fromResource(R.drawable.poshold)).draggable(false).anchor(markerAnchorX, markerAnchorY));

		// CurrentWPCircle = map.addCircle(new
		// CircleOptions().center(mapCenter).radius(CircleAroundWPinMeters).fillColor(Color.argb(50,
		// 0, 255, 50)));
		// CurrentWPCircle.setStrokeWidth(2);
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

			ModelMarker.setPosition(copterPositionLatLng);
			ModelMarker.setRotation(head);
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
