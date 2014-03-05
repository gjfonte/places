	package com.gjaf.places.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gjaf.places.GlobalSet;
import com.gjaf.places.R;
import com.gjaf.places.entities.EntityPlace;

public class AdapterPlace extends ArrayAdapter<EntityPlace> {

	private static LayoutInflater inflater = null;
	private ArrayList<EntityPlace> data;
	private EntityPlace item;
	private Context mCtx;
	
	public AdapterPlace(Context c, ArrayList<EntityPlace> d) {
		super(c, R.layout.list_view_row_place, d);
		mCtx = c;
		data = d;
		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public EntityPlace getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		item = data.get(position);
		return item.get_id();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		Bitmap photo = null;
		byte[] bytePhoto = null;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_view_row_place, null);
		}

		ImageView imgPhoto = (ImageView) vi.findViewById(R.id.listViewRowPlacePhoto);
		TextView txtDesign = (TextView) vi.findViewById(R.id.listViewRowPlaceDesign);
		TextView txtDescr = (TextView) vi.findViewById(R.id.listViewRowPlaceDescr);
		TextView txtDet = (TextView) vi.findViewById(R.id.listViewRowPlaceDet);
		TextView txtLat = (TextView) vi.findViewById(R.id.listViewRowPlaceLat);
		TextView txtLatDec = (TextView) vi.findViewById(R.id.listViewRowPlaceLatDec);
		TextView txtLon = (TextView) vi.findViewById(R.id.listViewRowPlaceLon);
		TextView txtLonDec = (TextView) vi.findViewById(R.id.listViewRowPlaceLonDec);

		item = data.get(position);
		bytePhoto = item.getPhoto(); 
		if(bytePhoto != null){
			photo = BitmapFactory.decodeByteArray(bytePhoto, 0, bytePhoto.length);
			imgPhoto.setImageBitmap(photo);
		}
		txtDesign.setText(item.getDesign());
		txtDescr.setText(item.getDescr());
		txtDet.setText(item.getCateg());
		txtDet.setTextColor(mCtx.getResources().getColor(GlobalSet.mColors[item.getIdIcon()]));
		
		String[] lat = String.valueOf(item.getLat()).split("\\.");
		txtLat.setText(lat[0]);
		txtLatDec.setText(lat.length == 1 ? "00": lat[1].subSequence(0, 2));
		String[] lon = String.valueOf(item.getLon()).split("\\.");
		txtLon.setText(lon[0]);
		txtLonDec.setText(lon.length == 1 ? "00": lon[1].subSequence(0, 2));
		return vi;
	}	
	
}
