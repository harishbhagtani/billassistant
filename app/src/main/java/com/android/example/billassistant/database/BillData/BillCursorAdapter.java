package com.android.example.billassistant.database.BillData;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.example.billassistant.R;

public class BillCursorAdapter extends CursorAdapter{

    public BillCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.bill_data_layout,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //Declaration of all the views in the parent view

        TextView textViewBillStoreName = view.findViewById(R.id.text_view_bill_store_name);
        TextView textViewBillID = view.findViewById(R.id.text_view_bill_id);
        TextView textViewBillDate = view.findViewById(R.id.text_view_bill_date);
        TextView textViewBillPayDate = view.findViewById(R.id.text_view_bill_pay_date);
        TextView textViewBillAmount = view.findViewById(R.id.text_view_bill_amount);

        //Declaration of the drawable that will change the colour on the basis of payment status


        //Variables to store Cursor Values

        String getStoreName = cursor.getString(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_STORE_NAME));
        String getBillID = cursor.getString(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_BILL_ID));
        String getBillDate = cursor.getString(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_DATE_OF_BILL));
        String getBillPayDate = cursor.getString(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT));
        int getBillPaymentStatus = cursor.getInt(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_PAYMENT_STATUS));
        int getBillAmount = cursor.getInt(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_AMOUNT));
        int getBillUnpaidAmount = cursor.getInt(cursor.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_UNPAID_AMOUNT));

        //Setting the Value of the text view to the values in database to fill the list view

        textViewBillStoreName.setText(String.valueOf(getStoreName));
        textViewBillID.setText("Bill ID : " + String.valueOf(getBillID));
        textViewBillDate.setText("Bill Date : " + String.valueOf(getBillDate));
        if(getBillUnpaidAmount == 0) {
            textViewBillAmount.setText("Rs. " + String.valueOf(getBillAmount));
        }else{
            textViewBillAmount.setText("Rs. " + String.valueOf(getBillUnpaidAmount));
        }

        //Changing the colour of amount text view on the basis of payment paid or not

        if(getBillPaymentStatus == 0){
            textViewBillPayDate.setText("Payment Date : Not Paid");
        }else{
            textViewBillPayDate.setText("Payment Date : " + String.valueOf(getBillPayDate));
        }
    }
}
