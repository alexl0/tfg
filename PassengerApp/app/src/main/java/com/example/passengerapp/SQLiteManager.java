package com.example.passengerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class was created following the next tutorial:
 * https://www.youtube.com/watch?v=4k1ZMpO9Zn0
 */
public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "PassengerAppDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "History";
    private static final String COUNTER = "Counter";

    private static final String DATA_FIELD = "data"; //this stores, for example: "Autobus L5 5 zonas 12/02/2022 20:32"

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        if(sqLiteManager ==null)
            sqLiteManager = new SQLiteManager(context);
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(DATA_FIELD)
                .append(" TEXT)");
        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addDataFieldToDB(String dataField){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATA_FIELD, dataField);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void populateHistoryListArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        try(Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null)){
            if(result.getCount()!=0){
                while(result.moveToNext()){
                    String data = result.getString(1);
                    SingletonClass.get().getHistory().add(data);
                }
            }
        }
    }



}
