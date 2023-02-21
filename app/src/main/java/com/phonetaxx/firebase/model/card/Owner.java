
package com.phonetaxx.firebase.model.card;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Owner {

    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("verified_address")
    @Expose
    private Object verifiedAddress;
    @SerializedName("verified_email")
    @Expose
    private Object verifiedEmail;
    @SerializedName("verified_name")
    @Expose
    private Object verifiedName;
    @SerializedName("verified_phone")
    @Expose
    private Object verifiedPhone;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public Object getVerifiedAddress() {
        return verifiedAddress;
    }

    public void setVerifiedAddress(Object verifiedAddress) {
        this.verifiedAddress = verifiedAddress;
    }

    public Object getVerifiedEmail() {
        return verifiedEmail;
    }

    public void setVerifiedEmail(Object verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    public Object getVerifiedName() {
        return verifiedName;
    }

    public void setVerifiedName(Object verifiedName) {
        this.verifiedName = verifiedName;
    }

    public Object getVerifiedPhone() {
        return verifiedPhone;
    }

    public void setVerifiedPhone(Object verifiedPhone) {
        this.verifiedPhone = verifiedPhone;
    }

}
