package com.gjaf.places.entities;

import java.io.Serializable;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALCateg;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;

public class EntityCateg implements Serializable {

	private int _id;
	private String design;
	private String descr;
	private int idIcon;

	public EntityCateg() {
		super();
		this._id = 0;
	}
	public EntityCateg(int _id, String design, String descr, int IdIcon) {
		super();
		this._id = _id;
		this.design = design;
		this.descr = descr;
		this.idIcon = IdIcon;
	}

	public EntityCateg(String design, String descr, int IdIcon) {
		super();
		this._id = 0;
		this.design = design;
		this.descr = descr;
		this.idIcon = IdIcon;
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

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	public int getIdIcon() {
		return idIcon;
	}
	public void setIdIcon(int idIcon) {
		this.idIcon = idIcon;
	}
	
	public int getTotPlaces(Context mCtx) {
		double count = 0;
		
		count = DBMain.getCount(
				mCtx, 
				DALPlace.TABLE_NAME, 
				String.format("%s = %s", DALPlace.ID_CATEG, this._id));
		
		return (int)count;
	}
	
	private boolean verifRequiredFields(Context mCtx)
	{
		if(this.getDesign().length() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_categ_design), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public boolean save(Context mCtx) {
		long result = 0;

		if(!verifRequiredFields(mCtx))
			return false;
		
		if (this._id == 0)
			result = DBMain.insert(mCtx, DALCateg.TABLE_NAME, DALCateg.getContentValues(mCtx, this));
		else
			result = DBMain.update(mCtx, DALCateg.TABLE_NAME, this._id, DALCateg.getContentValues(mCtx, this));

		return result >= 0;
	}

	public boolean delete(Context mCtx) {
		long result = 0;
		
		if(this._id > 0)
			result = DBMain.deleteById(mCtx, DALCateg.TABLE_NAME, this._id);

		return result > 0;
	}
}
