package com.dtech.tenakatainterview.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "user_details";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER_DETAILS = "user_details";

    private static final String KEY_ID = "id";

    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_MARITAL_STATUS = "marital_status";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_GPS = "gps_location";
    private static final String KEY_GENDER = "gender";

    private static final String CREATE_TABLE_USER_DETAILS = "CREATE TABLE "
            + TABLE_USER_DETAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT, "
            + KEY_AGE + " TEXT, "
            + KEY_MARITAL_STATUS + " TEXT, "
            + KEY_PHOTO + " TEXT, "
            + KEY_HEIGHT + " TEXT, "
            + KEY_GPS + " TEXT, "
            + KEY_GENDER + " TEXT );";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER_DETAILS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS '" + TABLE_USER_DETAILS +"'");

        onCreate(db);
    }

    public void addUserDetails(String name,String age,String status, String photo, String height,
                         String gps, String gender){

        SQLiteDatabase db = this.getWritableDatabase();

        //TABLE_COORDINATES_ATTRIBUTE_NAME
        ContentValues valuesRef = new ContentValues();
        valuesRef.put(KEY_NAME, name);
        valuesRef.put(KEY_AGE, age);
        valuesRef.put(KEY_MARITAL_STATUS, status);
        valuesRef.put(KEY_PHOTO, photo);
        valuesRef.put(KEY_HEIGHT, height);
        valuesRef.put(KEY_GPS, gps);
        valuesRef.put(KEY_GENDER, gender);
        db.insert(TABLE_USER_DETAILS, null, valuesRef);

    }

}
