
package com.phonetaxx.firebase.model.plan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result {

    private List<Plan> plans = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
