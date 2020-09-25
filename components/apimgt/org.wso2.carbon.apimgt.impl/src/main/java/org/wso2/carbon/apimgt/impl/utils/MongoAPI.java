package org.wso2.carbon.apimgt.impl.utils;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class MongoAPI {

    //@BsonProperty("id")
    private String _id;

    //@BsonProperty("overview_name")
    private String name;

//    @BsonProperty("overview_context")
    private String context;

//    @BsonProperty("overview_version")
    private String version;

//    @BsonProperty("overview_apiOwner")
    private String apiOwner;

//    @BsonProperty("overview_status")
    private String lcStatus;

//    @BsonProperty("overview_description")
    private String description;

//    @BsonProperty("overview_type")
    private String type;

    public String get_id() {

        return _id;
    }

    public void set_id(String _id) {

        this._id = _id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getName() {

        return name;
    }

    public void setContext(String context) {

        this.context = context;
    }

    public String getContext() {

        return context;
    }

    public void setVersion(String version) {

        this.version = version;
    }

    public String getVersion() {

        return version;
    }

    public void setApiOwner(String apiOwner) {

        this.apiOwner = apiOwner;
    }

    public String getApiOwner() {

        return apiOwner;
    }

    public void setLcStatus(String lcStatus) {

        this.lcStatus = lcStatus;
    }

    public String getLcStatus() {

        return lcStatus;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getType() {

        return type;
    }
}
