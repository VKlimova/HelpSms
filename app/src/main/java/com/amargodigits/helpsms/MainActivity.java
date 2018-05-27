package com.amargodigits.helpsms;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import com.amargodigits.helpsms.data.PhoneReqDbHelper;
import com.amargodigits.helpsms.model.JsonReq;
import com.amargodigits.helpsms.model.PhoneReq;
import com.amargodigits.helpsms.utils.NetworkUtils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.amargodigits.helpsms.data.PhoneReqDbHelper.makePhoneReqArrayFromSQLite;

public class MainActivity extends AppCompatActivity {
    public static String LOG_TAG = "Help SMS Log";
    public static String lastReqId="";
    public static ArrayList<PhoneReq> reqList = new ArrayList<>();
    public static ArrayList<JsonReq> mJReq = new ArrayList<>();
    public static SQLiteDatabase mDb;
    public static PhoneReqDbHelper dbHelper;
    public static PhoneListAdapter mAdapter;
    public static GridView mGridview;
    public static Context mContext;
    public static SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = getApplicationContext();
        // Creating grid from database
        dbHelper = new PhoneReqDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        makePhoneReqArrayFromSQLite(mDb);
        mGridview = (GridView) findViewById(R.id.phonelist_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mAdapter = new PhoneListAdapter(this, R.layout.grid_item_layout, reqList);
        mGridview.setAdapter(mAdapter);
        // eof creating grid from database

        updateGridFromJson();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager manager = getFragmentManager();
                SendSmsDialogFragment importDialogFragment = new SendSmsDialogFragment();
                importDialogFragment.show(manager, "dialog");
            }
        });

        // Checking the permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                        updateGridFromJson();
                    }
                }
        );
    }

    //
    // Creating array from Json and updating database
    //
    public void updateGridFromJson(){
//        Log.i(LOG_TAG, "updateGridFromJson");
        for (int i = 0; (i < reqList.size()); i++)
            try {
            String key = reqList.get(i).getReqId();
            NetworkUtils.LoadJsonReqTask mAsyncTasc = new NetworkUtils.LoadJsonReqTask(getApplicationContext());
            mAsyncTasc.execute(key);
        } catch (Exception e) {
            Log.i(LOG_TAG, "Loading data exception: " + e.toString());
        }
    }

public static void doGridView(JsonReq jsonReq){
        //TOD update db with jsonReq
    mAdapter.notifyDataSetChanged();
    mSwipeRefreshLayout.setRefreshing(false);
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            AlertDialog.Builder aboutbuilder = new AlertDialog.Builder(MainActivity.this);
            AlertDialog alert = aboutbuilder.create();

            aboutbuilder.setTitle(getString(R.string.app_name)+ " " + BuildConfig.VERSION_NAME)
                    .setMessage(getString(R.string.help_string) +
                                    "\n\n(c)AmargoDigits\namargodigits@gmail.com")
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(true)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            //               AlertDialog
            alert = aboutbuilder.create();
            alert.show();
            return true;
        }

        if (id == R.id.action_jrefresh){
            mSwipeRefreshLayout.setRefreshing(true); // %(
            Toast.makeText(mContext, "Refreshing...", Toast.LENGTH_SHORT).show();
            updateGridFromJson();
        }

        if (id==R.id.action_addreq){
            android.app.FragmentManager manager = getFragmentManager();
            ImportDialogFragment importDialogFragment = new ImportDialogFragment();
            importDialogFragment.show(manager, "dialog");
        }

            return super.onOptionsItemSelected(item);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //      S e n d   S M S    d i a l o g
    public static class SendSmsDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String button1String = "Ok";
            String button2String = "Cancel";

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View import_view = inflater.inflate(R.layout.send_sms_fragment, null);
            builder.setView(import_view);
            builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Dialog f = (Dialog) dialog;
                    EditText Phonetxt, Aliastxt;
                    Phonetxt = (EditText) f.findViewById(R.id.telnum_str);
                    Aliastxt = (EditText) f.findViewById(R.id.alias_str);
                    String mPhonestr = Phonetxt.getText().toString();
                    String aliasStr = Aliastxt.getText().toString();
                    send2lost(aliasStr, mPhonestr);
                }
            });
            builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setCancelable(true);
            return builder.create();
        }
    }


    //       I M P O R T     d i a l o g
    public static class ImportDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String button1String = "Ok";
            String button2String = "Cancel";

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View import_view = inflater.inflate(R.layout.import_sms_fragment, null);
            builder.setView(import_view);
            builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Dialog f = (Dialog) dialog;

                    EditText Codetxt = (EditText) f.findViewById(R.id.code_str);
                    String codeStr = Codetxt.getText().toString();
                    Long curTime = Calendar.getInstance().getTimeInMillis();

                    PhoneReq curPhoneReq = new PhoneReq("", "", "", "",Long.toString(curTime), "1", "200", "", "");
                    PhoneReqDbHelper.addPhoneReqId(curPhoneReq, codeStr);
