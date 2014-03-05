package com.gjaf.places;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALCountry;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCountry;

public class EditCountry extends Activity {

	private EntityCountry country;
	private int idCountry;
	private int numPlaces;
	private Context mCtx;

	private EditText txtDesign;
	private TextView lblNumPlaces;
	private Button btnConf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_country);

		mCtx = this;

		txtDesign = (EditText) findViewById(R.id.editCountryTxtDesign);
		lblNumPlaces = (TextView) findViewById(R.id.editCountryLblNumPlaces);
		btnConf = (Button) findViewById(R.id.editCountryBtnConf);

		idCountry = getIntent().getIntExtra("id", 0);
		numPlaces = 0;
		if (idCountry == 0)
			country = new EntityCountry();
		else{
			country = DALCountry.getFirst(DBMain.getById(mCtx, DALCountry.TABLE_NAME, DALCountry.columns, idCountry));
			numPlaces = country.getTotPlaces(mCtx);		
		}

		setControls();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setControls() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			getActionBar().setCustomView(R.layout.action_bar_edit);
			btnConf.setVisibility(View.GONE);
		}
		
		lblNumPlaces.setText(String.valueOf(numPlaces));
		txtDesign.setText(country.getDesign());
	}

	public void confClicked(View v) {
		country.setDesign(txtDesign.getText().toString());

		boolean result = country.save(mCtx);

		if (result) {
			setResult(RESULT_OK, new Intent());
			finish();
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_save_success),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void confDel() {
		if(idCountry == 0)
			return;
		
		if(numPlaces > 0){
			new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_info)
	        .setTitle(mCtx.getString(R.string.msg_tit_inf))
	        .setMessage(mCtx.getString(R.string.msg_cannot_del))
	        .setPositiveButton(android.R.string.ok, null)
		    .show();		
		}
		else{
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage(mCtx.getString(R.string.msg_conf_del))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					boolean result = country.delete(mCtx);
					if (result) {
						setResult(RESULT_OK, new Intent());
						finish();
						Toast.makeText(
								mCtx,
								mCtx.getString(R.string.msg_del_success),
								Toast.LENGTH_SHORT).show();
					}
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.show();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toast.makeText(mCtx, mCtx.getString(R.string.msg_not_changed), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_country, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuEditCountryDelete:
			confDel();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
