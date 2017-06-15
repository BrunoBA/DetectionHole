package com.sample.foo.usingawarenessapi;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.sql.Date;

/**
 * Created by artur on 14/06/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="holes.db";
    private static final int SCHEMA_VERSION=1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE restaurants (_id INTEGER PRIMARY KEY AUTOINCREMENT, x TEXT, y TEXT, creation_date TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // no-op, since will not be called until 2nd schema
        // version exists
    }

    public Cursor getAll() {
        return(getReadableDatabase()
                .rawQuery("SELECT _id, x, y, creation_date FROM holes ORDER BY creation_date",
                        null));
    }

    public void insert(Integer x, Integer y,
                       Date creation_date) {
        ContentValues cv= new ContentValues();

        cv.put("x", x);
        cv.put("y", y);
        cv.put("creation_date", creation_date.toString());

        getWritableDatabase().insert("holes", "x", cv);
    }

    public Integer getX(Cursor c) {
        return(c.getInt(1));
    }

    public Integer getY(Cursor c) {
        return(c.getInt(2));
    }

    public String getCreationDate(Cursor c) {
        return(c.getString(3));
    }

}