//                    send2lost(aliasStr, mPhonestr);
                }
            });
            builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.setCancelable(true);
            return builder.create();
        }
    }

    //
    // Sends Sms, add a record to Db
    //
    public static void send2lost(String alias, String phoneStr) {
        String md5 = md5(phoneStr);
        Long curTime = Calendar.getInstance().getTimeInMillis();
        PhoneReq curPhoneReq = new PhoneReq("", alias, phoneStr, md5, Long.toString(curTime), "1", "100", "", "");
        String reqId = PhoneReqDbHelper.addPhoneReq(curPhoneReq);
        String link2send = mContext.getString(R.string.lazyhome_link) + "?alias=" + URLEncoder.encode(  alias )+ "&key=" + reqId;
        String message = "TBOE MECTO HA KAPTE: " + link2send;
        Log.i(LOG_TAG, "SMS to send: " + message);
        sendSMS(phoneStr, message, reqId);
    }

    //---sends an SMS message
    private static void sendSMS(String phoneNumber, String message, final String reqId) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        lastReqId=reqId;
        Intent sentIntent= new Intent(SENT);
//        sentIntent.putExtra("reqId", reqId);
        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0,
                sentIntent, 0);

        Intent deliveredIntent = new Intent(DELIVERED);
//        deliveredIntent.putExtra("reqId", reqId);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0,
                deliveredIntent, 0);

        //---when the SMS has been sent---
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String textCode="";
                        String resultCode = String.valueOf(getResultCode());
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        textCode = mContext.getString(R.string.RESULT_OK_SENT);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        textCode = mContext.getString(R.string.RESULT_ERROR_GENERIC_FAILURE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        textCode = mContext.getString(R.string.RESULT_ERROR_NO_SERVICE);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        textCode = mContext.getString(R.string.RESULT_ERROR_NULL_PDU);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        textCode = mContext.getString(R.string.RESULT_ERROR_RADIO_OFF);
                        break;
                }

                PhoneReqDbHelper.updatePhoneReqStatus(reqId, resultCode);
                Log.i(LOG_TAG, "SMS Sending -->  " + resultCode + " : " + textCode);
                Toast.makeText(mContext, resultCode+ " : " + textCode, Toast.LENGTH_LONG).show();

            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String textCode="", resultCode = "";

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        textCode = mContext.getString(R.string.RESULT_OK_DELIVERED);
                        resultCode="-2";
                        break;
                    case Activity.RESULT_CANCELED:
                        textCode = mContext.getString(R.string.RESULT_CANCELED);
                        break;
                }

                PhoneReqDbHelper.updatePhoneReqStatus(reqId, resultCode);
                Log.i(LOG_TAG, "SMS Delivery  -->  " + resultCode + " : " + textCode);
                Toast.makeText(mContext, textCode, Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(DELIVERED));


        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}
