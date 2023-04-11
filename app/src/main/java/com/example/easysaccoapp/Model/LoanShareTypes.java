package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoanShareTypes {
    @SerializedName("loanTypes")
    private List<String> LoanTypes;
    @SerializedName("shareTypes")
    private List<String> ShareTypes;

    public LoanShareTypes(List<String> loanTypes, List<String> shareTypes) {
        LoanTypes = loanTypes;
        ShareTypes = shareTypes;
    }

    public List<String> getLoanTypes() {
        return LoanTypes;
    }

    public void setLoanTypes(List<String> loanTypes) {
        LoanTypes = loanTypes;
    }

    public List<String> getShareTypes() {
        return ShareTypes;
    }

    public void setShareTypes(List<String> shareTypes) {
        ShareTypes = shareTypes;
    }
}
