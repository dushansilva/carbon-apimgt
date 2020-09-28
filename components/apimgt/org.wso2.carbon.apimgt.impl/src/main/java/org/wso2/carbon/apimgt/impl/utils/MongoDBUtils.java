package org.wso2.carbon.apimgt.impl.utils;

import com.google.gson.Gson;
//import com.mongodb.MongoClient;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.DeploymentEnvironments;
import org.wso2.carbon.apimgt.api.model.Documentation;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.bson.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.wso2.carbon.apimgt.impl.utils.APIUtil.writeEnvironmentsToArtifact;

public class MongoDBUtils {

    private static final Log log = LogFactory.getLog(MongoDBUtils.class);
    private static MongoCollection<Document> apisCollection = null;
    private static MongoClient mongo = null;

    public static Document createMongoAPIDocument(API api) throws APIManagementException {

        String uuid = UUID.randomUUID().toString();
        Document apiMongoTemplate = new Document("_id", api.getUUID());
        String apiStatus = api.getStatus();
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_NAME, api.getId().getApiName());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_VERSION, api.getId().getVersion());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_IS_DEFAULT_VERSION, String.valueOf(api.isDefaultVersion()));

        apiMongoTemplate.append(APIConstants.API_OVERVIEW_CONTEXT, api.getContext());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_PROVIDER, api.getId().getProviderName());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_DESCRIPTION, api.getDescription());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_WSDL, api.getWsdlUrl());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_WADL, api.getWadlUrl());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_THUMBNAIL_URL, api.getThumbnailUrl());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_STATUS, apiStatus);
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_TEC_OWNER, api.getTechnicalOwner());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_TEC_OWNER_EMAIL, api.getTechnicalOwnerEmail());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_BUSS_OWNER, api.getBusinessOwner());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_BUSS_OWNER_EMAIL, api.getBusinessOwnerEmail());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_VISIBILITY, api.getVisibility());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_VISIBLE_ROLES, api.getVisibleRoles());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_VISIBLE_TENANTS, api.getVisibleTenants());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENDPOINT_SECURED, Boolean.toString(api.isEndpointSecured()));

        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENDPOINT_USERNAME, api.getEndpointUTUsername());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENDPOINT_PASSWORD, api.getEndpointUTPassword());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_TRANSPORTS, api.getTransports());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_INSEQUENCE, api.getInSequence());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_OUTSEQUENCE, api.getOutSequence());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_FAULTSEQUENCE, api.getFaultSequence());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_RESPONSE_CACHING, api.getResponseCache());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_CACHE_TIMEOUT, Integer.toString(api.getCacheTimeout()));

        apiMongoTemplate.append(APIConstants.API_OVERVIEW_REDIRECT_URL, api.getRedirectURL());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_OWNER, api.getApiOwner());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ADVERTISE_ONLY, Boolean.toString(api.isAdvertiseOnly()));

        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENDPOINT_CONFIG, api.getEndpointConfig());

        apiMongoTemplate.append(
                APIConstants.API_OVERVIEW_SUBSCRIPTION_AVAILABILITY, api.getSubscriptionAvailability());
        apiMongoTemplate.append(
                APIConstants.API_OVERVIEW_SUBSCRIPTION_AVAILABLE_TENANTS, api.getSubscriptionAvailableTenants());

        apiMongoTemplate.append(APIConstants.PROTOTYPE_OVERVIEW_IMPLEMENTATION, api.getImplementation());

        apiMongoTemplate.append(APIConstants.API_PRODUCTION_THROTTLE_MAXTPS, api.getProductionMaxTps());
        apiMongoTemplate.append(APIConstants.API_SANDBOX_THROTTLE_MAXTPS, api.getSandboxMaxTps());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_AUTHORIZATION_HEADER, api.getAuthorizationHeader());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_API_SECURITY, api.getApiSecurity());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENABLE_JSON_SCHEMA,
                Boolean.toString(api.isEnabledSchemaValidation()));
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENABLE_STORE, Boolean.toString(api.isEnableStore()));
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_TESTKEY, api.getTestKey());

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
        // This is to support the pluggable version strategy.
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_CONTEXT_TEMPLATE, api.getContextTemplate());
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_VERSION_TYPE, "context");
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_TYPE, api.getType());

        StringBuilder policyBuilder = new StringBuilder();
        for (Tier tier : api.getAvailableTiers()) {
            policyBuilder.append(tier.getName());
            policyBuilder.append("||");
        }

        String policies = policyBuilder.toString();

        if (!"".equals(policies)) {
            policies = policies.substring(0, policies.length() - 2);
            apiMongoTemplate.append(APIConstants.API_OVERVIEW_TIER, policies);
        }

        StringBuilder tiersBuilder = new StringBuilder();
        for (Tier tier : api.getAvailableTiers()) {
            tiersBuilder.append(tier.getName());
            tiersBuilder.append("||");
        }

        String tiers = tiersBuilder.toString();

        if (!"".equals(tiers)) {
            tiers = tiers.substring(0, tiers.length() - 2);
            apiMongoTemplate.append(APIConstants.API_OVERVIEW_TIER, tiers);
        } else {
            apiMongoTemplate.append(APIConstants.API_OVERVIEW_TIER, tiers);
        }

        if (APIConstants.PUBLISHED.equals(apiStatus)) {
            apiMongoTemplate.append(APIConstants.API_OVERVIEW_IS_LATEST, "true");
        }
