package com.gjaf.places;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gjaf.places.R;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBAdapter;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityPlace;

public class ShowMap extends FragmentActivity implements OnInfoWindowClickListener {
	
	class CustomInfoWindowAdapter implements InfoWindowAdapter {
	    private final View mContents;
	
	    CustomInfoWindowAdapter() {
	        mContents = getLayoutInflater().inflate(R.layout.point_info, null);
	    }
	
	    @Override
	    public View getInfoWindow(Marker marker) {
	        return null;
	    }
	
	    @Override
	    public View getInfoContents(Marker marker) {
	        render(marker, mContents);
	        return mContents;
	    }
	
		private void render(Marker marker, View view) {
		    Bitmap badge;	
		    
	        badge = getPhoto(marker);
	        
	        ImageView img = (ImageView) view.findViewById(R.id.badge);
	        if(badge == null)
	        	img.setImageResource(R.drawable.ic_point_blue);
	        else
	        	img.setImageBitmap(badge);
		
		    String title = marker.getTitle();
		    TextView titleUi = ((TextView) view.findViewById(R.id.title));
		    if (title != null) {
		        SpannableString titleText = new SpannableString(title);
		        titleUi.setText(titleText);
		    } else {
		        titleUi.setText("");
		    }
		
		    String snippet = marker.getSnippet();
		    TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		    if (snippet != null) {
		        SpannableString snippetText = new SpannableString(snippet);
		        snippetUi.setText(snippetText);
		    } 
		    else {
		        snippetUi.setText("");
		    }
	   }
	}
	
	final int RQS_GooglePlayServices = 1;
	
	private Context mCtx;
	private GoogleMap mMap;
	private LatLngBounds.Builder bounds;
	
	private Cursor c_adap ;
	private ArrayList<EntityPlace> places;
	
	private String striIds;

	private TextView lblNumber;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_map);
		
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
		
		lblNumber = (TextView) findViewById(R.id.showMapLblPointNumber);

		striIds = getIntent().getStringExtra("ids");
		setUpMapIfNeeded();

		setControls();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setControls(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.showMapSpLayers);
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
    
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.showMapMap);
			mMap = fm.getMap();

			if (mMap != null) {
				setUpMap(); 
			}
		}
	}
	
	private void setUpMap() {

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnInfoWindowClickListener(this);
		mMap.setMyLocationEnabled(true);

        // Pan to see all markers in view.
        // Cannot zoom to bounds until the map has a size.
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.showMapMap).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation") // We use the new method when supported
                @SuppressLint("NewApi") // We check which build version we are using.
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                      mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } 
                    else {
                      mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
                }
            });
        }
    }
	
	private void addMarkersToMap() {
		bounds = new LatLngBounds.Builder();

		String where = String.format("%s and %s.%s in (%s)", 
				DALPlace.WHERE_JOIN, 
				DALPlace.TABLE_NAME, 
				DBAdapter.KEY_ID, 
				striIds);
		c_adap = DBMain.getAllJoin(mCtx, DALPlace.TABLES_JOIN, where, DALPlace.columnsJoin, null);
		places = DALPlace.converte(c_adap);

		for (EntityPlace p : places) {
			
			LatLng latlog = new LatLng(p.getLat(), p.getLon());
				mMap.addMarker(new MarkerOptions()
						.position(latlog)
						.title(p.getDesign())
						.snippet(p.getDescr())
						.icon(BitmapDescriptorFactory.fromResource(GlobalSet.mPoints[p.getIdIcon()])));
				bounds.include(latlog);
		}
		
		lblNumber.setText(String.valueOf(places.size()));		
    }
	
	public Bitmap getPhoto(Marker marker){
		Bitmap photo = null;	
		
		for (EntityPlace p : places) {
			if(marker.getTitle().equals(p.getDesign())){
				if(p.getPhoto() != null)
					photo = BitmapFactory.decodeByteArray(p.getPhoto(), 0,p.getPhoto().length);
			}
		}
		
		return photo;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
}
