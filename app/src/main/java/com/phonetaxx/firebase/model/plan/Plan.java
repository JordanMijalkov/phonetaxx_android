
package com.phonetaxx.firebase.model.plan;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plan {

    private String price_id;
    private String product_id;
    private String name;
    private Object description;
    private List<Object> images = null;
    private String currency;
    private Integer unit_amount;
    private Recurring recurring;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getPriceId() {
        return price_id;
    }

    public void setPriceId(String priceId) {
        this.price_id = priceId;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProductId(String productId) {
        this.product_id = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public List<Object> getImages() {
        return images;
    }

    public void setImages(List<Object> images) {
        this.images = images;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getUnitAmount() {
        return unit_amount;
    }

    public void setUnitAmount(Integer unitAmount) {
        this.unit_amount = unitAmount;
    }

    public Recurring getRecurring() {
        return recurring;
    }

    public void setRecurring(Recurring recurring) {
        this.recurring = recurring;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
