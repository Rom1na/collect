package ar.gob.buenosaires.barelevamiento.storage.migration;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import ar.gob.buenosaires.barelevamiento.application.Collect;

import javax.inject.Inject;

import ar.gob.buenosaires.barelevamiento.application.Collect;

public class StorageMigrationService extends IntentService {

    public static final String SERVICE_NAME = "StorageMigrationService";

    @Inject
    StorageMigrator storageMigrator;

    public StorageMigrationService() {
        super(SERVICE_NAME);
        Collect.getInstance().getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        storageMigrator.performStorageMigration();
    }
}
