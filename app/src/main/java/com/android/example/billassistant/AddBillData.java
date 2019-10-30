package com.android.example.billassistant;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.billassistant.database.BillData.BillContract;
import com.android.example.billassistant.database.BillData.BillDBHelper;
import com.android.example.billassistant.database.StoreData.StoreContract;
import com.android.example.billassistant.database.StoreData.StoreDBHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import static android.R.attr.bitmap;
import static android.R.attr.id;

public class AddBillData extends AppCompatActivity {

    //Constant for Camera Request
    private static final int CAMERA_REQUEST = 1888;

    //Declaring all the Views in the Activity
    EditText editTextStoreName;
    EditText editTextBillAmount;
    DatePicker datePickerBillDate;
    DatePicker datePickerPaymentDate;
    RadioGroup radioGroupPaymentStatus;
    RadioButton radioButtonPaymentPaid;
    RadioButton radioButoonPaymentUnpaid;
    LinearLayout linearLayoutPaymentDatePicker;

    //Buttons
    Button buttonAddImage;
    TextView textViewImageString;

    //Declaration of the Variables to hold the bill data

    String getBillDate;
    String getBillImageString;
    int getBillAmount;
    String getBillDateOfPayment;
    boolean getPaymentStatus;
    boolean isImageAdded;

    //Declaration of Variable to hold the store data

    String getStoreName;
    int getStoreTotalAmount;
    int getStorePayableAmount;
    int getStoreTotalBills;
    int getStorePayableBills;
    long getStoreRowID;

    //Uri of the row that was clicked
    Uri storeURI;

    //Database objects for Store Database
    SQLiteDatabase storeReadableDatabase;
    SQLiteDatabase storeWriteableDatabase;
    SQLiteOpenHelper storeDBHelper;

    //Database objects for Bill Database
    SQLiteDatabase billReadableDatabase;
    SQLiteDatabase billWriteAbleDatabase;
    SQLiteOpenHelper billDBHelper;

