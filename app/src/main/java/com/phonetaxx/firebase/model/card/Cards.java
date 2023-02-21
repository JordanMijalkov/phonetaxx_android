
package com.phonetaxx.firebase.model.card;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cards {

    private String object;
    private List<Datum> data = null;
    private Boolean hasMore;
    private String url;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
