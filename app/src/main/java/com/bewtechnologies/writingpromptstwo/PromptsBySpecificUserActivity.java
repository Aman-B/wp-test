package com.bewtechnologies.writingpromptstwo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class PromptsBySpecificUserActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {


    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompts_by_specific_user);

        userID= getIntent().getStringExtra("userID");
        Toast.makeText(this, "UserID "+userID, Toast.LENGTH_SHORT).show();


        Fragment profileFragment = ProfileFragment.newInstance();

        Bundle bundle = new Bundle();
        boolean isOtherProfile = true;
        bundle.putBoolean("isOtherProfile", isOtherProfile);
        bundle.putString("userID",userID);

        profileFragment.setArguments(bundle);

        FragmentManager mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .replace(R.id.specific_profile_container,profileFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction() {

    }
}
