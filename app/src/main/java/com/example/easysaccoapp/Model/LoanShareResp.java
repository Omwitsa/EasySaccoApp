package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoanShareResp {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private LoanShareTypes loanShareTypes;

    public LoanShareResp(boolean success, String message, LoanShareTypes loanShareTypes) {
        this.success = success;
        this.message = message;
        this.loanShareTypes = loanShareTypes;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoanShareTypes getLoanShareTypes() {
        return loanShareTypes;
    }

    public void setLoanShareTypes(LoanShareTypes loanShareTypes) {
        this.loanShareTypes = loanShareTypes;
    }
}
