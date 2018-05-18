package com.amargodigits.helpsms;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.amargodigits.helpsms.data.PhoneReqDbHelper;
import com.amargodigits.helpsms.model.PhoneReq;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import static com.amargodigits.helpsms.data.PhoneReqDbHelper.makePhoneReqArrayFromSQLite;

public class MainActivity extends AppCompatActivity {
public static String LOG_TAG="Help SMS Log";
public static ArrayList<PhoneReq> reqList = new ArrayList<>();
    public static SQLiteDatabase mDb;
    public static PhoneReqDbHelper dbHelper;
    public static PhoneListAdapter mAdapter;
    public static GridView mGridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new PhoneReqDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        makePhoneReqArrayFromSQLite(mDb);
         mGridview = (GridView) findViewById(R.id.phonelist_view);
       // TeaMenuAdapter adapter = new TeaMenuAdapter(this, R.layout.grid_item_layout, teas);
        mAdapter = new PhoneListAdapter(this, R.layout.grid_item_layout, reqList);
        mGridview.setAdapter(mAdapter);
        // Set a click listener on that View
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                PhoneReq item = (PhoneReq) adapterView.getItemAtPosition(position);
//                String phone = item.getPhoneNumber();
                String mds5 = item.getMds5();
                Log.i(LOG_TAG, view.toString());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lazyhome.ru/m/show/?code="+mds5));
                startActivity(browserIntent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.FragmentManager manager = getFragmentManager();
                ImportDialogFragment importDialogFragment = new ImportDialogFragment();
                importDialogFragment.show(manager, "dialog");

//                String number = "+79039606073";  // The number on which you want to send SMS
////                Snackbar.make(view, "Number="+ number + ", MD5=" + md5(number), Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                Uri uri = Uri.parse("smsto:" + number);
//                String link2send="https://lazyhome.ru/m/?code="+md5(number)+"&ph="+number;
//                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
//                it.putExtra("sms_body", "Кликните на ссылку, после открытия страницы подтвердите доступ к GPS:\n" + link2send);
//               startActivity(it);

            }
        });

        // Checking the permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},0);
        }
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
        if (id == R.id.action_settings) {
            return true;
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

    //       I M P O R T     d i a l o g
    public static class ImportDialogFragment extends DialogFragment {
        //  @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String title = getString(R.string.impor);
            String button1String = "Ok";
            String button2String = "Cancel";

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.import_fragment, null);
            builder.setView(view);
            //           builder.setTitle(title);  // заголовок
            builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Dialog f = (Dialog) dialog;
                    EditText Phonetxt;
                    Phonetxt = (EditText) f.findViewById(R.id.import_str);
                   String mPhonestr=Phonetxt.getText().toString();
                    Log.i(LOG_TAG, "mJSONstr="+mPhonestr);
//                    String number = "+79039606073";  // The number on which you want to send SMS
                    String number = mPhonestr;
                    String md5 = md5(number);
                    Long curTime = Calendar.getInstance().getTimeInMillis();
                    PhoneReq curPhoneReq= new PhoneReq("", number, md5, Long.toString(curTime), "1");
                    PhoneReqDbHelper.addPhoneReq(curPhoneReq);
                    String link2send="https://lazyhome.ru/m/?code="+ md5 +"&ph="+number;
                    String message = "TBOE MECTO HA KAPTE: " + link2send;
//                    message = link2send;
//                Snackbar.make(view, "Number="+ number + ", MD5=" + md5(number), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                    Uri uri = Uri.parse("smsto:" + number);
//                    String link2send="https://lazyhome.ru/m/?code="+ md5 +"&ph="+number;
//                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
//                    it.putExtra("sms_body", "Кликните на ссылку, после открытия страницы подтвердите доступ к GPS:\n" + link2send);
//                    startActivity(it);


                    sendSMS(number, message);

                    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

                        @Override
                        protected void onPostExecute(String[] result) {
                            super.onPostExecute(result);
                        }

                        @Override
                        protected String[] doInBackground(String... params) {

                            if (params.length == 0) {
                                return null;
                            }
                            try {
                                Log.i(LOG_TAG, "sending");
                            } catch (Exception e) {
                                Log.i(LOG_TAG, "exception");
                                e.printStackTrace();
                            }
                            return null;}}
                    new FetchWeatherTask().execute("");
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

    private static void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        // Here, thisActivity is the current activity
        Log.i(LOG_TAG, "SMS sending: message length=" + message.length());
//message = "aaa";
        try {
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Log.i(LOG_TAG, "SMS sending: phoneNumber=" +phoneNumber+"\nmessage=" + message);
        }
        catch (Exception e){
            Log.i(LOG_TAG, "SMS sending exception: phoneNumber=" +phoneNumber+"\nmessage=" + message+ "\nError=" + e.toString());
        }
    }
}
