package org.ashot.shellflow.data.util;

import java.util.List;

public record Recents(List<String> recentlyOpenedFiles, String lastAccessedDirectory) {

}
