package com.kiusoftech.bazaar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ExportFragment extends Fragment {

    ListView exportList;
    ArrayList<String> exportArrayList;
    ArrayAdapter<String> exportAdapter;


    public ExportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_export, container, false);

        exportList = (ListView) rootView.findViewById(R.id.listExport);

        exportArrayList = new ArrayList<>();


        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportArrayList.add("15000");
        exportAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, exportArrayList);

        exportList.setAdapter(exportAdapter);



        return rootView;
    }

}
