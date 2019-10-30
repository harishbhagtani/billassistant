package com.android.example.billassistant.database.BillData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BillContract {

    private BillContract() {
    }

    public static final class BillEntry implements BaseColumns {

        //Name of the database to keep bill record

        public static final String TABLE_NAME = "bill_details";
        public static final String CONTENT_AUTHORITY = "com.android.example.billassistant.database.BillData";
        private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        private static final String PATH_BILL_DETAILS = "bill_details";

        //Get Type

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILL_DETAILS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILL_DETAILS;

        //Content  URI

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI,PATH_BILL_DETAILS);

        //Unique ID Number for bill
        //Type: INTEGER

        public static final String _ID = BaseColumns._ID;

        //Column to keep name of the Bill provider
        //Type: TEXT

        public static final String COLUMN_STORE_NAME = "store_name";

        //Bill Amount to take
        //Type: INTEGER
        public static final String COLUMN_AMOUNT = "amount";

        //Bill Unpaid Amount
        //Tyope: INTEGER
        public static final String COLUMN_UNPAID_AMOUNT = "unpaid_amount";

        //Bill paid or not
        //Type: INTEGER of Type 0 or 1

        public static final String COLUMN_PAYMENT_STATUS = "payment";

        //Column to store date
        //Type String

        public static final String COLUMN_DATE_OF_BILL = "date_of_bill";

        //Column to store date of payment
        //Type String

        public static final String COLUMN_DATE_OF_PAYMENT = "date_of_payment";

        //Column to store bill id
        //Type Text

        public static final String COLUMN_BILL_ID = "bill_id";

        //possible values for bill payed

        public static final int BILL_PAYED = 1;
        public static final int BILL_NOT_PAYED = 0;
    }
}







