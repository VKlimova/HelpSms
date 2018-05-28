package com.amargodigits.helpsms.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.amargodigits.helpsms.MainActivity;
import com.amargodigits.helpsms.model.JsonReq;
import com.amargodigits.helpsms.model.PhoneReq;

import java.security.SecureRandom;
import static com.amargodigits.helpsms.MainActivity.LOG_TAG;
import static com.amargodigits.helpsms.MainActivity.lastReqId;
import static com.amargodigits.helpsms.MainActivity.mAdapter;
import static com.amargodigits.helpsms.MainActivity.mDb;
import static com.amargodigits.helpsms.MainActivity.reqList;
import static com.amargodigits.helpsms.data.ReqContract.*;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_ALIAS;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_DATE;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_JSON_STATUS;
import static com.amargodigits.helpsms.data.ReqContract.ReqEntry.COLUMN_JSON_TIMESTAMP;
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
    private static final int DATABASE_VERSION = 3;

    public PhoneReqDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_REQSQL_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_REQ_ID + " TEXT NOT NULL, "
                + COLUMN_ALIAS + " TEXT NOT NULL, "
                + COLUMN_PHONE_NUMBER + " TEXT NOT NULL, "
                + COLUMN_MD5 + " TEXT NOT NULL, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_REQ_COUNT + " TEXT NOT NULL, "
                + COLUMN_REQ_SMS_STATUS + " TEXT NOT NULL,"
                + COLUMN_JSON_TIMESTAMP + " TEXT NOT NULL,"
                + COLUMN_JSON_STATUS + " TEXT NOT NULL"
                + "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_REQSQL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
         final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
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
                ReqEntry.COLUMN_ALIAS,
                ReqEntry.COLUMN_PHONE_NUMBER,
                ReqEntry.COLUMN_MD5,
                ReqEntry.COLUMN_DATE,
                ReqEntry.COLUMN_REQ_COUNT,
                ReqEntry.COLUMN_REQ_SMS_STATUS,
                ReqEntry.COLUMN_JSON_TIMESTAMP,
                ReqEntry.COLUMN_JSON_STATUS
        };

        String sortOrder = ReqEntry.COLUMN_DATE + " DESC";
        int i = 0;
        Cursor cursor = sqLiteDatabase.query(ReqEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
            while (cursor.moveToNext()) {
                try {
                    reqList.add(new PhoneReq(
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_ID)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_ALIAS)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_PHONE_NUMBER)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_MD5)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_DATE)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_COUNT)),
                                    cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_SMS_STATUS)),
                            cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_JSON_TIMESTAMP)),
                            cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_JSON_STATUS))
                            )
                    );
