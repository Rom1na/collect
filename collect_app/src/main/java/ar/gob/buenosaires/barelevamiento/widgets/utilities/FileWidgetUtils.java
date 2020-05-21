package ar.gob.buenosaires.barelevamiento.widgets.utilities;

import androidx.annotation.NonNull;

import ar.gob.buenosaires.barelevamiento.utilities.FileUtil;

import java.io.File;

import ar.gob.buenosaires.barelevamiento.utilities.FileUtil;

public class FileWidgetUtils {

    private FileWidgetUtils() {

    }

    public static String getDestinationPathFromSourcePath(@NonNull String sourcePath, String instanceFolder, FileUtil fileUtil) {
        String extension = sourcePath.substring(sourcePath.lastIndexOf('.'));
        return instanceFolder + File.separator + fileUtil.getRandomFilename() + extension;
    }
}
