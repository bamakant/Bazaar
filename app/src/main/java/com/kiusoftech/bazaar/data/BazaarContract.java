package com.kiusoftech.bazaar.data;

/**
 * Created by bamakantkar on 15/8/17.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
API Contract for the Bazzar app

 */
public class BazaarContract {

    // To prevent someone rom accidentally instantiating the contract class
    // giving it to an empty constructor

    private BazaarContract() {}

    public static final String CONTENT_AUTHORITY = "com.kiusoftech.bazaar";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //@ import table path
    public static final String PATH_IMPORTS = "imports";

    //@ export table path
    public static final String PATH_EXPORTS  = "exports";

    //@ expense table path
    public static final String PATH_EXPENSE = "expenses";

    public static final class BazaarEntry implements BaseColumns {

        public static final Uri IMPORT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_IMPORTS);
        public static final Uri EXPORT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPORTS);

        public static final String IMPORT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMPORTS;
        public static final String EXPORT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPORTS;

        public static final String IMPORT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMPORTS;
        public static final String EXPORT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPORTS;

        /** Name of the database table of import bazaar  */

        /** Import table defination */
        public final static String IMPORT_TABLE_NAME = "imports";

        public final static String IMPORT_ID = BaseColumns._ID;

        public final static String COLUMN_IMPORT_DATE = "importdate";

        public final static String COLUMN_IMPORT_PRICE = "importprice";

        public final static String COLUMN_IMPORT_QUANTITY = "importquantity";

        public final static String COLUMN_IMPORT_TOTAL_PRICE = "importtotalprice";

        /** Export table defination */

        public final static String EXPORT_TABLE_NAME = "exports";

        public final static String EXPORT_ID = BaseColumns._ID;

        public final static String COLUMN_EXPORT_DATE = "exportdate";

        public final static String COLUMN_EXPORT_PRICE = "exportprice";

        public final static String COLUMN_EXPORT_QUANTITY = "exportquantity";

        /**
         *  Expense table defination
         */
        public static final Uri EXPENSE_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSE);
        public static final String EXPENSE_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;
        public static final String EXPENSE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE;

        public final static String EXPENSE_TABLE_NAME = "expenses";

        public final static String EXPENSE_ID = BaseColumns._ID;

        public final static String COLUMN_EXPENSE_DATE = "expensedate";

        public final static String COLUMN_EXPENSE_FOOD = "expensefood";

        public final static String COLUMN_EXPENSE_AUTO = "expenseauto";

        public final static String COLUMN_EXPENSE_OTHERS = "expenseothers";

        public final static String COLUMN_EXPENSE_KAKU = "expensekaku";

    }

}
