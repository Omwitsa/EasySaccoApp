package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

public class SynchData {
    @SerializedName("memberNo")
    private String memberNo;
    @SerializedName("amount")
    private String amount;
    @SerializedName("loanShareType")
    private String loanShareType;
    @SerializedName("date")
    private String date;
    @SerializedName("auditID")
    private String auditID;
    @SerializedName("transType")
    private int transType;

    public SynchData(String memberNo, String amount, String loanShareType, String date, String auditID, int transType) {
        this.memberNo = memberNo;
        this.amount = amount;
        this.loanShareType = loanShareType;
        this.date = date;
        this.auditID = auditID;
        this.transType = transType;
    }

    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLoanShareType() {
        return loanShareType;
    }

    public void setLoanShareType(String loanShareType) {
        this.loanShareType = loanShareType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuditID() {
        return auditID;
    }

    public void setAuditID(String auditID) {
        this.auditID = auditID;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }
}
