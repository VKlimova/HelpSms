package com.amargodigits.helpsms.data;

import android.provider.BaseColumns;

public  class ReqContract {
    public static final class ReqEntry implements BaseColumns{
        public static final String TABLE_NAME="phoneReq";
        public static final String COLUMN_REQ_ID = "reqId";
        public static final String COLUMN_PHONE_NUMBER = "phoneNumber";
        public static final String COLUMN_MD5 = "md5";
        public static final String COLUMN_DATE = "reqDate";
        public static final String COLUMN_REQ_COUNT = "reqCount";
    }
}
