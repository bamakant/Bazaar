package com.kiusoftech.bazaar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ExpenseFragment extends Fragment {

    ListView expenseList;
    ArrayList<String> expenseArrayList;
    ArrayAdapter<String> expenseAdapter;

    public ExpenseFragment() {
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
        View rootView=inflater.inflate(R.layout.fragment_expense, container, false);

        expenseList = (ListView) rootView.findViewById(R.id.listExpense);

        expenseArrayList = new ArrayList<>();

        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");
        expenseArrayList.add("10000");

        expenseAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, expenseArrayList);

        expenseList.setAdapter(expenseAdapter);

        return rootView;
    }

}
