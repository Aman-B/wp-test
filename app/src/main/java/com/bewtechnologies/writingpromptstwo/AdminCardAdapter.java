package com.bewtechnologies.writingpromptstwo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by ab on 19/05/18.
 */

public class AdminCardAdapter extends RecyclerView.Adapter<AdminCardAdapter.ViewHolder>  {


    private final User mUser;
    private final ArrayList<String> mPromptKeysArrayList;
    private Context mContext;
    private ArrayList<UserPrompts> mUserPromptsArrayList;

    private boolean isApproved=false;

    //for prompts feed.
    /*public AdminCardAdapter(Map<User, ArrayList<UserPrompts>> userWithPrompts, Context context) {
        this.userWithPrompts = userWithPrompts;
        mContext=context;




    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView card_userImage;
        private final TextView card_username;
        private  TextView card_email,card_accept,card_edit,card_reject;
        // each data item is just a string in this case
        public TextView prompt_content;
        public TextView card_content;


        public  TextView card_delete,card_share,card_date;

        public ViewHolder(View v) {
            super(v);
            prompt_content = (TextView) v.findViewById(R.id.prompt);
            card_userImage = (ImageView) v.findViewById(R.id.userImage);
            card_username=(TextView) v.findViewById(R.id.user_name);
            card_date=v.findViewById(R.id.date_tv);
            card_email=v.findViewById(R.id.email);
            card_accept=v.findViewById(R.id.accept_prompt);
            card_edit=v.findViewById(R.id.edit_prompt);
            card_reject=v.findViewById(R.id.reject_prompt);



        }
    }

    public AdminCardAdapter(User muser, ArrayList<UserPrompts> userPrompts, ArrayList<String> promptKeys) {
        this.mUser=muser;
        mUserPromptsArrayList=userPrompts;
        mPromptKeysArrayList=promptKeys;
       // mContext=applicationContext;

    }


    @Override
    public AdminCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v=
                LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_prompt_card,parent,false);        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        mContext=parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(AdminCardAdapter.ViewHolder holder, int position) {
//showing feed prompts


        final UserPrompts userPrompt = mUserPromptsArrayList.get(position);
        final String userPromptKey = mPromptKeysArrayList.get(position);

        final int indexOfChangedPrompt = position;


        holder.card_email.setText(mUser.getUserEmail());

        holder.prompt_content.setText(userPrompt.getUserPrompt());

       /* SpannableString content = new SpannableString(userPrompt.getUserName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);*/

        holder.card_username.setText(userPrompt.getUserName());
/*
        holder.card_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,PromptsBySpecificUserActivity.class);
                intent.putExtra("userID",userPrompt.getUserID());
                mContext.startActivity(intent);

            }
        });*/

        Glide.with(mContext).load(userPrompt.getUserImageURL()).into(holder.card_userImage);

        holder.card_date.setText(userPrompt.getTimeDifference(userPrompt));

        holder.card_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isApproved=true;
                AlertDialog acceptAlertDialog = createDialog(isApproved,mUser,userPromptKey,userPrompt.getUserPrompt());
                acceptAlertDialog.show();


                Toast.makeText(mContext, "Yet to be accepted!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.card_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isApproved=false;
                AlertDialog rejectAlertDialog = createDialog(isApproved, mUser, userPromptKey,userPrompt.getUserPrompt());
                rejectAlertDialog.show();

                Toast.makeText(mContext, "Yet to be rejected!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.card_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mContext, "Yet to be edited!", Toast.LENGTH_SHORT).show();
                AlertDialog alertDialog = createEditPrompAlertDialog(mContext,userPrompt,userPromptKey,indexOfChangedPrompt);
                alertDialog.show();


            }
        });


    }

    private AlertDialog createDialog(final boolean isApproved, final User mUser, final String userPromptKey, final String userPrompt) {

        FirebaseHandler mFirebaseHandler = new FirebaseHandler();
        final DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild("Prompts").child(userPromptKey);

        String  message="Are you sure you want to delete prompt?",
                positiveBtnMessage= "Yes, nuke it!!!",
                negativeBtnMessage="No, stop, don't delete it.";

        if(isApproved)
        {
            message="Are you sure you want to approve prompt?";
            positiveBtnMessage="Yes, Approve it!!";
            negativeBtnMessage="No, wait, don't approve it!";

        }



        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(message)
                .setPositiveButton(positiveBtnMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isApproved)
                        {

                            AdminActivity.buildEmail(isApproved, mUser,userPrompt);

                            //uncomment this later.
                           /* mDatabaseReference.child("isApproved").setValue(true, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    //remove from pending list.
                                    mDatabaseReference.child("isPending").setValue(false);
                                    Toast.makeText(mContext, "Accepted prompt", Toast.LENGTH_SHORT).show();

                                }
                            });*/

                        }
                        else
                        {
                            AdminActivity.buildEmail(isApproved, mUser,userPrompt);


                            //uncomment this later.
                           /* mDatabaseReference.removeValue(new DatabaseReference.CompletionListener() {
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
                                        removePromptIDInUserInFirebase(keyOfValueToBeRemoved);
                                    }


                                    Toast.makeText(mContext, "Removed prompt", Toast.LENGTH_SHORT).show();
                                }
                            });*/
                        }

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

    private void removePromptIDInUserInFirebase(String keyToBeRemoved) {

        FirebaseHandler mFirebaseHandler = new FirebaseHandler();
        final DatabaseReference mDatabaseReference = mFirebaseHandler.getDatabaseReferenceOfChild("Users").child(mFirebaseHandler.getCurrentUserID()).child("UserPrompts").child(keyToBeRemoved);

        mDatabaseReference.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(mContext, "Removed prompt from user reference also!", Toast.LENGTH_SHORT).show();

            }
        });
       /* mDatabaseReference.setValue(userPrompts.getUserPrompts(),new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Toast.makeText(mContext, "Removed prompt from user reference also!", Toast.LENGTH_SHORT).show();

            }
        });*/

    }


    private AlertDialog createEditPrompAlertDialog(final Context mContext, UserPrompts userPrompt, String userPromptKey, final int indexOfChangedPrompt) {

        FirebaseHandler mFirebaseHandler = new FirebaseHandler();
       final DatabaseReference mref= mFirebaseHandler.getDatabaseReferenceOfChild("Prompts/"+userPromptKey+"/userPrompt");

        final Query query = mFirebaseHandler.getDatabaseReferenceOfChild("Prompts/"+userPromptKey+"/userPrompt");

        query.keepSynced(true);

        AlertDialog.Builder alertBuilder= new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext.getApplicationContext());
        View customView= inflater.inflate(R.layout.edit_prompt_dialog,null);

        final EditText editText=customView.findViewById(R.id.et_editPrompt);
        editText.setText(userPrompt.getUserPrompt());



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertBuilder.setView(customView);
        }
        alertBuilder.setCancelable(false)
                    .setPositiveButton("Done Editing!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String changedPromptText =editText.getText().toString();
                            mref.setValue(changedPromptText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(mContext, "Done!", Toast.LENGTH_SHORT).show();
                                    //((AdminActivity)mContext).updateList(indexOfChangedPrompt,changedPromptText);

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "Failed to update prompt!", Toast.LENGTH_LONG).show();

                                }
                            });


                        }
                    })
                    .setNegativeButton("Discard Edit.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           // Toast.makeText(mContext, "Discarded!", Toast.LENGTH_SHORT).show();

                        }
                    });

        return alertBuilder.create();


    }

    @Override
    public int getItemCount() {
        return mUserPromptsArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
