package org.ashot.shellflow.data.utility;

import java.util.List;

public record Recent(List<String> recentlyOpenedFiles, String lastAccessedDirectory) {

}
