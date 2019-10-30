package com.android.example.billassistant.database.BillData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.R.attr.id;

public class BillProvider extends ContentProvider {

    SQLiteDatabase billDB;
    BillDBHelper billDBObject;
    SQLiteDatabase billDBWrite;

    public static final int BILL = 100;
    public static final int BILL_QUERY = 101;

    UriMatcher sUriMatcher;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        billDBObject = new BillDBHelper(getContext());
        billDB = billDBObject.getReadableDatabase();
        billDBWrite = billDBObject.getWritableDatabase();
        sURIMatcher.addURI(BillContract.BillEntry.CONTENT_AUTHORITY, "/bill_details", BILL);
        sURIMatcher.addURI(BillContract.BillEntry.CONTENT_AUTHORITY, "/bill_details/#", BILL_QUERY);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] stringArgs, @Nullable String sortOrder) {

        Cursor cursor;
        int match = sURIMatcher.match(uri);
        switch (match) {
            case BILL: // Perform database query here.
                cursor = billDB.query(BillContract.BillEntry.TABLE_NAME, projection, selection, stringArgs, null, null, sortOrder);
                break;
            case BILL_QUERY:
                selection = BillContract.BillEntry._ID + "=?";
                stringArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = billDB.query(BillContract.BillEntry.TABLE_NAME, projection, selection, stringArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Query Unknown URI : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final  int match = sURIMatcher.match(uri);
        switch (match){
            case BILL:
                return BillContract.BillEntry.CONTENT_LIST_TYPE;
            case BILL_QUERY:
                return BillContract.BillEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalStateException("Unknown URI : " + uri + "With Match.");
        }
    }

    private Uri insertPet(Uri uri, ContentValues values){
        billDB.insert(BillContract.BillEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sURIMatcher.match(uri);
        switch (match){
            case BILL:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for : " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] stringArgs) {
        final  int match = sURIMatcher.match(uri);
        switch (match){
            case BILL:
                int rowsDeleted = billDBWrite.delete(BillContract.BillEntry.TABLE_NAME,selection,stringArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsDeleted;
            case BILL_QUERY:
                selection = BillContract.BillEntry._ID + "=?";
                stringArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = billDB.delete(BillContract.BillEntry.TABLE_NAME,selection,stringArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsDeleted;
            default: throw new IllegalArgumentException("Cannot Delete : " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] stringArgs) {

        final  int match = sURIMatcher.match(uri);
        switch (match){
            case BILL:
                return billUpdate(uri,contentValues,selection,stringArgs);
            case BILL_QUERY:
                selection = BillContract.BillEntry._ID + "=?";
                stringArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return billUpdate(uri,contentValues,selection,stringArgs);
            default : throw new IllegalArgumentException("Updation is not valid for : " + uri);
        }
    }

    public int billUpdate(Uri uri, ContentValues values, String selection, String[] stringArgs){
        if(values.size() == 0){
            return 0;
        }
        int rowUpdated = billDBWrite.update(BillContract.BillEntry.TABLE_NAME,values,selection,stringArgs);
        if(rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }
}
