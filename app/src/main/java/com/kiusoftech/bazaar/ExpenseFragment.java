package com.kiusoftech.bazaar;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kiusoftech.bazaar.data.BazaarContract;
import com.kiusoftech.bazaar.data.BazaarDbHelper;

import java.util.ArrayList;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

public class ExpenseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG ="ExpenseFragment" ;
    ListView expenseListView;
    TextView lastUpdatedData;
    public BazaarDbHelper mDbHelper;
    public static final int EXPENSE_LOADER = 0;
    ExpenseCursorAdapter mExpenseCursorAdapter;

    public ExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_expense, container, false);

        mDbHelper = new BazaarDbHelper(getActivity());

        expenseListView = (ListView) rootView.findViewById(R.id.listExpense);
        lastUpdatedData = (TextView) rootView.findViewById(R.id.lastUpdatedDataTextView);

        mExpenseCursorAdapter = new ExpenseCursorAdapter(getActivity(), null);
        expenseListView.setAdapter(mExpenseCursorAdapter);

        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ExpenseEditorActivity.class);

                Uri currentExpenseUri = ContentUris.withAppendedId(BazaarEntry.EXPENSE_URI, id);

                intent.setData(currentExpenseUri);

                startActivity(intent);

            }
        });

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String lastUpdateDbQuery = "SELECT MAX(" +BazaarEntry.COLUMN_EXPENSE_DATE + ") FROM "
                + BazaarEntry.EXPENSE_TABLE_NAME;
        Log.d("lastUpdateDbQuery",lastUpdateDbQuery);
        Cursor lastUpdateDbQueryCursor = db.rawQuery(lastUpdateDbQuery,null);

        if(lastUpdateDbQueryCursor.moveToFirst()){
            String lastUpdated = lastUpdateDbQueryCursor.getString(0);
            lastUpdatedData.setText(lastUpdated);
        }

        getActivity().getSupportLoaderManager().initLoader(EXPENSE_LOADER, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BazaarEntry.EXPENSE_ID,
                BazaarEntry.COLUMN_EXPENSE_DATE,
                BazaarEntry.COLUMN_EXPENSE_FOOD,
                BazaarEntry.COLUMN_EXPENSE_AUTO,
                BazaarEntry.COLUMN_EXPENSE_OTHERS,
                BazaarEntry.COLUMN_EXPENSE_KAKU
        };

        return new CursorLoader(getActivity(),
                BazaarEntry.EXPENSE_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      mExpenseCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
mExpenseCursorAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        getLoaderManager().restartLoader(0, null, this);
    }
}
