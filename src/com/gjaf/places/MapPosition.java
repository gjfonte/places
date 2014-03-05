package com.gjaf.places;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MapPosition extends FragmentActivity implements LocationListener,
		OnMapClickListener, OnMapLongClickListener, OnMarkerDragListener {

	final int RQS_GooglePlayServices = 1;
	
	private Context mCtx;
	private GoogleMap mMap;
	private LocationManager mLocationManager;
	private Marker mMarker;
	private double latitude;
	private double longitude;
	
	private TextView lblLat;
	private TextView lblLon;
	
	private MenuItem searchItem;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_position);

		this.mCtx = this;
		
		// Check Google Play Service Available
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mCtx);
		try {
			if(status != ConnectionResult.SUCCESS){
		        GooglePlayServicesUtil.getErrorDialog(status, this, RQS_GooglePlayServices).show();
		    }
		} catch (Exception e) {
		    Log.e("Error: GooglePlayServiceUtil: ", "" + e);
		}
		
		setUpMapIfNeeded();
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);		
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) mCtx);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) mCtx);
		
		lblLat = (TextView) findViewById(R.id.mapPositionLblLat);
		lblLon = (TextView) findViewById(R.id.mapPositionLblLon);

		latitude = getIntent().getDoubleExtra("lat", 0);
		longitude = getIntent().getDoubleExtra("lon", 0);

		setControls();

		if(latitude != 0 && longitude != 0)
			setMapPosition(new LatLng(latitude, longitude), true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationManager.removeUpdates(this);
	}
	
	private void setControls()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			getActionBar().setCustomView(R.layout.action_bar_edit);
		}
		
		lblLat.setText("...");
		lblLon.setText("...");

		Spinner spinner = (Spinner) findViewById(R.id.mapPositionSpLayers);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.mapLayers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		        setLayer((String) parent.getItemAtPosition(position));
				TextView text = (TextView)view.findViewById(android.R.id.text1);
		        text.setTextColor(Color.WHITE);
		    }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
        	
		});
	}
	
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.msg_map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
	
    private void setLayer(String layerName) {
        if (!checkReady()) {
            return;
        }
        if (layerName.equals(getString(R.string.layer_normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.layer_hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);
        } else if (layerName.equals(getString(R.string.layer_satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.layer_terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }
    
	private void setLatLon() {
		lblLat.setText(String.format("%s %s", mCtx.getString(R.string.lat_short), latitude));
		lblLon.setText(String.format("%s %s", mCtx.getString(R.string.lon_short), longitude));
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapPositionMap);
			mMap = fm.getMap();

			if (mMap != null) {
				setUpMap(); 
			}
		}
	}

	private void setUpMap() {
		mMap.setOnMapClickListener(this);
		mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
		mMap.setMyLocationEnabled(true);
	}

    
	public void confClicked(View v){
		Intent result = new Intent();
		result.putExtra("lat", latitude);
		result.putExtra("lon", longitude);
		setResult(RESULT_OK, result);
		finish();	
	}
	
	private void setupSearchView(final MenuItem searchItem) {

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
        		WSGetLocationInfo geo = new WSGetLocationInfo();
        		geo.execute(query);
            	searchItem.collapseActionView();
            	
            	/*
				Geocoder geoCoder = new Geocoder(mCtx, Locale.getDefault());
				try {
					List<Address> addresses = geoCoder.getFromLocationName(query, 5);
					String strCompleteAddress = "";
					if (addresses.size() > 0) {
						LatLng l = new LatLng(addresses.get(0).getLatitude(),
								addresses.get(0).getLongitude());

						latitude = addresses.get(0).getLatitude();
						longitude = addresses.get(0).getLongitude();

						CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(l) // Sets the center
								.zoom(17) // Sets the zoom
								.bearing(90) // Sets the orientation of the
												// camera to east
								.tilt(30) // Sets the tilt of the camera to 30
											// degrees
								.build(); // Creates a CameraPosition from the
											// builder
						mMap.animateCamera(CameraUpdateFactory
								.newCameraPosition(cameraPosition));

						mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}         
				*/   	
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_position, menu);
		searchItem = menu.findItem(R.id.menu_map_position_search);
		searchView = (SearchView) searchItem.getActionView();
		setupSearchView(searchItem);
		
		
		return true;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		latitude = point.latitude;
		longitude = point.longitude;
		confClicked(null);
	}

	@Override
	public void onMapClick(LatLng point) {
		latitude = point.latitude;
		longitude = point.longitude;
		setMapPosition(new LatLng(latitude, longitude), false);
	}

	@Override
	public void onLocationChanged(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();

		setMapPosition(new LatLng(latitude, longitude), true);
		
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub	
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	public void setMapPosition(LatLng point, Boolean move) {
		if(point.latitude == 0 && point.longitude == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_location_not_found), Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mMarker == null){
			mMarker = mMap.addMarker(new MarkerOptions()
									 .position(point)
									 .draggable(true)
								 	 .icon(BitmapDescriptorFactory.fromResource(GlobalSet.mPoints[0]))
									 );
		}
		else
			mMarker.setPosition(point);
		
		if(move){
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(point) // Sets the center
					.zoom(17) // Sets the zoom
					.bearing(90) // Sets the orientation of the camera to east
					.tilt(30) // Sets the tilt of the camera to 30 degrees
					.build(); // Creates a CameraPosition from the builder
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
		setLatLon();
	}
	
	public class WSGetLocationInfo extends AsyncTask<String, Void, StringBuilder> {

		@Override
		protected StringBuilder doInBackground(String... strs) {
			try {
				String address = strs[0];

				address = address.replace("\n"," ").replace(" ", "%20");
				
				HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
				HttpClient client = new DefaultHttpClient();
				HttpResponse response;
				StringBuilder stringBuilder = new StringBuilder();

				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}

				return stringBuilder;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(StringBuilder stringBuilder) {
			super.onPostExecute(stringBuilder);

			if(stringBuilder != null){
				JSONObject result = new JSONObject();
				try {
					result = new JSONObject(stringBuilder.toString());
					setMapPosition(getLatLon(result), true);
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static LatLng getLatLon(JSONObject jsonObject) {

		double lon = 0;
		double lat = 0;

		try {

			lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lng");

			lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lat");
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new LatLng(lat, lon);
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
		setLatLon();
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// TODO Auto-generated method stub
		
	}


}
