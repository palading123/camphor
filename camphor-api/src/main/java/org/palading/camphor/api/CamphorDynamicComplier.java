package org.palading.camphor.api;

import java.io.File;

public interface CamphorDynamicComplier {

    Class compile(File file) throws Exception;
}
