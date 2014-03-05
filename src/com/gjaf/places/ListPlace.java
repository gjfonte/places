package com.gjaf.places;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import com.gjaf.places.adapters.AdapterPlace;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityPlace;

public class ListPlace extends Activity {

	private AdapterPlace adapter;
	private Cursor c_adap;
	private ListView list = null;
	private int idCateg;
	
	private Context mCtx;
	
	private static int REQUEST_CODE_LOGIN = 1;
	private static int REQUEST_CODE_PLACE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_place);
		
		mCtx = this;
		
		list = (ListView) findViewById(R.id.listPlaceList);

		idCateg = getIntent().getIntExtra("idCateg", 0);
		
		setControls();
		fillList();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		c_adap.close();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setControls() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adpt, View view,
					int position, long id) {
				AdapterPlace adp = (AdapterPlace) adpt.getAdapter();
				EntityPlace place = adp.getItem(position);
				openPlace(place.get_id());
			}
		});
		
	}

	protected void openPlace(int id) {
		Intent place = new Intent(this, EditPlace.class);
		place.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		place.putExtra("id", id);
		place.putExtra("idCateg", idCateg);
		startActivityForResult(place, REQUEST_CODE_PLACE);
	}

	private void fillList() {
		try {
			//c_adap = DBMain.getAll(mCtx, DALPlace.TABLE_NAME, DALPlace.columns, DALPlace.DESIGN);
			String where = String.format("%s and %s=%s", DALPlace.WHERE_JOIN, DALPlace.ID_CATEG, idCateg);
			c_adap = DBMain.getAllJoin(mCtx, DALPlace.TABLES_JOIN, where, DALPlace.columnsJoin, null);
			adapter = new AdapterPlace(mCtx, DALPlace.converte(c_adap));
			list.setAdapter(adapter);
		} catch (Exception ex) {
			throw new Error(ex.getMessage());
		}
	}

	private void login() {
		startActivityForResult(new Intent(this, Login.class), REQUEST_CODE_LOGIN);	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == REQUEST_CODE_LOGIN)
		{
			if(resultCode != RESULT_OK)
				finish();
		}
		else if(requestCode == REQUEST_CODE_PLACE)
		{
			if(resultCode == RESULT_OK)
				fillList();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_place, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_list_place_add:
			openPlace(0);
			return true;
		case R.id.menu_list_place_search:
			Intent search = new Intent(this, SearchPlace.class);
			search.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
			search.putExtra("idCateg", idCateg);
			startActivity(search);
			return true;
		/*case R.id.menuListPlacePlacesByCateg:
			startActivity(new Intent(this, ListPlacesByCateg.class));
			return true;
		case R.id.menuListPlaceAdd:
			openPlace(0);
			return true;
		case R.id.menuListPlaceSearch:
			startActivity(new Intent(this, ListPlacesToMap.class));
			return true;*/
		}
		return super.onOptionsItemSelected(item);
	}

}
