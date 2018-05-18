package com.amargodigits.helpsms.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amargodigits.helpsms.MainActivity;
import com.amargodigits.helpsms.model.PhoneReq;

import static com.amargodigits.helpsms.MainActivity.LOG_TAG;
import static com.amargodigits.helpsms.MainActivity.mAdapter;
import static com.amargodigits.helpsms.MainActivity.mDb;
import static com.amargodigits.helpsms.MainActivity.mGridview;
import static com.amargodigits.helpsms.MainActivity.reqList;
import static com.amargodigits.helpsms.data.ReqContract.*;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_DATE;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_MD5;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_PHONE_NUMBER;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_REQ_COUNT;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_REQ_ID;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.TABLE_NAME;

public class PhoneReqDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "phonereq.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public PhoneReqDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_REQ_ID + " TEXT NOT NULL, "
                + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
                + COLUMN_MD5 + " TEXT NOT NULL, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_REQ_COUNT + " TEXT NOT NULL "
                + "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    /**
     * Fills in the movieList[] array, and returns the length of the array
     */
    public static void makePhoneReqArrayFromSQLite(SQLiteDatabase sqLiteDatabase) {
// This projection  specifies which columns from the database
// we will actually use in this query.
        reqList.clear();
        String[] projection = {
                ReqEntry.COLUMN_REQ_ID,
                ReqEntry.COLUMN_PHONE_NUMBER,
                ReqEntry.COLUMN_MD5,
                ReqEntry.COLUMN_DATE,
                ReqEntry.COLUMN_REQ_COUNT
        };

        String sortOrder = ReqEntry.COLUMN_DATE + " DESC";
        int i = 0;
        Cursor cursor = sqLiteDatabase.query(ReqEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
            while (cursor.moveToNext()) {
                try {
                    reqList.add(new PhoneReq(
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_ID)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_PHONE_NUMBER)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_MD5)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_DATE)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_COUNT))
                            )
                    );
                    Log.i(LOG_TAG, "makePhoneReqArrayFromSQLite: " + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_PHONE_NUMBER)));
                } catch (Exception e) {
                    Log.i(LOG_TAG, "makePhoneReqArrayFromSQLite Exception: " + e.toString());
                }
                i++;
            }
        }

    /**
     * addPhoneReq insert the record with "phoneReq" to mDb
     *
     * @param phoneReq -  phone Request data
     **/
    public static void addPhoneReq(PhoneReq phoneReq) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REQ_ID, phoneReq.getReqId());
        cv.put(COLUMN_PHONE_NUMBER, phoneReq.getPhoneNumber());
        cv.put(COLUMN_MD5, phoneReq.getMds5());
        cv.put(COLUMN_DATE, phoneReq.getReqDate());
        cv.put(COLUMN_REQ_COUNT, phoneReq.getReqCount());
        Log.i(LOG_TAG, "Adding "+  phoneReq.getPhoneNumber());
        MainActivity.mDb.insert(ReqContract.ReqEntry.TABLE_NAME, null, cv);
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
//        mGridview.setAdapter(mAdapter);
    }
    }

