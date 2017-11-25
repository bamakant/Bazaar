package com.kiusoftech.bazaar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

/**
 * Created by bamakantkar on 15/8/17.
 */
public class BazaarDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BazaarDbHelper.class.getSimpleName();

    /** Name of the database file */

    private static final String DATABASE_NAME = "bazaar.db";

    /** Database version , if you change the database schema you have to increament the databse version.  */

    private static final int DATABASE_VERSION = 3;

    public BazaarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating a string that contains the SQl command for create the table "import"

        String SQL_CREATE_IMPORTS_TABLE = "CREATE TABLE " +
                BazaarEntry.IMPORT_TABLE_NAME + " ("
                + BazaarEntry.IMPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BazaarEntry.COLUMN_IMPORT_DATE + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_IMPORT_PRICE + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_IMPORT_QUANTITY + " INTEGER NOT NULL DEFAULT 1, "
                + BazaarEntry.COLUMN_IMPORT_TOTAL_PRICE + " INTEGER NOT NULL);";

        // Creating s string that contains the SQL command for create the table "export"

        String SQL_CREATE_EXPORTS_TABLE = "CREATE TABLE " +
                BazaarEntry.EXPORT_TABLE_NAME + " ("
                + BazaarEntry.EXPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BazaarEntry.COLUMN_EXPORT_DATE + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_EXPORT_PRICE + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_EXPORT_QUANTITY + " INTEGER NOT NULL DEFAULT 1);";

        // Creating SQL statement to create expense table

        String SQL_CREATE_EXPENSE_TABLE = "CREATE TABLE " +
                BazaarEntry.EXPENSE_TABLE_NAME + " ("
                + BazaarEntry.EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BazaarEntry.COLUMN_EXPENSE_DATE + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_EXPENSE_FOOD + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_EXPENSE_AUTO + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_EXPENSE_OTHERS + " INTEGER NOT NULL, "
                + BazaarEntry.COLUMN_EXPENSE_KAKU + " INTEGER NOT NULL);";

        // Executing SQL statement to create the table "imports"
        db.execSQL(SQL_CREATE_IMPORTS_TABLE);
        // Executing SQL statement to create the table "exports"
        db.execSQL(SQL_CREATE_EXPORTS_TABLE);
        // Executing SQL statement to create the table "expenses"
        db.execSQL(SQL_CREATE_EXPENSE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
