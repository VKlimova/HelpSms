package com.amargodigits.helpsms.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.amargodigits.helpsms.MainActivity;
import com.amargodigits.helpsms.model.PhoneReq;

import java.security.SecureRandom;
import java.util.UUID;

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
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_REQ_SMS_STATUS;
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
                + COLUMN_REQ_COUNT + " TEXT NOT NULL, "
                + COLUMN_REQ_SMS_STATUS + " TEXT NOT NULL"
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
                ReqEntry.COLUMN_REQ_COUNT,
                ReqEntry.COLUMN_REQ_SMS_STATUS
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
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_COUNT)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_SMS_STATUS))
                            )
                    );
                    Log.i(LOG_TAG, "makePhoneReqArrayFromSQLite: " + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_PHONE_NUMBER)) + "-" + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_ID))+ "-");
                } catch (Exception e) {
                    Log.i(LOG_TAG, "makePhoneReqArrayFromSQLite Exception: " + e.toString());
                }
                i++;
            }
        }

    /**
     * addPhoneReq insert the record with "phoneReq" to mDb
     * @Return the ReqID value, generated as random 64-based
     *
     * @param phoneReq -  phone Request data
     **/
    public static String addPhoneReq(PhoneReq phoneReq) {
        ContentValues cv = new ContentValues();
//        cv.put(COLUMN_REQ_ID, phoneReq.getReqId());
        SecureRandom srand = new SecureRandom();
        String txt=  srand.nextInt() +""+srand.nextInt();
        byte[] data = txt.getBytes();
        String base64 = Base64.encodeToString(data, Base64.URL_SAFE + Base64.NO_PADDING); ;
        String reqId = base64;
        cv.put(COLUMN_REQ_ID, reqId);
        cv.put(COLUMN_PHONE_NUMBER, phoneReq.getPhoneNumber());
        cv.put(COLUMN_MD5, phoneReq.getMds5());
        cv.put(COLUMN_DATE, phoneReq.getReqDate());
        cv.put(COLUMN_REQ_COUNT, phoneReq.getReqCount());
        cv.put(COLUMN_REQ_SMS_STATUS, phoneReq.getReqSmsStatus());
        Log.i(LOG_TAG, "Adding "+  phoneReq.getPhoneNumber() + " reqId=" + reqId);
        MainActivity.mDb.insert(ReqContract.ReqEntry.TABLE_NAME, null, cv);
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
        return reqId;
    }

    /**
     * addPhoneReq delete the record with "phoneReq" from mDb
     * @param phoneReqId -  phone Request Id
     **/

    public static void deletePhoneReqID(String phoneReqId) {
        Log.i(LOG_TAG, "Deleting "+  phoneReqId);
        String where = COLUMN_REQ_ID + "='" + phoneReqId+"'";
        Log.i(LOG_TAG, "where to delete =  " + where);
        MainActivity.mDb.delete(ReqContract.ReqEntry.TABLE_NAME, where, null );
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * addPhoneReq update  the record with "phoneReq" to mDb
     * @param phoneReqId -  phone Request Id
     **/
    public static void updatePhoneReqStatus(String phoneReqId, String phoneReqStatus) {
        Log.i(LOG_TAG, "Updating "+  phoneReqId + " to " + phoneReqStatus);
        String where = COLUMN_REQ_ID + "='" + phoneReqId+"'";
        Log.i(LOG_TAG, "where to update =  " + where);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REQ_SMS_STATUS, phoneReqStatus);
        MainActivity.mDb.update(ReqContract.ReqEntry.TABLE_NAME, cv, where, null );
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
    }


}

