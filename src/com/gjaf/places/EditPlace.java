package com.gjaf.places;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALCateg;
import com.gjaf.places.dal.DALCountry;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCateg;
import com.gjaf.places.entities.EntityCountry;
import com.gjaf.places.entities.EntityPlace;

public class EditPlace extends Activity {

	private ArrayList<EntityCateg> listCateg = new ArrayList<EntityCateg>();
	private ArrayAdapter<String> adapCateg = null;
	private ArrayList<EntityCountry> listCountry = new ArrayList<EntityCountry>();
	private ArrayAdapter<String> adapCountry = null;

	private EntityPlace place;
	private int idPlace;
	private int idCateg;
	private Context mCtx;
	
	private ImageView imgPhoto;
	private TextView lblLat;
	private TextView lblLon;
	private EditText txtDesign;
	private EditText txtDescr;
	private EditText txtCity;
	private Spinner spCountry;
	private Spinner spCateg;
	private EditText txtObs;
	private Button btnConf;
	private Bitmap photo;	
	
	// Dialog
	private Dialog dialogPoints = null;
	private TextView txtDialogPointLat;
	private TextView txtDialogPointLon;
    private Button btnDialogGetGpsPos;

	private GPSPosition gpsPosition;
	private Timer timerGetGPSPos;
	
