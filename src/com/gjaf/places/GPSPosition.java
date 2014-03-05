package com.gjaf.places;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import com.gjaf.places.R;

public class GPSPosition extends Service implements LocationListener {

	private Context mCtx;
	private LocationManager locationManager;
	public static final float MIN_DISTANCE = 10; // meters
	public static final long MIN_TIME = 10000; // milliseconds
	private boolean canGetGPSLocation;
	private boolean newLocation;
	private double latitude;
	private double longitude;
	
	public GPSPosition(Context mCtx) {
		this.mCtx = mCtx;

		locationManager = (LocationManager) mCtx.getSystemService(LOCATION_SERVICE);	

		if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			this.canGetGPSLocation = false;
		}
		else{
			this.canGetGPSLocation = true;
		}

		getLocation();
		newLocation = false;		
	}
	
	private void getLocation() {
		
		Criteria criteria = new Criteria(); 
		String provider = locationManager.getBestProvider(criteria, true); 
		Location location = locationManager.getLastKnownLocation(provider); 
		if (location != null) { 
			this.onLocationChanged(location); 
		} 

		locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);		
	}

	public void getNowLocation() {
		stopLocationUpdates();

		newLocation = false;

		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) { 
			this.onLocationChanged(location); 
		} 

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);		
	}

	public double getLatitude(){
		return this.latitude;
	}

	public double getLongitude(){
		return this.longitude;
	}
	
	public boolean canGetGPSLocation(){
		return this.canGetGPSLocation;
	}
	
	public boolean isNowLocation(){
		return this.newLocation;
	}	
	public void stopLocationUpdates(){
		if(locationManager != null){
			locationManager.removeUpdates(this);
		}		
	}
	
	public void showLocationSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
   	    alertDialog.setTitle(mCtx.getResources().getString(R.string.gps_disabled));
        alertDialog.setMessage(mCtx.getResources().getString(R.string.gps_disabled_info));
 
        // On pressing Settings button
        alertDialog.setPositiveButton(mCtx.getResources().getString(R.string.menu_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mCtx.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		if(location != null){
			if(latitude != location.getLatitude() && longitude != location.getLongitude())
				newLocation = true;
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
