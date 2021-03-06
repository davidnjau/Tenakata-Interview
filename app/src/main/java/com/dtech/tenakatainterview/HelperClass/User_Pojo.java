package com.dtech.tenakatainterview.HelperClass;

public class User_Pojo {

    private String name;
    private String age;
    private String marital_status;
    private String photo_url;
    private String height;
    private double latitude;
    private double longitude;
    private String iq_rating;
    private String gender;
    private String keyId;

    public User_Pojo(String name, String age, String marital_status, String photo_url, String height, double latitude, double longitude, String iq_rating, String gender, String keyId) {
        this.name = name;
        this.age = age;
        this.marital_status = marital_status;
        this.photo_url = photo_url;
        this.height = height;
        this.latitude = latitude;
        this.longitude = longitude;
        this.iq_rating = iq_rating;
        this.gender = gender;
        this.keyId = keyId;
    }

    public User_Pojo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIq_rating() {
        return iq_rating;
    }

    public void setIq_rating(String iq_rating) {
        this.iq_rating = iq_rating;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
