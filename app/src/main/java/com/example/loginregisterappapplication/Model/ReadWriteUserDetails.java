package com.example.loginregisterappapplication.Model;

public class ReadWriteUserDetails {
    public String doB;
    public String gender;
    public String mobile;

    //Constructor
    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile) {
        this.doB = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
