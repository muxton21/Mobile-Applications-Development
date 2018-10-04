package com.example.comg3.toomuchstuff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by comg3 on 12/05/2018.
 */

public class DatabaseSQLite extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Wardrobe.db";
    public static final String TABLE_NAME = "userWardrobe";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "BRAND";
    public static final String COL_4 = "SOURCE";
    public static final String COL_5 = "TYPE";
    public static final String COL_6 = "IMG";



    public DatabaseSQLite(final Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, BRAND TEXT, SOURCE TEXT, TYPE TEXT, IMG BLOB) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    //function to insert data in the db
    public boolean insertData (String name, String brand, String source, String type, byte[] img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, brand);
        contentValues.put(COL_4, source);
        contentValues.put(COL_5, type);
        contentValues.put(COL_6, img);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        }
        else{
            return true;
        }
    }
    public void queryData(String sql){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
    }

    //function to get data from db
    public Cursor getData(String sql) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    //gets the number of records in the db
    public int count() {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = " SELECT * FROM "+TABLE_NAME;
        int recordCount = db.rawQuery(sql, null).getCount();
        db.close();

        return recordCount;
    }
    // function to delete a record from the db
    public void deleteItem(String id){
        SQLiteDatabase db = getWritableDatabase();
        String sql = " DELETE FROM " + TABLE_NAME + " WHERE "+COL_1+" = '"+id +"' ;";

        db.execSQL(sql);
        db.close();
    }

    public Cursor getItemId(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = " SELECT "+COL_1+" FROM "+ TABLE_NAME+ " WHERE "+COL_2+" = '"+name+"' ; ";
        Cursor data = db.rawQuery(sql, null);
        return data;
    }

    //function to update the values in db records
    public void updateItem( String id, String newName, String newBrand, String newSource, String newType){
        SQLiteDatabase db = this.getWritableDatabase();
        printLog(newName);
        String sql = " UPDATE " + TABLE_NAME + " SET "+ COL_2+" = '"+newName+ "' , "+ COL_3+" = '"+newBrand+ "' , "+ COL_4+" = '"+newSource+ "' , "+ COL_5+" = '"+newType+ "' WHERE "+COL_1+ " = '"+id+"';";
        db.execSQL(sql);
        db.close();
    }



    public void printLog(String text){
        Log.d(TAG, text);
    }


}
