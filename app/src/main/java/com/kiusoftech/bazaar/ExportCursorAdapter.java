package com.kiusoftech.bazaar;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

public class ExportCursorAdapter extends CursorAdapter {

    public ExportCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return LayoutInflater.from(context).inflate(R.layout.export_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView dateExTextView = (TextView) view.findViewById(R.id.dateExportTextView);
        TextView priceExTextView = (TextView) view.findViewById(R.id.priceExportTextView);
        TextView quantityExTextView = (TextView) view.findViewById(R.id.quantityExportTextView);

        try {
            int dateExColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPORT_DATE);
            int priceExColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPORT_PRICE);
            int quantityExColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_EXPORT_QUANTITY);

            String date = cursor.getString(dateExColumnIndex);
            String price = cursor.getString(priceExColumnIndex);
            String quantity = cursor.getString(quantityExColumnIndex);

            dateExTextView.setText(date);
            priceExTextView.setText(price);
            quantityExTextView.setText(quantity);
        }catch(Exception e){}
    }
}