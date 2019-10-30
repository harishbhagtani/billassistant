package com.android.example.billassistant;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.billassistant.database.BillData.BillContract;
import com.android.example.billassistant.database.StoreData.StoreContract;

import java.util.Calendar;

import static android.R.attr.id;

public class BillInfoActivity extends AppCompatActivity {

    //Defining all the UI elements

    TextView textViewBillStoreName;
    TextView textViewBillAmount;
    TextView textViewBillDate;
    TextView textViewBillID;
    TextView textViewBillPaymentStatus;
    TextView textViewBillPaymentDate;
    Button buttonMarkAsPaid;
    Button buttonPartiallyPaid;
    Button buttonDelete;
    Button buttonSavePartialAmount;
    EditText editTextGetPartialAmount;
    LinearLayout linearLayoutPaymentOptions;
    RelativeLayout relativeLayoutPartialPayment;

    //Variables to store the data after loading

    String getBillStoreName;
    String getBillDate;
    String getBillID;
    String getBillPaymentDate;
    int getBillAmount;
    int getBillPaymentStatus;
    long getBillNumber;

    //Declaration of Variable to hold the store data

    String getStoreName;
    int getStoreTotalAmount;
    int getStorePayableAmount;
    int getStoreTotalBills;
    int getStorePayableBills;
    long getStoreRowID;

    //Bill Uri Variable
    Uri getBillUri;

    //Store URI Variable
    Uri getStoreUri;

    //Cursor to hold the bill data
    Cursor cursorBillData;

