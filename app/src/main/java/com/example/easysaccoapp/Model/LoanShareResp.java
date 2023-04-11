package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoanShareResp {
    @SerializedName("success")
    private boolean Success;
    @SerializedName("message")
    private String Message;
    @SerializedName("data")
    private LoanShareTypes LoanShareTypes;

    public LoanShareResp(boolean success, String message, com.example.easysaccoapp.Model.LoanShareTypes loanShareTypes) {
        Success = success;
        Message = message;
        LoanShareTypes = loanShareTypes;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public com.example.easysaccoapp.Model.LoanShareTypes getLoanShareTypes() {
        return LoanShareTypes;
    }

    public void setLoanShareTypes(com.example.easysaccoapp.Model.LoanShareTypes loanShareTypes) {
        LoanShareTypes = loanShareTypes;
    }
}
