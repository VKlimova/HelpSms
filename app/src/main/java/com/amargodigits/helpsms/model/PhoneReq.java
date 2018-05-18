package com.amargodigits.helpsms.model;

import com.amargodigits.helpsms.data.ReqContract;

public class PhoneReq {
    private String reqId;
    private String phoneNumber;
    private String mds5;
    private String reqDate;
    private String reqCount;

    public PhoneReq(String reqID, String phoneNumber, String mds5, String reqDate, String reqCount) {
        this.reqId = reqID;
        this.phoneNumber = phoneNumber;
        this.mds5 = mds5;
        this.reqDate = reqDate;
        this.reqCount = reqCount;
    }

    public String getReqId()  { return this.reqId;}
    public String getPhoneNumber() { return this.phoneNumber;}
    public String getMds5() { return this.mds5;}
    public String getReqCount() { return reqCount; }
    public String getReqDate() { return reqDate; }
}
