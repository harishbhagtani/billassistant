package com.android.example.billassistant.database.StoreData;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.example.billassistant.R;


public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.store_list_item_layout,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Declaring all the view in the parent view

        TextView textViewStoreName = view.findViewById(R.id.text_view_store_name);
        TextView textViewBillsLeft = view.findViewById(R.id.text_view_bills_left);
        TextView textViewTotalBills = view.findViewById(R.id.text_view_total_bills);
        TextView textViewAmountLeft = view.findViewById(R.id.text_view_amount_left);
        TextView textViewTotalAmount = view.findViewById(R.id.text_view_total_amount);

        //Variables to hold the values by the cursor

        String getStoreName = cursor.getString(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_NAME));
        int getBillsLeft = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT));
        int getTotalBills = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS));
        int getAmountLeft = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT));
        int getTotalAmount = cursor.getInt(cursor.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT));

        //Assigning values to the text Views

        textViewStoreName.setText(String.valueOf(getStoreName));
        textViewAmountLeft.setText("Payable Amount : Rs.  " + String.valueOf(getAmountLeft));
        textViewTotalAmount.setText("Total Amount : Rs. " + String.valueOf(getTotalAmount));
        textViewBillsLeft.setText(String.valueOf(getBillsLeft));
        textViewTotalBills.setText("Total Bills : " + String.valueOf(getTotalBills));
        if(getAmountLeft > 10000) {
            textViewAmountLeft.setTextColor(Color.RED);
        }else{
            textViewAmountLeft.setTextColor(Color.GRAY);
        }
    }
}
