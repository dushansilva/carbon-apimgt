package org.wso2.carbon.apimgt.impl.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.bson.types.ObjectId;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.APIProductIdentifier;
import org.wso2.carbon.apimgt.api.model.CORSConfiguration;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.utils.MongoAPI;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoDBPersistentDAO {

    private static MongoDBPersistentDAO instance = null;

    public void createApi(API api) throws APIManagementException {
        MongoCollection<MongoDBAPIDocument> collection = getCollection();
        Set<String> visibleRoles = new HashSet<>(Arrays.asList(api.getVisibleRoles().split(",")));
        Set<String> subscriptionAvailableTenants =
                new HashSet<>(Arrays.asList(api.getSubscriptionAvailableTenants().split(",")));
        MongoDBAPIDocument mongoDBAPIDocument = new MongoDBAPIDocument();
        String uuid = UUID.randomUUID().toString();
        mongoDBAPIDocument.setUuid(uuid);
        mongoDBAPIDocument.setName(api.getId().getName());
        mongoDBAPIDocument.setVersion(api.getId().getVersion());
        mongoDBAPIDocument.setDefaultVersion(api.isDefaultVersion());
        mongoDBAPIDocument.setProvider(api.getId().getProviderName());
        mongoDBAPIDocument.setContext(api.getContext());
        mongoDBAPIDocument.setDescription(api.getDescription());
        mongoDBAPIDocument.setWsdlUrl(api.getWsdlUrl());
        mongoDBAPIDocument.setWadlUrl(api.getWadlUrl());
        mongoDBAPIDocument.setThumbnailUrl(api.getThumbnailUrl());
        mongoDBAPIDocument.setStatus(api.getStatus());
        mongoDBAPIDocument.setTechnicalOwner(api.getTechnicalOwner());
        mongoDBAPIDocument.setTechnicalOwnerEmail(api.getTechnicalOwnerEmail());
        mongoDBAPIDocument.setBusinessOwner(api.getBusinessOwner());
        mongoDBAPIDocument.setBusinessOwnerEmail(api.getBusinessOwnerEmail());
        mongoDBAPIDocument.setVisibility(api.getVisibility());
        mongoDBAPIDocument.setVisibleRoles(visibleRoles);
        mongoDBAPIDocument.setEndpointSecured(api.isEndpointSecured());
        mongoDBAPIDocument.setEndpointAuthDigest(api.isEndpointAuthDigest());
        mongoDBAPIDocument.setEndpointUTUsername(api.getEndpointUTUsername());
        mongoDBAPIDocument.setEndpointUTPassword(api.getEndpointUTPassword());
        mongoDBAPIDocument.setTransports(api.getTransports());
        mongoDBAPIDocument.setInSequence(api.getInSequence());
        mongoDBAPIDocument.setOutSequence(api.getOutSequence());
        mongoDBAPIDocument.setFaultSequence(api.getFaultSequence());
        mongoDBAPIDocument.setResponseCache(api.getResponseCache());
        mongoDBAPIDocument.setCacheTimeout(api.getCacheTimeout());
        mongoDBAPIDocument.setRedirectURL(api.getRedirectURL());
        mongoDBAPIDocument.setApiOwner(api.getApiOwner());
        mongoDBAPIDocument.setAdvertiseOnly(api.isAdvertiseOnly());
        mongoDBAPIDocument.setEndpointConfig(api.getEndpointConfig());
        mongoDBAPIDocument.setSubscriptionAvailability(api.getSubscriptionAvailability());
        mongoDBAPIDocument.setSubscriptionAvailableTenants(subscriptionAvailableTenants);
        mongoDBAPIDocument.setImplementation(api.getImplementation());
        mongoDBAPIDocument.setProductionMaxTps(api.getProductionMaxTps());
        mongoDBAPIDocument.setSandboxMaxTps(api.getSandboxMaxTps());
        mongoDBAPIDocument.setAuthorizationHeader(api.getAuthorizationHeader());
        mongoDBAPIDocument.setApiSecurity(api.getApiSecurity());
        mongoDBAPIDocument.setEnableSchemaValidation(api.isEnabledSchemaValidation());
        mongoDBAPIDocument.setEnableStore(api.isEnableStore());
        mongoDBAPIDocument.setTestKey(api.getTestKey());

        //Validate if the API has an unsupported context before setting it in the artifact
        String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        if (APIConstants.SUPER_TENANT_DOMAIN.equals(tenantDomain)) {
            String invalidContext = File.separator + APIConstants.VERSION_PLACEHOLDER;
            if (invalidContext.equals(api.getContextTemplate())) {
                throw new APIManagementException(
                        "API : " + api.getId() + " has an unsupported context : " + api.getContextTemplate());
            }
        } else {
            String invalidContext =
                    APIConstants.TENANT_PREFIX + tenantDomain + File.separator + APIConstants.VERSION_PLACEHOLDER;
            if (invalidContext.equals(api.getContextTemplate())) {
                throw new APIManagementException(
                        "API : " + api.getId() + " has an unsupported context : " + api.getContextTemplate());
            }
        }
        mongoDBAPIDocument.setContextTemplate(api.getContextTemplate());
        mongoDBAPIDocument.setVersionType("context");
        mongoDBAPIDocument.setType(api.getType());

        Set<TiersDocument> tiersDocumentList = new LinkedHashSet<>();
        Set<Tier> availableTiers = api.getAvailableTiers();
        for (Tier tier : availableTiers) {
            TiersDocument tiersDocument = new TiersDocument();
            tiersDocument.setDisplayName(tier.getDisplayName());
            tiersDocument.setDescription(tier.getDescription());
            tiersDocument.setName(tier.getName());
            tiersDocument.setMonetizationAttributes(tier.getMonetizationAttributes());
            tiersDocument.setPolicyContent(tier.getPolicyContent());
            tiersDocument.setRequestCount(tier.getRequestCount());
            tiersDocument.setRequestsPerMin(tier.getRequestsPerMin());
            tiersDocument.setStopOnQuotaReached(tier.isStopOnQuotaReached());
            tiersDocument.setTierAttributes(tier.getTierAttributes());
            tiersDocument.setTierPermission(tier.getTierPermission());
            tiersDocument.setTierPlan(tier.getTierPlan());
            tiersDocument.setTimeUnit(tier.getTimeUnit());
            tiersDocument.setUnitTime(tier.getUnitTime());
            tiersDocumentList.add(tiersDocument);
        }
        mongoDBAPIDocument.setAvailableTiers(tiersDocumentList);
        if (APIConstants.PUBLISHED.equals(api.getStatus())) {
            mongoDBAPIDocument.setLatest(true);
        }

        Set<URITemplateDocument> uriTemplateDocumentList = new LinkedHashSet<URITemplateDocument>();
        Set<URITemplate> uriTemplates = api.getUriTemplates();
        for (URITemplate uriTemplate : uriTemplates) {
            URITemplateDocument uriTemplateDocument = new URITemplateDocument();
            uriTemplateDocument.setAmznResourceName(uriTemplate.getAmznResourceName());
            uriTemplateDocument.setAmznResourceTimeout(uriTemplate.getAmznResourceTimeout());
            uriTemplateDocument.setApplicableLevel(uriTemplate.getApplicableLevel());
            uriTemplateDocument.setAuthType(uriTemplate.getAuthType());
            uriTemplateDocument.setAuthTypes(uriTemplate.getAuthTypesList());
            uriTemplateDocument.setConditionGroups(uriTemplate.getConditionGroups());
            uriTemplateDocument.setHttpVerb(uriTemplate.getHTTPVerb());
            uriTemplateDocument.setHttpVerbs(uriTemplate.getHttpVerbsList());
            uriTemplateDocument.setId(uriTemplate.getId());
            uriTemplateDocument.setMediationScript(uriTemplate.getMediationScript());
            uriTemplateDocument.setMediationScripts(uriTemplate.getMediationScriptList());
            uriTemplateDocument.setResourceSandboxURI(uriTemplate.getResourceSandboxURI());
            uriTemplateDocument.setResourceURI(uriTemplate.getResourceURI());
            uriTemplateDocument.setScope(uriTemplate.getScope());
            uriTemplateDocument.setScopes(uriTemplate.getScopesList());
            uriTemplateDocument.setThrottlingConditions(uriTemplate.getThrottlingConditions());
            uriTemplateDocument.setThrottlingTier(uriTemplate.getThrottlingTier());
            uriTemplateDocument.setThrottlingTiers(uriTemplate.getThrottlingTiers());
            uriTemplateDocument.setUriTemplate(uriTemplate.getUriTemplate());

            Set<APIProductIdentifierDocument> usedByProductsList = new LinkedHashSet<>();
            Set<APIProductIdentifier> usedByProducts = uriTemplate.getUsedByProducts();
            for (APIProductIdentifier identifier : usedByProducts) {
                APIProductIdentifierDocument apiProductIdentifierDocument = new APIProductIdentifierDocument();
                apiProductIdentifierDocument.setApiProductName(identifier.getName());
                apiProductIdentifierDocument.setApplicationId(identifier.getApplicationId());
                apiProductIdentifierDocument.setProductId(identifier.getProductId());
                apiProductIdentifierDocument.setProviderName(identifier.getProviderName());
                apiProductIdentifierDocument.setTier(identifier.getTier());
                apiProductIdentifierDocument.setUuid(identifier.getUUID());
                apiProductIdentifierDocument.setVersion(identifier.getVersion());
                usedByProductsList.add(apiProductIdentifierDocument);
            }
            uriTemplateDocument.setUsedByProducts(usedByProductsList);

            uriTemplateDocumentList.add(uriTemplateDocument);

        }

        mongoDBAPIDocument.setEnvironments(api.getEnvironments());

        CORSConfigurationDocument corsConfigurationDocument = new CORSConfigurationDocument();
        CORSConfiguration corsConfiguration = api.getCorsConfiguration();
        corsConfigurationDocument.setAccessControlAllowCredentials(corsConfiguration.isAccessControlAllowCredentials());
        corsConfigurationDocument.setAccessControlAllowHeaders(corsConfiguration.getAccessControlAllowHeaders());
        corsConfigurationDocument.setAccessControlAllowMethods(corsConfiguration.getAccessControlAllowMethods());
        corsConfigurationDocument.setAccessControlAllowOrigins(corsConfiguration.getAccessControlAllowOrigins());

        mongoDBAPIDocument.setGatewayLabels(api.getGatewayLabels());
        mongoDBAPIDocument.setApiCategories(api.getApiCategories());

        mongoDBAPIDocument.setMonetizationEnabled(api.getMonetizationStatus());
        mongoDBAPIDocument.setMonetizationProperties(api.getMonetizationProperties().toJSONString());

        mongoDBAPIDocument.setKeyManagers(api.getKeyManagers());
        mongoDBAPIDocument.setDeploymentEnvironments(api.getDeploymentEnvironments());
        collection.insertOne(mongoDBAPIDocument);
    }

    public API getAPIs() {
        MongoCollection<MongoDBAPIDocument> collection = getCollection();
        MongoCursor<MongoDBAPIDocument> iterator = collection.find().iterator();
        while (iterator.hasNext()) {
            MongoDBAPIDocument doc = iterator.next();
            System.out.println(doc.getName());
            System.out.println(doc.getAuthorizationHeader());
        }

        return new API(new APIIdentifier("12", "213", "213"));
    }

    private MongoCollection<MongoDBAPIDocument> getCollection() {
        MongoClient mongoClient = MongoDBUtil.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("APIM_DB");
        return database.getCollection("APIs", MongoDBAPIDocument.class);
    }

    /**
     * Method to get the instance of the Mongo DB Persistance .
     *
     * @return {@link MongoDBPersistentDAO} instance
     */
    public static MongoDBPersistentDAO getInstance() {
        if (instance == null) {
            instance = new MongoDBPersistentDAO();
        }
        return instance;
    }
}
