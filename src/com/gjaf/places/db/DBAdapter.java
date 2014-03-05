package com.gjaf.places.db;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Build;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "places";
	private static final int DATABASE_VERSION = 16;
	private static final String TAG = "DBAdapter";

	private static SQLiteDatabase db;
	public static final String KEY_ID = "_id";
	public static final String[] columnsCount = new String[] { "count(*)" };
	public static final String[] columnsSum = new String[] { "sum(*)" };
	public static final String[] columnsMax = new String[] { "max(" + KEY_ID
			+ ")" };

	private static final String country_TABLE_CREATE = "create table country("
			+ "_id integer primary key, " + "design varchar(20));";
	private static final String categ_TABLE_CREATE = "create table categ("
			+ "_id integer primary key, " + "design varchar(20), "
			+ "descr varchar(200), idIcon integer);";
	private static final String place_TABLE_CREATE = "create table place("
			+ "_id integer primary key, " + "design varchar(20), "
			+ "descr varchar(200), " + "obs varchar(300), "
			+ "lat double, " + "lon double, city varchar(40), photo blob, "
			+ "id_country integer, " + "id_categ integer, "
			+ "FOREIGN KEY(id_country) REFERENCES country(_id), "
			+ "FOREIGN KEY(id_categ) REFERENCES categ(_id));";

	private static DBAdapter instance;

	public DBAdapter(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public DBAdapter(Context ctx, String dbName, String sql, String tableName,
			int ver) {

		super(ctx, dbName, null, ver);
		Log.i(TAG, "Creating or opening database [ " + dbName + " ].");
	}

	public static DBAdapter getInstance(Context ctx) {
		if (instance == null) {
			instance = new DBAdapter(ctx);
			try {
				db = instance.getWritableDatabase();
			} catch (SQLiteException se) {
				Log.e(TAG, "Cound not create and/or open the database [ "
						+ DATABASE_NAME
						+ " ] that will be used for reading and writing.", se);
			}
		}
		return instance;
	}

	public static DBAdapter getInstance(Context ctx, String dbName, String sql,
			String tableName, int ver) {
		if (instance == null) {
			instance = new DBAdapter(ctx, dbName, sql, tableName, ver);
			try {
				Log.i(TAG, "Creating or opening the database [ " + dbName
						+ " ].");
				db = instance.getWritableDatabase();
			} catch (SQLiteException se) {
				Log.e(TAG, "Cound not create and/or open the database [ "
						+ dbName
						+ " ] that will be used for reading and writing.", se);
			}
		}
		return instance;
	}

	public void close() {
		if (instance != null) {
			Log.i(TAG, "Closing the database [ " + DATABASE_NAME + " ].");
			db.close();
			instance = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Trying to create database table if it isn't existed.");
		try {
			db.execSQL(country_TABLE_CREATE);
			db.execSQL(categ_TABLE_CREATE);
			db.execSQL(place_TABLE_CREATE);

			db.execSQL("insert into country values (1, 'Alemanha')");
			db.execSQL("insert into country values (2, 'Espanha')");
			db.execSQL("insert into country values (3, 'França')");
			db.execSQL("insert into country values (4, 'Portugal')");
			db.execSQL("insert into country values (5, 'Reino Unido')");

			db.execSQL("insert into categ values (1, 'Bares', 'Bares, Animação noturna, Diversão, Lazer', 0);");
			db.execSQL("insert into categ values (2, 'Hoteis', 'Descanso, Dormir', 0);");
			db.execSQL("insert into categ values (3, 'Praias', 'Praia, Mar, Água', 0);");
			db.execSQL("insert into categ values (4, 'Restaurantes', 'Almoçar, Jantar, Comer', 0);");

			Log.w(TAG, "Database create country, categ");
			/*
			db.execSQL("insert into place(_id, design, descr, lat, lon, city, id_country, id_categ) values (1, 'Restaurantes XPTO', 'Boa localização...', '41.696164', '-8.815906', 'Viana do Castelo', 4, 4);");
			db.execSQL("insert into place(_id, design, descr, lat, lon, city, id_country, id_categ) values (2, 'Praia Norte', 'Boas ondas...', '41.695283', '-8.850217', 'Viana do Castelo', 4, 3);");
			*/
			Log.w(TAG, "Database create successfuly");

		} catch (SQLException ex) {
			Log.e(TAG,
					"Cound not create the database table according to the SQL statement ",
					ex);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("drop table if exists place");
			db.execSQL("drop table if exists categ");
			db.execSQL("drop table if exists country");
			onCreate(db);
		} catch (SQLException ex) {
			Log.e(TAG, "Cound not drop the database table ", ex);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onConfigure(SQLiteDatabase db) {
		super.onConfigure(db);
		db.execSQL("PRAGMA foreign_keys=ON;");
	}

	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	public Cursor get(String table, String[] columns, String where,
			String orderBy, long id) {
		Cursor cursor = db.query(true, table, columns, createWhere(where, id),
				null, null, null, orderBy, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public Cursor getJoin(String inTables, String whereJoin,
			String[] columns, String orderBy) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(inTables);
		queryBuilder.appendWhere(whereJoin);

		Cursor c = queryBuilder.query(db, columns, null, null, null, null,
				orderBy);
		return c;
	}

	public int delete(String table, String where, long id) {
		return db.delete(table, createWhere(where, id), null);
	}

	public int update(String table, long id, ContentValues values, String where) {
		return db.update(table, values, createWhere(where, id), null);
	}

	public void execSQL(String query) {
		db.execSQL(query);
	}

	private String createWhere(String where, long id) {
		String whereFinal = where;
		if (id != -1) {
			if (where != null) {
				whereFinal = where + ", " + KEY_ID + "=" + id;
			} else {
				whereFinal = KEY_ID + "=" + id;
			}
		}
		return whereFinal;
	}
}