//                    Log.i(LOG_TAG, "makePhoneReqArrayFromSQLite: " +
//                            cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_ALIAS)) +" " +
//                            cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_PHONE_NUMBER)) +
////                            "-" + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_ID))+ "-" +
//                            " json status=" + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_JSON_STATUS))+ " " +
//                            " sms status=" + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_SMS_STATUS))+ " "
////                            + cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_JSON_TIMESTAMP))
//                    );
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
        String reqId = base64.trim();
        cv.put(COLUMN_REQ_ID, reqId);
        cv.put(COLUMN_ALIAS, phoneReq.getAlias());
        cv.put(COLUMN_PHONE_NUMBER, phoneReq.getPhoneNumber());
        cv.put(COLUMN_MD5, phoneReq.getMds5());
        cv.put(COLUMN_DATE, phoneReq.getReqDate());
        cv.put(COLUMN_REQ_COUNT, phoneReq.getReqCount());
        cv.put(COLUMN_REQ_SMS_STATUS, phoneReq.getReqSmsStatus());
        cv.put(COLUMN_JSON_STATUS, "");
        cv.put(COLUMN_JSON_TIMESTAMP, "");
        Log.i(LOG_TAG, "Adding " + phoneReq.getAlias() + " " +  phoneReq.getPhoneNumber() + " reqId=-" + reqId + "-");
        MainActivity.mDb.insert(ReqContract.ReqEntry.TABLE_NAME, null, cv);
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
        return reqId;
    }

    /**
     * addPhoneReqId insert the record with "phoneReq" to mDb
     * with the specified ReqID value
     *
     * @param phoneReq -  phone Request data
     **/
    public static void addPhoneReqId(PhoneReq phoneReq, String reqId) {
        ContentValues cv = new ContentValues();
//        cv.put(COLUMN_REQ_ID, phoneReq.getReqId());
//        SecureRandom srand = new SecureRandom();
//        String txt=  srand.nextInt() +""+srand.nextInt();
//        byte[] data = txt.getBytes();
//        String base64 = Base64.encodeToString(data, Base64.URL_SAFE + Base64.NO_PADDING); ;
//        String reqId = base64.trim();

        cv.put(COLUMN_REQ_ID, reqId);
        cv.put(COLUMN_ALIAS, phoneReq.getAlias());
        cv.put(COLUMN_PHONE_NUMBER, phoneReq.getPhoneNumber());
        cv.put(COLUMN_MD5, phoneReq.getMds5());
        cv.put(COLUMN_DATE, phoneReq.getReqDate());
        cv.put(COLUMN_REQ_COUNT, phoneReq.getReqCount());
        cv.put(COLUMN_REQ_SMS_STATUS, phoneReq.getReqSmsStatus());
        cv.put(COLUMN_JSON_STATUS, "");
        cv.put(COLUMN_JSON_TIMESTAMP, "");
        Log.i(LOG_TAG, "Adding " + phoneReq.getAlias() + " " +  phoneReq.getPhoneNumber() + " reqId=-" + reqId + "-");
        MainActivity.mDb.insert(ReqContract.ReqEntry.TABLE_NAME, null, cv);
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
        return;
    }


    /**
     * deletePhoneReqID delete the record with "phoneReq" from mDb
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
     * updatePhoneReqStatus update  the record with "phoneReq" to mDb
     * @param phoneReqId -  phone Request Id
     **/
    public static void updatePhoneReqStatus(String phoneReqId, String phoneReqStatus) {
        Log.i(LOG_TAG, "Updating "+  phoneReqId + " to " + phoneReqStatus);
        String where = COLUMN_REQ_ID + "='" + phoneReqId+"'";

//        if (!lastReqId.equals(phoneReqId)) return;
//        String[] projection = {
//                ReqEntry.COLUMN_REQ_ID,
//                ReqEntry.COLUMN_ALIAS,
//                ReqEntry.COLUMN_REQ_SMS_STATUS,
//                ReqEntry.COLUMN_JSON_STATUS
//        };
//        Cursor cursor = mDb.query(ReqEntry.TABLE_NAME, projection, where, null, null, null, null);
//        while (cursor.moveToNext()) {
//            try {
//                String smsStatus = cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_REQ_SMS_STATUS));
//                if (smsStatus.contains("-2")) {
//                    Log.i(LOG_TAG, cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_ALIAS)) + " smsStatus -2, exiting");
//                    return;
//                }
//
//                if (smsStatus.contains("-1") && (phoneReqStatus!="-2")) {
//                    Log.i(LOG_TAG, cursor.getString(cursor.getColumnIndex(ReqEntry.COLUMN_ALIAS)) + " smsStatus -1 and next not -2, exiting");
//                    return;
//                }
//
//            } catch (Exception e) {
//                Log.i(LOG_TAG, "Exception checking sms status in db " + e.toString());
//            }
//        }

//        Log.i(LOG_TAG, "where to update =  " + where);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_REQ_SMS_STATUS, phoneReqStatus);
        MainActivity.mDb.update(ReqContract.ReqEntry.TABLE_NAME, cv, where, null );
        makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * updateJsonStatus update  the record with "phoneReq" to mDb
     * @param jsonReq -  jsonReq with info to update in mDb
     **/
    public static void updateJsonStatus(JsonReq jsonReq) {
//        Log.i(LOG_TAG, "updateJsonStatus. Updating "+  jsonReq.getReqId() + " timestamp to " + jsonReq.getTimestamp() + " Json Status " + jsonReq.getJsonStatus());
        String where = COLUMN_REQ_ID + "='" + jsonReq.getReqId().trim() + "'";
//        Log.i(LOG_TAG, "where to update =  " + where);
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_JSON_TIMESTAMP, jsonReq.getTimestamp());
        cv.put(COLUMN_JSON_STATUS, jsonReq.getJsonStatus());
try {
    int i = MainActivity.mDb.update(ReqContract.ReqEntry.TABLE_NAME, cv, where, null);
 //   Log.i(LOG_TAG, " Updated " + i + " records");
}
catch (Exception e)
{Log.i(LOG_TAG, "Exception updating JsonStatus: " +e.toString());
}
       makePhoneReqArrayFromSQLite(mDb);
        mAdapter.notifyDataSetChanged();
    }

}

