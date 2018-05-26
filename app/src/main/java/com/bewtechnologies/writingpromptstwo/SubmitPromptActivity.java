package com.bewtechnologies.writingpromptstwo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubmitPromptActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String userImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_prompt);

        //Todo: make a submit prompt form here

        final EditText prompt = (EditText) findViewById(R.id.submitPrompt);
        Button submitPromptButton = (Button) findViewById(R.id.submitPromptBtn);



        mAuth = FirebaseAuth.getInstance();

        final String userID = mAuth.getCurrentUser().getUid();
        String userName = mAuth.getCurrentUser().getDisplayName();



        ArrayList<String> genres = new ArrayList<>();
        genres.add("fiction"); //keeping default as fiction, because most wil be fiction.


        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference ref = database.child("Prompts");

        final String key = ref.push().getKey();

        final UserPrompts userPrompt = new UserPrompts();


        userPrompt.setUserName(userName);
        userPrompt.setUserID(userID);
        userPrompt.setUpvotes(0);
        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("time", ServerValue.TIMESTAMP);
        userPrompt.setTime(timestampNow);
        userPrompt.setisDeleted(false);
        userPrompt.setisPending(true);
        userPrompt.setisApproved(false);
        userPrompt.setisReported(false);
        userPrompt.setGenre(genres);


        FirebaseHandler mFirebaseHandler = new FirebaseHandler();

        DatabaseReference userRef= mFirebaseHandler.getDatabaseReferenceOfChild("Users").child(userID).child(getString(R.string.user_image_url_firebase));

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    userImageURL= dataSnapshot.getValue().toString();
                    userPrompt.setUserImageURL(userImageURL);

                } catch (Exception e) {
                    e.printStackTrace();
                    userPrompt.setUserImageURL("");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //[Add this in later version, for genre]
        /*ArrayList <String> genres = new ArrayList<>();
        genres.add("fiction");
        genres.add("non-fiction");
        userPrompt.setGenre(genres);*/




        final Map<String, Object> childUpdates = new HashMap<>();

        submitPromptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userPrompt.setUserPrompt(prompt.getText().toString());

                Map<String, Object> userPrompt_values =userPrompt.toMap();

                childUpdates.put(key,userPrompt_values);

                //submit to root "Prompts"
                ref.updateChildren(childUpdates);

                //Submit to admin
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                DatabaseReference promptsRef = database.child("Prompts");




                promptsRef.updateChildren(childUpdates);




                //submit the prompt id (key) under the users' userID
                DatabaseReference userRef = database.child("Users");
                userRef.child(userID).child("UserPrompts").push().setValue(key, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError == null)
                        {
                            Toast.makeText(getApplicationContext(),"Submitted successfully!", Toast.LENGTH_SHORT).show();
                            submitNegativeTime(key);

                        }
                        else
                        {


                            setResult(RESULT_CANCELED);
                            finish();
                        }


                    }
                });




            }
        });
    }

    private void submitNegativeTime(final String key) {

        //first get time
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = database.child("Prompts").child(key);




        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    UserPrompts prompts = dataSnapshot.getValue(UserPrompts.class);
                    HashMap<String,Object> time = null;
                    if (prompts != null) {
                        time = prompts.getTime();
                        Toast.makeText(getApplicationContext(), "time value "+time.get("time").getClass(),Toast.LENGTH_LONG).show();
                        putNegativeTimeStampInFirebase(time,key);

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void putNegativeTimeStampInFirebase(HashMap<String, Object> time, String key) {
        Long newTime = (long)(-1) * (long)time.get("time");

        time.put("time",newTime);

        //put in Firebase

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = database.child("Prompts").child(key);

        userRef.child("time").setValue(time);
        setResult(RESULT_OK);
        finish();

    }
}
