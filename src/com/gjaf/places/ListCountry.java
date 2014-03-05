package com.gjaf.places;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gjaf.places.R;
import com.gjaf.places.adapters.AdapterCountry;
import com.gjaf.places.dal.DALCountry;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCountry;

public class ListCountry extends Activity {

	private AdapterCountry adapter;
	private Cursor c_adap;
	private ListView list = null;
	
	private Context mCtx;
	
	private static int REQUEST_CODE = 1; 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_country);
		
		mCtx = this;
		
		list = (ListView) findViewById(R.id.listCountryList);

		setControls();
		fillList();
	}

	
	@SuppressLint("NewApi")
	private void setControls() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adpt, View view,
					int position, long id) {
				AdapterCountry adp = (AdapterCountry) adpt.getAdapter();
				EntityCountry country = adp.getItem(position);
				openCountry(country.get_id());
			}
		});
	}


	protected void openCountry(int get_id) {
		Intent country = new Intent(this, EditCountry.class);
		country.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NO_ANIMATION);
		country.putExtra("id", get_id);
		startActivityForResult(country, REQUEST_CODE);
	}


	private void fillList() {
		try {
			c_adap = DBMain.getAll(mCtx, DALCountry.TABLE_NAME, DALCountry.columns, DALCountry.DESIGN);
			adapter = new AdapterCountry(mCtx, DALCountry.converte(c_adap));
			list.setAdapter(adapter);
		} catch (Exception ex) {
			throw new Error(ex.getMessage());
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_CODE){
			if(resultCode == RESULT_OK)
				fillList();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_country, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menuListCountryAdd:
			openCountry(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
