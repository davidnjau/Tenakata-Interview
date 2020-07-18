package com.dtech.tenakatainterview.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";

    private static final String KEY_GENDER = "gender";

    private static final String CREATE_TABLE_USER_DETAILS = "CREATE TABLE "
            + TABLE_USER_DETAILS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT, "
            + KEY_AGE + " TEXT, "
            + KEY_MARITAL_STATUS + " TEXT, "
            + KEY_PHOTO + " TEXT, "
            + KEY_HEIGHT + " TEXT, "
            + KEY_LONGITUDE + " TEXT, "
            + KEY_LATITUDE + " TEXT, "
            + KEY_GENDER + " TEXT );";

    private DatabaseReference mDatabase;

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

    public long addUserDetails(String name,String age,String status, String photo, String height,
                         String latitude, String longitude, String gender){

        SQLiteDatabase db = this.getWritableDatabase();

        //TABLE_COORDINATES_ATTRIBUTE_NAME
        ContentValues valuesRef = new ContentValues();
        valuesRef.put(KEY_NAME, name);
        valuesRef.put(KEY_AGE, age);
        valuesRef.put(KEY_MARITAL_STATUS, status);
        valuesRef.put(KEY_PHOTO, photo);
        valuesRef.put(KEY_HEIGHT, height);

        valuesRef.put(KEY_LATITUDE, latitude);
        valuesRef.put(KEY_LONGITUDE, longitude);

        valuesRef.put(KEY_GENDER, gender);

        return db.insertWithOnConflict(TABLE_USER_DETAILS, null, valuesRef, SQLiteDatabase.CONFLICT_IGNORE);

    }

    public void updateImageUrl(long id, String url) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO, url);

        // update row in students table base on students.is value
        db.update(TABLE_USER_DETAILS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});

        UploadData(id);

    }

    public void UploadData(long id){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tenakata_db");
        DatabaseReference newPost = mDatabase.push();

        String selectQuery = "SELECT * FROM " + TABLE_USER_DETAILS +" WHERE " + KEY_ID + " = '"+id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null){

            if (c.moveToFirst()){

                do {

                    String Name = c.getString(c.getColumnIndex(KEY_NAME));
                    String Age = c.getString(c.getColumnIndex(KEY_AGE));
                    String Marital_Status = c.getString(c.getColumnIndex(KEY_MARITAL_STATUS));
                    String Photo_url = c.getString(c.getColumnIndex(KEY_PHOTO));
                    String Height = c.getString(c.getColumnIndex(KEY_HEIGHT));
                    String Latitude = c.getString(c.getColumnIndex(KEY_LATITUDE));
                    String Longitude = c.getString(c.getColumnIndex(KEY_LONGITUDE));
                    String Gender = c.getString(c.getColumnIndex(KEY_GENDER));

                    newPost.child("name").setValue(Name);
                    newPost.child("age").setValue(Age);
                    newPost.child("marital_status").setValue(Marital_Status);
                    newPost.child("photo_url").setValue(Photo_url);
                    newPost.child("height").setValue(Height);
                    newPost.child("latitude").setValue(Latitude);
                    newPost.child("longitude").setValue(Longitude);
                    newPost.child("gender").setValue(Gender);

                }while (c.moveToNext());

            }

            c.close();
        }

    }


}
