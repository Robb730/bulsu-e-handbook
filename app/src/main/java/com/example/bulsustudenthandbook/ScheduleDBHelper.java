package com.example.bulsustudenthandbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "schedule.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "classes";
    public static final String COL_ID = "id";
    public static final String COL_CODE = "subject_code";
    public static final String COL_NAME = "subject_name";
    public static final String COL_DAY = "day";
    public static final String COL_TIME = "time";

    public ScheduleDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CODE + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_DAY + " TEXT, " +
                COL_TIME + " TEXT)";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
