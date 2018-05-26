package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ab on 10/04/18.
 */

public class FirebaseHandler {




    public DatabaseReference getDatabaseReferenceOfChild(String childname)
    {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        return firebaseDatabase.getReference().child(childname);

    }

    public FirebaseUser getCurrentUser()
    {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getCurrentUserID()
    {
        return getCurrentUser().getUid();
    }


    public boolean checkIfUserExistsInFirebaseDB()
    {
       final boolean[] userExists = {false}; //user doesn't exists, by default
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child("Users").child(getCurrentUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    //user exists
                    Log.i("ref ", "checkIfUserExistsInFirebaseDB: "+dataSnapshot.exists());

                    userExists[0] =true;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return userExists[0];
    }

    public void putUserDetailsInFirebaseDB(FirebaseUser user)
    {
        User newUser= new User();
        newUser.setUserName(user.getDisplayName());
        newUser.setUserEmail(user.getEmail());
        newUser.setUserImageURL(user.getPhotoUrl().toString());

        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("Users").child(getCurrentUserID()).setValue(newUser);

    }

    public void loadMorePrompts(Context mContext, GeneralPromptAdapter mAdapter, ProgressBar mProgressBar, DatabaseReference mDatabaseReference, ArrayList<UserPrompts> userPromptsArrayList, String lastPostLoadedTimestamp) {
        // Toast.makeText(mContext,"Here hear", Toast.LENGTH_SHORT).show();


    }
}
