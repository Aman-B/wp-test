package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSignInButtonClickedListener} interface
 * to handle interaction events.
 * Use the {@link SubmitPromptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubmitPromptFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public String userEmail,userPassword;

    public EditText user_email;
    public EditText user_password;

    public Context mContext;

    private OnSignInButtonClickedListener mListener;

    private DatabaseReference mDatabase;

    private ArrayList<WritingPrompt> myDataset  ;


    private WritingPrompt datasetToSet;


    private int prompt_position =0;

    private DailyPrompt dailyPrompt;

    private TextView dailyPromptView;


    public SubmitPromptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment SubmitPromptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubmitPromptFragment newInstance(int sectionNumber) {
        SubmitPromptFragment fragment = new SubmitPromptFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);

        fragment.setArguments(args);
        return fragment;
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
        View v= inflater.inflate(R.layout.fragment_submit_prompt, container, false);

        user_email=(EditText) v.findViewById(R.id.userEmail);
        user_password=(EditText) v.findViewById(R.id.userPassword);

        Button b= (Button) v.findViewById(R.id.signUpButton);
        b.setOnClickListener(this);

        dailyPromptView=(TextView) v.findViewById(R.id.promptView);

        final EditText et = (EditText) v.findViewById(R.id.editText);



        Button submitPrompt = (Button) v.findViewById(R.id.button5);
        submitPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String prompt= et.getText().toString();
                 dailyPrompt = new DailyPrompt(prompt);




                putInFireBase(prompt);
            }
        });


        // Add value event listener to the post
        // [START post_value_event_listener]
        mDatabase = FirebaseDatabase.getInstance().getReference("Daily_WP").child("0");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                DailyPrompt dp = dataSnapshot.getValue(DailyPrompt.class);

                dailyPromptView.setText(dp.content);
                Toast.makeText(getContext(),"content "+dp.content,Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        };
        mDatabase.addValueEventListener(postListener);
        // [END post_value_event_listener]

        return v;
    }

    private void putInFireBase(String prompt) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mReadDataOnce("WP");




    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
            if (context instanceof OnSignInButtonClickedListener) {
            mListener = (OnSignInButtonClickedListener) context;
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnSignInButtonClickedListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        userEmail=user_email.getText().toString();
        userPassword=user_password.getText().toString();

        Log.i("tag", "onClick: submit");
        Toast.makeText(mContext,"Here",Toast.LENGTH_SHORT).show();

        mListener.onSignInButtonClicked(userEmail,userPassword);
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
    public interface OnSignInButtonClickedListener {
        // TODO: Update argument type and name
        void onSignInButtonClicked(String userEmail, String userPassword);
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


    public void onSuccess(DataSnapshot dataSnapshot) {
       myDataset =new ArrayList<WritingPrompt>();

        for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            datasetToSet= singleSnapshot.getValue(WritingPrompt.class);

            myDataset.add(datasetToSet);

          Log.i("tag", "onDataChange: " +datasetToSet.content);
        }

        for(int i =0; i<myDataset.size();i++)
        {
            Log.i("hit", "onCreateView: "+myDataset.get(i).getContent());

        }

        Toast.makeText(getContext(), "Countsy : "+myDataset.size(),Toast.LENGTH_LONG).show();


        prompt_position=myDataset.size();

        updateFireBase(prompt_position);
    }

    private void updateFireBase(int prompt_position) {
        Map<String, Object> dp_values= dailyPrompt.toMap();

        int wp_position2= prompt_position+1;
        String wp_title2= Integer.toString(wp_position2);

        WritingPrompt writingPrompt = new WritingPrompt();
        writingPrompt.setContent(dailyPrompt.content);
        writingPrompt.setTitle(Integer.toString((prompt_position)));

        Map<String, Object> wp_values =writingPrompt.toMap();



        WritingPrompt writingPrompt2 = new WritingPrompt();
        writingPrompt2.setContent("More prompt every week or so.");
        writingPrompt2.setTitle(wp_title2);

        Map<String, Object> wp_values2 =writingPrompt2.toMap();



        Map<String, Object> childUpdates = new HashMap<>();

        Toast.makeText(getContext(),"val : "+prompt_position,Toast.LENGTH_SHORT).show();
        childUpdates.put("/Daily_WP/0",dp_values);


        childUpdates.put("/WP/"+Integer.toString((prompt_position-1)),wp_values);
        childUpdates.put("/WP/"+Integer.toString((prompt_position)),wp_values2);


        mDatabase.updateChildren(childUpdates);
        myDataset.clear();

        for(int i =0; i<myDataset.size();i++)
        {
            Log.i("hit second","onupdate "+myDataset.get(i).getContent());

        }


    }


    public void onFailed(DatabaseError databaseError) {
        Toast.makeText(getContext(), "Oops! Something went wrong, we're fixing it" +databaseError,Toast.LENGTH_LONG).show();
    }


}
