package com.kiusoftech.bazaar;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.kiusoftech.bazaar.data.BazaarContract;
import com.kiusoftech.bazaar.data.BazaarDbHelper;

import java.util.ArrayList;
import com.kiusoftech.bazaar.data.BazaarContract.BazaarEntry;

public class ExportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG ="Export Fragment" ;
    ListView exportListView;
    TextView lastUpdatedData;
    public BazaarDbHelper mDbHelper;
    public static final int EXPORT_LOADER = 0;

    ExportCursorAdapter mExportCursorAdapter;


    public ExportFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_export, container, false);

        mDbHelper = new BazaarDbHelper(getActivity());

        exportListView = (ListView) rootView.findViewById(R.id.listExport);
        lastUpdatedData = (TextView) rootView.findViewById(R.id.lastUpdatedDataTextView);

        mExportCursorAdapter = new ExportCursorAdapter(getActivity(), null);

        exportListView.setAdapter(mExportCursorAdapter);

        exportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ExportEditorActivity.class);

                Uri currentExportUri = ContentUris.withAppendedId(BazaarEntry.EXPORT_URI, id);

                intent.setData(currentExportUri);

                startActivity(intent);
            }
        });

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String lastUpdateDbQuery = "SELECT MAX(" +BazaarEntry.COLUMN_EXPORT_DATE + ") FROM "
                + BazaarEntry.EXPORT_TABLE_NAME;
        Log.d("lastUpdateDbQuery",lastUpdateDbQuery);
        Cursor lastUpdateDbQueryCursor = db.rawQuery(lastUpdateDbQuery,null);

        if(lastUpdateDbQueryCursor.moveToFirst()){
            String lastUpdated = lastUpdateDbQueryCursor.getString(0);
            lastUpdatedData.setText(lastUpdated);
        }

        getActivity().getSupportLoaderManager().initLoader(EXPORT_LOADER, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BazaarEntry.EXPORT_ID,
                BazaarEntry.COLUMN_EXPORT_DATE,
                BazaarEntry.COLUMN_EXPORT_PRICE,
                BazaarEntry.COLUMN_EXPORT_QUANTITY
        };

        return new CursorLoader(getActivity(),
                BazaarEntry.EXPORT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mExportCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    mExportCursorAdapter.swapCursor(null);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        getLoaderManager().restartLoader(0, null, this);
    }
}
