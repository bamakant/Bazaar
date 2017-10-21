package com.kiusoftech.bazaar;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.kiusoftech.bazaar.data.BazaarDbHelper;
import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

import java.util.Date;


public class ReportFragment extends Fragment {

    Button startDateButton, endDateButton;

    TextView reportProfitLostTextView,reportImportTotalAmount,reportImportTotalQuantity,reportExportTotalAmount,reportExportTotalQuantity,reportFoodTotalExpense,reportAutoTotalExpense,reportOthersTotalExpense,reportKakuTakenAmount;

    public BazaarDbHelper reportDbHelper;

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getActivity().setTheme(R.style.ReportActivityMaterialTheme);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        reportQuery();   // Calling the Query method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_report, container, false);

        /**
         * Initializing the Bazaar database
         */

        reportDbHelper = new BazaarDbHelper(getActivity());

        /**
         * @link the report fragment elements
         */
        startDateButton = (Button) rootView.findViewById(R.id.startDatePickerButton);
        endDateButton = (Button) rootView.findViewById(R.id.endDatePickerButton);

        reportProfitLostTextView = (TextView) rootView.findViewById(R.id.reportProfitLoseTextView);
        reportImportTotalAmount = (TextView) rootView.findViewById(R.id.reportImportTotalAmountTextView);
        reportImportTotalQuantity = (TextView) rootView.findViewById(R.id.reportImportTotalQuantityTextView);
        reportExportTotalAmount = (TextView) rootView.findViewById(R.id.reportExportTotalAmountTextView);
        reportExportTotalQuantity = (TextView) rootView.findViewById(R.id.reportExportTotalQuantityTextView);
        reportFoodTotalExpense = (TextView) rootView.findViewById(R.id.reportTotalFoodExpenseTextView);
        reportAutoTotalExpense = (TextView) rootView.findViewById(R.id.reportTotalAutoExpenseTextView);
        reportOthersTotalExpense = (TextView) rootView.findViewById(R.id.reportTotalOthersExpenseTextView);
        reportKakuTakenAmount = (TextView) rootView.findViewById(R.id.reportTotalAmountKakuTakenTextView);

        /**
         * Date pickers for date range to query between
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        String currentDate = sdf.format(cal.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, -6);
        String previousDate = sdf.format(cal.getTimeInMillis());

        /**
         * Current date is end Date and the current date - 6 is set to start Date ( SUNDAY TO LAST MONDAY)
         */

        startDateButton.setText(previousDate);   // from this date
        endDateButton.setText(currentDate);      // till this date

        /**
         * FROM date picker button
         */
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                String startDate = Integer.toString(year) + String.format("%02d",(monthOfYear+1)) + String.format("%02d",dayOfMonth);
                                startDateButton.setText(startDate);
                                reportQuery();   // Calling the Query method on each date change
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        /**
         * TILL date picker button
         */

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text

                                String endDate = Integer.toString(year) + String.format("%02d",(monthOfYear+1)) + String.format("%02d",dayOfMonth);
                                endDateButton.setText(endDate);

                                reportQuery();   // Calling the Query method on each date change
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        /**
         * Date picker control end
         */


