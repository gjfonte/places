package com.gjaf.places.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gjaf.places.R;
import com.gjaf.places.entities.EntityCountry;

public class AdapterCountry extends ArrayAdapter<EntityCountry> {

	private static LayoutInflater inflater = null;
	private ArrayList<EntityCountry> data;
	private EntityCountry item;
	private Context mCtx;
	
	public AdapterCountry(Context c, ArrayList<EntityCountry> d) {
		super(c, R.layout.list_view_row_country, d);
		mCtx = c;
		data = d;
		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public EntityCountry getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		item = data.get(position);
		return item.get_id();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_view_row_country, null);
		}

		//ImageView imgPhoto = (ImageView) vi.findViewById(R.id.listViewRowCategPhoto);
		TextView txtDesign = (TextView) vi.findViewById(R.id.listViewRowCountryDesign);
		TextView txtNumPlaces = (TextView) vi.findViewById(R.id.listViewRowCountryNumPlaces);

		item = data.get(position);
		txtDesign.setText(item.getDesign());
		txtNumPlaces.setText("...");
		int numPlaces = item.getTotPlaces(mCtx);
		if(numPlaces > 0)
			txtNumPlaces.setText(String.format("%s %s", mCtx.getString(R.string.num_places), Integer.toString(numPlaces)));
		return vi;
	}	
	
}
