package com.kiusoftech.bazaar;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kiusoftech.bazaar.data.BazaarDbHelper;

import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

public class ImportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "Import Fragment";
    ListView importListView;
    public BazaarDbHelper mDbHelper;
    public static final int IMPORT_LOADER = 0;
    TextView lastUpdatedData;

    ImportCursorAdapter mImportCursorAdapter;

    public ImportFragment() {// Required empty public constructor

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_import, container, false);

        mDbHelper = new BazaarDbHelper(getActivity());


        importListView = (ListView) rootView.findViewById(R.id.listImport);
        lastUpdatedData = (TextView) rootView.findViewById(R.id.lastUpdatedDataTextView);

        mImportCursorAdapter  = new ImportCursorAdapter(getActivity(), null);
        importListView.setAdapter(mImportCursorAdapter);

        importListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ImportEditorActivity.class);

                Uri currentUri = ContentUris.withAppendedId(BazaarEntry.IMPORT_URI, id);

                intent.setData(currentUri);

                startActivity(intent);

            }
        });

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String lastUpdateDbQuery = "SELECT MAX(" +BazaarEntry.COLUMN_IMPORT_DATE + ") FROM "
                + BazaarEntry.IMPORT_TABLE_NAME;
        Log.d("lastUpdateDbQuery",lastUpdateDbQuery);
        Cursor lastUpdateDbQueryCursor = db.rawQuery(lastUpdateDbQuery,null);

        if(lastUpdateDbQueryCursor.moveToFirst()){
            String lastUpdated = lastUpdateDbQueryCursor.getString(0);
            lastUpdatedData.setText(lastUpdated);
        }

        getActivity().getSupportLoaderManager().initLoader(IMPORT_LOADER, null, this);

        return rootView;
        
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                BazaarEntry.IMPORT_ID,
                BazaarEntry.COLUMN_IMPORT_DATE,
                BazaarEntry.COLUMN_IMPORT_PRICE,
                BazaarEntry.COLUMN_IMPORT_QUANTITY,
                BazaarEntry.COLUMN_IMPORT_TOTAL_PRICE
        };

        return new CursorLoader(getActivity(),
                BazaarEntry.IMPORT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mImportCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
mImportCursorAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        getLoaderManager().restartLoader(0, null, this);
    }
}
