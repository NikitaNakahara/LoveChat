package com.android.lovechat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;


public class ChatDatabase extends SQLiteOpenHelper {
    private final String TABLE_NAME = "MESSAGES";
    private final String MSG_TEXT = "TEXT";
    private final String MSG_TYPE = "TYPE";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase writableDb = null;

    public ChatDatabase(@Nullable Context context) {
        super(context, "database.db", null, DATABASE_VERSION);
    }

    public void createWritableDb() {
        writableDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "(" +
                MSG_TEXT + " TEXT," +
                MSG_TYPE + " TEXT" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addMessage(String text, String type) {
        ContentValues map = new ContentValues();
        map.put(MSG_TEXT, text);
        map.put(MSG_TYPE, type);

        writableDb.insert(TABLE_NAME, null, map);
    }

    public Cursor getCursor() {
        return getReadableDatabase().rawQuery(
                "SELECT " + MSG_TEXT + ", " + MSG_TYPE + " FROM " + TABLE_NAME,
                null);
    }
}
