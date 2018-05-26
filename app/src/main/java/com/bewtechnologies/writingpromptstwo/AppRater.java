package com.bewtechnologies.writingpromptstwo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ab on 26/12/17.
 */

public class AppRater {
    private final static String APP_TITLE = "Writing Prompts";// App Name
    private final static String APP_PNAME = "com.bewtechnologies.writingprompts";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 1;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 1;//Min number of launches

    public static void app_launched(Context mContext, int random_prompt) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                //If mainactivity is opened by notification dont show ratedialog, it's annoying.
                if(random_prompt!=1)
                {
                    showRateDialog(mContext, editor);
                }

            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Rate " + APP_TITLE);

//        LinearLayout ll = new LinearLayout(mContext);
//        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(mContext);
        tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        //ll.addView(tv);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.apprater, null);

        Button b1 = (Button) promptView.findViewById(R.id.button3);

        b1.setText("Rate " +"now" );
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
            }
        });
//        ll.addView(b1);

        Button b2 = (Button) promptView.findViewById(R.id.button2);
        b2.setText("Remind me later");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        ll.addView(b2);

        Button b3 = (Button) promptView.findViewById(R.id.button4);
        b3.setText("No, thanks");
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
//        ll.addView(b3);

        dialog.setContentView(promptView);
        dialog.show();
    }
}
