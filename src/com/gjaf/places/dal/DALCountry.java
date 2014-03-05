package com.gjaf.places.dal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gjaf.places.db.DBAdapter;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCountry;

public class DALCountry {

	public static final String TABLE_NAME = "country";
	public static final String DESIGN = "design";

	public static final String[] columns = new String[] { DBAdapter.KEY_ID, DESIGN };

	public DALCountry(Context context) {
	}	

	public static ContentValues getContentValues(Context mCtx, Object obj) {
		ContentValues values = new ContentValues();
		
		EntityCountry ent = (EntityCountry) obj;
		
		if(ent.get_id() == 0)
			ent.set_id((int)DBMain.getAggregate(mCtx, TABLE_NAME, DBAdapter.columnsMax, null) + 1);
		values.put(DBAdapter.KEY_ID, ent.get_id());
		values.put(DESIGN, ent.getDesign());

		return values;
	}
	
	public static boolean save(Context mCtx, EntityCountry obj, long id) {
		long result = 0;

		if (id == 0)
			result = DBMain.insert(mCtx, DALCountry.TABLE_NAME, DALCountry.getContentValues(mCtx, obj));
		else
			result = DBMain.update(mCtx, DALCountry.TABLE_NAME, id, DALCountry.getContentValues(mCtx, obj));

		return result >= 0;
	}	

	public static EntityCountry getFirst(Cursor c) {
		EntityCountry single;
		
		c.moveToFirst();
		single = new EntityCountry(c.getInt(0), c.getString(1));
		return single;
	}
	
	public static ArrayList<EntityCountry> converte(Cursor c) {
		ArrayList<EntityCountry> arr = new ArrayList<EntityCountry>();
		EntityCountry single;
		
		c.moveToFirst();
        while (c.isAfterLast() == false) {
    		single = new EntityCountry(c.getInt(0), c.getString(1));
        	arr.add(single);
        	c.moveToNext();
        }
        c.close();
		
		return arr;
	}
}
