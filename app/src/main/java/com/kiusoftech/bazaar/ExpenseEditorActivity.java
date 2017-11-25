package com.kiusoftech.bazaar;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.annotation.IntegerRes;
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

public class ExpenseEditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    Toolbar toolbar;
    Button expenseBtnSave,expenseBtnDelete,changeDateBtn;
    EditText expeneEditFood, expenseEditAuto, expenseEditOthers, expenseEditKaku;
    TextView currentDateView;
    Calendar c;
    SimpleDateFormat dateFormat;
    String formatedDate;

    private static final int EXISTING_EXPENSE_LOADER = 0;

    private Uri mCurrentExpenseUri;


    private boolean mExpenseHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mExpenseHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.ExpenseActivityMaterialTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_editor);

        //@ link to action bar layout

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * @link to layout components
          */

        expenseBtnSave = (Button) findViewById(R.id.expense_editor_save_button);
        expenseBtnDelete = (Button) findViewById(R.id.expense_editor_delete_button);
        changeDateBtn = (Button) findViewById(R.id.changeDateExpenseButton);
        expeneEditFood = (EditText) findViewById(R.id.expense_edit_food_amount);
        expenseEditAuto = (EditText) findViewById(R.id.expense_edit_auto_amount);
        expenseEditOthers = (EditText) findViewById(R.id.expense_edit_others_amount);
        expenseEditKaku = (EditText) findViewById(R.id.expense_edit_kaku_amount);
        currentDateView = (TextView) findViewById(R.id.currentTextView);

        /**
         * set CURRENT Date on Date textView
         */

        c = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyyMMdd");
        formatedDate = dateFormat.format(c.getTime());

        currentDateView.setText(formatedDate);  //Input current date

        /**
         * change the background color of buttons
         */

        expenseBtnSave.setBackgroundResource(R.color.expenseAccent);
        expenseBtnDelete.setBackgroundResource(R.color.expenseAccent);
        changeDateBtn.setBackgroundResource(R.color.expenseAccent);

        /**
         * For back date entry you need to change the date using date picker
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ExpenseEditorActivity.this,
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
         * SAVE button click event handler
         */
        expenseBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String foodEmpty = expeneEditFood.getText().toString();
                String autoEmpty = expenseEditAuto.getText().toString();
                String othersEmpty = expenseEditOthers.getText().toString();
                String kakuEmpty = expenseEditKaku.getText().toString();

                if (foodEmpty.trim().equals("")){
                    Toast.makeText(ExpenseEditorActivity.this, "Please enter food expense", Toast.LENGTH_SHORT).show();
                }
                else if (autoEmpty.trim().equals("")){
                    Toast.makeText(ExpenseEditorActivity.this, "Please enter auto expense", Toast.LENGTH_SHORT).show();
                }
                else if (othersEmpty.trim().equals("")){
                    Toast.makeText(ExpenseEditorActivity.this, "Please enter extra expense", Toast.LENGTH_SHORT).show();
                }
                else if (kakuEmpty.trim().equals("")){
                    Toast.makeText(ExpenseEditorActivity.this, "Please enter expense", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveExpense();  //This method insert the data on database table IMPORT Table
                    expeneEditFood.setText("");
                    expenseEditAuto.setText("");
                    expenseEditOthers.setText("");
                    expenseEditKaku.setText("");
                }
            }
        });

        /**
         * Button delete onclick handler
          */
        expenseBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteConfirmationDialog();   // POP-Up delete confirmation dialog
            }
        });

        /**
         * To check editor will be used as new entry or edit existing entry
         */
        Intent intent = getIntent();
        mCurrentExpenseUri = intent.getData();

        if (mCurrentExpenseUri == null) {
            setTitle("Add expense");
            expenseBtnDelete.setVisibility(View.GONE);
        }
        else {
            setTitle("Edit Expense");
            getSupportLoaderManager().initLoader(EXISTING_EXPENSE_LOADER, null, this);
        }

        expeneEditFood.setOnTouchListener(mTouchListener);
        expenseEditAuto.setOnTouchListener(mTouchListener);
        expenseEditOthers.setOnTouchListener(mTouchListener);
        expenseEditKaku.setOnTouchListener(mTouchListener);
        changeDateBtn.setOnTouchListener(mTouchListener);
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
                if (!mExpenseHasChanged) {
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
        if (!mExpenseHasChanged) {
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

    private void saveExpense() {

        String dateString = currentDateView.getText().toString().trim();
        String foodString = expeneEditFood.getText().toString().trim();
        String autoString = expenseEditAuto.getText().toString().trim();
        String othersString = expenseEditOthers.getText().toString().trim();
        String kakuString = expenseEditKaku.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(BazaarEntry.COLUMN_EXPENSE_DATE, dateString);
        values.put(BazaarEntry.COLUMN_EXPENSE_FOOD, foodString);
        values.put(BazaarEntry.COLUMN_EXPENSE_AUTO, autoString);
        values.put(BazaarEntry.COLUMN_EXPENSE_OTHERS, othersString);
        values.put(BazaarEntry.COLUMN_EXPENSE_KAKU, kakuString);

        if (mCurrentExpenseUri == null) {
            Uri newUri = getContentResolver().insert(BazaarEntry.EXPENSE_URI, values);

            if (newUri == null) {
                Toast.makeText(ExpenseEditorActivity.this, "Error with saving data", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ExpenseEditorActivity.this, "Expense Saved",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int rowsAffected = getContentResolver().update(mCurrentExpenseUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(ExpenseEditorActivity.this," Error with updating data.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ExpenseEditorActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Creating an AlertDialog.Builder and seting the message, and click listeners
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

    private void showDeleteConfirmationDialog() {
        // Creating an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteExpense();
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
    private void deleteExpense()
    {
        //Deletion is possible only if there is an existing entry
        if (mCurrentExpenseUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentExpenseUri,null, null);

            // Showing the message depending on whether entry has been deleted or not
            if (rowsDeleted == 0) {
                // If entry has not been deleted
                Toast.makeText(this, getString(R.string.editor_delete_entry_failed),Toast.LENGTH_SHORT).show();
            } else {
                //Otherwise, the delete was successful and showing successful toast.
                Toast.makeText(this, getString(R.string.editor_delete_entry_successful),Toast.LENGTH_SHORT).show();
            }
        }

        //closing the activity
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BazaarEntry.EXPENSE_ID,
                BazaarEntry.COLUMN_EXPENSE_DATE,
                BazaarEntry.COLUMN_EXPENSE_FOOD,
                BazaarEntry.COLUMN_EXPENSE_AUTO,
                BazaarEntry.COLUMN_EXPENSE_OTHERS,
                BazaarEntry.COLUMN_EXPENSE_KAKU
        };

        return new CursorLoader(this,
                mCurrentExpenseUri,
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

try
{
            if (cursor.moveToFirst()) {

            // selecting attributes that i want to display on editor activity


            int dateColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_DATE);
            int foodColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_FOOD);
            int autoColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_AUTO);
            int othersColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_OTHERS);
            int kakuColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_KAKU);

            // Now extracting the values of cursor for given column indexes

                String date = cursor.getString(dateColumnIndex);
                String food = cursor.getString(foodColumnIndex);
                String auto = cursor.getString(autoColumnIndex);
                String others = cursor.getString(othersColumnIndex);
                String kaku = cursor.getString(kakuColumnIndex);

            currentDateView.setText(date);
            expeneEditFood.setText(food);
            expenseEditAuto.setText(auto);
            expenseEditOthers.setText(others);
            expenseEditKaku.setText(kaku);
        }
}catch (Exception e){}

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        currentDateView.setText("");
        expeneEditFood.setText("");
        expenseEditAuto.setText("");
        expenseEditOthers.setText("");
        expenseEditKaku.setText("");
    }
}