package com.nazir.shopping.Model;

import java.util.List;

public class RequestUserInfo {

    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String paymentmethod;
    private String paymentstate;
    private String latlng;
    private List<Order> hookahs; //list of hookahs order

    public RequestUserInfo() {
    }

    public RequestUserInfo(String phone, String name, String address, String total, String status, String comment, String paymentmethod, String paymentstate, String latlng, List<Order> hookahs) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.paymentmethod = paymentmethod;
        this.paymentstate = paymentstate;
        this.latlng = latlng;
        this.hookahs = hookahs;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPaymentstate() {
        return paymentstate;
    }

    public void setPaymentstate(String paymentstate) {
        this.paymentstate = paymentstate;
    }

    public List<Order> getHookahs() {
        return hookahs;
    }

    public void setHookahs(List<Order> hookahs) {
        this.hookahs = hookahs;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        this.paymentmethod = paymentmethod;
    }
}
