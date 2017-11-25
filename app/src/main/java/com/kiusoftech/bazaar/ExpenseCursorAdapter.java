package com.kiusoftech.bazaar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

import org.w3c.dom.Text;

import java.util.StringTokenizer;

public class ExpenseCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    public ExpenseCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView dateExpenseTextView = (TextView) view.findViewById(R.id.dateExpenseTextView);
        TextView foodExpenseTextView = (TextView) view.findViewById(R.id.foodExpenseTextView);
        TextView autoExpenseTextView = (TextView) view.findViewById(R.id.autoExpenseTextView);
        TextView othersExpenseTextView = (TextView) view.findViewById(R.id.othersExpenseTextView);
        TextView kakuExpenseTextView  = (TextView) view.findViewById(R.id.kakuExpenseTextView);

        // need to keep it inside try-catch block

        try {
            // Now extracting the values of cursor for given column indexes

            String date = cursor.getString(cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_DATE));
            String food = cursor.getString(cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_FOOD));
            String auto = cursor.getString(cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_AUTO));
            String others = cursor.getString(cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_OTHERS));
            String kaku = cursor.getString(cursor.getColumnIndex(BazaarEntry.COLUMN_EXPENSE_KAKU));

            dateExpenseTextView.setText(date);
            foodExpenseTextView.setText(food);
            autoExpenseTextView.setText(auto);
            othersExpenseTextView.setText(others);
            kakuExpenseTextView.setText(kaku);

        }catch (Exception e){

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.expense_list_item, parent, false);
    }
}