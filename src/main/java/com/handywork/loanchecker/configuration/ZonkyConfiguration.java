package com.handywork.loanchecker.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Zonky related configuration properties.
 */
@Component
@ConfigurationProperties(prefix = "zonky")
public class ZonkyConfiguration {

    private String serviceUri;
    private String loansMarketplacePath;
    private int batchSize;

    public String getServiceUri() {
        return serviceUri;
    }

    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    public String getLoansMarketplacePath() {
        return loansMarketplacePath;
    }

    public void setLoansMarketplacePath(String loansMarketplacePath) {
        this.loansMarketplacePath = loansMarketplacePath;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return "ZonkyConfiguration{" +
                "serviceUri='" + serviceUri + '\'' +
                ", loansMarketplacePath='" + loansMarketplacePath + '\'' +
                ", batchSize=" + batchSize +
                '}';
    }
}