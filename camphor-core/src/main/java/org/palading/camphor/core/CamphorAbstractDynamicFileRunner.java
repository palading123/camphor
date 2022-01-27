/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.camphor.core;

import org.apache.commons.lang3.StringUtils;
import org.palading.camphor.api.CamphorDynamicComplier;
import org.palading.camphor.api.CamphorDynamicFileRunner;
import org.palading.camphor.common.spi.CamphorExtendClassLoader;
import org.palading.camphor.dynamic.groovy.CamphorGroovyCheckFilter;
import org.palading.camphor.common.spi.CamphorExtendClassLoader;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;

/**
 * @author palading_cr
 * @title CamphorAbstractDynamicFileRunner
 * @project camphor-core
 */
public abstract class CamphorAbstractDynamicFileRunner implements CamphorDynamicFileRunner {


    // protected abstract void dynamicFileLoad(CamphorServerProperties camphorServerProperties, ApplicationContext
    // applicationContext,
    // CamphorCache camphorCache, String dynamicPath) throws Exception;
    //
    /**
     * load the dynamic filters
     *
     * @author palading_cr
     *
     */
    protected void dynamicFileLoad(String dynamicFilePath,String dynamicFileType) throws Exception {
        loadDyanmic(dynamicFilePath, dynamicFileType, getCamphorDynamicFileLoad());
    }

    protected abstract CamphorDynamicFileLoad getCamphorDynamicFileLoad();

    /**
     * check file path
     *
     * @author palading_cr /3
     */
    private void camphorDynamicFileCheck(String dynamicFilePath) throws Exception {
        Optional
            .of(dynamicFilePath)
            .filter(StringUtils::isNotEmpty)
            .orElseThrow(
                () -> new Exception("CamphorAbstractDynamicFileRunner[camphorDynamicFileCheck] path[" + dynamicFilePath
                    + "] not exists"));
        Optional
            .of(new File(dynamicFilePath.split(",")[0]))
            .filter(File::isDirectory)
            .orElseThrow(
                () -> new Exception("CamphorAbstractDynamicFileRunner[camphorDynamicFileCheck]dynamicFilePath is not file path"));
    }

    /**
     * load dynamic file
     *
     * @author palading_cr
     *
     */
    protected void loadDyanmic(String dynamicFilePath, String dynamicFileType,CamphorDynamicFileLoad camphorDynamicFileLoad) throws Exception {
        CamphorDynamicFileFactory camphorDynamicFileFactory = new CamphorDynamicFileFactory(dynamicFileType);
        CamphorDynamicFileLoader camphorDynamicFileLoader = new CamphorDynamicFileLoader(camphorDynamicFileLoad);
        camphorDynamicFileLoader.registerDynamicFileManager(camphorDynamicFileFactory,dynamicFilePath);
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public void loadDynamicFile(String dynamicFilePath,String dynamicFileType)
        throws Exception {
        try {
            camphorDynamicFileCheck(dynamicFilePath);
            dynamicFileLoad(dynamicFilePath,dynamicFileType);
        } catch (Exception e) {
            throw e;
        }
    }

    class CamphorDynamicFileLoader {

        private CamphorDynamicFileLoad camphorDynamicFileLoad;

        public CamphorDynamicFileLoader(CamphorDynamicFileLoad camphorDynamicFileLoad) {
            this.camphorDynamicFileLoad = camphorDynamicFileLoad;
        }

        public <T> void registerDynamicFileManager(CamphorDynamicFileFactory camphorDynamicFileFactory,
             String... directories) throws Exception {
            camphorDynamicFileLoad.setFilenameFilter(camphorDynamicFileFactory.getFilenameFilter());
            camphorDynamicFileLoad.setCamphorDynamicFileComplier(camphorDynamicFileFactory.getCamphorDynamicFileComplier());
            camphorDynamicFileLoad.registerDynamicFileManager(directories);
        }

    }

    class CamphorDynamicFileFactory {
        private static final String camphorGroovyFilterType = "groovy";
        private FilenameFilter filenameFilter;
        private CamphorDynamicComplier camphorDynamicComplier;

        public CamphorDynamicFileFactory(String dynamicFileType) {
            String camphorDynamicFileType =
                Optional.ofNullable(dynamicFileType).orElse(camphorGroovyFilterType);
            if (camphorDynamicFileType.equals(camphorGroovyFilterType)) {
                this.filenameFilter = new CamphorGroovyCheckFilter();
            }
            this.camphorDynamicComplier = getCamphorDynamicFileComplier(camphorDynamicFileType);
        }

        private CamphorDynamicComplier getCamphorDynamicFileComplier(String camphorDynamicFileType) {
            return CamphorExtendClassLoader.getCamphorExtendClassLoaderInstance().getExtendClassInstance(
                CamphorDynamicComplier.class, camphorDynamicFileType);
        }

        public FilenameFilter getFilenameFilter() {
            return filenameFilter;
        }

        public CamphorDynamicComplier getCamphorDynamicFileComplier() {
            return camphorDynamicComplier;
        }
    }

}
