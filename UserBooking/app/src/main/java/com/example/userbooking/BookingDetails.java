package com.example.userbooking;


public class BookingDetails {

    private String username,email,mobileNo,bookingDate,bookingTime;


    public BookingDetails() {
        //empty constructor needed
    }

    public BookingDetails(String username,String email,String mobileNo,String bookingDate,String bookingTime) {
        this.username = username;
        this.email= email;
        this.mobileNo = mobileNo;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }

    public String getBookingDate() {
        return this.bookingDate;
    }
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBookingTime() {
        return this.bookingTime;
    }
    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }


}

