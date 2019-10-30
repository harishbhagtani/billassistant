package com.android.example.billassistant;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.billassistant.database.BillData.BillContract;
import com.android.example.billassistant.database.StoreData.StoreContract;
import com.android.example.billassistant.database.StoreData.StoreCursorAdapter;
import com.android.example.billassistant.database.StoreData.StoreDBHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Declaration of all the views

    EditText editTextStoreName;
    Button buttonAddStore;
    RelativeLayout relativeLayoutAddStoreLayout;
    ListView storeList;
    LinearLayout linearLayoutActivityMain;
    RelativeLayout emptyListLayout;

    //Declaration of Database and Database helper Variables Variables

    SQLiteDatabase storeDBRead;
    SQLiteDatabase storeDBWrite;
    SQLiteOpenHelper storeDbHelper;

    //Declaring cursorAdapter

    CursorAdapter mStoreCursorAdapter;

    //Variables

    boolean isAddLayoutEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Attaching views to the view variables
        editTextStoreName = (EditText) findViewById(R.id.edit_text_add_store_name);
        buttonAddStore = (Button) findViewById(R.id.button_add_store);
        relativeLayoutAddStoreLayout = (RelativeLayout) findViewById(R.id.relative_layout_add_store);
        storeList = (ListView) findViewById(R.id.list_view_store);
        emptyListLayout = (RelativeLayout) findViewById(R.id.relative_layout_empty_list);
        linearLayoutActivityMain = (LinearLayout)findViewById(R.id.linear_layout_activity_main);
        isAddLayoutEnabled = false;

        //Assigning helper to the database variable
        storeDbHelper = new StoreDBHelper(this);
        storeDBRead = storeDbHelper.getReadableDatabase();
        storeDBWrite = storeDbHelper.getWritableDatabase();

        //Initializing Cursor Adapter
        mStoreCursorAdapter = new StoreCursorAdapter(this, null);

        //Setting adapter To lust view
        storeList.setAdapter(mStoreCursorAdapter);

        //Making relative layout of add store invisible
        relativeLayoutAddStoreLayout.setVisibility(View.GONE);

        //Setting onClickListener on add Button
        buttonAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStore();
            }
        });

        //Initializing the Loader
        getLoaderManager().initLoader(100, null, this);

        //Setting onListViewClickListener on the list view
        storeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView getNumberOfBills = view.findViewById(R.id.text_view_total_bills);
                String getNumberOFBillString = getNumberOfBills.getText().toString();
                String newString = getNumberOFBillString.split(": ")[1];
                int getBills = Integer.valueOf(newString);
                if (getBills == 0) {
                    Toast.makeText(MainActivity.this,"Add First Bill",Toast.LENGTH_SHORT).show();
                    Intent openNewActivity = new Intent(MainActivity.this, AddBillData.class);
                    openNewActivity.putExtra("clickedItemRowId", l);
                    startActivity(openNewActivity);
                } else {
                    Intent openNewActivity = new Intent(MainActivity.this, BillList.class);
                    openNewActivity.putExtra("clickedItemRowId", l);
                    startActivity(openNewActivity);
                }
            }
        });
        storeList.setEmptyView(emptyListLayout);
    }

    public void addStore() {
        //Checking if the EditText is not empty
        if (!editTextStoreName.getText().toString().isEmpty()) {

            //Additng Data to the Database
            String getStoreName = editTextStoreName.getText().toString();
            ContentValues contentValues = new ContentValues();
            contentValues.put(StoreContract.StoreEntry.COLUMN_STORE_NAME, getStoreName);
            getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, contentValues);

            //Showing Toast On Process Completion
            Toast.makeText(this, "Store Added", Toast.LENGTH_SHORT).show();

            //Hiding the Android Soft Keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            //Making Add Store Layout Invisible and Store Number Text View Visibile
            relativeLayoutAddStoreLayout.setVisibility(View.GONE);
            editTextStoreName.setText("");
            editTextStoreName.setHintTextColor(Color.GRAY);
            isAddLayoutEnabled = false;
        }else{
            Toast.makeText(this,"Store Name Cannot Be empty",Toast.LENGTH_SHORT).show();
            editTextStoreName.setHintTextColor(Color.RED);
        }
    }

    @Override
    public void onBackPressed() {
        if (isAddLayoutEnabled) {
            relativeLayoutAddStoreLayout.setVisibility(View.GONE);
            editTextStoreName.setText("");
            isAddLayoutEnabled = false;
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        relativeLayoutAddStoreLayout.setVisibility(View.GONE);
        editTextStoreName.setText("");
        isAddLayoutEnabled = false;
        //Hiding the Android Soft Keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_store) {
            relativeLayoutAddStoreLayout.setVisibility(View.VISIBLE);
            editTextStoreName.requestFocus();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        linearLayoutActivityMain.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
            isAddLayoutEnabled = true;
        }

        if(id == R.id.action_delete_accounts){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Do you really want to delete all the accounts from the database? All the data and bills associated with those accounts will be deleted and you will not be able to recover them in future. Do you want to continue?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            getContentResolver().delete(BillContract.BillEntry.CONTENT_URI,null,null);
                            getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI,null,null);
                            Toast.makeText(MainActivity.this,"Accounts Deleted",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
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

        if(id == R.id.action_delete_bills){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Confirm");
            builder.setMessage("Do you really want to delete all the bills associated with each account? All the bills from each account will be deleted leaving a blank account and you will not be able to recover the bill data in future. Do you want to continue?")
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ContentValues newStoreValue = new ContentValues();
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS,0);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT,0);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT,0);
                            newStoreValue.put(StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT,0);
                            getContentResolver().delete(BillContract.BillEntry.CONTENT_URI,null,null);
                            getContentResolver().update(StoreContract.StoreEntry.CONTENT_URI,newStoreValue,null,null);
                            Toast.makeText(MainActivity.this,"Bills from all accounts deleted",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                StoreContract.StoreEntry.COLUMN_STORE_ID,
                StoreContract.StoreEntry.COLUMN_STORE_NAME,
                StoreContract.StoreEntry.COLUMN_STORE_NUMBER_OF_BILLS_LEFT,
                StoreContract.StoreEntry.COLUMN_STORE_TOTAL_NUMBER_OF_BILLS,
                StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT,
                StoreContract.StoreEntry.COLUMN_STORE_TOTAL_AMOUNT};

        return new CursorLoader(this, StoreContract.StoreEntry.CONTENT_URI, projection, null, null, StoreContract.StoreEntry.COLUMN_STORE_AMOUNT_LEFT + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mStoreCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStoreCursorAdapter.swapCursor(null);
    }
}

