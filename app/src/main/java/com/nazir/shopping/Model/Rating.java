package com.nazir.shopping.Model;

public class Rating {

    private String userPhone;
    private String hookahid;
    private String ratevalue;
    private String comment;

    public Rating() {
    }

    public Rating(String userPhone, String hookahid, String ratevalue, String comment) {
        this.userPhone = userPhone;
        this.hookahid = hookahid;
        this.ratevalue = ratevalue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getHookahid() {
        return hookahid;
    }

    public void setHookahid(String hookahid) {
        this.hookahid = hookahid;
    }

    public String getRatevalue() {
        return ratevalue;
    }

    public void setRatevalue(String ratevalue) {
        this.ratevalue = ratevalue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
