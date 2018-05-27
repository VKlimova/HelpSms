package com.amargodigits.helpsms.utils;

import android.util.Log;
import com.amargodigits.helpsms.model.JsonReq;
import com.amargodigits.helpsms.model.PhoneReq;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import static com.amargodigits.helpsms.MainActivity.LOG_TAG;

public class Json {
    /**
     * takes as the input raw Json string, fills in the phoneReqJsonArr<> array
     *
     * @param rawJsonStr - raw string with JSON data
     */
    public static JsonReq getTimestampFromJson(String key, String rawJsonStr)
            throws JSONException {
//        Log.i(LOG_TAG, "rawJsonStr=" + rawJsonStr);
        String timestamp="";
        String jsonStatus = "";
        try {
            JSONObject jsonObject = new JSONObject(rawJsonStr);
            timestamp = jsonObject.getString("timestamp");
//            Log.i(LOG_TAG, "timestamp=" + timestamp);
        } catch (Exception e) {
//            Log.i(LOG_TAG, "Caught JSon exception parsing:" + e.toString());
        }

        try {
            JSONObject jsonObject = new JSONObject(rawJsonStr);
            String jsonStatus1 = jsonObject.getString("status");
//            Log.i(LOG_TAG, "jsonStatus jsonStatus1=" + jsonStatus1);
            JSONObject jsonObjectVal = new JSONObject(jsonStatus1);
            jsonStatus = jsonObjectVal.getString("val");
//            Log.i(LOG_TAG, "jsonStatus val=" + jsonStatus);
        } catch (Exception e) {
            Log.i(LOG_TAG, "Caught JSon exception parsing status:" + e.toString());
        }
        JsonReq jsonReq = new JsonReq(key, timestamp, jsonStatus);
        return jsonReq;
    }
}


