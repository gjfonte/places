package com.gjaf.places;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.adapters.AdapterSetIcon;

public class EditCategSetIcon extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_categ_set_icon);
		
	    GridView gridview = (GridView) findViewById(R.id.editCategSetIconGrid);
	    gridview.setAdapter(new AdapterSetIcon(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	    		Intent result = new Intent();
	    		result.putExtra("index", position);
	    		setResult(RESULT_OK, result);
	    		finish();	
    		}
	    });		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_categ_set_icon, menu);
		return true;
	}

}
