package com.nazir.shopping.Model;

public class User {

    private String name;
    private String password;
    private String phone;
    private String isstaff;
    private String securecode;
    private String address;
    private String postcode;
    private String city;




    public User(String name, String password, String securecode) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.isstaff = "false";
        this.securecode = securecode;

    }

    public User() {
    }

    public String getIsstaff() {
        return isstaff;
    }

    public void setIsstaff(String isstaff) {
        this.isstaff = isstaff;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurecode() {
        return securecode;
    }

    public void setSecurecode(String securecode) {
        this.securecode = securecode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
