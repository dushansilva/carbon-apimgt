package org.wso2.carbon.apimgt.impl.mongodb;

import java.util.List;

public class CORSConfigurationDocument {
    private boolean corsConfigurationEnabled;
    private List<String> accessControlAllowOrigins;
    private boolean accessControlAllowCredentials;
    private List<String> accessControlAllowHeaders;
    private List<String> accessControlAllowMethods;

    public boolean isCorsConfigurationEnabled() {
        return corsConfigurationEnabled;
    }

    public void setCorsConfigurationEnabled(boolean corsConfigurationEnabled) {
        this.corsConfigurationEnabled = corsConfigurationEnabled;
    }

    public List<String> getAccessControlAllowOrigins() {
        return accessControlAllowOrigins;
    }

    public void setAccessControlAllowOrigins(List<String> accessControlAllowOrigins) {
        this.accessControlAllowOrigins = accessControlAllowOrigins;
    }

    public boolean isAccessControlAllowCredentials() {
        return accessControlAllowCredentials;
    }

    public void setAccessControlAllowCredentials(boolean accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
    }

    public List<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders;
    }

    public void setAccessControlAllowHeaders(List<String> accessControlAllowHeaders) {
        this.accessControlAllowHeaders = accessControlAllowHeaders;
    }

    public List<String> getAccessControlAllowMethods() {
        return accessControlAllowMethods;
    }

    public void setAccessControlAllowMethods(List<String> accessControlAllowMethods) {
        this.accessControlAllowMethods = accessControlAllowMethods;
    }
}