        return rootView;
    }

    private void reportQuery() {

        int sumImportAmount = 0;
        int sumImportQuantity = 0;
        int sumExportAmount = 0;
        int sumExportQuantity = 0;
        int sumFoodExpense = 0;
        int sumAutoExpense = 0;
        int sumOthersExpense = 0;
        int sumkakuTakenAmount = 0;
        int profitLoseAmount = 0;

        int sDate = Integer.parseInt(startDateButton.getText().toString());
        int eDate = Integer.parseInt(endDateButton.getText().toString());

        SQLiteDatabase db = reportDbHelper.getReadableDatabase();

        String sumImportAmountQuery = "SELECT SUM(" +BazaarEntry.COLUMN_IMPORT_TOTAL_PRICE + ") FROM "
                + BazaarEntry.IMPORT_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_IMPORT_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_IMPORT_DATE +" <='"
                + eDate +"'";
        Log.d("sumImportAmountQuery",sumImportAmountQuery);
        Cursor sumImportAmountCursor = db.rawQuery(sumImportAmountQuery,null);

        if(sumImportAmountCursor.moveToFirst()){
            sumImportAmount = sumImportAmountCursor.getInt(0);
        }

        reportImportTotalAmount.setText(Integer.toString(sumImportAmount));

        String sumImportQuantityQuery = "SELECT SUM(" +BazaarEntry.COLUMN_IMPORT_QUANTITY + ") FROM "
                + BazaarEntry.IMPORT_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_IMPORT_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_IMPORT_DATE +" <='"
                + eDate +"'";
        Log.d("sumImportQuantityQuery",sumImportQuantityQuery);
        Cursor sumImportQuantityCursor = db.rawQuery(sumImportQuantityQuery,null);

        if(sumImportQuantityCursor.moveToFirst()){
            sumImportQuantity = sumImportQuantityCursor.getInt(0);
        }

        reportImportTotalQuantity.setText(Integer.toString(sumImportQuantity));

        String sumExportAmountQuery = "SELECT SUM(" +BazaarEntry.COLUMN_EXPORT_PRICE + ") FROM "
                + BazaarEntry.EXPORT_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_EXPORT_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_EXPORT_DATE +" <='"
                + eDate +"'";
        Log.d("sumExportAmountQuery",sumExportAmountQuery);
        Cursor sumExportAmountCursor = db.rawQuery(sumExportAmountQuery,null);

        if(sumExportAmountCursor.moveToFirst()){
            sumExportAmount = sumExportAmountCursor.getInt(0);
        }

        reportExportTotalAmount.setText(Integer.toString(sumExportAmount));

        String sumExportQuantityQuery = "SELECT SUM(" +BazaarEntry.COLUMN_EXPORT_QUANTITY + ") FROM "
                + BazaarEntry.EXPORT_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_EXPORT_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_EXPORT_DATE +" <='"
                + eDate +"'";
        Log.d("sumExportQuantityQuery",sumExportQuantityQuery);
        Cursor sumExportQuantityCursor = db.rawQuery(sumExportQuantityQuery,null);

        if(sumExportQuantityCursor.moveToFirst()){
            sumExportQuantity = sumExportQuantityCursor.getInt(0);
        }

        reportExportTotalQuantity.setText(Integer.toString(sumExportQuantity));

        String sumFoodExpenseQuery = "SELECT SUM(" +BazaarEntry.COLUMN_EXPENSE_FOOD + ") FROM "
                + BazaarEntry.EXPENSE_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_EXPENSE_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_EXPENSE_DATE +" <='"
                + eDate +"'";
        Log.d("sumFoodExpenseQuery",sumFoodExpenseQuery);
        Cursor sumFoodExpenseCursor = db.rawQuery(sumFoodExpenseQuery,null);

        if(sumFoodExpenseCursor.moveToFirst()){
            sumFoodExpense = sumFoodExpenseCursor.getInt(0);
        }

        reportFoodTotalExpense.setText(Integer.toString(sumFoodExpense));

        String sumAutoExpenseQuery = "SELECT SUM(" +BazaarEntry.COLUMN_EXPENSE_AUTO + ") FROM "
                + BazaarEntry.EXPENSE_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_EXPENSE_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_EXPENSE_DATE +" <='"
                + eDate +"'";
        Log.d("sumAutoExpenseQuery",sumAutoExpenseQuery);
        Cursor sumAutoExpenseCursor = db.rawQuery(sumAutoExpenseQuery,null);

        if(sumAutoExpenseCursor.moveToFirst()){
            sumAutoExpense = sumAutoExpenseCursor.getInt(0);
        }

        reportAutoTotalExpense.setText(Integer.toString(sumAutoExpense));

        String sumOthersExpenseQuery = "SELECT SUM(" +BazaarEntry.COLUMN_EXPENSE_OTHERS + ") FROM "
                + BazaarEntry.EXPENSE_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_EXPENSE_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_EXPENSE_DATE +" <='"
                + eDate +"'";
        Log.d("sumOthersExpenseQuery",sumOthersExpenseQuery);
        Cursor sumOthersExpenseCursor = db.rawQuery(sumOthersExpenseQuery,null);

        if(sumOthersExpenseCursor.moveToFirst()){
            sumOthersExpense = sumOthersExpenseCursor.getInt(0);
        }

        reportOthersTotalExpense.setText(Integer.toString(sumOthersExpense));

        String sumKakuTakenAmountQuery = "SELECT SUM(" +BazaarEntry.COLUMN_EXPENSE_KAKU + ") FROM "
                + BazaarEntry.EXPENSE_TABLE_NAME + " WHERE "
                + BazaarEntry.COLUMN_EXPENSE_DATE + " >='"
                + sDate +"' AND "
                +BazaarEntry.COLUMN_EXPENSE_DATE +" <='"
                + eDate +"'";
        Log.d("sumKakuTakenAmountQuery",sumKakuTakenAmountQuery);
        Cursor sumKakuTakenAmountCursor = db.rawQuery(sumKakuTakenAmountQuery,null);

        if(sumKakuTakenAmountCursor.moveToFirst()){
            sumkakuTakenAmount = sumKakuTakenAmountCursor.getInt(0);
        }

        reportKakuTakenAmount.setText(Integer.toString(sumkakuTakenAmount));

        /**
         * calculating profit or lose
         */

        profitLoseAmount = sumExportAmount - (sumImportAmount + sumFoodExpense + sumAutoExpense + sumOthersExpense);

        reportProfitLostTextView.setText(Integer.toString(profitLoseAmount));

    }
}
