package com.android.example.billassistant.database.StoreData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.R.attr.id;

public class StoreProvider extends ContentProvider {

    //Declaring the variables for database and database helpers

    SQLiteDatabase storeDBRead;
    SQLiteDatabase storeDBWrite;
    SQLiteOpenHelper storeDBHelper;

    //Declaring the COnstant for URI MATCHER

    public static final int STORE = 100;
    public static final int STORE_QUERY = 101;

    //Declaration of URI Matcher

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {

        //Connection databases with the SQLite Database helper
        //Helper user : StoreDBHelper

        storeDBHelper = new StoreDBHelper(getContext());
        storeDBRead = storeDBHelper.getReadableDatabase();
        storeDBWrite = storeDBHelper.getWritableDatabase();

        //Adding Uri to the URI matcher

        sURIMatcher.addURI(StoreContract.StoreEntry.CONTENT_AUTHORITY, "/store_details", STORE);
        sURIMatcher.addURI(StoreContract.StoreEntry.CONTENT_AUTHORITY, "/store_details/#", STORE_QUERY);

        //The return statement

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        int match = sURIMatcher.match(uri);
        switch (match){
            case STORE: // Perform database query here.
                cursor = storeDBRead.query(StoreContract.StoreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case STORE_QUERY:
                selection = StoreContract.StoreEntry._ID+ "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = storeDBRead.query(StoreContract.StoreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:throw new IllegalArgumentException("Cannot Query Unknown URI : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final  int match = sURIMatcher.match(uri);
        switch (match){
            case STORE:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case STORE_QUERY:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalStateException("Unknown URI : " + uri + "With Match.");
        }
    }

    private Uri insertStore(Uri uri, ContentValues values){
        storeDBRead.insert(StoreContract.StoreEntry.TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sURIMatcher.match(uri);
        switch (match){
            case STORE:
                return insertStore(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for : " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final  int match = sURIMatcher.match(uri);
        switch (match){
            case STORE:
                int rowsDeleted = storeDBWrite.delete(StoreContract.StoreEntry.TABLE_NAME,selection,selectionArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsDeleted;
            case STORE_QUERY:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = storeDBWrite.delete(StoreContract.StoreEntry.TABLE_NAME,selection,selectionArgs);
                if(rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowsDeleted;
            default: throw new IllegalArgumentException("Cannot Delete : " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final  int match = sURIMatcher.match(uri);
        switch (match){
            case STORE:
                return storeUpdate(uri,contentValues,selection,selectionArgs);
            case STORE_QUERY:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return storeUpdate(uri,contentValues,selection,selectionArgs);
            default : throw new IllegalArgumentException("Updation is not valid for : " + uri);
        }
    }

    public int storeUpdate(Uri uri, ContentValues values, String selection, String[] stringArgs){
        if(values.size() == 0){
            return 0;
        }
        int rowUpdated = storeDBWrite.update(StoreContract.StoreEntry.TABLE_NAME,values,selection,stringArgs);
        if(rowUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowUpdated;
    }
}
