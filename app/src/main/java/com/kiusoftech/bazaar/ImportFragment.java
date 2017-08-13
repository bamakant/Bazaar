package com.kiusoftech.bazaar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ImportFragment extends Fragment {

    ListView importList;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;

    public ImportFragment() {// Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_import, container, false);

        importList = (ListView) rootView.findViewById(R.id.listImport);

        arrayList = new ArrayList<>();

        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");
        arrayList.add("5000");



        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,arrayList);

        importList.setAdapter(arrayAdapter);



        return rootView;
    }

}
