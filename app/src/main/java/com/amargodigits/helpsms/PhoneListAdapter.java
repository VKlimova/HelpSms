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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amargodigits.helpsms.data.PhoneReqDbHelper;
import com.amargodigits.helpsms.model.PhoneReq;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import java.text.DateFormatSymbols;
import java.util.Locale;

import static com.amargodigits.helpsms.MainActivity.LOG_TAG;

import static com.amargodigits.helpsms.data.PhoneReqDbHelper.deletePhoneReqID;

/**
 * PhoneListAdapter is backed by an ArrayList of {@link PhoneReq} objects which populate
 * the GridView in MenuActivity
 */

public class PhoneListAdapter extends ArrayAdapter<PhoneReq> {

    private static Context mContext;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();
    public static PhoneReq currentPhReq;

    public PhoneListAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = context;
        this.data = data;
    }

    static class ViewHolder {
        TextView phTitle, phDate, smsStatus;
        ImageButton mapBtn, submenuBtn;

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
            holder.smsStatus = (TextView) convertView.findViewById(R.id.sms_status);
            holder.mapBtn = (ImageButton) convertView.findViewById(R.id.map_btn);
            holder.submenuBtn = (ImageButton) convertView.findViewById(R.id.submenu_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            ;
        }

        //      String mds5 = currentPhReq.getMds5();


        //       holder.submenuBtn.setOnClickListener(submenuBtnOnClickListener);
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
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        String minPref = "";
        if (mMinute < 10) minPref = "0";
        String hPref = "";
        if (mHour < 10) hPref = "0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL", Locale.getDefault());
        String strMonth = dateFormat.format(dateLong);
        String reqDateTime = mDay + " " + strMonth + " " + hPref + mHour + ":" + minPref + mMinute;

        holder.phTitle.setText(currentPhReq.getPhoneNumber());
        holder.phDate.setText(reqDateTime);
        holder.smsStatus.setText(currentPhReq.getReqSmsStatus());

        setOnMapClick(holder.mapBtn, currentPhReq.getPhoneNumber(), currentPhReq.getReqId());
        setOnSubmenuClick(holder.submenuBtn, currentPhReq.getPhoneNumber(), currentPhReq.getReqId());

        return convertView;
    }

    private void setOnMapClick(final ImageButton btn, final String phNum, final String reqId) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do whatever you want(str can be used here)
                Log.i(LOG_TAG, "setOnMapClick " + phNum);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lazyhome.ru/s/show/?key=" + reqId));
                getContext().startActivity(browserIntent);
            }
        });
    }

    private void setOnSubmenuClick(final ImageButton btn, final String phNum, final String reqId) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do whatever you want(str can be used here)
                Log.i(LOG_TAG, "setOnSubmenuClick " + phNum);
                android.app.FragmentManager manager = ((MainActivity) mContext).getFragmentManager();
                SubmenuDialogFragment submenuDialogFragment = new SubmenuDialogFragment();
                // Supply num input as an argument.
                Bundle args = new Bundle();
                args.putString("num", phNum);
                args.putString("reqId", reqId);
                submenuDialogFragment.setArguments(args);
                submenuDialogFragment.show(manager, "dialog");

            }
        });

    }

    ;

    View.OnClickListener submenuBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.app.FragmentManager manager = ((MainActivity) mContext).getFragmentManager();
            SubmenuDialogFragment submenuDialogFragment = new SubmenuDialogFragment();
            submenuDialogFragment.show(manager, "dialog");

        }
    };

    //       S U B - M E N U   d i a l o g
    public static class SubmenuDialogFragment extends DialogFragment {
        //  @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final String phoneNum = getArguments().getString("num");
            ;
            final String reqId = getArguments().getString("reqId");
            ;
            String title = getString(R.string.submenuTxt) + " " + phoneNum;
            String button1String = "Ok";
            String button2String = "Cancel";
            final ArrayList<Integer> mSelectedItems = new ArrayList();  // Where we track the selected items

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setSingleChoiceItems(R.array.submenuArray, 0, null)
                    // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            switch (selectedPosition) {
                                case 0:
                                    MainActivity.send2lost(phoneNum);
                                    break;
                                case 1:
                                    doShare(phoneNum, "https://lazyhome.ru/s/show/?key=" + reqId + "&ph=" + phoneNum);
                                    break;
                                case 2:
                                    deletePhoneReqID(phoneNum);
                                    break;
                            }
                        }
                    })

                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
    }

    public static void doShare(String text1, String text2) {

        // S H A R E intent

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, text1);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text2);

        shareIntent.setType("image/jpeg");
        Intent chooser = Intent.createChooser(shareIntent, text1);
        mContext.startActivity(chooser);
    }


}