//        String[] keys = apiMongoTemplate.getAttributeKeys();
//        for (String key : keys) {
//            if (key.contains("URITemplate")) {
//                apiMongoTemplate.removeAttribute(key);
//            }
//        }

        Set<URITemplate> uriTemplateSet = api.getUriTemplates();
        int i = 0;
        for (URITemplate uriTemplate : uriTemplateSet) {
            apiMongoTemplate.append(APIConstants.API_URI_PATTERN + i, uriTemplate.getUriTemplate());
            apiMongoTemplate.append(APIConstants.API_URI_HTTP_METHOD + i, uriTemplate.getHTTPVerb());
            apiMongoTemplate.append(APIConstants.API_URI_AUTH_TYPE + i, uriTemplate.getAuthType());

            i++;

        }
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_ENVIRONMENTS, writeEnvironmentsToArtifact(api));

        apiMongoTemplate.append(APIConstants.API_OVERVIEW_CORS_CONFIGURATION,
                APIUtil.getCorsConfigurationJsonFromDto(api.getCorsConfiguration()));

        //attaching micro-gateway labels to the API
//        attachLabelsToAPIArtifact(artifact, api, tenantDomain);

        //attaching api categories to the API
//        List<APICategory> attachedApiCategories = api.getApiCategories();
//        apiMongoTemplate.removeAttribute(APIConstants.API_CATEGORIES_CATEGORY_NAME);
//        if (attachedApiCategories != null) {
//            for (APICategory category : attachedApiCategories) {
//                apiMongoTemplate.append(APIConstants.API_CATEGORIES_CATEGORY_NAME, category.getName());
//            }
//        }

        //set monetization status (i.e - enabled or disabled)
        apiMongoTemplate.append(
                APIConstants.Monetization.API_MONETIZATION_STATUS, Boolean.toString(api.getMonetizationStatus()));
        //set additional monetization data
        if (api.getMonetizationProperties() != null) {
            apiMongoTemplate.append(APIConstants.Monetization.API_MONETIZATION_PROPERTIES,
                    api.getMonetizationProperties().toJSONString());
        }
        if (api.getKeyManagers() != null) {
            apiMongoTemplate.append(APIConstants.API_OVERVIEW_KEY_MANAGERS, new Gson().toJson(api.getKeyManagers()));
        }

        //check in github code to see this method was removed
//        String apiSecurity = apiMongoTemplate.getAttribute(APIConstants.API_OVERVIEW_API_SECURITY);
//        if (apiSecurity != null && !apiSecurity.contains(APIConstants.DEFAULT_API_SECURITY_OAUTH2) &&
//                !apiSecurity.contains(APIConstants.API_SECURITY_API_KEY)) {
//            apiMongoTemplate.setAttribute(APIConstants.API_OVERVIEW_TIER, "");
//        }

