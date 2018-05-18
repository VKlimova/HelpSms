/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.amargodigits.helpsms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amargodigits.helpsms.model.PhoneReq;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import java.text.DateFormatSymbols;
import java.util.Locale;

import static com.amargodigits.helpsms.MainActivity.LOG_TAG;

/**
 * PhoneListAdapter is backed by an ArrayList of {@link PhoneReq} objects which populate
 * the GridView in MenuActivity
 */

public class PhoneListAdapter extends ArrayAdapter<PhoneReq> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();
    public PhoneReq currentPhReq;

    public PhoneListAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.data = data;
    }

    static class ViewHolder {
        TextView phTitle;
        TextView phDate;
        ImageButton mapBtn;
//        ImageView image;
    }

    @Override
    // Create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        currentPhReq = getItem(position);

        if (convertView == null) {
            // If it's not recycled, initialize some attributes
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.phTitle = (TextView) convertView.findViewById(R.id.phone_num);
            holder.phDate = (TextView) convertView.findViewById(R.id.phone_date);
            holder.mapBtn = (ImageButton) convertView.findViewById(R.id.map_btn);
//            holder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();;
        }

        String mds5 = currentPhReq.getMds5();

        holder.mapBtn.setOnClickListener(mapBtnOnClickListener);

//        holder.mapBtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                PhoneReq item = (PhoneReq) adapterView.getItemAtPosition(position);
////                String phone = item.getPhoneNumber();
//                String mds5 = item.getMds5();
//                Log.i(LOG_TAG, view.toString());
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lazyhome.ru/m/show/?code="+mds5));
//                startActivity(browserIntent);
//            }
//        });

//        (new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                PhoneReq item = (PhoneReq) adapterView.getItemAtPosition(position);
////                String phone = item.getPhoneNumber();
//                String mds5 = item.getMds5();
//                Log.i(LOG_TAG, view.toString());
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lazyhome.ru/m/show/?code="+mds5));
//                getContext().startActivity(browserIntent);
//            }
//        });


        Calendar calendar = Calendar.getInstance();
        long dateLong = Long.parseLong(currentPhReq.getReqDate());
        calendar.setTimeInMillis(dateLong);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour= calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute= calendar.get(Calendar.MINUTE);
        String minPref=""; if (mMinute<10) minPref="0";
        String hPref=""; if(mHour<10) hPref="0";
        SimpleDateFormat dateFormat = new SimpleDateFormat( "LLLL", Locale.getDefault() );
        String strMonth = dateFormat.format(dateLong);

        String reqDateTime = mDay + " "+ strMonth + " " + hPref + mHour + ":" + minPref+ mMinute;
        holder.phTitle.setText(currentPhReq.getPhoneNumber());
        holder.phDate.setText(reqDateTime);
        return convertView;
    }

    View.OnClickListener mapBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mds5 = currentPhReq.getMds5();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lazyhome.ru/m/show/?code="+mds5));
            getContext().startActivity(browserIntent);
        }
    };

}