package com.bewtechnologies.writingpromptstwo;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SubmitPromptFragment.OnSignInButtonClickedListener,
        FirstLinePromptFragment.OnFragmentInteractionListener{

    //for refreshing SavedPrompt tab everytime user saves a prompt.
    public static boolean newData=false;
    public static boolean hasInternet=true;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private AdView mAdView;
    int isRated=0;

    private FirebaseAuth mAuth;

    //(used as boolean 0 is dont show, 1 is showRandomPrompt)
    int random_prompt =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!isOnline())
        {
            if(FirebaseDatabase.getInstance()==null)
            {
                hasInternet=false;

            }
        }



        //If this is opened by notif, show randomprompt (used as boolean 0 is dont show, 1 is showRandomPrompt);
        try {
            if (getIntent().getExtras().getBoolean("by_notif")) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(1001);
                random_prompt=1;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), random_prompt);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);

        tabLayout.setupWithViewPager(mViewPager);*/




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Test ID =  ca-app-pub-3940256099942544~3347511713


        //My ID = ca-app-pub-7106690066422766~8580141957
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");

        mAdView =(AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
       // mAdView.loadAd(adRequest);

        //For "Rate app" dialog
        AppRater.app_launched(this,random_prompt);



        if(FirebaseDatabase.getInstance()!=null && savedInstanceState==null)
        {
            try{
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                FirebaseDatabase.getInstance().getReference().keepSynced(true);

            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "If prompts are not visible : Check your internet connection or restart the app.", Toast.LENGTH_LONG).show();


            }

        }

        //for firebase user signin
        mAuth = FirebaseAuth.getInstance();

        //testing notifications
        //setNotificationAlarm();



    }

    private void setNotificationAlarm() {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,18);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, AlarmManager.INTERVAL_DAY, pendingIntent);

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();


    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Uncomment this for menu at top right.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rate) {
            // Handle the rate action
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.bewtechnologies.writingprompts"); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);


        }   else if (id == R.id.nav_share) {

            String textToShare = "Check out this amazing app for writers: WritingPrompts \n(Download here: https://goo.gl/yojsHx )";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Writing Prompt!");
            sendIntent.putExtra(Intent.EXTRA_TEXT,textToShare);
            sendIntent.setType("text/html");
            getApplicationContext().startActivity(Intent.createChooser(sendIntent,"Share the app!"));

        }
        else if (id == R.id.online_community) {

            mAuth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser !=null)
            {
                Intent onlineIntent = new Intent(MainActivity.this, OnlinePromptsActivity.class);
                startActivity(onlineIntent);
            }
            else
            {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);

            }


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSignInButtonClicked(String userEmail, String userPassword) {

        //TODO: createUser if signUp or signIn if user exists.

        Toast.makeText(getApplicationContext(),"SignUp Button Clicked" + " email : "+userEmail+" password : "+userPassword,Toast.LENGTH_SHORT).show();

        createUserInFirebase(userEmail,userPassword);
    }

    private void createUserInFirebase(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), "User created!", Toast.LENGTH_SHORT).show();
                            //TODO: show SubmitPrompt page next.

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "createUserWithEmail:failure", task.getException());

                            Toast.makeText(getApplicationContext(),"Failed auth",Toast.LENGTH_SHORT).show();
                        }

                    }
                });




    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i("firstlineinteractio", "onFragmentInteraction: implemented!");
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_landing, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
*/            return rootView;
        }
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
                    return  TrendingFragment.newInstance(position, show_random);

                case 1:
                    return SavedPromptFragment.newInstance(position);
                case 2:
                    return SubmitPromptFragment.newInstance(position);

                case 3:
                    return  FirstLinePromptFragment.newInstance(position);

                default:
                    return TrendingFragment.newInstance(position, show_random);
            }


        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Writing Prompts";
                case 1:
                    return "Saved Prompts";
                case 2:
                    return "Submit Prompt";
                case 3:
                    return "First Line Prompts";
            }
            return null;
        }

       
    }


}
