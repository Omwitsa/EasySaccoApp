package com.example.easysaccoapp.Model;

import java.util.ArrayList;
import java.util.List;

public class DailyReport {
    private  String Transaction;
    private Double Total;
    private List<MemberCollection> MemberCollection;

    public DailyReport(String transaction, Double total, List<MemberCollection> memberCollection) {
        Transaction = transaction;
        Total = total;
        MemberCollection = memberCollection;
    }

    public String getTransaction() {
        return Transaction;
    }

    public void setTransaction(String transaction) {
        Transaction = transaction;
    }

    public Double getTotal() {
        return Total;
    }

    public void setTotal(Double total) {
        Total = total;
    }

    public List<MemberCollection> getMemberCollection() {
        return MemberCollection;
    }

    public void setMemberCollection(List<MemberCollection> memberCollection) {
        MemberCollection = memberCollection;
    }
}
