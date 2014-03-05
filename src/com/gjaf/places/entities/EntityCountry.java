package com.gjaf.places.entities;

import java.io.Serializable;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALCountry;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;

public class EntityCountry implements Serializable {

	private int _id;
	private String design;

	public EntityCountry() {
		super();
		this._id = 0;
	}
	
	public EntityCountry(int _id, String design) {
		super();
		this._id = _id;
		this.design = design;
	}

	public EntityCountry(String design) {
		super();
		this.design = design;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	public int getTotPlaces(Context mCtx) {
		double count = 0;
		
		count = DBMain.getCount(
				mCtx, 
				DALPlace.TABLE_NAME, 
				String.format("%s = %s", DALPlace.ID_COUNTRY, this._id));

		return (int)count;
	}
	
	private boolean verifRequiredFields(Context mCtx)
	{
		if(this.getDesign().length() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_country_design), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public boolean save(Context mCtx) {
		long result = 0;

		if(!verifRequiredFields(mCtx))
			return false;
		
		if (this._id == 0)
			result = DBMain.insert(mCtx, DALCountry.TABLE_NAME, DALCountry.getContentValues(mCtx, this));
		else
			result = DBMain.update(mCtx, DALCountry.TABLE_NAME, this._id, DALCountry.getContentValues(mCtx, this));

		return result >= 0;
	}

	public boolean delete(Context mCtx) {
		long result = 0;
		
		if(this._id > 0)
			result = DBMain.deleteById(mCtx, DALCountry.TABLE_NAME, this._id);

		return result > 0;
	}
}
