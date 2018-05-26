package com.bewtechnologies.writingpromptstwo;

/**
 * Created by ab on 14/11/17.
 */

import android.provider.BaseColumns;



public class DBContract implements BaseColumns {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.

    private DBContract()
    {

    }
    /* Inner class that defines the table contents */
    public static class DBEntry implements BaseColumns {
        public static final String TABLE_NAME = "wp";
        public static final String COLUMN_NAME_TITLE="title";
        public static final String COLUMN_NAME_CONTENT="content";


        public static final String FL_TABLE_NAME = "fl_wp";
        public static final String FL_COLUMN_NAME_TITLE="title";
        public static final String FL_COLUMN_NAME_CONTENT="content";


    }





}