package com.amargodigits.helpsms.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import com.amargodigits.helpsms.MainActivity;
import com.amargodigits.helpsms.model.JsonReq;
import com.amargodigits.helpsms.model.PhoneReq;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import static com.amargodigits.helpsms.MainActivity.LOG_TAG;
import static com.amargodigits.helpsms.data.PhoneReqDbHelper.updateJsonStatus;


public class NetworkUtils {
    /**
     * Builds the URL used to get Recipies list from the server.
     */

    public static URL buildReqUrl(String key) {
        Uri builtUri = Uri.parse("https://lazyhome.ru/s/get/?key="+key).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * The main entry point
     * This method creates AsyncTask to make a Network request in background
     * To load phone records list from Json and update the database.
     */
    public static class LoadJsonReqTask extends AsyncTask<String, Void, JsonReq> {
        Context mContext;
        public LoadJsonReqTask(Context context) {
            mContext = context;
        }
        /**
         * This method make a Network request in background
         * Load  list
         * @return smth [] -  the reviews  array
         */
        @Override
        protected JsonReq doInBackground(String... params) {
            String timestamp="";
            JsonReq jsonReq=null;
            if (isOnline(mContext)) {
                try {
                    String key=params[0];
                    URL scheduleRequestUrl = NetworkUtils.buildReqUrl(key);
                    Log.i(LOG_TAG, " scheduleRequestUrl="+ scheduleRequestUrl);
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(scheduleRequestUrl);
                    Log.i(LOG_TAG, " jsonResponse="+ jsonResponse);
                    jsonReq = Json.getTimestampFromJson(key, jsonResponse);
                    return jsonReq;
                } catch (Exception e) {
                    Log.i(LOG_TAG, "Error importing JSON: " + e.toString());
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(mContext, "No data", Toast.LENGTH_LONG).show();
            }
            return jsonReq;
        }

        @Override
        protected void onPostExecute(JsonReq jsonReq) {
            super.onPostExecute(jsonReq);
            updateJsonStatus(jsonReq);
            MainActivity.doGridView(jsonReq);
            Log.i(LOG_TAG, "  onPostExecute before return jsonReqID=" + jsonReq.getReqId() + " jsonReqTimestamp=" + jsonReq.getTimestamp() );
            return;
        }
    }

    /**
     * Checks if the device has network connection
     * @param tContext - context variable
     * @return true if the device is connected to network, otherwise returns false
     */
    public static boolean isOnline(Context tContext) {
        ConnectivityManager cm =
                (ConnectivityManager) tContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
