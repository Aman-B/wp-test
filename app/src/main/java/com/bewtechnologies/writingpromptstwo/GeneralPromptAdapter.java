package com.bewtechnologies.writingpromptstwo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by ab on 08/04/18.
 */

public class GeneralPromptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

    private ArrayList<String> mDataset;
    private Context mContext;

    private int STATIC_CARD_TYPE=0;

    private  Boolean isProfile =false; //by default it's feed.

    private Boolean isOtherProfile= false; //if showing other's profile, this is true.

    //for promtps feed
    private ArrayList<UserPrompts> mUserPromptsArrayList;
    private  ArrayList<String> userPromptKeys;


    private User mUser;

    private static final int Head = 1;
    private static final int List = 0;


    public static class ViewHolderList extends RecyclerView.ViewHolder {
        private final ImageView card_userImage;
        private final TextView card_username;
        // each data item is just a string in this case
        public TextView prompt_content;
        public TextView card_content;


        public  TextView card_delete,card_share,card_date;

        public ViewHolderList(View v) {
            super(v);
            prompt_content = (TextView) v.findViewById(R.id.prompt);
            card_userImage = (ImageView) v.findViewById(R.id.userImage); //userimage with the prompt which is on top left;
            card_username=(TextView) v.findViewById(R.id.user_name);     //username also on top left;
            card_share=(TextView) v.findViewById(R.id.share);
            card_delete=v.findViewById(R.id.delete);
            card_date=v.findViewById(R.id.date_tv);


        }
    }

    public static class ViewHolderHead extends RecyclerView.ViewHolder {

        private  TextView profileName;
        // each data item is just a string in this case
        public TextView prompt_content;
        public TextView card_content;

        public ImageView profileImage;

        public  TextView card_delete,card_share,card_date;

        public ViewHolderHead(View v) {
            super(v);


            /*View profile_detailsCard = (View) v.findViewById(R.layout.profile_details_top_card);*/

             profileImage = (ImageView) v.findViewById(R.id.userImage);

             profileName= (TextView) v.findViewById(R.id.userName);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    //For profile fragment.
   /* public GeneralPromptAdapter(ArrayList<String> myDataset, Context mContext,Boolean isProfile) {
        mDataset = myDataset;
        this.mContext=mContext;

        this.isProfile=isProfile;
    }*/

    //for prompts feed.
    public GeneralPromptAdapter(ArrayList<UserPrompts> myDataset, Context mContext, boolean isProfile, boolean isOtherProfile, User userFromFirebase, ArrayList<String> promptKeys) {
        mUserPromptsArrayList = myDataset;
        this.mContext=mContext;


        this.isProfile=isProfile;
        this.isOtherProfile=isOtherProfile;

        mUser=userFromFirebase;
        userPromptKeys= promptKeys;
    }


    public GeneralPromptAdapter(ArrayList<UserPrompts> myDataset, Context mContext, boolean isProfile, boolean isOtherProfile) {

        mUserPromptsArrayList = myDataset;
        this.mContext=mContext;


        this.isProfile=isProfile;
        this.isOtherProfile=isOtherProfile;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view


            View v =null;

            if(isProfileOrOtherProfile())
            {
                switch (viewType)
                {
                    case Head:
                        return new ViewHolderHead(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_head_card_view,parent,false));

                    case List:
                        return  new ViewHolderList(LayoutInflater.from(parent.getContext()).inflate(R.layout.userprompts_card_view,parent,false));

                }

            }

                return new ViewHolderList(LayoutInflater.from(parent.getContext()).inflate(R.layout.userprompts_card_view,parent,false));


            /*View v=
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.userprompts_card_view,parent,false); */       // set the view's size, margins, paddings and layout parameters

           /* ViewHolder vh = new ViewHolder(v);
            return vh;*/




    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final DBHelper mDBHelper = new DBHelper(mContext);
        final SQLiteDatabase mSqLiteDatabase= mDBHelper.getWritableDatabase();





        // Toast.makeText(mContext, "Here in profile "+isProfile, Toast.LENGTH_SHORT).show();


        if(isProfile)
        {
            if(getItemViewType(position)==Head)
            {
                ViewHolderHead viewHolderHead= (ViewHolderHead) holder;

                Glide.with(mContext).load(mUser.getUserImageURL()).into(viewHolderHead.profileImage);
                viewHolderHead.profileName.setText(mUser.getUserName());

            }

            else {
                // - get element from your dataset at this position
                // - replace the contents of the view with that element

                final int newPostion =position-1;
                ViewHolderList viewHolderList = (ViewHolderList) holder;
                Toast.makeText(mContext, "size "+mUserPromptsArrayList.size(), Toast.LENGTH_SHORT).show();
                Log.i("profile", "getItemCount: "+mUserPromptsArrayList.size());
                final UserPrompts userPrompt= mUserPromptsArrayList.get(newPostion);
                viewHolderList.prompt_content.setText(userPrompt.getUserPrompt());
                viewHolderList.card_userImage.setVisibility(View.INVISIBLE);

                viewHolderList.card_username.setVisibility(View.INVISIBLE);
                viewHolderList.card_date.setText(userPrompt.getTimeDifference(userPrompt));

                final String userPromptKey = userPromptKeys.get(newPostion);

                viewHolderList.card_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("general ", "onBindViewHolder: "+userPromptKeys.get(newPostion));

                        AlertDialog rejectAlertDialog = createDialog(mUser, userPromptKey,newPostion);
                        rejectAlertDialog.show();

                    }
                });

                viewHolderList.card_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String textToShare = "See this amazing writing prompt : \n\n\""+userPrompt.getUserPrompt()+"\" \n \nShared via app: WritingPrompts \n(Download here: https://goo.gl/yojsHx )";
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Writing Prompt!");
                        sendIntent.putExtra(Intent.EXTRA_TEXT,textToShare);
                        sendIntent.setType("text/html");
                        mContext.startActivity(Intent.createChooser(sendIntent,"Share the prompt!"));
                    }
                });
            }

        }

        else if(isOtherProfile)
        {
            if(getItemViewType(position)==Head)
            {
                ViewHolderHead viewHolderHead= (ViewHolderHead) holder;

                Glide.with(mContext).load(mUser.getUserImageURL()).into(viewHolderHead.profileImage);
                viewHolderHead.profileName.setText(mUser.getUserName());

            }

            else {
                // Toast.makeText(mContext, "Here in others", Toast.LENGTH_SHORT).show();
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                ViewHolderList viewHolderList = (ViewHolderList) holder;
                final UserPrompts userPrompt = mUserPromptsArrayList.get(position-1); //head is first one now, so removing 1 from position.
                viewHolderList.prompt_content.setText(userPrompt.getUserPrompt());
                viewHolderList.card_userImage.setVisibility(View.INVISIBLE);

                viewHolderList.card_username.setVisibility(View.INVISIBLE);
                viewHolderList.card_date.setText(userPrompt.getTimeDifference(userPrompt));

                viewHolderList.card_delete.setVisibility(View.INVISIBLE);

                viewHolderList.card_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String textToShare = "See this amazing writing prompt : \n\n\"" + userPrompt.getUserPrompt() + "\" \n \nShared via app: WritingPrompts \n(Download here: https://goo.gl/yojsHx )";
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Writing Prompt!");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                        sendIntent.setType("text/html");
                        mContext.startActivity(Intent.createChooser(sendIntent, "Share the prompt!"));
                    }
                });
            }
        }
        else
        {
            //showing feed prompts
            ViewHolderList viewHolderList = (ViewHolderList) holder;
            final UserPrompts userPrompt = mUserPromptsArrayList.get(position);
            viewHolderList.card_delete.setVisibility(View.INVISIBLE);

            viewHolderList.prompt_content.setText(userPrompt.getUserPrompt());

            SpannableString content = new SpannableString(userPrompt.getUserName());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

            viewHolderList.card_username.setText(content);

            viewHolderList.card_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext,PromptsBySpecificUserActivity.class);
                    intent.putExtra("userID",userPrompt.getUserID());
                    mContext.startActivity(intent);

                }
            });

            Glide.with(mContext).load(userPrompt.getUserImageURL()).into(viewHolderList.card_userImage);

            viewHolderList.card_date.setText(userPrompt.getTimeDifference(userPrompt));
            viewHolderList.card_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textToShare = "See this amazing writing prompt : \n\n\""+userPrompt.getUserPrompt()+"\" \n \nShared via app: WritingPrompts \n(Download here: https://goo.gl/yojsHx )";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Writing Prompt!");
                    sendIntent.putExtra(Intent.EXTRA_TEXT,textToShare);
                    sendIntent.setType("text/html");
                    mContext.startActivity(Intent.createChooser(sendIntent,"Share the prompt!"));
                }
            });

        }



      /*  Log.i("tag", "onBindViewHolder: "+mDataset.size());

        Log.i("tag", "onBindViewHolder: "+position);

        Log.i("tag", "onBindViewHolder: "+mDataset.get(position).getTitle());*/


       /* holder.card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog mPD = new ProgressDialog(mContext);

                mPD.show();
                mDBHelper.removeWritingPrompt(mDataset.get(position),mSqLiteDatabase);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mPD.dismiss();
                Toast.makeText(mContext, "Prompt removed!", Toast.LENGTH_SHORT).show();


            }
        });
*/





    }

    private AlertDialog createDialog(final User mUser, final String userPromptKey, final int position) {


        String  message="Are you sure you want to delete prompt?",
                positiveBtnMessage= "Yes, please delete it!",
                negativeBtnMessage="No, stop. Don't delete it.";





        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(message)
                .setPositiveButton(positiveBtnMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                           mUserPromptsArrayList.remove(mUserPromptsArrayList.get(position));
                          // notifyItemRemoved(position);
                           ProfileFragment.removePrompt(mUser,userPromptKey);


                    }
                })
                .setNegativeButton(negativeBtnMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                });

        return alertBuilder.create();

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        Log.i("tag", "getItemCount: "+mDataset.size());
         if(isProfileOrOtherProfile())
         {

             return mUserPromptsArrayList.size()+1;
         }
         else
         {
             return mUserPromptsArrayList.size();

         }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {

        if(isProfileOrOtherProfile())
          return (position == 0? Head : List);
        else
          return  position;
    }

    public  boolean isProfileOrOtherProfile()
    {
        return isProfile||isOtherProfile;
    }


}