//          set deployments selected
        Set<DeploymentEnvironments> deploymentEnvironments = api.getDeploymentEnvironments();
        String json = new Gson().toJson(deploymentEnvironments);
        apiMongoTemplate.append(APIConstants.API_OVERVIEW_DEPLOYMENTS, json);

        return apiMongoTemplate;
    }

    public static void createConnection() {
//        mongo = new MongoClient( "localhost" , 27017 );
//        MongoDatabase database = mongo.getDatabase("APIM_DB");
//        apisCollection =  database.getCollection("APIs");
        mongo = MongoClients.create("mongodb+srv://admin:admin@wso2-apim-cluster.eowdj.azure.mongodb" +
                        ".net/test?retryWrites=true&w=majority");
        MongoDatabase database = mongo.getDatabase("APIM_DB");
        apisCollection =  database.getCollection("APIs");
    }

    public static void addCollection(Document apiDoc) {
        createConnection();
        apisCollection.insertOne(apiDoc);
        mongo.close();
    }

    public static void saveSwaggerDefinition(API api, String jsonText) {
        createConnection();
        Document retrievedAPI = new Document();
        retrievedAPI.append(APIConstants.API_OVERVIEW_NAME, api.getId().getApiName());
        retrievedAPI.append(APIConstants.API_OVERVIEW_VERSION, api.getId().getVersion());
        retrievedAPI.append(APIConstants.API_OVERVIEW_CONTEXT, api.getContext());
        Document swaggerDoc = new Document("overview_swagger", jsonText);
        apisCollection.updateOne(retrievedAPI, new Document("$set", swaggerDoc));
        mongo.close();
    }

    public static List retrieveAPIs() {
        List apiList = new ArrayList();
        createConnection();
        Document query = new Document();
        // solr query used to retrieve APIs : name=*&type=(HTTP OR WS OR SOAPTOREST OR GRAPHQL OR SOAP)
        // similar mongo query -
        // {"overview_name":{$exists: true},
        // "overview_type": {$in: ["HTTP", "WS", "SOAPTOREST", "GRAPHQL", "SOAP"]}
        // }
        query.append("overview_name", new Document("$exists", true));
        ArrayList<String> types = new ArrayList<>(
                Arrays.asList("HTTP", "SOAP", "SOAPTOREST", "WS", "GRAPHQL")
        );
        query.append("overview_type", new Document("$in", types));
        MongoCursor iterator = apisCollection.find(query).iterator();
        mongo.close();
        while(iterator.hasNext()) {
            Document doc = (Document) iterator.next();
            MongoAPI mongoAPI = new MongoAPI();
            mongoAPI.set_id(doc.getString("_id").toString());
            mongoAPI.setName(doc.getString(APIConstants.API_OVERVIEW_NAME));
            mongoAPI.setContext(doc.getString(APIConstants.API_OVERVIEW_CONTEXT));
            mongoAPI.setDescription(doc.getString(APIConstants.API_OVERVIEW_DESCRIPTION));
            mongoAPI.setType(doc.getString(APIConstants.API_OVERVIEW_TYPE));
            mongoAPI.setLcStatus(doc.getString(APIConstants.API_OVERVIEW_STATUS));
            mongoAPI.setApiOwner(doc.getString(APIConstants.API_OVERVIEW_OWNER));
            apiList.add(mongoAPI);
        }
        return apiList;
    }

    public static void createDocumentation(API api, Documentation documentation) throws APIManagementException {
        Document apiDocumentMongoTemplate = new Document("_id", documentation.getId());
        apiDocumentMongoTemplate.append("apiId", api.getUUID());
        apiDocumentMongoTemplate.append(APIConstants.DOC_NAME, documentation.getName());
        apiDocumentMongoTemplate.append(APIConstants.DOC_SUMMARY, documentation.getSummary());
        apiDocumentMongoTemplate.append(APIConstants.DOC_TYPE, documentation.getType().getType());
        apiDocumentMongoTemplate.append(APIConstants.DOC_VISIBILITY, documentation.getVisibility().name());
        Documentation.DocumentSourceType sourceType = documentation.getSourceType();
        switch (sourceType) {
            case INLINE:
                sourceType = Documentation.DocumentSourceType.INLINE;
                break;
            case MARKDOWN:
                sourceType = Documentation.DocumentSourceType.MARKDOWN;
                break;
            case URL:
                sourceType = Documentation.DocumentSourceType.URL;
                break;
            case FILE: {
                sourceType = Documentation.DocumentSourceType.FILE;
            }
            break;
            default:
                throw new APIManagementException("Unknown sourceType " + sourceType + " provided for documentation");
        }
        if (documentation.getSourceUrl() == null) {
            documentation.setSourceUrl(" ");
        }
        apiDocumentMongoTemplate.append(APIConstants.DOC_SOURCE_TYPE, sourceType.name());
        apiDocumentMongoTemplate.append(APIConstants.DOC_SOURCE_URL, documentation.getSourceUrl());
        apiDocumentMongoTemplate.append(APIConstants.DOC_FILE_PATH, documentation.getFilePath());
        apiDocumentMongoTemplate.append(APIConstants.DOC_OTHER_TYPE_NAME, documentation.getOtherTypeName());
        addCollection(apiDocumentMongoTemplate);
    }

    public static void addDocContent(Documentation documentation, String docName, String inlineContent) {
        createConnection();
        Document retrievedDocument = new Document("_id", documentation.getId());
        Document docContent = new Document();
        docContent.append("documentName", docName);
        docContent.append("content", inlineContent);
        apisCollection.updateOne(retrievedDocument, new Document("$set", docContent));
    }
}