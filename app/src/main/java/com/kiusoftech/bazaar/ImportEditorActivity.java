package com.kiusoftech.bazaar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

import java.util.Date;

public class ImportEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Toolbar toolbar;
    Button btnSave,btnDelete,changeDate;
    EditText editAmount,editQuantity;
    TextView currentDateView;
    Calendar c;
    SimpleDateFormat dateFormat;
    String formatedDate;

    /**
     * Identifier for the imports data loader
     * */
    private static final int EXISTING_IMPORTS_LOADER = 0;

    /**
     * Content URI for the existing imports ( null if it's a new pet)
     */
    private Uri mCurrentImportsUri;

    /**
     * Boolean that keep track of whether imports data has been changed or not
     */
    private boolean mImportsHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on view and that has not beeen saved, if
     * user touches the view then we set the mImportsHasChanged to True
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mImportsHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.ImportActivityMaterialTheme);

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
        changeDate = (Button) findViewById(R.id.changeDateImExButton);
        editAmount = (EditText) findViewById(R.id.edit_pet_price);
        editQuantity = (EditText) findViewById(R.id.edit_pet_quantity);
        currentDateView = (TextView) findViewById(R.id.currentDateTextView);

        /**
         * button backgrounds
         */

        btnSave.setBackgroundResource(R.color.importAccent);
        btnDelete.setBackgroundResource(R.color.importAccent);
        changeDate.setBackgroundResource(R.color.importAccent);
        /**
         * Current date from system
         */

        c = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        formatedDate= dateFormat.format(c.getTime());
        currentDateView.setText(formatedDate);

        /**
         * For back date entry you need to change the date using date picker
         */

        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ImportEditorActivity.this,
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
        int day = c.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                currentDay = ", SUNDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
            case Calendar.MONDAY:
                currentDay = ", MONDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
            case Calendar.TUESDAY:
                currentDay = ", TUESDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
            case Calendar.WEDNESDAY:
                currentDay = ", WEDNESDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
            case Calendar.THURSDAY:
                currentDay = ", THURSDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
            case Calendar.FRIDAY:
                currentDay = ", FRIDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
            case Calendar.SATURDAY:
                currentDay = ", SATURDAY";
                currentDateView.setText(formatedDate + currentDay);
                break;
        }
*/
        /**
         * @Save button OnClick Listener
          */
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = editAmount.getText().toString();

                if(amount.trim().equals("")){
                    Toast.makeText(ImportEditorActivity.this,"Please enter amount",Toast.LENGTH_SHORT).show();
                }
                else {
                    /**
                     * Calculating total amount spend
                    int amountValue = Integer.parseInt(amount);
                    int total = amountValue*quantity;
                    */
                    saveImport();  // Insert data in to the IMPORT table
                    editAmount.setText("");
                    editQuantity.setText("1");

                }

            }
        });

        // @ Save button on click listener end

        /**
         * @Delete button onClick handler
         */
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();  // Pop-Up delete confirmation dialog
            }
        });

        // Suppose the intent that was used to launch this activity.
        // to figure out we are creating a new entry or editing an entry
        Intent intent = getIntent();
        mCurrentImportsUri = intent.getData();

        if (mCurrentImportsUri == null) {
            // This is for new entry
            setTitle("Import new Entry");

            // hide the delete button

            btnDelete.setVisibility(View.GONE);
        }
        else {
            // To edit a existing entry
            setTitle("Edit Entry");

            // Initialize the loader to read the database and display them on the editor
            getSupportLoaderManager().initLoader(EXISTING_IMPORTS_LOADER, null, this);
        }

        // Setup OnTouchListener on all input fields
        editAmount.setOnTouchListener(mTouchListener);
        editQuantity.setOnTouchListener(mTouchListener);
        changeDate.setOnTouchListener(mTouchListener);

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
                // If the entry hasn't changed, continue with navigating up to previuos activity
                // @link to ImportActivity.this
                if (!mImportsHasChanged) {
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
        if (!mImportsHasChanged) {
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

    private void saveImport() {

        // Read from input fields
        // Using trim method to eliminate lading or trailing white spaces

        int dateString = Integer.parseInt(currentDateView.getText().toString());
        String priceString = editAmount.getText().toString().trim();
        String quantityString = editQuantity.getText().toString().trim();

        int priceInt = Integer.parseInt(priceString);
        int quantityInt = Integer.parseInt(quantityString);
        int totalPrice = priceInt * quantityInt;

        // Creating content values to pass to sql statement as keys
        ContentValues values = new ContentValues();
        values.put(BazaarEntry.COLUMN_IMPORT_DATE, dateString);
        values.put(BazaarEntry.COLUMN_IMPORT_PRICE, priceInt);
        values.put(BazaarEntry.COLUMN_IMPORT_QUANTITY, quantityInt);
        values.put(BazaarEntry.COLUMN_IMPORT_TOTAL_PRICE, totalPrice);

        // First check whether this is new entry or existing one need to be update by
        // the help of mUcrrentImportsUri
        if (mCurrentImportsUri == null) {
            // This is for new entry
            Uri newUri = getContentResolver().insert(BazaarEntry.IMPORT_URI,values);

            // Showing Toast message depending on the operation
            if (newUri == null) {
                Toast.makeText(ImportEditorActivity.this, "Error with saving data",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ImportEditorActivity.this, "Entry Saved",Toast.LENGTH_SHORT).show();
            }
        }else {

            int rowsAffected = getContentResolver().update(mCurrentImportsUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(ImportEditorActivity.this, " Error with updating data.",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(ImportEditorActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Showing a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Creating an AlertDialog.Builder and setting the message, and click listeners
        // for the positive and negative buttons on the dialog.
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

    /**
     * Warming the user to confirm that they want to delete this Entry.
     */
    private void showDeleteConfirmationDialog() {
        // Creating an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteImport();
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
    private void deleteImport() {
        // Deletion is possible only if this is an existing entry
        if (mCurrentImportsUri != null) {
            // Calling the ContentResolver to delete the entry at the given URI
            int rowsDeleted = getContentResolver().delete(mCurrentImportsUri,null, null);

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
        // Defining projection for columns to be displayed

        String[] projection = {
                BazaarEntry.IMPORT_ID,
                BazaarEntry.COLUMN_IMPORT_DATE,
                BazaarEntry.COLUMN_IMPORT_PRICE,
                BazaarEntry.COLUMN_IMPORT_QUANTITY,
                BazaarEntry.COLUMN_IMPORT_TOTAL_PRICE
        };

        // Now execute the query
        return new CursorLoader(this,  //Parent activity context
                mCurrentImportsUri,     // query that contain URI
                projection,             // columns to be extracted
                null,                   // No selection clouse
                null,                   // no selection arguments
                null                    // default sorting order
                );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        try
        {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            // select attributes that we need to display on editor activity
            int dateColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_DATE);
            int priceColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_QUANTITY);

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
