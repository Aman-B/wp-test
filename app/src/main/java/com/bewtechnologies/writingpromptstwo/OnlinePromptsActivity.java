package com.bewtechnologies.writingpromptstwo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OnlinePromptsActivity extends AppCompatActivity
        implements ProfileFragment.OnFragmentInteractionListener,
        PromptsFeedFragment.OnFragmentInteractionListener{

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    int random_prompt =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_prompts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), random_prompt);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);




        // Set the dimensions of the sign-in button.
       /* SignInButton signInButton = findViewById(R.id.sign_in_button);
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
        });*/


/*
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();

        User newUser= new User();

        newUser.setUserEmail("test@mail.com");
        newUser.setUserName("myName");
        newUser.setUserImageURL("some url");
*/



       /* mDatabaseReference.child("Users").child("OfMjhz8I4SV4S7yfm8DFH78Fg3c2").setValue(newUser);
        String key= mDatabaseReference.child("Users").child("F4JqaRczOfg63FXSRxpbqEw7P5D2").child("UserPrompts").push().getKey();
        Log.i("key", "onCreate: "+key);
        Toast.makeText(getApplicationContext(),"uid "+key,Toast.LENGTH_SHORT).show();
        //save a prompt in prompts
        mDatabaseReference.child("Prompts").child(key).setValue("This also a new prompt.");

        //save UserPrompts ID in UserPrompts
        mDatabaseReference.child("Users").child("F4JqaRczOfg63FXSRxpbqEw7P5D2").child("userPromptsID").child("0").setValue(key);*/


        //Toast.makeText(getApplicationContext(),"uid "+currentUser.getUid(),Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null)
        {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        Toast.makeText(getApplicationContext(),"You are : "+currentUser.getDisplayName(),Toast.LENGTH_SHORT).show();

    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       // Toast.makeText(getApplicationContext(),"hlllooo",Toast.LENGTH_LONG).show();
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Log.i("", ""+user.getDisplayName());
                Toast.makeText(getApplicationContext(),""+user.getEmail(),Toast.LENGTH_LONG).show();
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }*/

    @Override
    public void onFragmentInteraction() {
            Toast.makeText(getApplicationContext(), "Changing UI", Toast.LENGTH_SHORT).show();
            mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        //to or not to show random prompt; show_prompt if 1 then showRandomPrompt
        int show_random;

        public SectionsPagerAdapter(FragmentManager fm, int show_random) {
            super(fm);
            this.show_random =show_random;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position)
            {
                case 0:
                    return  ProfileFragment.newInstance();

                case 1:
                    return PromptsFeedFragment.newInstance();


                default:
                    return TrendingFragment.newInstance(position, show_random);
            }


        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Your Prompts";
                case 1:
                    return "Prompts Feed";

            }
            return null;
        }


    }
}
