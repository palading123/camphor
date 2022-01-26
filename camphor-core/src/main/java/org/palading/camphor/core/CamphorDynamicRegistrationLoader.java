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
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Load the dynamic filter under the file address and add the filter to the cache. Currently, only one type of dynamic
 * file filter is supported.
 *
 * @author palading_cr
 * @title CamphorGroovyFileLoader
 * @project camphor
 *
 */
public class CamphorDynamicRegistrationLoader extends CamphorAbstractDynamicFileRegistrationLoader implements
    CamphorDynamicFileLoad {

    private static final String dynamic_filter_type = "dynamic-filter";

    private static final CamphorDynamicRegistrationLoader camphorFilterFileRegistrationLoader =
        new CamphorDynamicRegistrationLoader();

    private static ConcurrentHashMap<String, Long> camphorfilterClassLastModified = new ConcurrentHashMap<String, Long>();

    public static CamphorDynamicRegistrationLoader getCamphorFilterFileRegistrationLoader() {
        return camphorFilterFileRegistrationLoader;
    }

    /**
     * load dynamic filter
     *
     * @author palading_cr
     *
     */
    @Override
    public  void registerScheduledDynamicFileManager(String... directories) throws Exception {
        loadDynamicFiles(camphorDynamicFilter,directories);
    }

    /**
     * loadCacheByFile
     *
     * @author palading_cr
     *
     */
    @Override
    protected void loadCacheByFile(File codeFile, String dynamicKey) throws Exception {
        Class<?> clazz = camphorDynamicComplier.compile(codeFile);
        if (!Modifier.isAbstract(clazz.getModifiers())) {
                camphorfilterClassLastModified.putIfAbsent(dynamicKey, codeFile.lastModified());
        }
    }

    /*
    * Judge whether the current file has changed according to the file lastModified. If the file has changed, it needs to be reloaded
    *
      * @author palading_cr
     *
     */
    @Override
    public void loadCamphorDynamicFile(File file) throws Exception {
        if (null != file) {
            try {
                String camphorHasDynamicFilterKey = file.getAbsolutePath() + file.getName();
                if (camphorfilterClassLastModified.get(camphorHasDynamicFilterKey) != null
                    && (file.lastModified() != camphorfilterClassLastModified.get(camphorHasDynamicFilterKey))) {
                    camphorfilterClassLastModified.remove(camphorHasDynamicFilterKey);
                }
                if (null == camphorfilterClassLastModified.get(camphorHasDynamicFilterKey)) {
                    loadCacheByFile(file, camphorHasDynamicFilterKey);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * manually add the class object to the spring factory and return the instance. throws camphorException with no
     * annotation of Order
     *
     * @author palading_cr
     */
//    @Override
//    public Object getCamphorFileBySpring(ApplicationContext applicationContext, Class clazz) throws Exception {
//        registerSpringBeanDefinition(getDynamicType(), applicationContext, clazz);
//        return applicationContext.getBean(getDynamicType() + clazz.getName());
//    }

    @Override
    protected String getDynamicType() {
        return dynamic_filter_type;
    }

}
