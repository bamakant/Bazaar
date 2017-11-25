package com.kiusoftech.bazaar.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

/**
 * Created by bamakantkar on 15/8/17.
 */
public class BazaarProvider extends ContentProvider {

    /** URI matcher code for the content URI for the imports table */

    private static final int IMPORTS = 100;

    /** URI mather code for the content URI for a single element in imports table */

    private static final int IMPORTS_ID = 101;

    /** URI matcher code for the content URI for the exports table */

    private static final int EXPORTS = 200;

    /** URI mather code for the content URI for a single element in exports table */

    private static final int EXPORTS_ID = 201;

    /** URI matcher code for expenses table */

    private static final int EXPENSES = 300;
    private static final int EXPENSES_ID = 301;

    /** Default uri matcher */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String LOG_TAG = "com.kiusoftech.bazaar";

    // static initializer. This is run the fisrt time anything is called from this class.

    static {

        // The content URI of the form "content://com.kiusoftech.bazaar/imports wil map for all entries
        sUriMatcher.addURI(BazaarContract.CONTENT_AUTHORITY, BazaarContract.PATH_IMPORTS, IMPORTS);

        // The content URI of the form "content://com.kiusoftech.bazaar/imports/# for any single entry of imports table
        sUriMatcher.addURI(BazaarContract.CONTENT_AUTHORITY, BazaarContract.PATH_IMPORTS + "/#", IMPORTS_ID);

        // The content URI of the form "content://com.kiusoftech.bazaar/exports wil map for all entries
        sUriMatcher.addURI(BazaarContract.CONTENT_AUTHORITY, BazaarContract.PATH_EXPORTS, EXPORTS);

        // The content URI of the form "content://com.kiusoftech.bazaar/exports/# for any single entry of exports table
        sUriMatcher.addURI(BazaarContract.CONTENT_AUTHORITY, BazaarContract.PATH_EXPORTS + "/#", EXPORTS_ID);

        // The content URI of the form "content://com.kiusoftech.bazaar/expenses will map for all entries
        sUriMatcher.addURI(BazaarContract.CONTENT_AUTHORITY, BazaarContract.PATH_EXPENSE, EXPENSES);

        // The content URI of the form "content://com.kiusoftech.bazaar/expenses/# will map for all entries
        sUriMatcher.addURI(BazaarContract.CONTENT_AUTHORITY, BazaarContract.PATH_EXPENSE + "/#", EXPENSES_ID);
    }

    /** Database helper object*/
    private BazaarDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new BazaarDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        //Figure out the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case IMPORTS:

