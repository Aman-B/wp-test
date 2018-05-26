package com.bewtechnologies.writingpromptstwo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FirstLinePromptFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FirstLinePromptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstLinePromptFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
    private OnFragmentInteractionListener mListener;

    public FirstLinePromptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstLinePromptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstLinePromptFragment newInstance(String param1, String param2) {
        FirstLinePromptFragment fragment = new FirstLinePromptFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_first_line_prompt, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.first_line_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //save context for db in FirstLineAdapter;
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

            mReadDataOnce("FL_WP");


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

        return rootView;
    }

    private void showNoInternetCard() {

        datasetToSet=new WritingPrompt();
        datasetToSet.setTitle("");
        datasetToSet.setContent("No internet connection. If internet is connected, please restart the app. If not, meanwhile you can still read the saved writing prompts! :)");
        mAdapter=new FirstLinePromptAdapter(datasetToSet,mContext);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void mReadDataOnce(String wp) {

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
                showConfetti();
                Random r = new Random();
                Log.i("tag", "myDataset: "+myDataset.size());

                int num=0;
                if(myDataset.size() >3 )
                {
                    num = r.nextInt((myDataset.size()-2)) ;
                }
                else
                {
                    num=30;
                }


                data_position=num;

                try {
                    showPrompt(data_position);
                } catch (Exception e) {
                    e.printStackTrace();

                    showNoInternetCard();
                }
                break;
        }


    }

    private void showRandomPrompt() {
        showConfetti();
        Random r = new Random();
        int num = r.nextInt((myDataset.size()-2)) ;

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
        try {
            Log.i("read", "showPrompt: "+myDataset.get(1));
            datasetToSet = null;
            datasetToSet = myDataset.get(data_position);
            mAdapter = new FirstLinePromptAdapter(datasetToSet, mContext);
            mRecyclerView.setAdapter(mAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showNoInternetCard();
        }
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
        Toast.makeText(getContext(),"size"+myDataset.size(),Toast.LENGTH_LONG).show();


    }

    private void setUpUI(ArrayList<WritingPrompt> myDataset) {
        mAdapter = new FirstLinePromptAdapter(myDataset.get(data_position), mContext);
        mRecyclerView.setAdapter(mAdapter);
        mPD.dismiss();
        //if show_random =1, means opened by notif tap, then showRandom and surprise user.

    }


    public void onFailed(DatabaseError databaseError) {
        mPD.dismiss();
        Toast.makeText(getContext(), "Oops! Something went wrong, we're fixing it" +databaseError,Toast.LENGTH_LONG).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static Fragment newInstance(int position) {

        FirstLinePromptFragment firstLinePromptFragment= new FirstLinePromptFragment();
        return  firstLinePromptFragment;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
