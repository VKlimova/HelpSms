package com.amargodigits.helpsms.model;

public class JsonReq {
    private String reqId;
    private String timestamp;
    private String jsonStatus;

    public JsonReq(String reqID, String timestamp, String jsonStatus) {
        this.reqId = reqID;
        this.timestamp = timestamp;
        this.jsonStatus = jsonStatus;

    }

    public String getReqId() {
        return this.reqId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getJsonStatus() {
        return jsonStatus;
    }
}
