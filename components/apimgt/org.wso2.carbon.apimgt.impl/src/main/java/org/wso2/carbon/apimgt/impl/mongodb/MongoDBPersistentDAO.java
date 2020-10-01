package org.wso2.carbon.apimgt.impl.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.bson.types.ObjectId;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MongoDBPersistentDAO {

    private static MongoDBPersistentDAO instance = null;

    public void createApi(API api) throws APIManagementException {
        MongoCollection collection = getCollection();
        Set<String> visibleRoles = new HashSet<>(Arrays.asList(api.getVisibleRoles().split(",")));
        Set<String> subscriptionAvailableTenants =
                new HashSet<>(Arrays.asList(api.getSubscriptionAvailableTenants().split(",")));
        MongoDBAPIDocument mongoDBAPIDocument = new MongoDBAPIDocument();
        String uuid = UUID.randomUUID().toString();
        mongoDBAPIDocument.setUuid(uuid);
        mongoDBAPIDocument.setName(api.getId().getName());
//        mongoDBAPIDocument.setVersion(api.getId().getVersion());
//        mongoDBAPIDocument.setDefaultVersion(api.isDefaultVersion());
//        mongoDBAPIDocument.setProvider(api.getId().getProviderName());
//        mongoDBAPIDocument.setContext(api.getContext());
//        mongoDBAPIDocument.setDescription(api.getDescription());
//        mongoDBAPIDocument.setWsdlUrl(api.getWsdlUrl());
//        mongoDBAPIDocument.setWadlUrl(api.getWadlUrl());
//        mongoDBAPIDocument.setThumbnailUrl(api.getThumbnailUrl());
//        mongoDBAPIDocument.setStatus(api.getStatus());
//        mongoDBAPIDocument.setTechnicalOwner(api.getTechnicalOwner());
//        mongoDBAPIDocument.setTechnicalOwnerEmail(api.getTechnicalOwnerEmail());
//        mongoDBAPIDocument.setBusinessOwner(api.getBusinessOwner());
//        mongoDBAPIDocument.setBusinessOwnerEmail(api.getBusinessOwnerEmail());
//        mongoDBAPIDocument.setVisibility(api.getVisibility());
//        mongoDBAPIDocument.setVisibleRoles(visibleRoles);
//        mongoDBAPIDocument.setEndpointSecured(api.isEndpointSecured());
//        mongoDBAPIDocument.setEndpointAuthDigest(api.isEndpointAuthDigest());
//        mongoDBAPIDocument.setEndpointUTUsername(api.getEndpointUTUsername());
//        mongoDBAPIDocument.setEndpointUTPassword(api.getEndpointUTPassword());
//        mongoDBAPIDocument.setTransports(api.getTransports());
//        mongoDBAPIDocument.setInSequence(api.getInSequence());
//        mongoDBAPIDocument.setOutSequence(api.getOutSequence());
//        mongoDBAPIDocument.setFaultSequence(api.getFaultSequence());
//        mongoDBAPIDocument.setResponseCache(api.getResponseCache());
//        mongoDBAPIDocument.setCacheTimeout(api.getCacheTimeout());
//        mongoDBAPIDocument.setRedirectURL(api.getRedirectURL());
//        mongoDBAPIDocument.setApiOwner(api.getApiOwner());
//        mongoDBAPIDocument.setAdvertiseOnly(api.isAdvertiseOnly());
//        mongoDBAPIDocument.setEndpointConfig(api.getEndpointConfig());
//        mongoDBAPIDocument.setSubscriptionAvailability(api.getSubscriptionAvailability());
//        mongoDBAPIDocument.setSubscriptionAvailableTenants(subscriptionAvailableTenants);
//        mongoDBAPIDocument.setImplementation(api.getImplementation());
//        mongoDBAPIDocument.setProductionMaxTps(api.getProductionMaxTps());
//        mongoDBAPIDocument.setSandboxMaxTps(api.getSandboxMaxTps());
//        mongoDBAPIDocument.setAuthorizationHeader(api.getAuthorizationHeader());
//        mongoDBAPIDocument.setApiSecurity(api.getApiSecurity());
//        mongoDBAPIDocument.setEnableSchemaValidation(api.isEnabledSchemaValidation());
//        mongoDBAPIDocument.setEnableStore(api.isEnableStore());
//        mongoDBAPIDocument.setTestKey(api.getTestKey());
//
//        //Validate if the API has an unsupported context before setting it in the artifact
//        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
//        if (APIConstants.SUPER_TENANT_DOMAIN.equals(tenantDomain)) {
//            String invalidContext = File.separator + APIConstants.VERSION_PLACEHOLDER;
//            if (invalidContext.equals(api.getContextTemplate())) {
//                throw new APIManagementException(
//                        "API : " + api.getId() + " has an unsupported context : " + api.getContextTemplate());
//            }
//        } else {
//            String invalidContext =
//                    APIConstants.TENANT_PREFIX + tenantDomain + File.separator + APIConstants.VERSION_PLACEHOLDER;
//            if (invalidContext.equals(api.getContextTemplate())) {
//                throw new APIManagementException(
//                        "API : " + api.getId() + " has an unsupported context : " + api.getContextTemplate());
//            }
//        }
//        mongoDBAPIDocument.setContextTemplate(api.getContextTemplate());
//        mongoDBAPIDocument.setVersionType("context");
//        mongoDBAPIDocument.setType(api.getType());
//        mongoDBAPIDocument.setAvailableTiers(api.getAvailableTiers());
//        if (APIConstants.PUBLISHED.equals(api.getStatus())) {
//            mongoDBAPIDocument.setLatest(true);
//        }
//        mongoDBAPIDocument.setUriTemplates(api.getUriTemplates());
//        mongoDBAPIDocument.setEnvironments(api.getEnvironments());
//        mongoDBAPIDocument.setCorsConfiguration(api.getCorsConfiguration());
//        mongoDBAPIDocument.setGatewayLabels(api.getGatewayLabels());
//        mongoDBAPIDocument.setApiCategories(api.getApiCategories());
//
//        mongoDBAPIDocument.setMonetizationEnabled(api.getMonetizationStatus());
//        mongoDBAPIDocument.setMonetizationProperties(api.getMonetizationProperties());
//
//        mongoDBAPIDocument.setKeyManagers(api.getKeyManagers());
//        mongoDBAPIDocument.setDeploymentEnvironments(api.getDeploymentEnvironments());
        collection.insertOne(mongoDBAPIDocument);
    }

    private MongoCollection getCollection() {
        MongoClient mongoClient = MongoDBUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("APIM_DB");
        MongoCollection collection = database.getCollection("APIs");
        return collection;
    }

    /**
     * Method to get the instance of the ApiMgtDAO.
     *
     * @return {@link ApiMgtDAO} instance
     */
    public static MongoDBPersistentDAO getInstance() {
        if (instance == null) {
            instance = new MongoDBPersistentDAO();
        }
        return instance;
    }
}
