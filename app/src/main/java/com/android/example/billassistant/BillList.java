package com.android.example.billassistant;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.example.billassistant.database.BillData.BillContract;
import com.android.example.billassistant.database.BillData.BillCursorAdapter;
import com.android.example.billassistant.database.StoreData.StoreContract;

import java.util.Calendar;

import static com.android.example.billassistant.R.id.fab;

public class BillList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Declaration of Variable to hold the store data

    String getStoreName;
    int getStoreTotalAmount;
    int getStorePayableAmount;
    int getStoreTotalBills;
    int getStorePayableBills;
    long getStoreRowID;

    //Variable to count the number of bills
    int billCount;

    //Uri of the row that was clicked
    Uri storeURI;

    //Cursor To Store Database of the Store
    Cursor storeInfo;

    //Cursor To store all unpaid Bills for adding account payments
    Cursor cursorUnpaidBills;

    //Declaring The List View
    ListView listViewBill;
    RelativeLayout emptyList;
    RelativeLayout relativeLayoutAddAccountPayment;
    EditText editTextAddAccountPayment;
    Button buttonAddAccountPayment;
    LinearLayout linearLayoutBillList;

    //Declaration of Adapter Instance
    BillCursorAdapter mBillAdapter;

    //Variable to check whether add acount is activated or not
    boolean isAddPaymentActivated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        getStoreData();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openNewActivity = new Intent(BillList.this, AddBillData.class);
                openNewActivity.putExtra("clickedItemRowId", getStoreRowID);
                startActivity(openNewActivity);
            }
        });

        //Initializing Views
        listViewBill = (ListView) findViewById(R.id.list_view_bill);
        emptyList = (RelativeLayout) findViewById(R.id.relative_layout_empty_list_bill);
        relativeLayoutAddAccountPayment = (RelativeLayout) findViewById(R.id.relative_layout_add_account_payment);
        editTextAddAccountPayment = (EditText) findViewById(R.id.edit_text_add_account_payment);
        buttonAddAccountPayment = (Button) findViewById(R.id.button_add_account_payment);
        linearLayoutBillList = (LinearLayout)findViewById(R.id.linear_layout_bill_list);
        mBillAdapter = new BillCursorAdapter(this, null);
        listViewBill.setAdapter(mBillAdapter);
        getLoaderManager().initLoader(1, null, this);

        //Setting onListItemClickListener on the Bill data list view
        listViewBill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent startNewActivity = new Intent(BillList.this, BillInfoActivity.class);
                startNewActivity.putExtra("getBillID", l);
                startNewActivity.putExtra("getStoreID", getStoreRowID);
                startActivity(startNewActivity);
            }
        });

        //Setting onCLickListener to Account Payment Button
        buttonAddAccountPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextAddAccountPayment.getText().toString().isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    relativeLayoutAddAccountPayment.setVisibility(View.GONE);
                    isAddPaymentActivated = false;
                    addAcoountPayment();
                } else {
                    Toast.makeText(BillList.this, "Amount Cannot Be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        billCount = listViewBill.getAdapter().getCount();
        isAddPaymentActivated = false;
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

    public void addAcoountPayment() {
        getLoaderManager().initLoader(120, null, this);
    }

    public void updateAccountInfoAfterPayment(){
        //Getting The Amount from EditText

        int paymentAmount = Integer.valueOf(editTextAddAccountPayment.getText().toString());

        //Variables to store current unpaid bill info
        int unpaidBillID;
        int unpaidBillAmount;
        Uri unpaidBillUri;


        if (cursorUnpaidBills.moveToFirst()) {
            do {
                unpaidBillID = cursorUnpaidBills.getInt(cursorUnpaidBills.getColumnIndexOrThrow(BillContract.BillEntry._ID));
                unpaidBillUri = ContentUris.withAppendedId(BillContract.BillEntry.CONTENT_URI, unpaidBillID);
                unpaidBillAmount = cursorUnpaidBills.getInt(cursorUnpaidBills.getColumnIndexOrThrow(BillContract.BillEntry.COLUMN_AMOUNT));

                int paymentDate = Calendar.getInstance().get(Calendar.DATE);
                int paymentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
                int paymentYear = Calendar.getInstance().get(Calendar.YEAR);
                String newDateStr = String.valueOf(paymentDate) + "/" + String.valueOf(paymentMonth) + "/" + String.valueOf(paymentYear);

                if (unpaidBillAmount < paymentAmount) {
                    ContentValues newValues = new ContentValues();
                    newValues.put(BillContract.BillEntry.COLUMN_PAYMENT_STATUS, 1);
                    newValues.put(BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT,newDateStr);
                    getContentResolver().update(unpaidBillUri, newValues, null, null);
                    paymentAmount = paymentAmount - unpaidBillAmount;
                    updateStoreDataForPaid(paymentAmount, true);
                } else {
                    unpaidBillAmount = unpaidBillAmount - paymentAmount;
                    ContentValues newValues = new ContentValues();
                    newValues.put(BillContract.BillEntry.COLUMN_AMOUNT, unpaidBillAmount);
                    getContentResolver().update(unpaidBillUri, newValues, null, null);
                    Toast.makeText(BillList.this, "Balance : " + unpaidBillAmount, Toast.LENGTH_SHORT);
                    updateStoreDataForPaid(paymentAmount, false);
                    break;
                }
            } while (cursorUnpaidBills.moveToNext());
        }
        cursorUnpaidBills.close();
    }

    public void updateStoreDataForPaid(int paidAmount, boolean isFullPaid) {
        //Variables to hold the new store values
        String setStoreName;
        int setStorePayableAmount;
        int setStoreTotalAmount;
        int setStorePayableBills;
        int setStoreTotalBills;

        //Setting new values to the variables
        setStoreName = getStoreName;


        setStorePayableAmount = getStorePayableAmount - paidAmount;
        setStoreTotalAmount = getStoreTotalAmount;
        if (isFullPaid) {
            setStorePayableBills = getStorePayableBills - 1;
        } else {
            setStorePayableBills = getStorePayableBills;
        }
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
        getContentResolver().update(storeURI, newStoreValues, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bill_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BillList.this);
            builder.setTitle("Confirm");
            builder.setMessage("Do you really want to delete all bills of " + getStoreName + " Permanently?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ContentValues newStoreValue = new ContentValues();
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_NAME, getStoreName);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS, 0);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT, 0);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT, 0);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT, 0);
                            getContentResolver().update(storeURI, newStoreValue, null, null);
                            getContentResolver().delete(BillContract.BillEntry.CONTENT_URI, " " + StoreContract.StoreEntry.COLUMN_STORE_NAME + " = \"" + getStoreName + "\"", null);
                            Toast.makeText(BillList.this, "Bills of " + getStoreName + " Deleted", Toast.LENGTH_SHORT).show();
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

        if (id == R.id.action_delete_account) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BillList.this);
            builder.setTitle("Confirm");
            builder.setMessage("Do you really want to delete the account of " + getStoreName + " Permanently? All the bills and stored information will be deleted.")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getContentResolver().delete(storeURI, null, null);
                            getContentResolver().delete(BillContract.BillEntry.CONTENT_URI, " " + StoreContract.StoreEntry.COLUMN_STORE_NAME + " = \"" + getStoreName + "\"", null);
                            Toast.makeText(BillList.this, "Account of " + getStoreName + " Deleted", Toast.LENGTH_SHORT).show();
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

        if (id == R.id.action_add_account_payment) {
            relativeLayoutAddAccountPayment.setVisibility(View.VISIBLE);
            editTextAddAccountPayment.requestFocus();
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                    linearLayoutBillList.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
            isAddPaymentActivated = true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isAddPaymentActivated){
            relativeLayoutAddAccountPayment.setVisibility(View.GONE);
            isAddPaymentActivated = false;
        }else{
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        switch (i){
            case 1:
                String[] projection = {
                        BillContract.BillEntry._ID,
                        BillContract.BillEntry.COLUMN_STORE_NAME,
                        BillContract.BillEntry.COLUMN_DATE_OF_BILL,
                        BillContract.BillEntry.COLUMN_DATE_OF_PAYMENT,
                        BillContract.BillEntry.COLUMN_AMOUNT,
                        BillContract.BillEntry.COLUMN_PAYMENT_STATUS,
                        BillContract.BillEntry.COLUMN_BILL_ID};
                String selection = BillContract.BillEntry.COLUMN_STORE_NAME + " = \"" + getStoreName + "\"";
                return new CursorLoader(this, BillContract.BillEntry.CONTENT_URI, projection, selection, null, BillContract.BillEntry.COLUMN_PAYMENT_STATUS);

            case 120:
                String[] projection2 = {
                        BillContract.BillEntry.COLUMN_STORE_NAME,
                        BillContract.BillEntry._ID,
                        BillContract.BillEntry.COLUMN_PAYMENT_STATUS,
                        BillContract.BillEntry.COLUMN_AMOUNT};
                String selection1 = BillContract.BillEntry.COLUMN_STORE_NAME + " = \"" + getStoreName + "\"" + " AND " + BillContract.BillEntry.COLUMN_PAYMENT_STATUS + " = " + " 0";
                return new CursorLoader(this, BillContract.BillEntry.CONTENT_URI, projection2, selection1, null, BillContract.BillEntry._ID + " ASC");
            default: return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        if (id == 1) {
            mBillAdapter.swapCursor(cursor);
            listViewBill.setEmptyView(emptyList);
        }
        if (id == 120) {
            cursorUnpaidBills = cursor;
            updateAccountInfoAfterPayment();
            getLoaderManager().destroyLoader(120);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBillAdapter.swapCursor(null);
    }
}
