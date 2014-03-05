package com.gjaf.places;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALCateg;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCateg;

public class EditCateg extends Activity {

	private EntityCateg categ;
	private int idCateg;
	private int numPlaces;
	private Context mCtx;
	
	private ImageView imgPhoto;
	private EditText txtDesign;
	private EditText txtDescr;
	private TextView lblNumPlaces;
	private Button btnConf;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_categ);
		
		mCtx = this;

		imgPhoto = (ImageView) findViewById(R.id.editCategImgPhoto);
		txtDesign = (EditText) findViewById(R.id.editCategTxtDesign);
		txtDescr = (EditText) findViewById(R.id.editCategTxtDescr);
		lblNumPlaces = (TextView) findViewById(R.id.editCategLblNumPlaces);
		btnConf = (Button) findViewById(R.id.editCategBtnConf);

		idCateg = getIntent().getIntExtra("id", 0);
		numPlaces = 0;
		if(idCateg == 0)
			categ = new EntityCateg();
		else{
			categ = DALCateg.getFirst(DBMain.getById(mCtx, DALCateg.TABLE_NAME, DALCateg.columns, idCateg));
			numPlaces = categ.getTotPlaces(mCtx);
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
		
		imgPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openSetIcon();
			}
		});
		
		setPoint(categ.getIdIcon());
		lblNumPlaces.setText(String.valueOf(numPlaces));
		txtDesign.setText(categ.getDesign());
		txtDescr.setText(categ.getDescr());
	}
	
	protected void openSetIcon() {
		startActivityForResult(new Intent(this, EditCategSetIcon.class), 0);
	}

	public void confClicked(View v){
		categ.setDesign(txtDesign.getText().toString());
		categ.setDescr(txtDescr.getText().toString());

		boolean result = categ.save(mCtx);
		
		if(result){
			setResult(RESULT_OK, new Intent());
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_save_success), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void confDel(){
		if(idCateg == 0)
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
		        public void onClick(DialogInterface dialog, int which) {
		        	boolean result = categ.delete(mCtx);
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
	}
	
	private void setPoint(int index) {
		categ.setIdIcon(index);		
		imgPhoto.setImageResource(GlobalSet.mPoints[index]);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Toast.makeText(mCtx, mCtx.getString(R.string.msg_not_changed), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_categ, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuEditCategDelete:
			confDel();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			setPoint(data.getIntExtra("index", 0));
		}
	}
}
