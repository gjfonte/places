package com.gjaf.places;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityPlace;

public class SearchPlace extends Activity {

	private Context mCtx;
	
	private ArrayAdapter<String> adapter;
	private Cursor c_adap;
	private ArrayList<EntityPlace> places;
	private ArrayList<String> listItems;
	private ListView list = null;
	private int idCateg;
	private String filtDesign = "";
	private String orderDesign = "asc";
	
	private MenuItem searchItem;
	private SearchView searchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_place);
		
		mCtx = this;
		
		list = (ListView) findViewById(R.id.listSearchPlaceList);
		
		listItems = new ArrayList<String>();
		idCateg = getIntent().getIntExtra("idCateg", 0);
		
		fillList();
		
		setControls();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setControls() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	private void fillList() {
		try {
			String order = String.format("%s.%s %s", DALPlace.TABLE_NAME, DALPlace.DESIGN, orderDesign);
			String where = "";
			if(idCateg == 0)
				where = DALPlace.WHERE_JOIN;
			else
				where = String.format("%s and %s.%s=%s", DALPlace.WHERE_JOIN, DALPlace.TABLE_NAME, DALPlace.ID_CATEG, idCateg);
				
			if(filtDesign.length() > 0){
				where += String.format(" and %s.%s like", DALPlace.TABLE_NAME, DALPlace.DESIGN) + "'%" + filtDesign + "%'";
			}
			c_adap = DBMain.getAllJoin(mCtx, DALPlace.TABLES_JOIN, where, DALPlace.columnsJoin, order);
			places = DALPlace.converte(c_adap);
			
			listItems.clear();
			for(EntityPlace p : places){
				listItems.add(p.getDesign());
			}
		    adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, listItems);
		    list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

			list.setAdapter(adapter);
		} catch (Exception ex) {
			throw new Error(ex.getMessage());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_place, menu);
		searchItem = menu.findItem(R.id.menu_search_place_search);
		searchView = (SearchView) searchItem.getActionView();
		setupSearchView(searchItem);		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;		 
		    case R.id.menu_search_place_map:
		    	openMap();
		        return true;
			case R.id.menu_search_place_del_search:
		    	filtDesign = "";
		    	fillList();
		        return true;
		    case R.id.menu_search_place_order_asc:
		    	orderDesign = "asc";
		    	fillList();
		        return true;
		    case R.id.menu_search_place_order_desc:
		    	orderDesign = "desc";
		    	fillList();
		        return true;
		    case R.id.menu_search_place_sel_all:
		    	setAllSellected();
		    	break;
	    }
		return super.onOptionsItemSelected(item);
	}

	private void setAllSellected() {
		for (int i=0;i < list.getCount();i++){
			list.setItemChecked(i, true);
		}	
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupSearchView(final MenuItem searchItem) {

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
        		setNewFilt(query);
            	searchItem.collapseActionView();
            	
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }
	
	protected void setNewFilt(String query) {
		filtDesign = query;
		fillList();
	}

	private void openMap() {
		Boolean checkedOk = false;
		String ids = "0";
		
		SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
		for (int i=0;i < list.getCount();i++){
		    if(checkedItemPositions.get(i)){
		    	ids += "," + places.get(i).get_id();
		    	checkedOk = true;
		    }
		}		
		
		if(!checkedOk){
			Toast.makeText(mCtx, getString(R.string.msg_not_selected), Toast.LENGTH_SHORT).show();
		}
		else{
			Intent map = new Intent(this, ShowMap.class);
			map.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_ANIMATION);
			map.putExtra("ids", ids);
			startActivity(map);
		
		}
	}	

}
