package com.gohachi.tugcair;

public class Person extends PersonId{

    public Person(){

    }

    public String getNo_card() {
        return no_card;
    }

    public void setNo_card(String no_card) {
        this.no_card = no_card;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNo_phone() {
        return no_phone;
    }

    public void setNo_phone(String no_phone) {
        this.no_phone = no_phone;
    }

    public String getAddress_now() {
        return address_now;
    }

    public void setAddress_now(String address_now) {
        this.address_now = address_now;
    }

    public Person(String fullname, String no_card, String address, String no_phone, String locationcoord) {
        this.fullname = fullname;
        this.no_card = no_card;
        this.address = address;
        this.no_phone = no_phone;
        this.locationcoord = locationcoord;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLocationcoord() {
        return locationcoord;
    }

    public void setLocationcoord(String locationcoord) {
        this.locationcoord = locationcoord;
    }

    public String getFilename_signature() {
        return filename_signature;
    }

    public void setFilename_signature(String filename_signature) {
        this.filename_signature = filename_signature;
    }

    String fullname;
    String no_card;
    String address;
    String address_now;
    String no_phone;
    String locationcoord;
    String filename_signature;


}
