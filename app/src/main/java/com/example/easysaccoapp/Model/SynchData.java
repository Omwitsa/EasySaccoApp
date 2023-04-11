package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

public class SynchData {
    @SerializedName("memberNo")
    private String MemberNo;
    @SerializedName("amount")
    private String Amount;
    @SerializedName("date")
    private String date;
    @SerializedName("auditID")
    private String AuditId;
    @SerializedName("transType")
    private String TransType;

    public SynchData(String memberNo, String amount, String date, String auditId, String transType) {
        MemberNo = memberNo;
        Amount = amount;
        this.date = date;
        AuditId = auditId;
        TransType = transType;
    }

    public String getMemberNo() {
        return MemberNo;
    }

    public void setMemberNo(String memberNo) {
        MemberNo = memberNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuditId() {
        return AuditId;
    }

    public void setAuditId(String auditId) {
        AuditId = auditId;
    }

    public String getTransType() {
        return TransType;
    }

    public void setTransType(String transType) {
        TransType = transType;
    }
}
