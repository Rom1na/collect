package ar.gob.buenosaires.barelevamiento.support;

import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.injection.config.AppDependencyComponent;
import ar.gob.buenosaires.barelevamiento.injection.config.AppDependencyModule;
import ar.gob.buenosaires.barelevamiento.injection.config.DaggerAppDependencyComponent;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowMediaMetadataRetriever;
import org.robolectric.shadows.ShadowMediaPlayer;
import org.robolectric.shadows.util.DataSource;

import ar.gob.buenosaires.barelevamiento.injection.config.DaggerAppDependencyComponent;

import static org.robolectric.Shadows.shadowOf;

public class RobolectricHelpers {

    private RobolectricHelpers() {}

    public static void overrideAppDependencyModule(AppDependencyModule appDependencyModule) {
        AppDependencyComponent testComponent = DaggerAppDependencyComponent.builder()
                .application(RuntimeEnvironment.application)
                .appDependencyModule(appDependencyModule)
                .build();
        ((Collect) RuntimeEnvironment.application).setComponent(testComponent);
    }

    public static AppDependencyComponent getApplicationComponent() {
        return ((Collect) RuntimeEnvironment.application).getComponent();
    }

    public static <T extends FragmentActivity> T createThemedActivity(Class<T> clazz) {
        return createThemedActivity(clazz, R.style.Theme_Collect_Light);
    }

    public static <T extends FragmentActivity> T createThemedActivity(Class<T> clazz, int theme) {
        T activity = Robolectric.setupActivity(clazz);
        activity.setTheme(theme); // Needed so attrs are available

        return activity;
    }

    public static FragmentActivity createThemedActivity() {
        return createThemedActivity(FragmentActivity.class);
    }

    public static <T extends FragmentActivity> ActivityController<T> buildThemedActivity(Class<T> clazz) {
        ActivityController<T> activity = Robolectric.buildActivity(clazz);
        activity.get().setTheme(R.style.Theme_Collect_Light); // Needed so attrs are available

        return activity;
    }

    public static int getCreatedFromResId(ImageButton button) {
        return shadowOf(button.getDrawable()).getCreatedFromResId();
    }

    public static int getCreatedFromResId(Drawable drawable) {
        return shadowOf(drawable).getCreatedFromResId();
    }

    public static DataSource setupMediaPlayerDataSource(String testFile) {
        return setupMediaPlayerDataSource(testFile, 322450);
    }

    public static DataSource setupMediaPlayerDataSource(String testFile, Integer duration) {
        DataSource dataSource = DataSource.toDataSource(testFile);
        ShadowMediaMetadataRetriever.addMetadata(dataSource, MediaMetadataRetriever.METADATA_KEY_DURATION, duration.toString());
        ShadowMediaPlayer.addMediaInfo(dataSource, new ShadowMediaPlayer.MediaInfo(duration, 0));
        return dataSource;
    }

    public static <T extends ViewGroup> T populateRecyclerView(T view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View child = view.getChildAt(i);

            if (child instanceof RecyclerView) {
                child.measure(0, 0);
                child.layout(0, 0, 100, 10000);
                break;
            } else if (child instanceof ViewGroup) {
                populateRecyclerView((ViewGroup) child);
            }
        }

        return view;
    }
}
