package com.bewtechnologies.writingpromptstwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ab on 14/11/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="Quiz.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DBContract.DBEntry.COLUMN_NAME_CONTENT + TEXT_TYPE +  " )";

    private static final String FL_SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.DBEntry.FL_TABLE_NAME + " (" +
                    DBContract.DBEntry._ID + " INTEGER PRIMARY KEY," +
                    DBContract.DBEntry.FL_COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DBContract.DBEntry.FL_COLUMN_NAME_CONTENT + TEXT_TYPE +  " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBContract.DBEntry.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(FL_SQL_CREATE_ENTRIES);

    }





    public void addWritingPrompt(String tableName,WritingPrompt wp,SQLiteDatabase sqLiteDatabase) {
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBContract.DBEntry.COLUMN_NAME_TITLE, wp.getTitle());
        values.put(DBContract.DBEntry.COLUMN_NAME_CONTENT, wp.getContent());

// Inserting Row
        sqLiteDatabase.insert(tableName, null, values);

    }

    public void removeWritingPrompt(String title,SQLiteDatabase sqLiteDatabase) {
            // Define 'where' part of query.
            String selection = DBContract.DBEntry.COLUMN_NAME_TITLE + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { title };
            // Issue SQL statement.
            sqLiteDatabase.delete(DBContract.DBEntry.TABLE_NAME, selection, selectionArgs);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }
}
