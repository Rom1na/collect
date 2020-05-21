package ar.gob.buenosaires.barelevamiento.formentry.saving;

import android.net.Uri;

import ar.gob.buenosaires.barelevamiento.analytics.Analytics;
import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.tasks.SaveToDiskResult;

public interface FormSaver {
    SaveToDiskResult save(Uri instanceContentURI, FormController formController, boolean shouldFinalize, boolean exitAfter, String updatedSaveName, ProgressListener progressListener, Analytics analytics);

    interface ProgressListener {
        void onProgressUpdate(String message);
    }
}
