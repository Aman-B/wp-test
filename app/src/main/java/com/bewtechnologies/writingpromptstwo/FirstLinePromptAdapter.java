package com.bewtechnologies.writingpromptstwo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ab on 14/03/18.
 */

public class FirstLinePromptAdapter extends RecyclerView.Adapter<FirstLinePromptAdapter.ViewHolder>{
        private WritingPrompt mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder

        private Context mContext;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView card_title;
            public TextView card_content;

            public  TextView card_save,card_share;

            public ViewHolder(View v) {
                super(v);
                card_title = (TextView) v.findViewById(R.id.title);
                card_content = (TextView) v.findViewById(R.id.content);
                card_save = (TextView) v.findViewById(R.id.save);
                card_save.setVisibility(View.INVISIBLE);
                card_share=(TextView)v.findViewById(R.id.share);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public FirstLinePromptAdapter(WritingPrompt myDataset, Context mContext) {
            mDataset = myDataset;
            this.mContext=mContext;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FirstLinePromptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view

            View v=
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_row,parent,false);        // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {


            final DBHelper mDBHelper = new DBHelper(mContext);
            final SQLiteDatabase mSqLiteDatabase= mDBHelper.getWritableDatabase();        // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.card_title.setText(mContext.getString(R.string.firstlineprompt_card_title,mDataset.getTitle()));
            holder.card_content.setText(mDataset.getContent());
           // holder.card_share.setVisibility(View.INVISIBLE);



            //TODO : set saving mechanism here. And do it quick.

           /* holder.card_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WritingPrompt wp = new WritingPrompt();
                    wp.setTitle(mDataset.getTitle());
                    wp.setContent(mDataset.getContent());
                    mDBHelper.addWritingPrompt("fl_wp",wp,mSqLiteDatabase);
                    MainActivity.newData=true;
                    Toast.makeText(mContext, "Prompt saved for later!",Toast.LENGTH_SHORT).show();
                    holder.card_save.setText("SAVED");

                }
            });*/

            holder.card_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textToShare = "See this amazing writing prompt : \n\n\""+mDataset.getContent()+"\" \n \nShared via app: WritingPrompts \n(Download here: https://goo.gl/yojsHx )";
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Writing Prompt!");
                    sendIntent.putExtra(Intent.EXTRA_TEXT,textToShare);
                    sendIntent.setType("text/html");
                    mContext.startActivity(Intent.createChooser(sendIntent,"Share the prompt!"));
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return 1;
        }
    }



