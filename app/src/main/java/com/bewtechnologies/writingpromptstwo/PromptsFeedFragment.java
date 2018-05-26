package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ab on 11/04/18.
 */

public class PromptsFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LinearLayoutManager mLayoutManager;
    private Context mContext;
    private RecyclerView promptsfeed_recyclerView;
    private FirebaseAuth mAuth;


    private String userImageURL =null,userName=null;


    private ArrayList <UserPrompts> userPromptsArrayList = new ArrayList<>();
    private GeneralPromptAdapter mAdapter;

    public FloatingActionButton addPromptFAB;


    private ProgressBar mProgressBar;


    private Long lastPostLoadedTimestamp;


    public PromptsFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PromptsFeedFragment newInstance() {
        PromptsFeedFragment fragment = new PromptsFeedFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //get args here if any.
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView= inflater.inflate(R.layout.fragment_promptsfeed,container,false);

        promptsfeed_recyclerView = (RecyclerView) rootView.findViewById(R.id.promptsfeed_recycler_view);

        View profile_detailsCard = (View) rootView.findViewById(R.layout.profile_details_top_card);

        final ImageView user_image = (ImageView) rootView.findViewById(R.id.userImage);

        final TextView user_name= (TextView) rootView.findViewById(R.id.userName);

        mProgressBar= rootView.findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.INVISIBLE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        promptsfeed_recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        promptsfeed_recyclerView.setLayoutManager(mLayoutManager);

        //save context for db in MyAdapter;
        mContext=getContext();

        //get current user image and name and set it on top card view
        mAuth = FirebaseAuth.getInstance();

        //my firebase handler
        final FirebaseHandler mFirebaseHandler = new FirebaseHandler();


        final FirebaseUser currentUser = mAuth.getCurrentUser();





        final DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild(getString(R.string.prompts_node_firebase));

        addPromptFAB = (FloatingActionButton)rootView.findViewById(R.id.floatingActionButton);
        addPromptFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SubmitPromptActivity.class);
                startActivityForResult(i,1);
            }
        });

        //TODO : Fix this

       mDatabaseReference.orderByChild("time/time").limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot promptSnapshot : dataSnapshot.getChildren())
                {
                    userPromptsArrayList.add(promptSnapshot.getValue(UserPrompts.class));
                }

                    if(userPromptsArrayList.size()!=0)
                    {
                        lastPostLoadedTimestamp=userPromptsArrayList.get(userPromptsArrayList.size()-1).getTimeValue();

                    }

                    Toast.makeText(mContext,"last! "+lastPostLoadedTimestamp,Toast.LENGTH_SHORT).show();


                //Toast.makeText(mContext,"Some error occurred. Please try again! "+lastPostLoadedTimestamp,Toast.LENGTH_SHORT).show();

                //success
                mAdapter= new GeneralPromptAdapter(userPromptsArrayList,mContext,false, false);
                promptsfeed_recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext,"Some error occurred. Please try again! "+databaseError,Toast.LENGTH_SHORT).show();
            }
        });






        promptsfeed_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addPromptFAB.getVisibility() == View.VISIBLE) {
                    addPromptFAB.hide();
                } else if (dy < 0 && addPromptFAB.getVisibility() != View.VISIBLE) {
                    addPromptFAB.show();

                }


                if(!recyclerView.canScrollVertically(1))
                {
                    //Toast.makeText(mContext,"Called scroll function",Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.VISIBLE);
                   // Toast.makeText(mContext,"last! "+lastPostLoadedTimestamp,Toast.LENGTH_SHORT).show();

                    //TODO : Fix this too.

                    mDatabaseReference.orderByChild("time/time").startAt(lastPostLoadedTimestamp).limitToFirst(3).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot promptSnapshot : dataSnapshot.getChildren())
                            {
                                if(!(lastPostLoadedTimestamp ==promptSnapshot.getValue(UserPrompts.class).getTimeValue()))
                                {
                                    userPromptsArrayList.add(promptSnapshot.getValue(UserPrompts.class));
                                }
                                else
                                {
                                    Toast.makeText(mContext,"That's all, we've got for now!", Toast.LENGTH_SHORT).show();
                                }

                            }

                            lastPostLoadedTimestamp=(userPromptsArrayList.get(userPromptsArrayList.size()-1).getTimeValue());
                            //  Toast.makeText(mContext,"Some error occurred. Please try again! "+lastPostLoadedTimestamp,Toast.LENGTH_SHORT).show();
                            //success
                            mAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(mContext,"Some error occurred. Please try again! "+databaseError,Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK)
        {
            // Toast.makeText(mContext,"Got it!",Toast.LENGTH_SHORT).show();
            //  Boolean isPromptSubmitted = data.getBooleanExtra("isSuccess",false);

            //reset adapter
            if (mListener != null) {
                mListener.onFragmentInteraction();
            }



        }
        else
        {
            Toast.makeText(getContext(),"Some error occured while submitting prompt. Please try again!",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           /* throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction();
    }
}