	private static final int REQUEST_CODE_MAP_POSITION = 1;
	private static final int REQUEST_CODE_PHOTO_CAMERA = 2;
	private static final int REQUEST_CODE_PHOTO_GALLERY = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_place);
		this.mCtx = this;

		imgPhoto = (ImageView) findViewById(R.id.editPlaceImgPhoto);
		lblLat = (TextView) findViewById(R.id.editPlaceLblLat);
		lblLon = (TextView) findViewById(R.id.editPlaceLblLon);
		txtDesign = (EditText) findViewById(R.id.editPlaceTxtDesign);
		txtDescr = (EditText) findViewById(R.id.editPlaceTxtDescr);
		txtCity = (EditText) findViewById(R.id.editPlaceTxtCity);
		spCountry = (Spinner) findViewById(R.id.editPlaceSpCountry);
		spCateg = (Spinner) findViewById(R.id.editPlaceSpCateg);
		txtObs = (EditText) findViewById(R.id.editPlaceTxtObs);
		btnConf = (Button) findViewById(R.id.editPlaceBtnConf);
		
		idPlace = getIntent().getIntExtra("id", 0);
		idCateg = getIntent().getIntExtra("idCateg", 0);
		if(idPlace == 0){
			place = new EntityPlace();
			place.setIdCateg(idCateg);
		}
		else
			place = DALPlace.getFirst(DBMain.getById(mCtx, DALPlace.TABLE_NAME, DALPlace.columns, idPlace));
		
		setControls();
		
		if(idPlace == 0)
			locationClicked(null);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setControls() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			getActionBar().setCustomView(R.layout.action_bar_edit);
			btnConf.setVisibility(View.GONE);
		}

		// Spinner
		listCateg = DALCateg.converte(DBMain.getAll(mCtx, DALCateg.TABLE_NAME, DALCateg.columns, DALCateg.DESIGN));
		ArrayList<String> listCategDesign = new ArrayList<String>(); 
		listCategDesign.add(getString(R.string.categ));
		for(EntityCateg c : listCateg)
			listCategDesign.add(c.getDesign());
		adapCateg = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listCategDesign);
		adapCateg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCateg.setAdapter(adapCateg);
		spCateg.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long arg3) {
				getSpCateg(position);
				
				TextView text = (TextView)view.findViewById(android.R.id.text1);
	            text.setTextColor(position == 0 ? Color.GRAY:Color.BLACK);     
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		}); 
		
		listCountry = DALCountry.converte(DBMain.getAll(mCtx, DALCountry.TABLE_NAME, DALCountry.columns, DALCountry.DESIGN));
		ArrayList<String> listCountryDesign = new ArrayList<String>(); 
		listCountryDesign.add(getString(R.string.country));
		for(EntityCountry c : listCountry)
			listCountryDesign.add(c.getDesign());
		adapCountry = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listCountryDesign);
		adapCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCountry.setAdapter(adapCountry);
		spCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long arg3) {
				setCountry(position);
				
				TextView text = (TextView)view.findViewById(android.R.id.text1);
	            text.setTextColor(position == 0 ? Color.GRAY:Color.BLACK);     
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		}); 
		
		// Views
		setSpCateg(place.getIdCateg());
		if(idPlace > 0)
		{
			setLblLatLon();
			txtDesign.setText(place.getDesign());
			txtDescr.setText(place.getDescr());
			txtCity.setText(place.getCity());
			setSpCountry(place.getIdCountry());
			txtObs.setText(place.getObs());
			if(place.getPhoto() != null){
				photo = BitmapFactory.decodeByteArray(place.getPhoto(), 0,place.getPhoto().length);
				imgPhoto.setImageBitmap(photo);
			}
			
		}
	}

	private void getSpCateg(int position) {
		position--;
		if(position >= 0)
			place.setIdCateg(listCateg.get(position).get_id());
		else
			place.setIdCateg(0);
	}

	private void setSpCateg(int id) {
		int idx = 1;
		
		for(EntityCateg c : listCateg)
		{
			if(c.get_id() == id)
				break;
			else
				idx++;
		}
		spCateg.setSelection(idx);
	}

	private void setCountry(int position) {
		position--;
		if(position >= 0)
			place.setIdCountry(listCountry.get(position).get_id());
		else
			place.setIdCountry(0);
	}
	
	private void setSpCountry(int id) {
		int idx = 1;
		
		for(EntityCountry c : listCountry)
		{
			if(c.get_id() == id)
				break;
			else
				idx++;
		}
		spCountry.setSelection(idx);
	}

	private void setLblLatLon() {
		lblLat.setText(String.format("%s: %.2f", getString(R.string.lat_short), place.getLat()));
		lblLon.setText(String.format("%s: %.2f", getString(R.string.lon_short), place.getLon()));
	}
	
	public void locationClicked(View v){
		dialogPoints = createDialogPoints(null);
		dialogPoints.show();
	}
	
	public Dialog createDialogPoints(Bundle savedInstanceState) {   

	    LayoutInflater factoryDialog;
	    View viewDialog;
	    Button btnDialogGetMapPos;
	
		factoryDialog = LayoutInflater.from(mCtx);
	    viewDialog = factoryDialog.inflate(R.layout.activity_edit_place_points, null);
		txtDialogPointLat = (EditText) viewDialog.findViewById(R.id.editPlacePointTxtLat);
        txtDialogPointLon = (EditText) viewDialog.findViewById(R.id.editPlacePointTxtLon);
	    btnDialogGetGpsPos = (Button) viewDialog.findViewById(R.id.editPlacePointBtnGps);
	    btnDialogGetMapPos = (Button) viewDialog.findViewById(R.id.editPlacePointBtnMap);
	    
	    btnDialogGetGpsPos.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGetGPSPos();
			}
		});
	    
	    btnDialogGetMapPos.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openGetMapPosition();
			}
		});
		
        txtDialogPointLat.setText(String.valueOf(place.getLat()));
        txtDialogPointLon.setText(String.valueOf(place.getLon()));
        
        return new AlertDialog.Builder(mCtx)                
	            .setTitle(getString(R.string.location))
	            .setView(viewDialog)
	            .setIcon(android.R.drawable.ic_menu_mylocation)
	            .setPositiveButton(android.R.string.ok, 
	            	new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int whichButton) {     
	            			String lat = txtDialogPointLat.getText().toString();
	            			String lon = txtDialogPointLon.getText().toString();
	            			if(lat == null || lat.length() == 0)
	            				place.setLat(0);
	            			else
	            				place.setLat(Double.parseDouble(lat));

	            			if(lon == null || lon.length() == 0)
	            				place.setLon(0);
	            			else
	            				place.setLon(Double.parseDouble(lon));
	            			setLblLatLon();
	            			
	            			stopGetGpsPosition();
	            			
	            			WSGetLocationInfo geo = new WSGetLocationInfo();
	            			geo.execute(String.valueOf(place.getLat()), String.valueOf(place.getLon()));
	            		}
	            	}
	            )
	            .setNegativeButton(android.R.string.cancel, 
            		new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int whichButton) {   
            				stopGetGpsPosition();	
	            			
	            			if(idPlace == 0)
	            				finish();
	            		}
	            	}
	            )
	            .create();
	}
	
	private void initTimerGetGPSPos(){
		
		timerGetGPSPos = new Timer();
		timerGetGPSPos.schedule(new TimerTask() {          
			@Override
			public void run() {
				EditPlace.this.runOnUiThread(new Runnable(){
				    public void run(){
				    	txtDialogPointLat.setText(String.valueOf(gpsPosition.getLatitude()));
						txtDialogPointLon.setText(String.valueOf(gpsPosition.getLongitude()));
						if(gpsPosition.isNowLocation() == true)
							stopGetGpsPosition();
					}  
				});
			}
		}, GPSPosition.MIN_TIME, GPSPosition.MIN_TIME); // initial delay 5 second, interval 5 second
	}

	private void startGetGPSPos() {
		
		if(gpsPosition != null){
			stopGetGpsPosition();
		}
		else{
			gpsPosition = new GPSPosition(mCtx);
	
	    	txtDialogPointLat.setText(String.valueOf(gpsPosition.getLatitude()));
	    	txtDialogPointLon.setText(String.valueOf(gpsPosition.getLongitude()));
	
	    	if(gpsPosition.canGetGPSLocation()){
	        	gpsPosition.getNowLocation();
	        	initTimerGetGPSPos();
	        }
	        else{
	        	gpsPosition.showLocationSettingsAlert();
	        }
	    	
	    	btnDialogGetGpsPos.setText(mCtx.getString(R.string.gps_get_locating));
		}
	}
	
	private void stopGetGpsPosition() {
		if(gpsPosition != null){
			if(timerGetGPSPos != null)
				timerGetGPSPos.cancel();
			gpsPosition.stopLocationUpdates();
			gpsPosition = null;
		}
    	btnDialogGetGpsPos.setText(mCtx.getString(R.string.get_from_gps));
	}
	
	private void openGetMapPosition() {
		stopGetGpsPosition();
		
		dialogPoints.dismiss();
		
		openMap(Double.parseDouble(txtDialogPointLat.getText().toString()), Double.parseDouble(txtDialogPointLon.getText().toString()));
	}

	private void openMap(double lat, double lon) {
		Intent getMapPosition = new Intent(this, MapPosition.class);
		getMapPosition.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		getMapPosition.putExtra("lat", lat);
		getMapPosition.putExtra("lon", lon);
		startActivityForResult(getMapPosition, REQUEST_CODE_MAP_POSITION);		
	}

	private Dialog createDialogPhoto(Bundle savedInstanceState) {   
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dialogPhoto_array, android.R.layout.select_dialog_item);
	    return new AlertDialog.Builder(mCtx)
	    	.setTitle(getString(R.string.dialogPhotoTitle))
	    	.setAdapter(adapter, new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int item) {
		            if(item==0){
		        		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		        		startActivityForResult(takePictureIntent, REQUEST_CODE_PHOTO_CAMERA);                  
		            }
		            else{
		                Intent galleryIntent = new Intent();
		                galleryIntent.setType("image/*");
		                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
		                startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.dialogPhotoTitle)), REQUEST_CODE_PHOTO_GALLERY);
		            }
	    		}   
	    	})
	    	.create();
	}
	
	public void photoClicked(View view) {
		Dialog dialog = createDialogPhoto(null);
		dialog.show();
	}
	
	public void confClicked(View v){
		place.setDesign(txtDesign.getText().toString());
		place.setDescr(txtDescr.getText().toString());
		place.setCity(txtCity.getText().toString());
		place.setObs(txtObs.getText().toString());
		if(photo != null)
			place.setPhoto(bitmaptoByteArray(photo));		
		boolean result = place.save(mCtx);
		
		if(result){
			setResult(RESULT_OK, new Intent());
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_save_success), Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	private void confDel(){
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setMessage(mCtx.getString(R.string.msg_conf_del))
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	boolean result = place.delete(mCtx);
	    		if(result){
	    			setResult(RESULT_OK, new Intent());
	    			Toast.makeText(mCtx, mCtx.getString(R.string.msg_del_success), Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	        }
	    })
	    .setNegativeButton(android.R.string.cancel, null)
	    .show();
	}
	
	// Web Service Soap
	public class WSGetPop extends AsyncTask<String, Void, String> { 
		private String NAMESPACE = "http://tempuri.org/"; 
		private String URL = "http://192.168.137.1/wsplacessoap/Service1.asmx"; 
		private String METHOD_NAME_GET_DETAILS = "getPop"; 

		private String response = ""; 
		
		@Override
		protected String doInBackground(String... strs) { 
			String name = strs[0]; 
	
			SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME_GET_DETAILS); 
	
			Request.addProperty("name", name); 
	
			SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER12); 
			soapEnvelope.dotNet = true; 
			soapEnvelope.setOutputSoapObject(Request); 
			HttpTransportSE trans = new HttpTransportSE(URL); 
			
			try { 
				trans.call(NAMESPACE + METHOD_NAME_GET_DETAILS, soapEnvelope); 
	
				SoapPrimitive resultString = (SoapPrimitive) soapEnvelope.getResponse(); 
				response = resultString.toString(); 
			} 
			catch (Exception ex) 
			{ 
				ex.printStackTrace(); 
				Log.e("ERROR", ex.getMessage()); 
				response = "An error occurred."; 
			} 
			return response;
		} 
	
		@Override 
		protected void onPostExecute(String result) { 
			super.onPostExecute(result);
			if(result != null)			
				Toast.makeText(mCtx, getString(R.string.menu_wspop) + ": " + result, Toast.LENGTH_SHORT).show();
		} 
	}

	// Web Service Rest
	public class WSGetWeather extends AsyncTask<String, Void, ArrayList<String>> {
		private final static String METHOD_NAME_GET_WEATHER = "http://192.168.137.1/wsplacesrest/Service1.svc/weather/";	

		@Override
		protected ArrayList<String> doInBackground(String... strs) {
			
			try {
				String name = strs[0];
				
				name = name.replace("\n"," ").replace(" ", "%20");

				HttpGet request = new HttpGet(METHOD_NAME_GET_WEATHER + name);
				request.setHeader("Accept", "application/json");
				request.setHeader("Content-type", "application/json");

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);
				HttpEntity responseEntity = response.getEntity();

				char[] buffer = new char[(int) responseEntity.getContentLength()];
				InputStream stream = responseEntity.getContent();
				InputStreamReader reader = new InputStreamReader(stream);
				reader.read(buffer);
				stream.close();

				JSONArray jsondata = new JSONArray(new String(buffer));
				
				ArrayList<String> data = new ArrayList<String>();
				for(int i=0; i<jsondata.length(); i++){
					data.add(jsondata.getString(i));
				}
				
				return data;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			super.onPostExecute(result);
			
			if(result != null)
				showTemp(result);
		}
	}	
	
	private void showTemp(ArrayList<String> result){
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_temp,
		                           (ViewGroup) findViewById(R.id.toastTempRoot));

		TextView txtText = (TextView) layout.findViewById(R.id.toastTempTxtText);
		TextView txtMin = (TextView) layout.findViewById(R.id.toastTempTxtMin);
		TextView txtSep = (TextView) layout.findViewById(R.id.toastTempTxtSep);
		TextView txtMax = (TextView) layout.findViewById(R.id.toastTempTxtMax);
		
		if(result.size() == 1){
			txtText.setText(result.get(0));
			txtMin.setText("");
			txtSep.setText("");
			txtMax.setText("");
		}
		else{
			txtText.setText(getString(R.string.menu_wstemp) + ":");
			txtMin.setText(result.get(0));
			txtMax.setText(result.get(1));
		}

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 56);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();		
	}
	
	// Web Service Reverse Geocoding
	public class WSGetLocationInfo extends AsyncTask<String, Void, StringBuilder> {

		@Override
		protected StringBuilder doInBackground(String... strs) {
			try {
				String lat = strs[0];
				String lon = strs[1];

				HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&sensor=false");
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
					setNameCity(result);
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setNameCity(JSONObject jsonObject){
		String city = "";
		String country = "";
		int idxType = 0;
		
		try {
			JSONArray results =  (JSONArray) jsonObject.get("results");
			
			for(int i = 0; i < results.length(); i++){
				JSONArray types = results.getJSONObject(i).getJSONArray("types");
				if(types.getString(0).equals("administrative_area_level_1"))
					idxType = i;
			}
			
			/*
			JSONArray address = ((JSONArray) results.getJSONObject(idxType).getJSONArray("address_components"))
			city = address.getJSONObject(0).getString("short_name");
			country = address.getJSONObject(1).getString("long_name");
			*/

			String address = (String) results.getJSONObject(idxType).get("formatted_address");
			address = address.replace("District","");
			String[] aux = address.split(",");
			city = aux[0].trim();
			country = aux[1].trim();
			
			txtCity.setText(city);
			
			// portugal
			if(country.equals("Portugal"))
				setSpCountry(4);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toast.makeText(mCtx, mCtx.getString(R.string.msg_not_changed), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_place, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_edit_place_delete:
			confDel();
			return true;
		case R.id.menu_edit_place_map:
			openMap(place.getLat(), place.getLon());
			return true;
		case R.id.menu_edit_place_wspop:
			WSGetPop wsSoap = new WSGetPop();
			wsSoap.execute(txtCity.getText().toString());
			return true;
		case R.id.menu_edit_place_wstemp:
			WSGetWeather wsRest = new WSGetWeather();
			wsRest.execute(txtCity.getText().toString());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != RESULT_OK)
			return;
		
		switch (requestCode) {
		case REQUEST_CODE_MAP_POSITION:
			place.setLat(data.getDoubleExtra("lat", 0));
			place.setLon(data.getDoubleExtra("lon", 0));
			setLblLatLon();
			WSGetLocationInfo geo = new WSGetLocationInfo();
			geo.execute(String.valueOf(place.getLat()), String.valueOf(place.getLon()));
			break;
		case REQUEST_CODE_PHOTO_CAMERA:
			getPictureFromCamera(data, true);
			break;
		case REQUEST_CODE_PHOTO_GALLERY:
			getPictureFromGallery(data, true);
			break;
		}
	}
	
	private void getPictureFromCamera(Intent data, boolean compress) {
		Bundle extras = data.getExtras();
		photo = (Bitmap) extras.get("data");
		if(compress){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
			photo.compress(CompressFormat.PNG, 0, bos);
		}
		imgPhoto.setImageBitmap(photo);		
	}
	
	private void getPictureFromGallery(Intent data, boolean compress) {
        Uri selectedImageUri = data.getData();
        photo = BitmapFactory.decodeFile(getPath(selectedImageUri));
		if(compress){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			photo = Bitmap.createScaledBitmap(photo, 200, 200, false);
			photo.compress(CompressFormat.PNG, 0, bos);
		}        
		imgPhoto.setImageBitmap(photo);	
	}
	
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }	

	public byte[] bitmaptoByteArray(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
	
	
}
