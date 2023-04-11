package com.example.easysaccoapp.Model;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String Message;
    @SerializedName("data")
    private String Data;

    public Response(boolean success, String message, String data) {
        this.success = success;
        Message = message;
        Data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
