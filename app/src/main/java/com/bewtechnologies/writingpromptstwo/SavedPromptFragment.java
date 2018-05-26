package com.bewtechnologies.writingpromptstwo;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by ab on 14/11/17.
 */

public class SavedPromptFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<WritingPrompt> myDataset =new ArrayList<WritingPrompt>() ;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int data_position=0;

    private WritingPrompt datasetToSet;

    private Button nextButton,prevButton;

    private ProgressDialog mPD ;


    public Context mContext;
    public SavedPromptFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SavedPromptFragment newInstance(int sectionNumber) {
        SavedPromptFragment fragment = new SavedPromptFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && MainActivity.newData) {

            Log.i("tag", "onResume: " + "Called");
            MainActivity.newData=false;
            myDataset.clear();
            getFragmentManager().beginTransaction().detach(this).commitNowAllowingStateLoss();

            getFragmentManager().beginTransaction().attach(this).commit();
        }

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  mPD=new ProgressDialog(getActivity());
        mPD.show();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_landing, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.saved_prompt_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        
        //save context for db in MyAdapter;
        mContext=getContext();

        // specify an adapter (see also next example)
/*
        WritingPrompt wp_temp = new WritingPrompt();
        wp_temp.title ="title 1";
        wp_temp.content ="content 1";
        myDataset.add(wp_temp);

        wp_temp.title ="title 2";
        wp_temp.content ="content 2";
        myDataset.add(wp_temp);

        datasetToSet=myDataset.get(data_position);*/

        mReadDataOnce();


        return rootView;
    }

    private void mReadDataOnce() {

        DBHelper mDbHelper= new DBHelper(getContext());
        SQLiteDatabase mSqLiteDatabase= mDbHelper.getReadableDatabase();

        Cursor mCursor= mSqLiteDatabase.rawQuery("select * from "+ DBContract.DBEntry.TABLE_NAME,null);



        if(mCursor.moveToFirst())
        {
            do
            {
                WritingPrompt wp = new WritingPrompt();
                wp.setTitle(mCursor.getString(1));
                wp.setContent(mCursor.getString(2));
                Log.i("Tag", "mReadDataOnce: "+mCursor.getString(1)+" "+mCursor.getString(2));
                myDataset.add(wp);

            }while(mCursor.moveToNext());


        }
        else
        {
            WritingPrompt wp = new WritingPrompt();
            wp.setTitle("");
            wp.setContent("No prompts saved yet! Save prompts to see them here.");

            myDataset.add(wp);
        }

        showPrompt();


    }




    private void showPrompt() {
        Log.i("tag", "showPrompt: "+myDataset);



        mAdapter = new SavedPromptAdapter(myDataset,mContext);
        mRecyclerView.setAdapter(mAdapter);


    }







}

