package com.example.karthik.spider4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

/**
 * Created by karthik on 02-07-2018.
 */

public class DatabaseArtist extends SQLiteOpenHelper {
    private final String table_name = "Artists";
    private static final String database = "ArtistData";
    private final String Col1 = "_id";
    private final String Col2 = "artistName";
    private final String Col3 = "artistRating";
    private final String Col4 = "artistGenre";

    public DatabaseArtist(Context context) {
        super(context, database, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + table_name + " (" + Col1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + Col2 + " VARCHAR(255)," + Col3 + " VARCHAR(255)," + Col4 + " VARCHAR(255));";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addData(String aname, String arating, String agenre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col2, aname);
        contentValues.put(Col4, agenre);
        contentValues.put(Col3, arating);
        db.insert(table_name, null, contentValues);
    }

    public Cursor getFavArtistData() {
        SQLiteDatabase database1 = this.getWritableDatabase();
        Cursor cursor = database1.rawQuery("SELECT * FROM " + table_name + " ", null);
        return cursor;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean staring(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor b = db.rawQuery("Select * from " + table_name + " where " + Col2 + "= ? ", new String[]{name}, null);
        if (b.getCount() != 0)
            return true;
        else
            return false;
    }



    public void deleteData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Checking column",Col2);

        db.delete(table_name,Col2+ " = '" + name + "'",null);

    }
}