    //Cursor To Store Database of the Store
    Cursor storeInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.string_add_bill));
        setContentView(R.layout.activity_add_bill_data);

        //Initializing the store database obejcts
        storeDBHelper = new StoreDBHelper(this);
        storeReadableDatabase = storeDBHelper.getReadableDatabase();
        storeWriteableDatabase = storeDBHelper.getWritableDatabase();

        //Initializing the bill database objects
        billDBHelper = new BillDBHelper(this);
        billReadableDatabase = billDBHelper.getReadableDatabase();
        billWriteAbleDatabase = billDBHelper.getWritableDatabase();

        //Linking The Declared views to the actual views in the activity
        editTextStoreName = (EditText) findViewById(R.id.edit_text_store_name);
        editTextBillAmount = (EditText) findViewById(R.id.edit_text_bill_amount);
        datePickerBillDate = (DatePicker) findViewById(R.id.date_picker_bill_date);
        datePickerPaymentDate = (DatePicker) findViewById(R.id.date_picker_payment_date);
        radioGroupPaymentStatus = (RadioGroup) findViewById(R.id.radio_group_payment_status);
        radioButtonPaymentPaid = (RadioButton) findViewById(R.id.radio_button_payment_paid);
        radioButoonPaymentUnpaid = (RadioButton) findViewById(R.id.radio_button_payment_unpaid);
        linearLayoutPaymentDatePicker = (LinearLayout) findViewById(R.id.linear_layout_date_picker);

        //Setting unpaid as the default selection of the radio group
        radioButoonPaymentUnpaid.setChecked(true);

        //Getting store Data
        getStoreData();

        //Updating the editText to show the name of store
        if (getStoreName != null) {
            editTextStoreName.setText(getStoreName);
        }

        //Initializing the variables
        getBillDate = "Date Not Provided";
        getBillAmount = 0;
        getPaymentStatus = false;
        isImageAdded = false;

        //Setting amount text view on focus
        editTextBillAmount.requestFocus();
    }


    public void getStoreData() {

        //Retrieving the URI of the store that was clicked
        if (getIntent().hasExtra("clickedItemRowId")) {
            getStoreRowID = getIntent().getLongExtra("clickedItemRowId", 0);
            storeURI = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, getStoreRowID);
        } else {
            Toast.makeText(this, "Problem Loading Row ID", Toast.LENGTH_SHORT).show();
        }

        //Loading the store name from the database from the database
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_STORE_NAME,
                StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT,
                StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT,
                StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT,
                StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS};

        storeInfo = getContentResolver().query(storeURI, projection, null, null, null);

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

    public boolean assignValuesToVariables() {

        //Checking if the form is correct or not
        //Condition True : Values will be assigned
        //Condition False : Values will not be assigned

        if (checkForm()) {

            //Assigning date to the date variable from date picker

            String day = String.valueOf(datePickerBillDate.getDayOfMonth());
            String month = String.valueOf(datePickerBillDate.getMonth() + 1);
            String year = String.valueOf(datePickerBillDate.getYear());
            String date = day + "/" + month + "/" + year;

            //Assigning Values
            //Assigning To : getStoreName, getAmount, getBillDate, getPaymentStatus;

            getStoreName = editTextStoreName.getText().toString();
            getBillAmount = Integer.valueOf(editTextBillAmount.getText().toString());
            getBillDate = date;

            switch (radioGroupPaymentStatus.getCheckedRadioButtonId()) {
                case R.id.radio_button_payment_paid:
                    getPaymentStatus = true;
                    break;
                case R.id.radio_button_payment_unpaid:
                    getPaymentStatus = false;
                    break;
                default:
                    getPaymentStatus = false;
            }
            return true;
        } else {
            return false;
        }
    }

    public void updateBillDatabase() {

        //Generating the BILL ID
        String idStoreName = getStoreName.toLowerCase().replace(" ", "") + (getStoreTotalBills + 1);

        //Declaring the Content Values Variable
        ContentValues newBillData = new ContentValues();

        //Putting The common data to the database
        newBillData.put(BillContract.BillEntry.COLUMN_STORE_NAME, getStoreName);
        newBillData.put(BillContract.BillEntry.COLUMN_AMOUNT, getBillAmount);
        newBillData.put(BillContract.BillEntry.COLUMN_UNPAID_AMOUNT,getBillAmount);
        newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_BILL, getBillDate);
        newBillData.put(BillContract.BillEntry.COLUMN_BILL_ID, idStoreName);

        //Putting Payemnt Date if the bill is paid
        if (getPaymentStatus) {
            String day = String.valueOf(datePickerPaymentDate.getDayOfMonth());
            String month = String.valueOf(datePickerPaymentDate.getMonth() + 1);
            String year = String.valueOf(datePickerPaymentDate.getYear());
            getBillDateOfPayment = day + "/" + month + "/" + year;
            newBillData.put(BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT, getBillDateOfPayment);

            //Putting payment status as paid
            newBillData.put(BillContract.BillEntry.COLUMN_PAYMENT_STATUS, 1);
        } else {
            newBillData.put(BillContract.BillEntry.COLUMN_PAYMENT_STATUS, 0);
        }

        //Inserting the values to the table
        getContentResolver().insert(BillContract.BillEntry.CONTENT_URI, newBillData);
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

        if (getPaymentStatus) {
            setStorePayableAmount = getStorePayableAmount;
        } else {
            setStorePayableAmount = getStorePayableAmount + getBillAmount;
        }

        setStoreTotalAmount = getStoreTotalAmount + getBillAmount;

        if (getPaymentStatus) {
            setStorePayableBills = getStorePayableBills;
        } else {
            setStorePayableBills = getStorePayableBills + 1;
        }

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
        getContentResolver().update(storeURI, newStoreValues, null, null);
    }

    public void addToDatabase() {

        if (assignValuesToVariables()) {
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            //Saving bill to the bill database
            updateBillDatabase();

            //Updating store data to the Store Database
            updateStoreData();
        }
    }

    public boolean checkForm() {

        // Declaration Of Variables to check if The Fields of Edit texts  are correct or not

        String checkName;
        String checkAmountStatus;
        int checkAmount;

        //Declaration of tracking Variable
        //Initial stage 0
        //Incremented to 1 if one field is correct
        //Incremented to 2 if all the fields are correct

        int correctFields = 0;

        //Assigning Values to the Variables

        checkName = editTextStoreName.getText().toString();
        checkAmountStatus = editTextBillAmount.getText().toString();
        checkAmount = Integer.valueOf(editTextBillAmount.getText().toString());

        //Checling if the Store Name field is true or not
        //Condition 1 : The Name Field Should not be empty
        //Result if Condition True : The correct field variable will be incremented to 1
        //Result if Condition False : Text Will Turn Red and A toast will appear

        if (checkName.isEmpty()) {
            editTextStoreName.setHintTextColor(Color.RED);
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
        } else {
            correctFields++;
        }

        //Checking if the Bill Amount field is correct or not
        //Condition 1 : The Amount field should not be empty
        //Condition 2 : The Amount should not be equal to zero and the amount should not be less
        //              than zero.
        //Result if condition True : Correct field variable will be incremented to 2.
        //Result if Condition false : Text will turn red and a toast will appear with instructions.

        if (checkAmountStatus.isEmpty()) {
            editTextBillAmount.setHintTextColor(Color.RED);
            Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
        } else {
            if (checkAmount == 0 || checkAmount < 0) {
                editTextBillAmount.setTextColor(Color.RED);
                Toast.makeText(this, "Not A Valid Amount", Toast.LENGTH_SHORT).show();
            } else {
                correctFields++;
            }
        }

        //Returning the result
        //Case 1 : correct field == 2. That means all fields are correct and return true
        //Case 2 : correct field != 2/ That means all fields are not correct and return false

        if (correctFields == 2) {
            return true;
        } else {
            return false;
        }
    }

    public void onPaymentPaid(View view) {
        linearLayoutPaymentDatePicker.setVisibility(View.VISIBLE);
        getPaymentStatus = true;
    }

    public void onPaymentUnpaid(View view) {
        linearLayoutPaymentDatePicker.setVisibility(View.GONE);
        getPaymentStatus = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Do you want to save the form data before Closing?")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addToDatabase();
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("DON\'T SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelable(true);
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Do you want to save the form Data before closing?")
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addToDatabase();
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("DON\'T SAVE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_bill_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            addToDatabase();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
