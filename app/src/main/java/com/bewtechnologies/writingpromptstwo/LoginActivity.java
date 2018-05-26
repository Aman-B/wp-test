package com.bewtechnologies.writingpromptstwo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private FirebaseHandler mFirebaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                );
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
            }
        });

        //intialize my firebase handler class
        mFirebaseHandler= new FirebaseHandler();


        //if user is already logged in, don't show login page, go to prompts directly.
        if(mFirebaseHandler.getCurrentUser()!=null)
        {
            Intent onlineIntent = new Intent(LoginActivity.this, OnlinePromptsActivity.class);
            startActivity(onlineIntent);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Toast.makeText(getApplicationContext(),"hlllooo",Toast.LENGTH_SHORT).show();
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user= mFirebaseHandler.getCurrentUser();
                String firebaseUserUID= mFirebaseHandler.getCurrentUserID();

                Toast.makeText(getApplicationContext(),"uid "+firebaseUserUID,Toast.LENGTH_SHORT).show();
                Log.i("", ""+user.getDisplayName());
                Toast.makeText(getApplicationContext(),""+mFirebaseHandler.checkIfUserExistsInFirebaseDB(),Toast.LENGTH_LONG).show();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                ref.child("Users").child(mFirebaseHandler.getCurrentUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {

                            //user exists
                            Toast.makeText(getApplicationContext(), "Welcome back, "+user.getDisplayName() + "!",Toast.LENGTH_SHORT).show();



                        }
                        else
                        {
                            mFirebaseHandler.putUserDetailsInFirebaseDB(user);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"Something went wrong. Please Sign-In again!",Toast.LENGTH_LONG).show();
                    }
                });

                Intent onlineIntent = new Intent(LoginActivity.this, OnlinePromptsActivity.class);
                startActivity(onlineIntent);
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }

}
