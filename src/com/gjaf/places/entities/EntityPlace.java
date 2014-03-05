package com.gjaf.places.entities;

import java.io.Serializable;

import android.content.Context;
import android.widget.Toast;

import com.gjaf.places.R;
import com.gjaf.places.dal.DALCateg;
import com.gjaf.places.dal.DALPlace;
import com.gjaf.places.db.DBMain;

public class EntityPlace implements Serializable {

	private int _id;
	private String design;
	private String descr;
	private String obs;
	private double lat;
	private double lon;
	private String city;
	private byte[] photo;
	private int idCountry;
	private int idCateg;
	private String country;
	private String categ;
	private int idIcon;

	public EntityPlace() {
		super();
		this._id = 0;
	}
	
	public EntityPlace(int _id, String design, String descr, String obs, double lat, double lon, String city, byte[] photo, int idCountry, int idCateg, String country, String categ, int idIcon) {
		super();
		this._id = _id;
		this.design = design;
		this.descr = descr;
		this.obs = obs;
		this.lat= lat;
		this.lon = lon;
		this.city = city;
		this.photo = photo;
		this.idCountry = idCountry;
		this.idCateg = idCateg;
		this.country = country;
		this.categ = categ;
		this.idIcon = idIcon; 
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

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	
	public int getIdCountry() {
		return idCountry;
	}

	public void setIdCountry(int idCountry) {
		this.idCountry = idCountry;
	}

	public int getIdCateg() {
		return idCateg;
	}

	public void setIdCateg(int idCateg) {
		this.idCateg = idCateg;
	}

	public String getCountry() {
		return country;
	}
	
	public String getCateg() {
		return categ;
	}

	public int getIdIcon() {
		return idIcon;
	}

	public EntityCateg getEntityCateg(Context mCtx) {
		return DALCateg.getFirst(DBMain.getById(mCtx, DALCateg.TABLE_NAME, DALCateg.columns, this.idCateg));
	}

	private boolean verifRequiredFields(Context mCtx)
	{
		if(this.getDesign().length() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_place_design), Toast.LENGTH_SHORT).show();
			return false;
		}
		if(this.getIdCateg() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_place_categ), Toast.LENGTH_SHORT).show();
			return false;
		}		
		if(this.getIdCountry() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_place_country), Toast.LENGTH_SHORT).show();
			return false;
		}
		if(this.getLat() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_place_lat), Toast.LENGTH_SHORT).show();
			return false;
		}
		if(this.getLon() == 0){
			Toast.makeText(mCtx, mCtx.getString(R.string.msg_required_place_lon), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public boolean save(Context mCtx) {
		long result = 0;

		if(!verifRequiredFields(mCtx))
			return false;

		if (this._id == 0)
			result = DBMain.insert(mCtx, DALPlace.TABLE_NAME, DALPlace.getContentValues(mCtx, this));
		else
			result = DBMain.update(mCtx, DALPlace.TABLE_NAME, this._id, DALPlace.getContentValues(mCtx, this));

		return result >= 0;
	}

	public boolean delete(Context mCtx) {
		long result = 0;
		
		if(this._id > 0)
			result = DBMain.deleteById(mCtx, DALPlace.TABLE_NAME, this._id);

		return result > 0;
	}
}
