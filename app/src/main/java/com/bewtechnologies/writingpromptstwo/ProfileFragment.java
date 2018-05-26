package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
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
    private RecyclerView profile_recyclerView;
    private FirebaseAuth mAuth;


    private String userImageURL =null,userName=null;


    private ArrayList <UserPrompts> userPromptList = new ArrayList<>();
    private GeneralPromptAdapter mAdapter;

    public FloatingActionButton addPromptFAB;


    private boolean isOtherProfile=false; //default own profile


    private String userID;
    private boolean isProfile =true; //default own profile
    private static Context staticContext;

    final ArrayList<String> promptKeys = new ArrayList<String>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
        staticContext = mContext;
        if (getArguments() != null) {
            //get args here if any.

            isOtherProfile=getArguments().getBoolean("isOtherProfile");
            if(isOtherProfile)
            {
                userID=getArguments().getString("userID");
                isProfile=false; //because isOtherProfile is true;
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView= inflater.inflate(R.layout.fragment_profile,container,false);

        profile_recyclerView= (RecyclerView) rootView.findViewById(R.id.profile_recycler_view);

        View profile_detailsCard = (View) rootView.findViewById(R.layout.profile_details_top_card);

        final ImageView user_image = (ImageView) rootView.findViewById(R.id.userImage);

        final TextView user_name= (TextView) rootView.findViewById(R.id.userName);

        Button logoutBtn = rootView.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"logout",Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                getActivity().finish();
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        profile_recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        profile_recyclerView.setLayoutManager(mLayoutManager);

        //save context for db in MyAdapter;
        mContext=getContext();

        //get current user image and name and set it on top card view
        mAuth = FirebaseAuth.getInstance();

        final FirebaseHandler mFirebaseHandler = new FirebaseHandler();
        if(!isOtherProfile)
        {
            //if own profile


            final FirebaseUser currentUser = mFirebaseHandler.getCurrentUser();

            userID= currentUser.getUid();
        }





        DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild(getString(R.string.users_node_firebase)).child(userID);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                User userFromFirebase= dataSnapshot.getValue(User.class);

//                Toast.makeText(mContext,"Here here"+userFromFirebase.getUserName(), Toast.LENGTH_SHORT).show();
                if (userFromFirebase != null) {
                    userImageURL=userFromFirebase.getUserImageURL();
                    Glide.with(mContext).load(userImageURL).into(user_image);
                    userName=userFromFirebase.getUserName();
                    user_name.setText(userName);

                }
                else
                {
                    user_name.setText("No name found. Login again.");
                }

                DataSnapshot userPromptSnapshot= dataSnapshot.child("UserPrompts");
                final Iterable <DataSnapshot> userPrompt=userPromptSnapshot.getChildren();


                Map<String, String> tempMapOfUserPrompts= new HashMap<String, String>();


                //Toast.makeText(mContext,"Here rar ! "+userPrompt.toString(),Toast.LENGTH_SHORT).show();

                //get promptIDs
                for(DataSnapshot prompt :userPrompt)
                {

                    tempMapOfUserPrompts.put(prompt.getKey(),prompt.getValue().toString());
                    //Toast.makeText(mContext,"Here in temp  : "+tempMapOfUserPrompts + " user "+userFromFirebase,Toast.LENGTH_SHORT).show();
                    Log.i("Here ", "temp: "+tempMapOfUserPrompts + " user "+userFromFirebase);

                }

                userFromFirebase.setUserPrompts(tempMapOfUserPrompts);
                //Toast.makeText(mContext, "user method called!" +userFromFirebase.getUserPrompts(), Toast.LENGTH_SHORT).show();
                getPromptsAndAssignAdapter(userFromFirebase,mFirebaseHandler);









            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),"Some Error Occured. Please restart the app",Toast.LENGTH_SHORT).show();
            }
        });


        addPromptFAB = (FloatingActionButton)rootView.findViewById(R.id.floatingActionButton);
        addPromptFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SubmitPromptActivity.class);
                startActivityForResult(i,1);
            }
        });

        profile_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && addPromptFAB.getVisibility() == View.VISIBLE) {
                    addPromptFAB.hide();
                } else if (dy < 0 && addPromptFAB.getVisibility() != View.VISIBLE) {
                    addPromptFAB.show();
                }
            }
        });

        rootView.findViewById(R.id.admin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent adminactivity = new Intent(mContext,AdminActivity.class);
                startActivity(adminactivity);
            }
        });

        return rootView;
    }

    private void getPromptsAndAssignAdapter(final User userFromFirebase, FirebaseHandler mFirebaseHandler) {
        //getPrompts to display from "Prompts"



        DatabaseReference promptRef = mFirebaseHandler.getDatabaseReferenceOfChild(getString(R.string.prompts_node_firebase));

        promptRef.orderByChild("time/time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userPromptList=new ArrayList<>();
                Toast.makeText(mContext, "Assign adapter called "+userPromptList, Toast.LENGTH_SHORT).show();
                Log.i("adapter ","getPromptsAndAssignAdapter: " +userPromptList);
                for(DataSnapshot promptSnapshot : dataSnapshot.getChildren())
                {
                    Set<String> promptKeysByUser = userFromFirebase.getUserPrompts().keySet();

                    for(String key : promptKeysByUser)
                    {
                       // Toast.makeText(mContext,"Here rar ! "+promptSnapshot.getKey() + " keys by user : " +key,Toast.LENGTH_SHORT).show();
                                /*Log.i("here", "onDataChange: "+promptSnapshot.getKey());
                                Log.i("here", "onDataChange1: "+key);*/

                        if(promptSnapshot.getKey().equals(userFromFirebase.getUserPrompts().get(key)))
                        {
                            //Toast.makeText(mContext,"Here rar ! "+promptSnapshot.child("userPrompt").getValue(),Toast.LENGTH_SHORT).show();
                            userPromptList.add(promptSnapshot.getValue(UserPrompts.class));
                            promptKeys.add(userFromFirebase.getUserPrompts().get(key));
                        }
                    }
                }
                //set adapter
                Log.i("Profile ", " onDataChange: "+isProfile);
              //  Toast.makeText(mContext, "Promptkeys "+promptKeys, Toast.LENGTH_SHORT).show();
                mAdapter= new GeneralPromptAdapter(userPromptList,mContext,isProfile,isOtherProfile,userFromFirebase,promptKeys); //true= set up profile prompt cards.
                profile_recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void removePrompt(final User mUser, final String userPromptKey) {

        FirebaseHandler mFirebaseHandler = new FirebaseHandler();
        final DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild("Prompts").child(userPromptKey);

        Toast.makeText(staticContext, "here remove prompt" + userPromptKey, Toast.LENGTH_SHORT).show();

        Log.i("here remove prompt "," "+userPromptKey);

        mDatabaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                Set<String> keys = mUser.getUserPrompts().keySet();

                String keyOfValueToBeRemoved =null;
                for(String key : keys)
                {
                    if(mUser.getUserPrompts().get(key).equals(userPromptKey)) {
                        keyOfValueToBeRemoved=key;
                    }
                }

                if(keyOfValueToBeRemoved!=null)
                {
                    mUser.getUserPrompts().remove(keyOfValueToBeRemoved);
                    removePromptIDFromUserInFirebase(keyOfValueToBeRemoved);
                }


                // Toast.makeText(mContext, "Removed prompt", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static void removePromptIDFromUserInFirebase(String keyToBeRemoved) {

        FirebaseHandler mFirebaseHandler = new FirebaseHandler();
        final DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild("Users").child(mFirebaseHandler.getCurrentUserID()).child("UserPrompts").child(keyToBeRemoved);

        mDatabaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(staticContext, "Removed prompt!", Toast.LENGTH_SHORT).show();

            }
        });


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
