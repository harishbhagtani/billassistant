package com.android.example.billassistant.database.StoreData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static android.drm.DrmStore.DrmObjectType.CONTENT;

public class StoreContract {

    public StoreContract() {
    }

    public static final class StoreEntry implements BaseColumns {

        //Information about the table and the Content URI

        public static final String TABLE_NAME = "store_details";
        public static final String CONTENT_AUTHORITY = "com.android.example.billassistant.database.StoreData";
        private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        private static final String PATH_STORE_DETAILS = "store_details";

        //Get Type

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE_DETAILS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE_DETAILS;

        //Content  URI

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_STORE_DETAILS);

        //Declaration of name of all the columns of the table

        //Column to save ID of the table

        public static final String COLUMN_STORE_ID = BaseColumns._ID;

        //Column To Save Store Names
        //Type: TEXT
        //Category : NOT NULL

        public static final String COLUMN_STORE_NAME = "store_name";

        //Column To Save Store Number of Bills
        //Type: INTEGER
        //Category: NOT NULL, DEFAULT 0

        public static final String COLUMN_STORE_NUMBER_OF_BILLS_LEFT = "number_of_bills_left";

        //Column to store the total number of bills
        //Type: INTEGER
        //Category : NOT NULL, DEFAULT 0

        public static final String COLUMN_STORE_TOTAL_NUMBER_OF_BILLS = "total_bills";

        //Column to save the total amount left
        //Type: INTEGER
        //Category: NOT NULL, DEFAULT 0

        public static final String COLUMN_STORE_AMOUNT_LEFT = "amount__left";

        //Coulumn to store total amount of all the bills stored
        //Type : INTEGER
        //Category : NOT NULL, DEFAULT 0

        public static final String COLUMN_STORE_TOTAL_AMOUNT = "amount_total";


    }
}
