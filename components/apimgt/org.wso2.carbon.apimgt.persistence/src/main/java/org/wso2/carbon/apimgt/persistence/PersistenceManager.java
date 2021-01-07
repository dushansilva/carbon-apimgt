/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.apimgt.persistence;

import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlParseResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.persistence.internal.ServiceReferenceHolder;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Properties;

public class PersistenceManager {

    private static APIPersistence apiPersistenceInstance;
    private static TomlParseResult tomlParseResult = null;
    private static Log log = LogFactory.getLog(PersistenceManager.class);

    public static APIPersistence getPersistenceInstance(Properties properties) {

        synchronized (RegistryPersistenceImpl.class) {
            if (apiPersistenceInstance == null) {
                if (tomlParseResult == null) {
                    Path source = Paths.get(CarbonUtils.getCarbonConfigDirPath() + File.separator + "deployment.toml");
                    try {
                        tomlParseResult = Toml.parse(source);
                    } catch (IOException e) {
                        log.error("error when parsing toml ");
                    }
                }
                String databaseType = tomlParseResult.getString("database.reg_db.type");
                if ("mongodb".equalsIgnoreCase(databaseType)) {
                    apiPersistenceInstance = ServiceReferenceHolder.getInstance().getApiPersistence();
                } else {
                    if (persistence == null) {
                        persistence = new RegistryPersistenceImpl(properties);
                    }
                }
            }
            return apiPersistenceInstance;
        }
        return persistence;
    }
}
