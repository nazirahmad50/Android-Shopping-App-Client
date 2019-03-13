package com.nazir.shopping.Model;

public class Favourites {

    private String HookahId;
    private String HookahName;
    private String HookahPrice;
    private String HookahMenuId;
    private String HookahImage;
    private String HookahDiscount;
    private String HookahDescription;
    private String UserPhone;

    public Favourites() {
    }

    public Favourites(String hookahId, String hookahName, String hookahPrice, String hookahMenuId, String hookahImage, String hookahDiscount, String hookahDescription, String userPhone) {
        HookahId = hookahId;
        HookahName = hookahName;
        HookahPrice = hookahPrice;
        HookahMenuId = hookahMenuId;
        HookahImage = hookahImage;
        HookahDiscount = hookahDiscount;
        HookahDescription = hookahDescription;
        UserPhone = userPhone;
    }

    public String getHookahId() {
        return HookahId;
    }

    public void setHookahId(String hookahId) {
        HookahId = hookahId;
    }

    public String getHookahName() {
        return HookahName;
    }

    public void setHookahName(String hookahName) {
        HookahName = hookahName;
    }

    public String getHookahPrice() {
        return HookahPrice;
    }

    public void setHookahPrice(String hookahPrice) {
        HookahPrice = hookahPrice;
    }

    public String getHookahMenuId() {
        return HookahMenuId;
    }

    public void setHookahMenuId(String hookahMenuId) {
        HookahMenuId = hookahMenuId;
    }

    public String getHookahImage() {
        return HookahImage;
    }

    public void setHookahImage(String hookahImage) {
        HookahImage = hookahImage;
    }

    public String getHookahDiscount() {
        return HookahDiscount;
    }

    public void setHookahDiscount(String hookahDiscount) {
        HookahDiscount = hookahDiscount;
    }

    public String getHookahDescription() {
        return HookahDescription;
    }

    public void setHookahDescription(String hookahDescription) {
        HookahDescription = hookahDescription;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
