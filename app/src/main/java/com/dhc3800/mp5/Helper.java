package com.dhc3800.mp5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class Helper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "locations.db";
    public static final String TABLE_NAME = "Locations2";
    public static final String COLUMN_NUMBER="locationNum";
    public static final String COLUMN_ID = "locationID";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ADDRESS= "address";
    public static final String COLUMN_NAME = "name";
//    public static final String COLUMN_SECONDS = "seconds";


    public Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ID + " TEXT,"
                    + COLUMN_LATITUDE + " TEXT,"
                    + COLUMN_LONGITUDE + " TEXT,"
                    + COLUMN_ADDRESS + " TEXT,"
                    + COLUMN_NAME + " TEXT)";
    private static final String DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE_ENTRIES);

    }
    public void insert(SetLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS, location.address);
        values.put(COLUMN_ID, location.id);
        values.put(COLUMN_LATITUDE, location.Latitude);
        values.put(COLUMN_LONGITUDE, location.Longitude);
        values.put(COLUMN_NAME, location.name);
        db.insert(TABLE_NAME, null, values);
    }

    public ArrayList<SetLocation> getAll() {
        ArrayList<SetLocation> locations = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                Double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
                Double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                SetLocation location = new SetLocation(latitude, longitude, id, address, name);
                locations.add(location);
            } while (cursor.moveToNext());
        }
        db.close();
        return locations;
    }


    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }
    public SetLocation getLocation(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_ADDRESS, COLUMN_LONGITUDE, COLUMN_LATITUDE, COLUMN_NUMBER},
                COLUMN_ID + " =?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        SetLocation location = new SetLocation(
                cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        cursor.close();
        return location;

    }



    public void delete(SetLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ADDRESS+" = ?", new String[]{location.id});
        db.close();
    }
}