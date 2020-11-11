///*
// * Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
// *
// * WSO2 Inc. licenses this file to you under the Apache License,
// * Version 2.0 (the "License"); you may not use this file except
// * in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package org.wso2.carbon.apimgt.persistence.documents;
//
//import org.apache.commons.lang3.StringUtils;
//import org.bson.codecs.pojo.annotations.BsonId;
//import org.bson.codecs.pojo.annotations.BsonProperty;
//import org.bson.types.ObjectId;
//import org.json.simple.JSONObject;
//import org.wso2.carbon.apimgt.api.model.APICategory;
//import org.wso2.carbon.apimgt.api.model.APIEndpoint;
//import org.wso2.carbon.apimgt.api.model.AuthorizationPolicy;
//import org.wso2.carbon.apimgt.api.model.DeploymentEnvironments;
//import org.wso2.carbon.apimgt.api.model.Documentation;
//import org.wso2.carbon.apimgt.api.model.Label;
//import org.wso2.carbon.apimgt.api.model.ResourceFile;
//import org.wso2.carbon.apimgt.api.model.Scope;
//import org.wso2.carbon.apimgt.api.model.policy.Policy;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//
//public class MongoDBAPIDocument2 extends MongoDBAPIDocument {
//
//    @BsonProperty(value = "_id")
//    @BsonId
//    private ObjectId mongodbUuId;
//
//    public ObjectId getMongodbUuId() {
//        return mongodbUuId;
//    }
//
//    public void setMongodbUuId(ObjectId mongodbUuId) {
//        this.mongodbUuId = mongodbUuId;
//    }
//
//    @Override
//    public String getUuid() {
//        return mongodbUuId.toString();
//    }
//}
