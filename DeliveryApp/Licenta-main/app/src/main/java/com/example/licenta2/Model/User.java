package com.example.licenta2.Model;

public class User {

    public String name;
    public String email;
    public String fullAddress;
    public Boolean admin = false;
    public GeocodingCoordinates coordinates;

    public User() { }

    public User(String name, String email, String fullAddress, Boolean admin, GeocodingCoordinates coordinates) {
        this.name = name;
        this.email = email;
        this.fullAddress = fullAddress;
        this.admin = admin;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }


    public String getFullAddress() {
        return fullAddress;
    }
    public void setFullAddress(String address) {
        this.fullAddress = address;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public GeocodingCoordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeocodingCoordinates coordinates) {
        this.coordinates = coordinates;
    }
}
