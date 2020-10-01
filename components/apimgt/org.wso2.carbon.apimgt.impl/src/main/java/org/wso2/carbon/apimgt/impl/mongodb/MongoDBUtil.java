package org.wso2.carbon.apimgt.impl.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.wso2.carbon.apimgt.impl.utils.MongoDBUtils;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public final class MongoDBUtil {

    private static final Log log = LogFactory.getLog(MongoDBUtils.class);
    private static MongoClient mongoClient = null;

    /**
     * Initializes the datasource for mongodb
     */
    public static void initialize() {
        if (mongoClient != null) {
            return;
        }

        synchronized (MongoDBUtil.class) {
            if (mongoClient == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Initializing mongodb datasource");
                }
                ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:admin@wso2-apim" +
                        "-cluster.eowdj.azure.mongodb.net/test?retryWrites=true&w=majority");

                CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
                CodecRegistry codecRegistry = fromRegistries(MongoClientSettings
                        .getDefaultCodecRegistry(), pojoCodecRegistry);
                MongoClientSettings clientSettings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .codecRegistry(codecRegistry)
                        .build();
                mongoClient = MongoClients.create(clientSettings);
            }
        }
    }

    /**
     * Utility method to get a new mongodb database connection
     *
     * @return MongoClient
     */
    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            initialize();
        }
        return mongoClient;
    }

    /**
     * Close mongo client connections
     */
    public static void closeClientConnections() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
