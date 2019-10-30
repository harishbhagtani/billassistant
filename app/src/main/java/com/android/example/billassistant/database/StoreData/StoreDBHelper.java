package com.android.example.billassistant.database.StoreData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.version;

public class StoreDBHelper extends SQLiteOpenHelper {

    //Constant for Database name
    public static final String DATABASE_NAME = "store_data";
    public static final int DATABASE_VERSION = 1;
    public StoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_DATABASE_TABLE = "CREATE TABLE " + StoreContract.StoreEntry.TABLE_NAME + " ("
                + StoreContract.StoreEntry.COLUMN_STORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StoreContract.StoreEntry.COLUMN_STORE_NAME + " TEXT NOT NULL DEFAULT \"No Name\","
                + StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS + " INTEGER DEFAULT 0,"
                + StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT + " INTEGER DEFAULT 0);";

        sqLiteDatabase.execSQL(CREATE_DATABASE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
