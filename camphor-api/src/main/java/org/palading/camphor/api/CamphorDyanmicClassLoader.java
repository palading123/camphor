package org.palading.camphor.api;

public interface CamphorDyanmicClassLoader {

    <T extends ClassLoader> T getClassLoader(Class<T> tClass) throws Exception;

}
