package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AdminActivity extends AppCompatActivity {

    //TODO : Simplify this and admincardadapter. NOW!

    TextView tv,userName;

    RecyclerView promptRecylerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<UserPrompts> userPromptList ;
    private ArrayList<String> userPromptKeysList ;
    private User user;
    private long userCount;
    private long count;
    private AdminCardAdapter mAdapter;
    private int userIndex =0;

    private  int currentUserIndex=0;

    private boolean isThereAPromptPendingForThisUser = false;

    final Map<User, ArrayList<UserPrompts>> userWithPrompts = new HashMap<>();

    final Map<User, ArrayList<String>> userWithPromptIDs = new HashMap<>();

    final Map<String, String> promptsByUser = new HashMap<>();

    private int counttoast =0;


    private static String sendMailTo = "";

    private static String SEND_MAIL_FROM = "writingpromptsapp@gmail.com";

    private static final String  SEND_MAIL_SUBJECT= "Prompts submitted by you have been reviewed.";

    static StringBuilder  stringBuilder = new StringBuilder();

    private static String sendMailText="Hello!";



    /*
    * In this class, a HashMap is used inside the method which iterates
    * over firebase data and adds prompts corresponding to a user.
    * Links the prompts to user. HashMap used is called : userWithPrompts <User,ArrayList<UserPrompts>>
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tv= findViewById(R.id.textView3);

        userName= findViewById(R.id.userName);
        promptRecylerView=findViewById(R.id.to_approve_prompts_recyclerView);


        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        promptRecylerView.setLayoutManager(mLayoutManager);

        findViewById(R.id.nextUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(AdminActivity.this, "prompts "+userWithPrompts.size() + "userIndex "+ userIndex, Toast.LENGTH_SHORT).show();
                if(userIndex < userWithPrompts.size()-1) //if it's equal to userWithPrompts.size(), then you get an outOfBoundException.
                {
                    if(userIndex !=userWithPrompts.size())
                        currentUserIndex=userIndex++;

                    User muser = (User)userWithPrompts.keySet().toArray()[currentUserIndex];

                    ArrayList<UserPrompts> userPrompts = userWithPrompts.get(muser);

                    ArrayList<String> userPromptsKeys =userWithPromptIDs.get(muser);


                    //Toast.makeText(AdminActivity.this, "promptkeys "+userPromptsKeys, Toast.LENGTH_SHORT).show();

                    mAdapter=new  AdminCardAdapter(muser,userPrompts,userPromptsKeys);
                    promptRecylerView.setAdapter(mAdapter);
                    //mAdapter.notifyDataSetChanged();

                }
                else
                {
                    Toast.makeText(AdminActivity.this, "No next user left to go back to my man.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.prev_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(AdminActivity.this, "prompts "+userWithPrompts.size() + "userIndex "+ userIndex, Toast.LENGTH_SHORT).show();
                if(userIndex !=0)//if it's less than 0, then you get an outOfBoundException.
                {

                    currentUserIndex=userIndex--;

                    User muser = (User)userWithPrompts.keySet().toArray()[currentUserIndex];

                    ArrayList<UserPrompts> userPrompts = userWithPrompts.get(muser);

                    ArrayList<String> userPromptsKeys =userWithPromptIDs.get(muser);


                   // Toast.makeText(AdminActivity.this, "promptkeys "+userPromptsKeys, Toast.LENGTH_SHORT).show();

                    mAdapter=new  AdminCardAdapter(muser,userPrompts,userPromptsKeys);
                    promptRecylerView.setAdapter(mAdapter);
                    //mAdapter.notifyDataSetChanged();

                }
                else
                {
                    Toast.makeText(AdminActivity.this, "No user left to go back to.", Toast.LENGTH_SHORT).show();
                }

            }
        });




        final FirebaseHandler mFirebaseHandler = new FirebaseHandler();
        DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild(getString(R.string.users_node_firebase));

        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userCount=dataSnapshot.getChildrenCount();
                count=0;
                for(DataSnapshot userDataSnapshot : dataSnapshot.getChildren())
                {

                    //keep track of user count
                    count++;


                    //reset is prompt pending for this user;
                    isThereAPromptPendingForThisUser =false;


                    //create new userPromptList for every user.
                    userPromptList = new ArrayList<>();
                    userPromptKeysList=new ArrayList<String>();

                    user=userDataSnapshot.getValue(User.class);






                    Map<String, String> tempMapOfUserPrompts= new HashMap<String, String>();

                    // Toast.makeText(AdminActivity.this, "init user name : "+user.getUserName(), Toast.LENGTH_SHORT).show();
                    DataSnapshot userPromptSnapshot= userDataSnapshot.child("UserPrompts"); //returns random key and val which is PromptID.




                    final Iterable <DataSnapshot> userPrompt=userPromptSnapshot.getChildren();

                    final ArrayList<String> promptKeys = new ArrayList<String>();

                    //Toast.makeText(mContext,"Here rar ! "+userPrompt.toString(),Toast.LENGTH_SHORT).show();

                    //get promptIDs
                    for(DataSnapshot prompt :userPrompt)
                    {

                        tempMapOfUserPrompts.put(prompt.getKey(),prompt.getValue().toString());
                        promptKeys.add(prompt.getValue().toString());


                    }

                    user.setUserPrompts(tempMapOfUserPrompts);
                   // Toast.makeText(AdminActivity.this, "user method called!", Toast.LENGTH_SHORT).show();
                    getPromptsAndAssignAdapter(user,promptKeys,count);

                    Set<String> keys= null;
                    if (user != null && user.getUserPrompts()!=null) {
                        keys = user.getUserPrompts().keySet();
                        for(String key : keys)
                        {
                           // Toast.makeText(AdminActivity.this, "Get userPrompts "+key + " value " + user.getUserPrompts().get(key) + "prompt key "+promptKeys, Toast.LENGTH_SHORT).show();
                            Log.i("admin", "onDataChange :  Key = "+key+ " value " + user.getUserPrompts().get(key));
                        }
                    }



                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        findViewById(R.id.sendMail_btn).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 //add regards and end of email.

                 //deep linked to : www.writingprompts.com/online_wp
                 stringBuilder.append("The approved prompts will be visible in the app now. Check it out <a href='www.writingpromptsapp.com/online_wp'> in the app here.</a> <br>");
                 stringBuilder.append("Regards, <br>");
                 stringBuilder.append("The Writing Prompts Team");


                 SendEmailASyncTask task = new SendEmailASyncTask(AdminActivity.this,
                         sendMailTo,
                         SEND_MAIL_FROM,
                         SEND_MAIL_SUBJECT,
                         stringBuilder.toString(),
                         null,
                         null,tv);
                 task.execute();
             }
        });

        stringBuilder.append(sendMailText).append("<br><br>").append(" We are pleased to inform you that we have reviewed the prompts submitted by you : ").append("<br><br>");


    }

    private void getPromptsAndAssignAdapter(User user, final ArrayList<String> promptKeys, final long count) {

        //getPrompts to display from "Prompts"

        final User gotUser =user;
        final FirebaseHandler mFirebaseHandler = new FirebaseHandler();


        DatabaseReference promptRef = mFirebaseHandler.getDatabaseReferenceOfChild(getString(R.string.prompts_node_firebase));


       /* promptRef.orderByChild("time/time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Toast.makeText(AdminActivity.this, "datasnapshot"+dataSnapshot.getValue(UserPrompts.class).getUserPrompt(), Toast.LENGTH_SHORT).show();
                promptRecylerView.notify();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        promptRef.orderByChild("time/time").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //create new userPromptList for every user.
                userPromptList = new ArrayList<>();
                userPromptKeysList=new ArrayList<String>();
                for(DataSnapshot promptSnapshot : dataSnapshot.getChildren())
                {

                    for(String key : promptKeys)
                    {
                        //Toast.makeText(mContext,"Here rar ! "+promptSnapshot.getKey(),Toast.LENGTH_SHORT).show();
                                /*Log.i("here", "onDataChange: "+promptSnapshot.getKey());
                                Log.i("here", "onDataChange1: "+key);*/

                        if(promptSnapshot.getKey().equals(key))
                        {
//                                        Toast.makeText(getApplicationContext(),"Here rar ! "+promptSnapshot.child("userPrompt").getValue(),Toast.LENGTH_SHORT).show();

                            UserPrompts userPrompts =promptSnapshot.getValue(UserPrompts.class);

                            if(userPrompts.getisPending())
                            {
                               // Toast.makeText(AdminActivity.this, "is user there "+UserPrompts.getUserPrompt()+" user name : "+user.getUserName(), Toast.LENGTH_SHORT).show();

                                isThereAPromptPendingForThisUser =true;
                                userPromptList.add(userPrompts);
                                userPromptKeysList.add(key);

                            }

                        }
                    }
                }
                //set adapter
                       /* mAdapter= new GeneralPromptAdapter(userPromptList,mContext,isProfile,isOtherProfile); //true= set up profile prompt cards.
                        profile_recyclerView.setAdapter(mAdapter);*/

                if(isThereAPromptPendingForThisUser)
                {
                    //add user with promptlist if this user has pending prompts, if not userpromptlist is empty, so need of adding this user.
                   // Toast.makeText(AdminActivity.this, "is pending prompt there "+ gotUser.getUserName(), Toast.LENGTH_SHORT).show();
                    userWithPrompts.put(gotUser,userPromptList);
                    //add prompt keys(promptIDs) corresponding to each user
                    userWithPromptIDs.put(gotUser,userPromptKeysList);
                    isThereAPromptPendingForThisUser=false;
                    

                }
                if(count == userCount)
                {
                    setIndexOfUser();
                    setAdapterWithData();
                }

            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    private void setIndexOfUser() {



            //TODO: Put this somewhere after all the data loads.
                                /*Set<String> keys= userWithPrompts.keySet();
                                String val="values : ";

                                for (String key : keys) {

                                    UserPrompts up =(UserPrompts) ((ArrayList)userWithPrompts.get(key)).get(0);
                                    Toast.makeText(getApplicationContext(),"Here userprompt "+up.getUserPrompt(),Toast.LENGTH_SHORT).show();

                                    val += ((ArrayList)userWithPrompts.get(key)).size();

                                }
                                userName.setText(val);
                                Toast.makeText(AdminActivity.this, "children "+dataSnapshot.hasChildren() + " count val ", Toast.LENGTH_SHORT).show();
                                */

            //Toast.makeText(AdminActivity.this, "userIndex : "+ userIndex + " count "+ count +" userCount : "+userCount, Toast.LENGTH_SHORT).show();
            Log.i("admin", "onDataChange: userIndex "+ userIndex + " count "+count);

            if(userIndex == userWithPrompts.size())
            {
                //if reloaded, show the prompt which was previously shown.
                userIndex=currentUserIndex;
            }

            currentUserIndex=userIndex++;




    }

    private void setAdapterWithData()
    {

        User mUser = getCurrentUserObject();


        ArrayList<UserPrompts> userPrompts = getListOfPromptsForCurrentUser(mUser);
        ArrayList<String> userPromptsKeys = getListOfPromptIDsForCurrentUser(mUser);


        // Toast.makeText(AdminActivity.this, "prompkeys"+userPromptsKeys.size() + " count is "+ count, Toast.LENGTH_SHORT).show();

        if(userPromptsKeys.size()!=0)
        {
            mAdapter=new  AdminCardAdapter(mUser,userPrompts,userPromptsKeys);
            promptRecylerView.setAdapter(mAdapter);

            //  mAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<String> getListOfPromptIDsForCurrentUser(User mUser) {
        return userWithPromptIDs.get(mUser);
    }

    private ArrayList<UserPrompts> getListOfPromptsForCurrentUser(User mUser) {
        return userWithPrompts.get(mUser);
    }

    private User getCurrentUserObject() {

        return (User)userWithPrompts.keySet().toArray()[userIndex];
    }

    public static void buildEmail(boolean isApproved, User mUser, String userPrompt) {

        //sendMailText+="\n\n We are pleased to inform you that we have reviewed the prompts submitted by you : " ;

        //stringBuilder.append("\n\n");



        sendMailTo=mUser.getUserEmail();


        if(isApproved)
        {
            stringBuilder.append("Your prompt \"").append(userPrompt).append("\" is <strong>approved! </strong>:) ").append("<br><br>");
        }
        else
        {
            stringBuilder.append(" Your prompt \"").append(userPrompt).append("\" is rejected. We are sorry about it, you can submit once more for through the app for review.").append("<br><br>");

        }




    }


    /**
     * ASyncTask that composes and sends email
     */
    private static class SendEmailASyncTask extends AsyncTask<Void, Void, Void> {

        private Context mAppContext;
        private String mMsgResponse;

        private String mTo;
        private String mFrom;
        private String mSubject;
        private String mText;
        private Uri mUri;
        private String mAttachmentName;

        TextView tv;

        public SendEmailASyncTask(Context context, String mTo, String mFrom, String mSubject,
                                  String mText, Uri mUri, String mAttachmentName,TextView tv) {
            this.mAppContext = context.getApplicationContext();
            this.mTo = mTo;
            this.mFrom = mFrom;
            this.mSubject = mSubject;
            this.mText = mText;
            this.mUri = mUri;
            this.mAttachmentName = mAttachmentName;
            this.tv=tv;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                SendGrid sendgrid = new SendGrid("amanb20", "itsagain123");

                SendGrid.Email email = new SendGrid.Email();


                // Get values from edit text to compose email
                email.addTo(mTo);
                email.setFrom(mFrom);
                email.setSubject(mSubject);
                email.setText(mText);
                email.setHtml(mText);


                // Attach image
                if (mUri != null) {
                    email.addAttachment(mAttachmentName, mAppContext.getContentResolver().openInputStream(mUri));
                }

                // Send email, execute http request
                SendGrid.Response response = sendgrid.send(email);
                mMsgResponse = response.getMessage();

                Log.d("SendAppExample", mMsgResponse);

            } catch (SendGridException | IOException e) {
                Log.e("SendAppExample", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tv.setText(mMsgResponse);
            Toast.makeText(mAppContext, mMsgResponse, Toast.LENGTH_SHORT).show();
        }
    }
}
