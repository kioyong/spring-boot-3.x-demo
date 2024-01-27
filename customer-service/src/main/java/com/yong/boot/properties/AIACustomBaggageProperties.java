package com.yong.boot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties("custom.baggage")
public class AIACustomBaggageProperties {

    private Map<String, String> correlationFieldsAlias = new HashMap<>();


    private List<String> remoteFields = new ArrayList<>();


    public Map<String, String> getCorrelationFieldsAlias() {
        return correlationFieldsAlias;
    }

    public void setCorrelationFieldsAlias(Map<String, String> correlationFieldsAlias) {
        this.correlationFieldsAlias = correlationFieldsAlias;
    }


    public List<String> getRemoteFields() {
        return remoteFields;
    }

    public void setRemoteFields(List<String> remoteFields) {
        this.remoteFields = remoteFields;
    }
}