                cursor =  database.query(BazaarEntry.IMPORT_TABLE_NAME,projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case IMPORTS_ID:

                selection = BazaarEntry.IMPORT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BazaarEntry.IMPORT_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPORTS:

                cursor = database.query(BazaarEntry.EXPORT_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPORTS_ID:

                selection = BazaarEntry.EXPORT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(BazaarEntry.EXPORT_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case EXPENSES:

                cursor = database.query(BazaarEntry.EXPENSE_TABLE_NAME, projection, selection, selectionArgs, null , null, sortOrder);
                break;

            case EXPENSES_ID:

                selection = BazaarEntry.EXPENSE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BazaarEntry.EXPENSE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                    throw new IllegalArgumentException("Cannot query unknown URI "+uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IMPORTS:
                return insertImports(uri, contentValues);
            case EXPORTS:
                return insertExports(uri, contentValues);
            case EXPENSES:
                return insertExpenses(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertImports(Uri uri, ContentValues values) {

        /**
         * no need to check values becoz its already checked on xml
         *
    // Check that the values are not null
        Integer importPrice = values.getAsInteger(BazaarEntry.COLUMN_IMPORT_PRICE);
        if(importPrice != null && importPrice < 0) {
            throw new IllegalArgumentException("Price required valuse.");
        }

        Integer importQuantity = values.getAsInteger(BazaarEntry.COLUMN_IMPORT_QUANTITY);
        if(importQuantity != null && importQuantity < 0) {
            throw new IllegalArgumentException("Quantity required.");
        }
         */
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert new data with given values
        long id = database.insert(BazaarEntry.IMPORT_TABLE_NAME, null, values);
        // If the ID is -1 then the insertion failed. Log an error.
        if(id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that data has changed for the imports contents URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI wiht the ID of newly inserted data appended at the end
        return ContentUris.withAppendedId(uri, id);
    }
    private Uri insertExports(Uri uri, ContentValues values) {

        SQLiteDatabase database  = mDbHelper.getWritableDatabase();

        long id = database.insert(BazaarEntry.EXPORT_TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return  ContentUris.withAppendedId(uri, id);
    }

    private Uri insertExpenses(Uri uri, ContentValues values) {

        SQLiteDatabase database  = mDbHelper.getWritableDatabase();

        long id = database.insert(BazaarEntry.EXPENSE_TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return  ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IMPORTS:
                return updateImports(uri, contentValues, selection, selectionArgs);
            case IMPORTS_ID:
                selection = BazaarEntry.IMPORT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateImports(uri, contentValues, selection, selectionArgs);
            case EXPORTS:
                return updateExports(uri, contentValues, selection, selectionArgs);
            case EXPORTS_ID:
                selection = BazaarEntry.EXPORT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateExports(uri, contentValues, selection, selectionArgs);
            case EXPENSES:
                return updateExpenses(uri, contentValues, selection, selectionArgs);
            case EXPENSES_ID:
                selection = BazaarEntry.EXPENSE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updateExpenses(uri,contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }

    private  int updateImports(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        /** No need to check

        if(values.containsKey(BazaarEntry.COLUMN_IMPORT_PRICE)) {
                Integer importPrice = values.getAsInteger(BazaarEntry.COLUMN_IMPORT_PRICE);
                if (importPrice == null) {
                    throw new IllegalArgumentException("Price is required.");
                }
            }
         */
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Notify all the listeners that data has been changed for the imports URI
        getContext().getContentResolver().notifyChange(uri, null);

        // return the no of rows effected by the update statement
        return database.update(BazaarEntry.IMPORT_TABLE_NAME, values, selection, selectionArgs);
    }
    private int updateExports(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if (values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);

        return database.update(BazaarEntry.EXPORT_TABLE_NAME, values, selection, selectionArgs);
    }

    private int updateExpenses(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri,null);

        return database.update(BazaarEntry.EXPENSE_TABLE_NAME, values, selection, selectionArgs);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Notify all listeners that data has been changed for the Imports URI
        getContext().getContentResolver().notifyChange(uri, null);

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IMPORTS:
                return database.delete(BazaarEntry.IMPORT_TABLE_NAME, selection, selectionArgs);
            case IMPORTS_ID:
                selection = BazaarEntry.IMPORT_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(BazaarEntry.IMPORT_TABLE_NAME, selection, selectionArgs);
            case EXPORTS:
                return database.delete(BazaarEntry.EXPORT_TABLE_NAME, selection, selectionArgs);
            case EXPORTS_ID:
                selection = BazaarEntry.EXPORT_ID + "=?";
                selectionArgs = new String [] { String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BazaarEntry.EXPORT_TABLE_NAME, selection, selectionArgs);
            case EXPENSES:
                return database.delete(BazaarEntry.EXPENSE_TABLE_NAME, selection, selectionArgs);
            case EXPENSES_ID:
                selection = BazaarEntry.EXPENSE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return database.delete(BazaarEntry.EXPENSE_TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not suppported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match)
        {
            case IMPORTS:
                return BazaarEntry.IMPORT_LIST_TYPE;
            case IMPORTS_ID:
                return BazaarEntry.IMPORT_ITEM_TYPE;
            case EXPORTS:
                return BazaarEntry.EXPORT_LIST_TYPE;
            case EXPORTS_ID:
                return BazaarEntry.EXPORT_ITEM_TYPE;
            case EXPENSES:
                return BazaarEntry.EXPENSE_LIST_TYPE;
            case EXPENSES_ID:
                return BazaarEntry.EXPENSE_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI "+ uri);
        }
    }
}
