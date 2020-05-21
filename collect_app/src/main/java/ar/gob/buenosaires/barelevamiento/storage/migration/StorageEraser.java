package ar.gob.buenosaires.barelevamiento.storage.migration;

import ar.gob.buenosaires.barelevamiento.database.ItemsetDbAdapter;
import ar.gob.buenosaires.barelevamiento.storage.StoragePathProvider;
import ar.gob.buenosaires.barelevamiento.storage.StorageSubdirectory;

import java.io.File;

import ar.gob.buenosaires.barelevamiento.database.ItemsetDbAdapter;

public class StorageEraser {

    private final StoragePathProvider storagePathProvider;

    public StorageEraser(StoragePathProvider storagePathProvider) {
        this.storagePathProvider = storagePathProvider;
    }

    void clearCache() {
        deleteDirectory(new File(storagePathProvider.getDirPath(StorageSubdirectory.CACHE)));
    }

    void removeItemsetsDb() {
        removeFile(storagePathProvider.getDirPath(StorageSubdirectory.CACHE) + File.separator + ItemsetDbAdapter.DATABASE_NAME);
    }

    void clearOdkDirOnScopedStorage() {
        deleteDirectory(new File(storagePathProvider.getScopedStorageRootDirPath()));
    }

    void deleteOdkDirFromUnscopedStorage() {
        deleteDirectory(new File(storagePathProvider.getUnscopedStorageRootDirPath()));
    }

    private static void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

    void removeFile(String filePath) {
        new File(filePath).delete();
    }
}
