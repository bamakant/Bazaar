package com.kiusoftech.bazaar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kiusoftech.bazaar.data.BazaarContract;
import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

import com.kiusoftech.bazaar.HomeActivity;

import java.util.Date;

public class ExportEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Toolbar toolbar;
    Button btnSave,btnDelete,changeDateBtn;
    EditText editAmount,editQuantity;
    TextView currentDateView;
    Calendar c;
    SimpleDateFormat dateFormat;
    String formatedDate;
    ExportFragment exportFragment;


    private static final int EXISTING_EXPORTS_LOADER = 0;

    private Uri mCurrentExportsUri;

    private boolean mExportsHasChanged = false;

    private View.OnTouchListener mTouchLintener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
         mExportsHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.ExportActivityMaterialTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im_ex_editor);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * @link all component of editor activity
         */
        btnSave = (Button) findViewById(R.id.editor_save_button);
        btnDelete = (Button) findViewById(R.id.editor_delete_button);
        changeDateBtn = (Button) findViewById(R.id.changeDateImExButton);
        editAmount = (EditText) findViewById(R.id.edit_pet_price);
        editQuantity = (EditText) findViewById(R.id.edit_pet_quantity);
        currentDateView = (TextView) findViewById(R.id.currentDateTextView);

        /**
         * Today's date , default date is current date
          */
        c = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        formatedDate= dateFormat.format(c.getTime());
        currentDateView.setText(formatedDate);

        /**
         * Change background color of buttons
         */

        btnSave.setBackgroundResource(R.color.exportAccent);
        btnDelete.setBackgroundResource(R.color.exportAccent);
        changeDateBtn.setBackgroundResource(R.color.exportAccent);

        /**
         * If user want to change the date for back date entry then date picker @OnClick currentDateTextView
         */

        changeDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExportEditorActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the textView

                                String startDate = Integer.toString(year) + String.format("%02d",(monthOfYear+1)) + String.format("%02d",dayOfMonth);
                                currentDateView.setText(startDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        /**
         * @Save button OnClick Listener
          */
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = editAmount.getText().toString();

               if(amount.trim().equals("")){
                   Toast.makeText(ExportEditorActivity.this,"Please enter amount",Toast.LENGTH_SHORT).show();
               }
                else {
                    saveExport();         // this button insert data on the EXPORT table
                    editAmount.setText("");
                    editAmount.setFocusable(true);
                    editQuantity.setText("1");
               }
            }
        });

        // @ Save button on click listener end

        /**
         * @Delete button OnClick event handler
         */
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();     // Pop-Up delete confirmation dialog
            }
        });

        /**
         * To check whether intent is used to launch this activity as editor or add new entry
         */
        Intent intent = getIntent();
        mCurrentExportsUri = intent.getData();

        if (mCurrentExportsUri == null) {
            setTitle("Add new Entry");
            btnDelete.setVisibility(View.GONE);
        }
        else {
            setTitle("Edit Entry");
            getSupportLoaderManager().initLoader(EXISTING_EXPORTS_LOADER, null, this);
        }

        editAmount.setOnTouchListener(mTouchLintener);
        editQuantity.setOnTouchListener(mTouchLintener);
        changeDateBtn.setOnTouchListener(mTouchLintener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.exit:
                finishAffinity();
                return true;
            case android.R.id.home:
                if (!mExportsHasChanged) {
                    //NavUtils.navigateUpFromSameTask(ImportEditorActivity.this);
                    finish();
                    return true;
                }
                else {
                    // Otherwise if there are unsaved changes then setup a dialog to warn them
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, close the current activity.
                                    finish();
                                }
                            };

                    // Show dialog that there are unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when back button is pressed
     */
    @Override
    public void onBackPressed() {
        // If the entry hasn't changed, continue with handling back button press
        if (!mExportsHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes then setup a dialog to warn them
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

    }

    private void saveExport() {

        String dateString = currentDateView.getText().toString().trim();
        String priceString = editAmount.getText().toString().trim();
        String quantityString = editQuantity.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BazaarEntry.COLUMN_EXPORT_DATE, dateString);
        values.put(BazaarEntry.COLUMN_EXPORT_PRICE, priceString);
        values.put(BazaarEntry.COLUMN_EXPORT_QUANTITY, quantityString);

        if (mCurrentExportsUri == null) {
            Uri newUri = getContentResolver().insert(BazaarEntry.EXPORT_URI, values);

            if (newUri == null) {
                Toast.makeText(ExportEditorActivity.this, "Error with saving data", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ExportEditorActivity.this, "Entry Saved",Toast.LENGTH_SHORT).show();
            }
        }
        else {

            int rowsAffected = getContentResolver().update(mCurrentExportsUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(ExportEditorActivity.this," Error with updating data.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ExportEditorActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Creating an AlertDialog.Builder and seting the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Creating an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteExport();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Performing deletion of entry in the database
     */
    private void deleteExport() {
        // Deletion is possible only if this is an existing entry
        if (mCurrentExportsUri != null) {
            // Calling the ContentResolver to delete the entry at the given URI
            int rowsDeleted = getContentResolver().delete(mCurrentExportsUri,null, null);

            // Showing the message depending on whether entry has been deleted or not
            if (rowsDeleted == 0) {
                // If entry has not been deleted
                Toast.makeText(this, getString(R.string.editor_delete_entry_failed),Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the delete was successful and showing successful toast.
                Toast.makeText(this, getString(R.string.editor_delete_entry_successful),Toast.LENGTH_SHORT).show();
            }
        }

        // Closing the activity.
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BazaarEntry.EXPORT_ID,
                BazaarEntry.COLUMN_EXPORT_DATE,
                BazaarEntry.COLUMN_EXPORT_PRICE,
                BazaarEntry.COLUMN_EXPORT_QUANTITY
        };

        return new CursorLoader(this,
                mCurrentExportsUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        try {
            if (cursor.moveToFirst()) {

                // select attributes that we need to display on editor activity
                int dateColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPORT_DATE);
                int priceColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPORT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPORT_QUANTITY);

                // Now extracting values from cursor for the given column index
                String date = cursor.getString(dateColumnIndex);
                int price = cursor.getInt(priceColumnIndex);
                int quantity = cursor.getInt(quantityColumnIndex);

                // Updating the view on the screen with the values from the database
                currentDateView.setText(date);
                editAmount.setText(Integer.toString(price));
                editQuantity.setText(Integer.toString(quantity));

            }
        }catch (Exception e){}

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        currentDateView.setText("");
        editAmount.setText("");
        editQuantity.setText("");
    }
}
