package com.gjaf.places.dal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gjaf.places.db.DBAdapter;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityPlace;

public class DALPlace {

	public static final String TABLE_NAME = "place";
	public static final String DESIGN = "design";
	public static final String DESCR = "descr";
	public static final String OBS = "obs";
	public static final String LAT = "lat";
	public static final String LON = "lon";
	public static final String CITY = "city";
	public static final String PHOTO = "photo";
	public static final String ID_COUNTRY = "id_country";
	public static final String ID_CATEG = "id_categ";

	public static final String[] columns = new String[] { 
		DBAdapter.KEY_ID, 
		DESIGN, 
		DESCR, 
		OBS,
		LAT, 
		LON, 
		CITY,
		PHOTO,
		ID_COUNTRY, 
		ID_CATEG 
	};

	public static final String TABLES_JOIN = String.format("%s, %s, %s", 
				TABLE_NAME, DALCountry.TABLE_NAME, DALCateg.TABLE_NAME);
	
	public static final String WHERE_JOIN = String.format("%s = %s and %s = %s", 
			String.format("%s.%s", TABLE_NAME, ID_COUNTRY), String.format("%s.%s", DALCountry.TABLE_NAME, DBAdapter.KEY_ID),
			String.format("%s.%s", TABLE_NAME, ID_CATEG), String.format("%s.%s", DALCateg.TABLE_NAME, DBAdapter.KEY_ID));

	public static final String[] columnsJoin = new String[] { 
		String.format("%s.%s", TABLE_NAME, DBAdapter.KEY_ID), 
		String.format("%s.%s", TABLE_NAME, DESIGN), 
		String.format("%s.%s", TABLE_NAME, DESCR),
		String.format("%s.%s", TABLE_NAME, OBS),
		String.format("%s.%s", TABLE_NAME, LAT),
		String.format("%s.%s", TABLE_NAME, LON), 
		String.format("%s.%s", TABLE_NAME, CITY),
		String.format("%s.%s", TABLE_NAME, PHOTO), 
		String.format("%s.%s", TABLE_NAME, ID_COUNTRY), 
		String.format("%s.%s", TABLE_NAME, ID_CATEG),

		String.format("%s.%s", DALCountry.TABLE_NAME, DALCountry.DESIGN),

		String.format("%s.%s", DALCateg.TABLE_NAME, DALCateg.DESIGN),
		String.format("%s.%s", DALCateg.TABLE_NAME, DALCateg.ID_ICON),
	};

	public DALPlace(Context context) {
	}	

	public static ContentValues getContentValues(Context mCtx, Object obj) {
		ContentValues values = new ContentValues();
		
		EntityPlace ent = (EntityPlace) obj;
		
		if(ent.get_id() == 0)
			ent.set_id((int)DBMain.getAggregate(mCtx, TABLE_NAME, DBAdapter.columnsMax, null) + 1);
		values.put(DBAdapter.KEY_ID, ent.get_id());
		values.put(DESIGN, ent.getDesign());
		values.put(DESCR, ent.getDescr());
		values.put(OBS, ent.getObs());
		values.put(LAT, ent.getLat());
		values.put(LON, ent.getLon());
		values.put(CITY, ent.getCity());
		values.put(PHOTO, ent.getPhoto());
		values.put(ID_COUNTRY, ent.getIdCountry());
		values.put(ID_CATEG, ent.getIdCateg());

		return values;
	}
	
	public static boolean save(Context mCtx, EntityPlace obj, long id) {
		long result = 0;

		if (id == 0)
			result = DBMain.insert(mCtx, DALPlace.TABLE_NAME, DALPlace.getContentValues(mCtx, obj));
		else
			result = DBMain.update(mCtx, DALPlace.TABLE_NAME, id, DALPlace.getContentValues(mCtx, obj));

		return result >= 0;
	}	

	public static EntityPlace getFirst(Cursor c) {
		EntityPlace single;
		
		c.moveToFirst();
		single = new EntityPlace(
				c.getInt(0), 
				c.getString(1), 
				c.getString(2), 
				c.getString(3), 
				c.getDouble(4), 
				c.getDouble(5), 
				c.getString(6), 
				c.getBlob(7),
				c.getInt(8),
				c.getInt(9),
				c.getColumnCount() > 10 ? c.getString(10) : null, 
				c.getColumnCount() > 11 ? c.getString(11) : null, 
				c.getColumnCount() > 12 ? c.getInt(12) : 0 
				);
		return single;
	}
	
	public static ArrayList<EntityPlace> converte(Cursor c) {
		ArrayList<EntityPlace> arr = new ArrayList<EntityPlace>();
		EntityPlace single;
		
		c.moveToFirst();
        while (c.isAfterLast() == false) {
    		single = new EntityPlace(
    				c.getInt(0), 
    				c.getString(1), 
    				c.getString(2), 
    				c.getString(3), 
    				c.getDouble(4), 
    				c.getDouble(5), 
    				c.getString(6), 
    				c.getBlob(7),
    				c.getInt(8),
    				c.getInt(9),
    				c.getColumnCount() > 10 ? c.getString(10) : null, 
    				c.getColumnCount() > 11 ? c.getString(11) : null, 
    				c.getColumnCount() > 12 ? c.getInt(12) : 0 
    				);
        	arr.add(single);
        	c.moveToNext();
        }
        c.close();
		
		return arr;
	}
}
