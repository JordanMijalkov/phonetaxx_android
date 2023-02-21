
package com.phonetaxx.firebase.model.card;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card_ {

    @SerializedName("exp_month")
    @Expose
    private Integer expMonth;
    @SerializedName("exp_year")
    @Expose
    private Integer expYear;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("address_zip_check")
    @Expose
    private String addressZipCheck;
    @SerializedName("cvc_check")
    @Expose
    private String cvcCheck;
    @SerializedName("funding")
    @Expose
    private String funding;
    @SerializedName("fingerprint")
    @Expose
    private String fingerprint;
    @SerializedName("three_d_secure")
    @Expose
    private String threeDSecure;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("address_line1_check")
    @Expose
    private Object addressLine1Check;
    @SerializedName("tokenization_method")
    @Expose
    private Object tokenizationMethod;
    @SerializedName("dynamic_last4")
    @Expose
    private Object dynamicLast4;

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAddressZipCheck() {
        return addressZipCheck;
    }

    public void setAddressZipCheck(String addressZipCheck) {
        this.addressZipCheck = addressZipCheck;
    }

    public String getCvcCheck() {
        return cvcCheck;
    }

    public void setCvcCheck(String cvcCheck) {
        this.cvcCheck = cvcCheck;
    }

    public String getFunding() {
        return funding;
    }

    public void setFunding(String funding) {
        this.funding = funding;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getThreeDSecure() {
        return threeDSecure;
    }

    public void setThreeDSecure(String threeDSecure) {
        this.threeDSecure = threeDSecure;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getAddressLine1Check() {
        return addressLine1Check;
    }

    public void setAddressLine1Check(Object addressLine1Check) {
        this.addressLine1Check = addressLine1Check;
    }

    public Object getTokenizationMethod() {
        return tokenizationMethod;
    }

    public void setTokenizationMethod(Object tokenizationMethod) {
        this.tokenizationMethod = tokenizationMethod;
    }

    public Object getDynamicLast4() {
        return dynamicLast4;
    }

    public void setDynamicLast4(Object dynamicLast4) {
        this.dynamicLast4 = dynamicLast4;
    }

}
