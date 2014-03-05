package com.gjaf.places.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gjaf.places.GlobalSet;
import com.gjaf.places.R;
import com.gjaf.places.entities.EntityCateg;

public class AdapterCateg extends ArrayAdapter<EntityCateg> {

	private static LayoutInflater inflater = null;
	private ArrayList<EntityCateg> data;
	private EntityCateg item;
	private Context mCtx;
	
    public AdapterCateg(Context c, ArrayList<EntityCateg> d) {
		super(c, R.layout.list_view_row_categ, d);
		mCtx = c;
		data = d;
		inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return data.size();
	}

	public EntityCateg getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		item = data.get(position);
		return item.get_id();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.list_view_row_categ, null);
		}

		LinearLayout box = (LinearLayout) vi.findViewById(R.id.listViewRowCategThumbnail);
		TextView txtLetter = (TextView) vi.findViewById(R.id.listViewRowCategLetter);
		TextView txtDesign = (TextView) vi.findViewById(R.id.listViewRowCategDesign);
		TextView txtDescr = (TextView) vi.findViewById(R.id.listViewRowCategDescr);
		TextView txtNumPlaces = (TextView) vi.findViewById(R.id.listViewRowCategNumPlaces);

		item = data.get(position);
		box.setBackgroundResource(GlobalSet.mBorders[item.getIdIcon()]);
		txtLetter.setText(item.getDesign().substring(0, 1));
		txtDesign.setText(item.getDesign());
		txtDescr.setText(item.getDescr());
		txtNumPlaces.setText("...");
		int numPlaces = item.getTotPlaces(mCtx);
		if(numPlaces > 0)
			txtNumPlaces.setText(Integer.toString(numPlaces));
		return vi;
	}	
	
}
