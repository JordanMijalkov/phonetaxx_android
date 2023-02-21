
package com.phonetaxx.firebase.model.plan;

import java.util.HashMap;
import java.util.Map;

public class Recurring {

    private Object aggregateUsage;
    private String interval;
    private Integer intervalCount;
    private Object trialPeriodDays;
    private String usageType;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Object getAggregateUsage() {
        return aggregateUsage;
    }

    public void setAggregateUsage(Object aggregateUsage) {
        this.aggregateUsage = aggregateUsage;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public Object getTrialPeriodDays() {
        return trialPeriodDays;
    }

    public void setTrialPeriodDays(Object trialPeriodDays) {
        this.trialPeriodDays = trialPeriodDays;
    }

    public String getUsageType() {
        return usageType;
    }

    public void setUsageType(String usageType) {
        this.usageType = usageType;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
