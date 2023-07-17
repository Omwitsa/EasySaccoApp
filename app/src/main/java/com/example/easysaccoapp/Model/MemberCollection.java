package com.example.easysaccoapp.Model;

public class MemberCollection {
    private String MemberNo;
    private String Amount;

    public MemberCollection(String memberNo, String amount) {
        MemberNo = memberNo;
        Amount = amount;
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
}
