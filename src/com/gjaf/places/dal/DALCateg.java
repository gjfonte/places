package com.gjaf.places.dal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.gjaf.places.db.DBAdapter;
import com.gjaf.places.db.DBMain;
import com.gjaf.places.entities.EntityCateg;

public class DALCateg {

	public static final String TABLE_NAME = "categ";
	public static final String DESIGN = "design";
	public static final String DESCR = "descr";
	public static final String ID_ICON = "idIcon";

	public static final String[] columns = new String[] { DBAdapter.KEY_ID, DESIGN, DESCR, ID_ICON };

	public DALCateg(Context context) {
	}	

	public static ContentValues getContentValues(Context mCtx, Object obj) {
		ContentValues values = new ContentValues();
		
		EntityCateg ent = (EntityCateg) obj;
		
		if(ent.get_id() == 0)
			ent.set_id((int)DBMain.getAggregate(mCtx, TABLE_NAME, DBAdapter.columnsMax, null) + 1);
		values.put(DBAdapter.KEY_ID, ent.get_id());
		values.put(DESIGN, ent.getDesign());
		values.put(DESCR, ent.getDescr());
		values.put(ID_ICON, ent.getIdIcon());

		return values;
	}
	
	public static boolean save(Context mCtx, EntityCateg obj, long id) {
		long result = 0;

		if (id == 0)
			result = DBMain.insert(mCtx, DALCateg.TABLE_NAME, DALCateg.getContentValues(mCtx, obj));
		else
			result = DBMain.update(mCtx, DALCateg.TABLE_NAME, id, DALCateg.getContentValues(mCtx, obj));

		return result >= 0;
	}	

	public static EntityCateg getFirst(Cursor c) {
		EntityCateg single;
		
		c.moveToFirst();
		single = new EntityCateg(
				c.getInt(0), 
				c.getString(1),
				c.getString(2),
				c.getInt(3)
				);
		return single;
	}
	
	public static ArrayList<EntityCateg> converte(Cursor c) {
		ArrayList<EntityCateg> arr = new ArrayList<EntityCateg>();
		EntityCateg single;
		
		c.moveToFirst();
        while (c.isAfterLast() == false) {
    		single = new EntityCateg(
    				c.getInt(0), 
    				c.getString(1),
    				c.getString(2),
    				c.getInt(3)
    				);
        	arr.add(single);
        	c.moveToNext();
        }
        c.close();
		
		return arr;
	}
}
