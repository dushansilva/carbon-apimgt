package org.wso2.carbon.apimgt.impl.mongodb;

public class APIProductIdentifierDocument {

    private String providerName;
    private String apiProductName;
    //In this initial api product implementation versioning is not supported, we are setting a default version to all api products
    //however we will create these models in such a way so that versioning can be easily introduced later.
    private String version;
    private String tier;
    private String applicationId;
    private String uuid;
    private int productId;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getApiProductName() {
        return apiProductName;
    }

    public void setApiProductName(String apiProductName) {
        this.apiProductName = apiProductName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
