package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ab on 17/01/18.
 */

public class SubmitPromptAdapter  extends RecyclerView.Adapter<SubmitPromptAdapter.ViewHolder>{
    private ArrayList<WritingPrompt> mDataset;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

    private Context mContext;

    private SubmitPromptFragment.OnSignInButtonClickedListener onSignInButtonClickedListener;

    public String userEmail,userPassword;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public EditText user_email;
        public EditText user_password;

        public  TextView card_delete,card_share;

        public ViewHolder(View v) {
            super(v);

            user_email= (EditText) v.findViewById(R.id.userEmail);

            user_password= (EditText) v.findViewById(R.id.userPassword);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SubmitPromptAdapter(ArrayList<WritingPrompt> myDataset, Context mContext) {
        mDataset = myDataset;
        this.mContext=mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SubmitPromptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view

        View v=
                LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_submit_prompt,parent,false);        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //TODO: Add the clicklistener here
        userEmail=holder.user_email.getText().toString();
        userPassword = holder.user_email.getText().toString();

        onSignInButtonClickedListener.onSignInButtonClicked(userEmail,userPassword);



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
//        Log.i("tag", "getItemCount: "+mDataset.size());

        return mDataset.size();
    }
}



