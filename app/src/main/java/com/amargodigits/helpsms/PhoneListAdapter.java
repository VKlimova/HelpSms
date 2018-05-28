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
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import static com.amargodigits.helpsms.MainActivity.mContext;
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
        TextView phTitle, phDate, smsStatus, alias, jsonTimestamp, jsonStatus;
        ImageButton smsBtn, mapBtn;
        ImageView submenuBtn;
        View textLayout, submenuLayout;

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
            holder.alias = (TextView) convertView.findViewById(R.id.alias);
            holder.jsonStatus = (TextView) convertView.findViewById(R.id.json_status);
            holder.jsonTimestamp = (TextView) convertView.findViewById(R.id.json_timestamp);
            holder.smsBtn = (ImageButton) convertView.findViewById(R.id.sms_btn);
//            holder.mapBtn = (ImageButton) convertView.findViewById(R.id.sms_btn);
            holder.submenuBtn = (ImageView) convertView.findViewById(R.id.submenu_btn);
            holder.textLayout = (View) convertView.findViewById(R.id.textLayout);
//            holder.submenuLayout = (View) convertView.findViewById(R.id.submenuLayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            ;
        }
        try {
            holder.phTitle.setText(currentPhReq.getPhoneNumber());
        } catch (Exception e) {
        }
        try {
            String reqDateTime = dateTimeString(Long.parseLong(currentPhReq.getReqDate()));
            holder.phDate.setText(mContext.getString(R.string.smsMgr) + ": " + reqDateTime);
        } catch (Exception e) {
        }
        String jStatusCode;// = "0";

        try {
            holder.alias.setText(currentPhReq.getAlias());
        } catch (Exception e) {
        }
        try {
            jStatusCode = currentPhReq.getJsonStatus();
        } catch (Exception e) {
            jStatusCode="0";
        }

        String smsStatus = "";
        try {
            smsStatus = currentPhReq.getReqSmsStatus();
        } catch (Exception e) {
        }

        switch (smsStatus.trim()) {
            case "100":
                holder.smsBtn.setImageResource(R.drawable.ic_status_sending);
                holder.smsStatus.setText(mContext.getString(R.string.Initial));
                break;
            case "200":
                holder.smsBtn.setImageResource(R.drawable.ic_status_wait);
                holder.smsStatus.setText(mContext.getString(R.string.Imported));
                jsonStatus(jStatusCode, holder.smsBtn, holder.jsonStatus, holder.jsonTimestamp);
                break;
//            case "SMS sent":
//                holder.smsBtn.setImageResource(R.drawable.ic_status_ok);
//                holder.smsStatus.setText(mContext.getString(R.string.RESULT_OK_SENT));
//                break;
            case "1":
                holder.smsBtn.setImageResource(R.drawable.ic_status_error);
                holder.smsStatus.setText(mContext.getString(R.string.RESULT_ERROR_GENERIC_FAILURE));
                break;
            case "4":
                holder.smsBtn.setImageResource(R.drawable.ic_status_error);
                holder.smsStatus.setText(mContext.getString(R.string.RESULT_ERROR_NO_SERVICE));
                break;
            case "3":
                holder.smsBtn.setImageResource(R.drawable.ic_status_error);
                holder.smsStatus.setText(mContext.getString(R.string.RESULT_ERROR_NULL_PDU));
                break;
            case "2":
                holder.smsBtn.setImageResource(R.drawable.ic_status_error);
                holder.smsStatus.setText(mContext.getString(R.string.RESULT_ERROR_RADIO_OFF));
                break;
            case "On map":
                holder.smsBtn.setImageResource(R.drawable.ic_status_place);
                break;

            case "-1": // sms sent
                holder.smsStatus.setText("SMS status: " + mContext.getString(R.string.RESULT_OK_SENT));
                holder.smsBtn.setImageResource(R.drawable.ic_status_ok);
                jsonStatus(jStatusCode, holder.smsBtn, holder.jsonStatus, holder.jsonTimestamp);
//                Log.i(LOG_TAG, "  S M S    S T A T U S    -1  ============================");
                break;

            case "-2":       // delivered sms
                holder.smsStatus.setText("SMS status: " + mContext.getString(R.string.RESULT_OK_DELIVERED));
                holder.smsBtn.setImageResource(R.drawable.ic_status_ok);
                jsonStatus(jStatusCode, holder.smsBtn, holder.jsonStatus, holder.jsonTimestamp);
//                Log.i(LOG_TAG, "  S M S    S T A T U S    -2  ============================");
                break;

//            case "SMS not delivered":
//                holder.smsBtn.setImageResource(R.drawable.ic_status_error);
//                break;
        }

        try {
//            Log.i(LOG_TAG, "holder.alias =" + currentPhReq.getAlias());
            setOnMapClick((View) holder.smsBtn, currentPhReq.getPhoneNumber(), currentPhReq.getReqId());
            setOnMapClick((View) holder.textLayout, currentPhReq.getPhoneNumber(), currentPhReq.getReqId());
            setOnSubmenuClick(holder.submenuBtn, currentPhReq.getAlias(), currentPhReq.getPhoneNumber(), currentPhReq.getReqId());
        } catch (Exception e) {
        }
        return convertView;
    }

    public String dateTimeString(Long dateTimeLong) {
        Calendar calendar = Calendar.getInstance();
        if ((dateTimeLong==null) || (dateTimeLong==0) || (dateTimeLong.equals(null))) return "";
        calendar.setTimeInMillis(dateTimeLong);
//    int mYear = calendar.get(Calendar.YEAR);
//    int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        String minPref = "";
        if (mMinute < 10) minPref = "0";
        String hPref = "";
        if (mHour < 10) hPref = "0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL", Locale.getDefault());
        String strMonth = dateFormat.format(dateTimeLong);
        String dateTimeString = mDay + " " + strMonth + " " + hPref + mHour + ":" + minPref + mMinute;
        return dateTimeString;
    }

    public void jsonStatus(String jStatusCode, ImageButton smsBtn, TextView jsonStatus, TextView jsonTimestamp) {
        String jStatusStr = "";
        switch (jStatusCode) {
            case "0":
                jStatusStr = mContext.getString(R.string.code0);
                smsBtn.setImageResource(R.drawable.ic_status_human);
                break;
            case "1":
                jStatusStr = mContext.getString(R.string.code1);
                smsBtn.setImageResource(R.drawable.ic_status_place);
                break;
            case "-1":
                jStatusStr = mContext.getString(R.string.codeminus1);
                smsBtn.setImageResource(R.drawable.ic_status_error);
                break;
            case "-2":
                jStatusStr = mContext.getString(R.string.codeminus2);
                smsBtn.setImageResource(R.drawable.ic_status_error);
                break;
        }

        try {
            String jsonDateTime = ""; // dateTimeString(Long.parseLong(currentPhReq.getJsonTimestamp()));
            jsonTimestamp.setText(mContext.getString(R.string.jsonSite) + ": " + jsonDateTime);
        } catch (Exception e) {
            Log.i(LOG_TAG, " jsonDateTime Exception " + e.toString());
        }

        if (jStatusCode.length() > 0) {
            jsonStatus.setText(jStatusStr);
        }

    }

    ;


    private void setOnMapClick(final View btn, final String phNum, final String reqId) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.share_link) + reqId));
                getContext().startActivity(browserIntent);
            }
        });
    }

    private void setOnSubmenuClick(final ImageView btn, final String alias, final String phNum, final String reqId) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do whatever you want(str can be used here)
                android.app.FragmentManager manager = ((MainActivity) mContext).getFragmentManager();
                SubmenuDialogFragment submenuDialogFragment = new SubmenuDialogFragment();
                // Supply num input as an argument.
                Bundle args = new Bundle();
                args.putString("alias", alias);
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

            final String alias = getArguments().getString("alias");
            final String phoneNum = getArguments().getString("num");
            final String reqId = getArguments().getString("reqId");
            String title = getString(R.string.submenuTxt) + " " + phoneNum + " " + alias;
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
                                    MainActivity.send2lost(alias, phoneNum);
                                    break;
                                case 1:
//                                    doShare(phoneNum, mContext.getString(R.string.lazyhome_link) + "show/?key=" + reqId + "&ph=" + phoneNum);
                                    doShare(phoneNum, alias + ": " + phoneNum + " "+ mContext.getString(R.string.share_link) + reqId);
                                    break;
                                case 2:
                                    deletePhoneReqID(reqId);
                                    break;
                                case 3:
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.share_link)  + reqId));
                                    mContext.startActivity(browserIntent);
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