    //Cursor to store the store data
    Cursor storeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info);
        getSupportActionBar().setTitle("Bill Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Assigning views to the variuables
        textViewBillStoreName = (TextView) findViewById(R.id.text_view_bill_data_store_name);
        textViewBillAmount = (TextView) findViewById(R.id.text_view_bill_data_amount);
        textViewBillID = (TextView) findViewById(R.id.text_view_bill_data_bill_id);
        textViewBillDate = (TextView) findViewById(R.id.text_view_bill_data_bill_date);
        textViewBillPaymentDate = (TextView) findViewById(R.id.text_view_bill_data_payment_date);
        textViewBillPaymentStatus = (TextView) findViewById(R.id.text_view_bill_data_payment_status);
        buttonDelete = (Button) findViewById(R.id.button_bill_data_delete);
        buttonMarkAsPaid = (Button) findViewById(R.id.button_bill_data_marks_as_paid);
        buttonPartiallyPaid = (Button) findViewById(R.id.button_bill_data_partially_paid);
        buttonSavePartialAmount = (Button) findViewById(R.id.button_partial_amount);
        editTextGetPartialAmount = (EditText) findViewById(R.id.edit_text_partial_amount);
        linearLayoutPaymentOptions = (LinearLayout) findViewById(R.id.linear_layout_bill_data_payment_options);
        relativeLayoutPartialPayment = (RelativeLayout) findViewById(R.id.relative_layout_partial_payment);

        //Intent to retrieve the bill number
        getBillNumber = getIntent().getLongExtra("getBillID", 0);

        //Intent to retrieve the rowID of the calling store
        getStoreRowID = getIntent().getLongExtra("getStoreID", 0);

        //Generating Uri from the Given BIll ID Number
        getBillUri = ContentUris.withAppendedId(BillContract.BillEntry.CONTENT_URI, getBillNumber);

        //Generating URI from the given store row ID
        getStoreUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, getStoreRowID);

        //Calling method to assign the data values to the Variables
        getBillData();

        //Assigning Variable Values to the views
        assignViews();

        //Getting The Store Data
        getStoreData();

        //Setting onCLickListeners
        setOnButtonClickListeners();

        //Hiding payment options view if payment is already done
        if (getBillPaymentStatus == 1) {
            linearLayoutPaymentOptions.setVisibility(View.INVISIBLE);
        }
    }

    public void getBillData() {
        String[] projection = {
                BillContract.BillEntry._ID,
                BillContract.BillEntry.COLUMN_STORE_NAME,
                BillContract.BillEntry.COLUMN_BILL_ID,
                BillContract.BillEntry.COLUMN_AMOUNT,
                BillContract.BillEntry.COLUMN_PAYMENT_STATUS,
                BillContract.BillEntry.COLUMN_DATE_OF_BILL,
                BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT};
        cursorBillData = getContentResolver().query(getBillUri, projection, null, null, null);

        //Assigning cursor gained data to the Varaibles
        if (cursorBillData.moveToFirst()) {
            getBillStoreName = cursorBillData.getString(cursorBillData.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_STORE_NAME));
            getBillAmount = cursorBillData.getInt(cursorBillData.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_AMOUNT));
            getBillID = cursorBillData.getString(cursorBillData.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_BILL_ID));
            getBillDate = cursorBillData.getString(cursorBillData.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_DATE_OF_BILL));
            getBillPaymentStatus = cursorBillData.getInt(cursorBillData.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_PAYMENT_STATUS));
            getBillPaymentDate = cursorBillData.getString(cursorBillData.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT));
        }
        cursorBillData.close();
    }

    public void updateBillDatabase() {

        //Declaring the Content Values Variable
        ContentValues newBillData = new ContentValues();

        //Putting The common data to the database
        newBillData.put(BillContract.BillEntry.COLUMN_STORE_NAME, getBillStoreName);
        newBillData.put(BillContract.BillEntry.COLUMN_AMOUNT, getBillAmount);
        newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_BILL, getBillDate);
        newBillData.put(BillContract.BillEntry.COLUMN_BILL_ID, getBillID);

        //Putting Payemnt Date if the bill is paid
        if (getBillPaymentStatus == 1) {
            int paymentDate = Calendar.getInstance().get(Calendar.DATE);
            int paymentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int paymentYear = Calendar.getInstance().get(Calendar.YEAR);
            String newDateStr = String.valueOf(paymentDate) + "/" + String.valueOf(paymentMonth) + "/" + String.valueOf(paymentYear);
            newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT, newDateStr);

            //Putting payment status as paid
            newBillData.put(BillContract.BillEntry.COLUMN_PAYMENT_STATUS, 1);
        }

        //Inserting the values to the table
        getContentResolver().update(getBillUri, newBillData, null, null);
    }

    public void getStoreData() {

        //Loading the store name from the database from the database
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_STORE_NAME,
                StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT,
                StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT,
                StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT,
                StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS};

        storeInfo = getContentResolver().query(getStoreUri, projection, null, null, null);

        //Initializing the variables to hold the store data
        if (storeInfo.moveToFirst()) {
            getStoreName = storeInfo.getString(storeInfo.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_NAME));
            getStorePayableAmount = storeInfo.getInt(storeInfo.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT));
            getStoreTotalAmount = storeInfo.getInt(storeInfo.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT));
            getStorePayableBills = storeInfo.getInt(storeInfo.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT));
            getStoreTotalBills = storeInfo.getInt(storeInfo.getColumnIndexOrThrow(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS));
        }

        //Closing the cursor to avoid memory leaks
        storeInfo.close();
    }

    public void updateStoreData() {
        //Variables to hold the new store values
        String setStoreName;
        int setStorePayableAmount;
        int setStoreTotalAmount;
        int setStorePayableBills;
        int setStoreTotalBills;

        //Setting new values to the variables
        setStoreName = getStoreName;

        if (getBillPaymentStatus == 1) {
            setStorePayableAmount = getStorePayableAmount;
        } else {
            setStorePayableAmount = getStorePayableAmount - getBillAmount;
        }

        setStoreTotalAmount = getStoreTotalAmount - getBillAmount;

        if (getBillPaymentStatus == 1) {
            setStorePayableBills = getStorePayableBills;
        } else {
            setStorePayableBills = getStorePayableBills - 1;
        }

        setStoreTotalBills = getStoreTotalBills - 1;

        //Declaration of ContentValues
        ContentValues newStoreValues = new ContentValues();

        //putting all the new values to the content values
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_NAME, setStoreName);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT, setStorePayableAmount);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT, setStoreTotalAmount);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT, setStorePayableBills);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS, setStoreTotalBills);

        //Updating the database
        getContentResolver().update(getStoreUri, newStoreValues, null, null);
    }

    public boolean updateBillDatabasePartialPaid() {

        //Generating the BILL ID
        String idStoreName = getStoreName.toLowerCase().replace(" ", "") + (getStoreTotalBills + 1);

        //Declaring the Content Values Variable
        ContentValues newBillData = new ContentValues();

        int newAmount = getBillAmount - Integer.valueOf(editTextGetPartialAmount.getText().toString());

        if (Integer.valueOf(editTextGetPartialAmount.getText().toString()) >= getBillAmount || editTextGetPartialAmount.getText().toString().isEmpty()) {
            return false;
        } else {

            //Putting The common data to the database
            newBillData.put(BillContract.BillEntry.COLUMN_STORE_NAME, getStoreName);
            newBillData.put(BillContract.BillEntry.COLUMN_AMOUNT, newAmount);
            newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_BILL, getBillDate);
            newBillData.put(BillContract.BillEntry.COLUMN_BILL_ID, idStoreName);
            newBillData.put(BillContract.BillEntry.COLUMN_PAYMENT_STATUS, 0);

            //Inserting the values to the table
            getContentResolver().insert(BillContract.BillEntry.CONTENT_URI, newBillData);
            return true;
        }
    }

    public void updateStoreDataForPaid() {
        //Variables to hold the new store values
        String setStoreName;
        int setStorePayableAmount;
        int setStoreTotalAmount;
        int setStorePayableBills;
        int setStoreTotalBills;

        //Setting new values to the variables
        setStoreName = getStoreName;


        setStorePayableAmount = getStorePayableAmount - getBillAmount;
        setStoreTotalAmount = getStoreTotalAmount;
        setStorePayableBills = getStorePayableBills - 1;
        setStoreTotalBills = getStoreTotalBills;

        //Declaration of ContentValues
        ContentValues newStoreValues = new ContentValues();

        //putting all the new values to the content values
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_NAME, setStoreName);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT, setStorePayableAmount);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT, setStoreTotalAmount);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT, setStorePayableBills);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS, setStoreTotalBills);

        //Updating the database
        getContentResolver().update(getStoreUri, newStoreValues, null, null);
    }

    public void updateOldBillForPartialPayment() {
        //Declaring the Content Values Variable
        ContentValues newBillData = new ContentValues();

        int newAmount = Integer.valueOf(editTextGetPartialAmount.getText().toString());

        //Putting The common data to the database
        newBillData.put(BillContract.BillEntry.COLUMN_STORE_NAME, getBillStoreName);
        newBillData.put(BillContract.BillEntry.COLUMN_AMOUNT, newAmount);
        newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_BILL, getBillDate);
        newBillData.put(BillContract.BillEntry.COLUMN_BILL_ID, getBillID);

        int paymentDate = Calendar.getInstance().get(Calendar.DATE);
        int paymentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int paymentYear = Calendar.getInstance().get(Calendar.YEAR);
        String newDateStr = String.valueOf(paymentDate) + "/" + String.valueOf(paymentMonth) + "/" + String.valueOf(paymentYear);
        newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT, newDateStr);

        //Putting payment status as paid
        newBillData.put(BillContract.BillEntry.COLUMN_PAYMENT_STATUS, 1);


        //Inserting the values to the table
        getContentResolver().update(getBillUri, newBillData, null, null);
    }

    public void updateStoreDataForPartialPaymenet() {
        //Variables to hold the new store values
        String setStoreName;
        int setStorePayableAmount;
        int setStoreTotalAmount;
        int setStorePayableBills;
        int setStoreTotalBills;

        //Setting new values to the variables
        setStoreName = getStoreName;


        setStorePayableAmount = getStorePayableAmount - Integer.valueOf(editTextGetPartialAmount.getText().toString());
        setStoreTotalAmount = getStoreTotalAmount;
        setStorePayableBills = getStorePayableBills;
        setStoreTotalBills = getStoreTotalBills + 1;

        //Declaration of ContentValues
        ContentValues newStoreValues = new ContentValues();

        //putting all the new values to the content values
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_NAME, setStoreName);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT, setStorePayableAmount);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT, setStoreTotalAmount);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT, setStorePayableBills);
        newStoreValues.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS, setStoreTotalBills);

        //Updating the database
        getContentResolver().update(getStoreUri, newStoreValues, null, null);
    }

    public void assignViews() {

        //Assigning Variable data to views
        textViewBillStoreName.setText(String.valueOf(getBillStoreName));
        textViewBillAmount.setText(": Rs." + String.valueOf(getBillAmount));
        textViewBillDate.setText(": " + String.valueOf(getBillDate));
        textViewBillID.setText(": " + String.valueOf(getBillID));
        if (getBillPaymentStatus == 0) {
            textViewBillPaymentStatus.setText(": Unpaid");
            textViewBillPaymentDate.setText(": Unpaid");
        } else {
            textViewBillPaymentStatus.setText(": Paid");
            textViewBillPaymentDate.setText(": " + String.valueOf(getBillPaymentDate));
        }
    }

    public void saveAsPartiallyPaid() {
        if (updateBillDatabasePartialPaid()) {
            updateOldBillForPartialPayment();
            updateStoreDataForPartialPaymenet();
            //Hiding the Android Soft Keyboard
            View s = BillInfoActivity.this.getCurrentFocus();
            if (s != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(s.getWindowToken(), 0);
            }
            finish();
        } else {
            Toast.makeText(this, "Please enter Valid Amount", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public void setOnButtonClickListeners() {
        //setting onClickListener for Delete button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BillInfoActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Do you really want to delete bill from " + getStoreName + " Dated " + getBillDate + " for the amount of " + getBillAmount + " ?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getContentResolver().delete(getBillUri, null, null);
                                Toast.makeText(BillInfoActivity.this, "Bill Deleted", Toast.LENGTH_SHORT).show();
                                updateStoreData();
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });
        buttonMarkAsPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBillPaymentStatus = 1;
                updateBillDatabase();
                Toast.makeText(BillInfoActivity.this, "Marked As Paid", Toast.LENGTH_SHORT).show();
                updateStoreDataForPaid();
                finish();
            }
        });
        buttonPartiallyPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutPaymentOptions.setVisibility(View.INVISIBLE);
                relativeLayoutPartialPayment.setVisibility(View.VISIBLE);
                buttonSavePartialAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveAsPartiallyPaid();
                    }
                });
            }
        });
    }
}
