package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoanShareTypes {
    @SerializedName("loanTypes")
    private List<String> loanTypes;
    @SerializedName("shareTypes")
    private List<String> shareTypes;

    public LoanShareTypes(List<String> loanTypes, List<String> shareTypes) {
        this.loanTypes = loanTypes;
        this.shareTypes = shareTypes;
    }

    public List<String> getLoanTypes() {
        return loanTypes;
    }

    public void setLoanTypes(List<String> loanTypes) {
        this.loanTypes = loanTypes;
    }

    public List<String> getShareTypes() {
        return shareTypes;
    }

    public void setShareTypes(List<String> shareTypes) {
        this.shareTypes = shareTypes;
    }
}
