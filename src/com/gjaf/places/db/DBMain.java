package com.gjaf.places.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DBMain {

	private static DBAdapter dbadap;

	public static Cursor getAll(Context mCtx, String table, String[] columns, String order) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.get(table, columns, null, order, -1);
		close();
		return c;
	}
	
	public static Cursor getAllJoin(Context mCtx, String inTables, String whereJoin, String[] columns, String order) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.getJoin(inTables, whereJoin, columns, order);
		//close();
		return c;
	}	

	public static Cursor getById(Context mCtx, String table, String[] columns,
			long id) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.get(table, columns, null, null, id);
		close();
		return c;
	}

	public static Cursor get(Context mCtx, String table, String[] columns,
			String where, String orderBy, long id) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.get(table, columns, where, orderBy, -1);
		close();
		return c;
	}

	public static int deleteById(Context mCtx, String table, long id) {
		dbadap = DBAdapter.getInstance(mCtx);
		int i = dbadap.delete(table, null, id);
		close();
		return i;
	}

	public static int deleteWhere(Context mCtx, String table, String where) {
		dbadap = DBAdapter.getInstance(mCtx);
		int i = dbadap.delete(table, where, -1);
		close();
		return i;
	}

	public static int deleteAll(Context mCtx, String table) {
		dbadap = DBAdapter.getInstance(mCtx);
		int i = dbadap.delete(table, null, -1);
		close();
		return i;
	}

	public static int update(Context mCtx, String table, long id,
			ContentValues values) {
		dbadap = DBAdapter.getInstance(mCtx);
		int i = dbadap.update(table, id, values, null);
		close();
		return i;
	}

	public static int updateWhere(Context mCtx, String table,
			ContentValues values, String where) {
		dbadap = DBAdapter.getInstance(mCtx);
		int i = dbadap.update(table, -1, values, where);
		close();
		return i;
	}

	public static double getAggregate(Context mCtx, String table,
			String[] columnsAggregate, String where) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.get(table, columnsAggregate, where, null, -1);
		c.moveToFirst();
		double d = c.getInt(0);
		close();
		return d;
	}

	public static double getCount(Context mCtx, String table, String where) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.get(table, dbadap.columnsCount, where, null, -1);
		c.moveToFirst();
		double d = c.getInt(0);
		close();
		return d;
	}

	public static double getSum(Context mCtx, String table, String where) {
		dbadap = DBAdapter.getInstance(mCtx);
		Cursor c = dbadap.get(table, dbadap.columnsSum, where, null, -1);
		c.moveToFirst();
		double d = c.getInt(0);
		close();
		return d;
	}

	public static long insert(Context mCtx, String table, ContentValues values) {
		dbadap = DBAdapter.getInstance(mCtx);
		long l = dbadap.insert(table, values);
		close();
		return l;
	}

	public static void close() {
		dbadap.close();
	}

	public static void execSQL(Context mCtx, String query) {
		dbadap = DBAdapter.getInstance(mCtx);
		dbadap.execSQL(query);
		close();
	}

}
