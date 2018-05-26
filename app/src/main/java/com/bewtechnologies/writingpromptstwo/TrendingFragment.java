package com.bewtechnologies.writingpromptstwo;

/**
 * Created by ab on 11/11/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

/**
 * A placeholder fragment containing a simple view.
 */
public  class TrendingFragment extends Fragment implements View.OnClickListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<WritingPrompt> myDataset =new ArrayList<WritingPrompt>() ;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_RANDOM_PROMPT = "prompt_number";


    private int data_position=0;

    private WritingPrompt datasetToSet;

    private Button nextButton,prevButton,surpriseMeButton;

    private ProgressDialog mPD ;


    public Context mContext;

    public KonfettiView konfettiView;


    int show_random=0;
    public TrendingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TrendingFragment newInstance(int sectionNumber, int show_random) {
        TrendingFragment fragment = new TrendingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putInt(ARG_RANDOM_PROMPT,show_random);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(MainActivity.hasInternet)
        {
            mPD=new ProgressDialog(getActivity());
            mPD.setMessage("Generating ideas, creating prompts!");
            mPD.show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trending, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);

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

        if(MainActivity.hasInternet)
        {
            //get show_random, if 1 showRandomPrompt;
            show_random= getArguments().getInt(ARG_RANDOM_PROMPT);
            mReadDataOnce("WP",show_random);


            nextButton= (Button) rootView.findViewById(R.id.next);
            nextButton.setOnClickListener(this);

            prevButton= (Button) rootView.findViewById(R.id.prev);
            prevButton.setOnClickListener(this);


            surpriseMeButton=(Button) rootView.findViewById(R.id.surprise_me_btn);
            surpriseMeButton.setOnClickListener(this);

            konfettiView = (KonfettiView)rootView.findViewById(R.id.viewKonfetti);


        }
        else
        {
            showNoInternetCard();

        }


        rootView.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SubmitPromptActivity.class);
                startActivity(i);
            }
        });



        return rootView;
    }

    private void showNoInternetCard() {

        datasetToSet=new WritingPrompt();
        datasetToSet.setTitle("");
        datasetToSet.setContent("No internet connection. If internet is connected, please restart the app. If not, meanwhile you can still read the saved writing prompts! :)");
            mAdapter=new MyAdapter(datasetToSet,mContext);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void mReadDataOnce(String wp, int show_random) {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref = database.child(wp);


            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //datasetToSet=dataSnapshot.getValue(WritingPrompt.class);

                    onSuccess(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    onFailed(databaseError);

                }
            });



    }


    @Override
    public void onClick(View v) {

        int id= v.getId();

        switch (id)
        {
            case R.id.next:
                data_position++;
                if(data_position== myDataset.size())
                {
                    data_position=0;
                    Log.i("Tag", "onClick1: "+data_position);
                }
                showPrompt(data_position);
                break;

            case R.id.prev:
                data_position--;
                if(data_position< 0)
                {
                    data_position=myDataset.size()-1;
                    Log.i("Tag", "onClick2: "+data_position);

                }
                showPrompt(data_position);
                break;


            case R.id.surprise_me_btn:
                showRandomPrompt();

                break;
        }




    }

    private void showRandomPrompt() {
        showConfetti();
        Toast.makeText(getContext(),"size"+myDataset.size(),Toast.LENGTH_LONG).show();
        Random r = new Random();
        int num = r.nextInt(myDataset.size()-1) ;

        data_position=num;
        showPrompt(data_position);


    }

    private void showConfetti() {
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                .setDirection(50.0, 109.0)
                .setSpeed(1f, 30f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5f))
                .setPosition(0f, konfettiView.getWidth() + 50f, 0f, -50f)
                .burst(300);
    }

    private void showPrompt(int data_position) {
        datasetToSet=null;
        datasetToSet=myDataset.get(data_position);
        mAdapter = new MyAdapter(datasetToSet,mContext);
        mRecyclerView.setAdapter(mAdapter);
    }


    public void onSuccess(DataSnapshot dataSnapshot) {
        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            datasetToSet= singleSnapshot.getValue(WritingPrompt.class);

            myDataset.add(datasetToSet);
//            Log.i("tag", "onDataChange: " +datasetToSet.title);
        }
       // Log.i(TAG, "onCreateView: "+datasetToSet);

        //set up UI;
        setUpUI(myDataset);

    }

    private void setUpUI(ArrayList<WritingPrompt> myDataset) {
        mAdapter = new MyAdapter(myDataset.get(data_position), mContext);
        mRecyclerView.setAdapter(mAdapter);
        mPD.dismiss();
        //if show_random =1, means opened by notif tap, then showRandom and surprise user.
        if(show_random == 1)
        {
            showRandomPrompt();
        }
    }


    public void onFailed(DatabaseError databaseError) {
            mPD.dismiss();
        Toast.makeText(getContext(), "Oops! Something went wrong, we're fixing it" +databaseError,Toast.LENGTH_LONG).show();
    }
}
