package ar.gob.buenosaires.barelevamiento.formentry.saving;

import android.net.Uri;

import ar.gob.buenosaires.barelevamiento.analytics.Analytics;
import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.tasks.SaveFormToDisk;
import ar.gob.buenosaires.barelevamiento.tasks.SaveToDiskResult;
import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.tasks.SaveFormToDisk;
import ar.gob.buenosaires.barelevamiento.tasks.SaveToDiskResult;

public class DiskFormSaver implements FormSaver {

    @Override
    public SaveToDiskResult save(Uri instanceContentURI, FormController formController, boolean shouldFinalize, boolean exitAfter, String updatedSaveName, ProgressListener progressListener, Analytics analytics) {
        SaveFormToDisk saveFormToDisk = new SaveFormToDisk(formController, exitAfter, shouldFinalize, updatedSaveName, instanceContentURI, analytics);
        return saveFormToDisk.saveForm(progressListener);
    }
}
