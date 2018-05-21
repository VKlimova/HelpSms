package com.amargodigits.helpsms.model;

import com.amargodigits.helpsms.data.ReqContract;

public class PhoneReq {
    private String reqId;
    private String alias;
    private String phoneNumber;
    private String mds5;
    private String reqDate;
    private String reqCount;
    private String reqSmsStatus;
    private String jsonTimestamp;
    private String jsonStatus;

    public PhoneReq(String reqID, String alias, String phoneNumber, String mds5, String reqDate, String reqCount, String reqSmsStatus,
                    String jsonTimestamp, String jsonStatus ) {
        this.reqId = reqID;
        this.alias = alias;
        this.phoneNumber = phoneNumber;
        this.mds5 = mds5;
        this.reqDate = reqDate;
        this.reqCount = reqCount;
        this.reqSmsStatus = reqSmsStatus;
        this.jsonTimestamp = jsonTimestamp;
        this.jsonStatus = jsonStatus;
    }

    public String getReqId() {
        return this.reqId;
    }

    public String getAlias() {
        return alias;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getMds5() {
        return this.mds5;
    }

    public String getReqCount() {
        return reqCount;
    }

    public String getReqDate() {
        return reqDate;
    }

    public String getReqSmsStatus() {
        return reqSmsStatus;
    }

    public String getJsonTimestamp() {
        return jsonTimestamp;
    }

    public String getJsonStatus() {
        return jsonStatus;
    }
}
