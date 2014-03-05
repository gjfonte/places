package com.gjaf.places;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.adapters.AdapterCateg;
import com.gjaf.places.dal.DALCateg;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCateg;

public class ListCateg extends Activity {

	private AdapterCateg adapter;
	private Cursor c_adap;
	private ListView list = null;

	private Context mCtx;
	
	private static int REQUEST_CODE_CATEG = 1;
	private static int REQUEST_CODE_LOGIN = 1;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_categ);

		mCtx = this;
		//login();
		
		list = (ListView) findViewById(R.id.listCategList);

		setControls();
		fillList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillList();
	}
	
	private void setControls() {
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adpt, View view, int position, long id) {
				AdapterCateg adp = (AdapterCateg) adpt.getAdapter();
				EntityCateg categ = adp.getItem(position);
				openListPlace(categ.get_id());
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adpt, View view, int position, long id) {
				AdapterCateg adp = (AdapterCateg) adpt.getAdapter();
				EntityCateg categ = adp.getItem(position);
				openCateg(categ.get_id());
				return false;
			}
			
		});
	}

	protected void openListPlace(int idCateg) {
		Intent list = new Intent(this, ListPlace.class);
		list.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		list.putExtra("idCateg", idCateg);
		startActivity(list);
	}
	
	private void openCateg(int idCateg){
		Intent categ = new Intent(this, EditCateg.class);
		categ.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
		categ.putExtra("id", idCateg);
		startActivityForResult(categ, REQUEST_CODE_CATEG);
		if(idCateg > 0)
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_edit_categ) , Toast.LENGTH_SHORT).show();
	}

	private void fillList() {
		try {
			c_adap = DBMain.getAll(mCtx, DALCateg.TABLE_NAME, DALCateg.columns, DALCateg.DESIGN);
			adapter = new AdapterCateg(mCtx, DALCateg.converte(c_adap));
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
		
		if(requestCode == REQUEST_CODE_CATEG){
			if(resultCode == RESULT_OK)
				fillList();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_categ, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_list_categ_add:
			openCateg(0);
			return true;
		case R.id.menu_list_categ_search:
			startActivity(new Intent(this, SearchPlace.class));
			return true;
		case R.id.menu_list_categ_country:
			startActivity(new Intent(this, ListCountry.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
