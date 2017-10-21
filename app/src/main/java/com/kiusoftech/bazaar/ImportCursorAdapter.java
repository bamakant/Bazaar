package com.kiusoftech.bazaar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

public class ImportCursorAdapter extends CursorAdapter {

    public ImportCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.import_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView dateTextView = (TextView) view.findViewById(R.id.dateImportTextView);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceImportTextView);
        TextView quantiyTextView = (TextView) view.findViewById(R.id.quantityImportTextView);
        TextView totalPriceTextView = (TextView) view.findViewById(R.id.totalImportPriceTextView);
try {
    int dateColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_DATE);
    int priceColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_PRICE);
    int quantityColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_QUANTITY);
    int totalPriceColumnIndex = cursor.getColumnIndex(BazaarEntry.COLUMN_IMPORT_TOTAL_PRICE);

    String importDate = cursor.getString(dateColumnIndex);
    String importPrice = cursor.getString(priceColumnIndex);
    String importQuantity = cursor.getString(quantityColumnIndex);
    String importTotalPrice = cursor.getString(totalPriceColumnIndex);

    dateTextView.setText(importDate);
    priceTextView.setText(importPrice);
    quantiyTextView.setText(importQuantity);
    totalPriceTextView.setText(importTotalPrice);
}catch (Exception e){}
    }
}