package ar.gob.buenosaires.barelevamiento.activities;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.injection.config.AppDependencyModule;
import ar.gob.buenosaires.barelevamiento.storage.StorageStateProvider;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageMigrationRepository;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageMigrationResult;
import ar.gob.buenosaires.barelevamiento.support.RobolectricHelpers;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.LooperMode;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowEnvironment;

import javax.inject.Singleton;

import dagger.Provides;

import static android.os.Environment.MEDIA_MOUNTED;
import static androidx.lifecycle.Lifecycle.State.DESTROYED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.annotation.LooperMode.Mode.PAUSED;

@RunWith(AndroidJUnit4.class)
@LooperMode(PAUSED)
public class MainMenuActivityTest {

    @Before
    public void setUp() throws Exception {
        ShadowEnvironment.setExternalStorageState(MEDIA_MOUNTED); // Required for ODK directories to be created
    }

    @Test
    public void pressingConfigureQRCode_launchesScanQRCodeActivity() {
        ActivityScenario<MainMenuActivity> firstActivity = ActivityScenario.launch(MainMenuActivity.class);
        firstActivity.onActivity(activity -> {
            MenuItem item = new RoboMenuItem(R.id.menu_configure_qr_code);
            activity.onOptionsItemSelected(item);

            Intent expectedIntent = new Intent(activity, ScanQRCodeActivity.class);
            Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
            assertThat(expectedIntent.getComponent(), equalTo(actual.getComponent()));
        });
    }

    @Test
    public void whenStorageMigrationIsFinished_storageCompletionBannerOnlyShowsInOneInstance() {
        whenStorageMigrationIsFinished();

        ActivityScenario<MainMenuActivity> firstActivity = ActivityScenario.launch(MainMenuActivity.class);
        firstActivity.onActivity(activity -> {
            assertThat(activity.findViewById(R.id.storageMigrationBanner).getVisibility(), equalTo(View.VISIBLE));
        });

        firstActivity.moveToState(DESTROYED);
        ActivityScenario<MainMenuActivity> secondActivity = ActivityScenario.launch(MainMenuActivity.class);
        secondActivity.onActivity(activity -> {
            assertThat(activity.findViewById(R.id.storageMigrationBanner).getVisibility(), equalTo(View.GONE));
        });
    }

    private void whenStorageMigrationIsFinished() {
        RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule() {
            @Provides
            @Singleton
            public StorageMigrationRepository providesStorageMigrationRepository() {
                StorageMigrationRepository storageMigrationRepository = new StorageMigrationRepository();
                storageMigrationRepository.setResult(StorageMigrationResult.SUCCESS);
                return storageMigrationRepository;
            }

            @Override
            public StorageStateProvider providesStorageStateProvider() {
                StorageStateProvider storageStateProvider = new StorageStateProvider();
                storageStateProvider.enableUsingScopedStorage();
                return storageStateProvider;
            }
        });
    }
}
