package com.android.example.billassistant.database.BillData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BillDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "bill_database";
    public static final int DATABASE_VERSION = 1;

    public BillDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_BILL_TABLE =  "CREATE TABLE " + BillContract.BillEntry.TABLE_NAME + " ("
                + BillContract.BillEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BillContract.BillEntry.COLUMN_STORE_NAME + " TEXT NOT NULL DEFAULT \"UNIDENTIFIED SHOP\", "
                + BillContract.BillEntry.COLUMN_AMOUNT + " INTEGER, "
                + BillContract.BillEntry.COLUMN_UNPAID_AMOUNT + " INTEGER DEFAULT 0,"
                + BillContract.BillEntry.COLUMN_PAYMENT_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + BillContract.BillEntry.COLUMN_DATE_OF_BILL + " TEXT DEFAULT \"NOT PROVIDED\", "
                + BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT + " TEXT DEFAULT \"PAYMENT NOT MADE YET\", "
                + BillContract.BillEntry.COLUMN_BILL_ID + " TEXT DEFAULT \"ID NOT PROVIDED\");";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BILL_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQLString = " ALTER TABLE " + BillContract.BillEntry.TABLE_NAME + " ADD " + BillContract.BillEntry.COLUMN_UNPAID_AMOUNT + " INTEGER DEFAULT 0;";
        db.execSQL(SQLString);
    }
}
