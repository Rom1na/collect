package ar.gob.buenosaires.barelevamiento.injection.config;

import android.app.Application;
import android.telephony.SmsManager;

import org.javarosa.core.reference.ReferenceManager;
import ar.gob.buenosaires.barelevamiento.activities.FormDownloadListActivity;
import ar.gob.buenosaires.barelevamiento.activities.FormEntryActivity;
import ar.gob.buenosaires.barelevamiento.activities.FormHierarchyActivity;
import ar.gob.buenosaires.barelevamiento.activities.FormMapActivity;
import ar.gob.buenosaires.barelevamiento.activities.GeoPointMapActivity;
import ar.gob.buenosaires.barelevamiento.activities.GeoPolyActivity;
import ar.gob.buenosaires.barelevamiento.activities.GoogleDriveActivity;
import ar.gob.buenosaires.barelevamiento.activities.GoogleSheetsUploaderActivity;
import ar.gob.buenosaires.barelevamiento.activities.InstanceUploaderListActivity;
import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;
import ar.gob.buenosaires.barelevamiento.activities.SplashScreenActivity;
import ar.gob.buenosaires.barelevamiento.adapters.InstanceUploaderAdapter;
import ar.gob.buenosaires.barelevamiento.analytics.Analytics;
import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.events.RxEventBus;
import ar.gob.buenosaires.barelevamiento.formentry.ODKView;
import ar.gob.buenosaires.barelevamiento.fragments.DataManagerList;
import ar.gob.buenosaires.barelevamiento.geo.GoogleMapFragment;
import ar.gob.buenosaires.barelevamiento.geo.MapboxMapFragment;
import ar.gob.buenosaires.barelevamiento.geo.OsmDroidMapFragment;
import ar.gob.buenosaires.barelevamiento.fragments.ShowQRCodeFragment;
import ar.gob.buenosaires.barelevamiento.logic.PropertyManager;
import ar.gob.buenosaires.barelevamiento.openrosa.OpenRosaHttpInterface;
import ar.gob.buenosaires.barelevamiento.preferences.AdminPasswordDialogFragment;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.FormManagementPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.FormMetadataFragment;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.IdentityPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.ServerPreferencesFragment;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageMigrationDialog;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageMigrationService;
import ar.gob.buenosaires.barelevamiento.tasks.InstanceServerUploaderTask;
import ar.gob.buenosaires.barelevamiento.tasks.ServerPollingJob;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsNotificationReceiver;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsSender;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsSentBroadcastReceiver;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService;
import ar.gob.buenosaires.barelevamiento.tasks.sms.contracts.SmsSubmissionManagerContract;
import ar.gob.buenosaires.barelevamiento.upload.AutoSendWorker;
import ar.gob.buenosaires.barelevamiento.utilities.AuthDialogUtility;
import ar.gob.buenosaires.barelevamiento.utilities.FormDownloader;
import ar.gob.buenosaires.barelevamiento.widgets.ExStringWidget;
import ar.gob.buenosaires.barelevamiento.widgets.QuestionWidget;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * Dagger component for the application. Should include
 * application level Dagger Modules and be built with Application
 * object.
 *
 * Add an `inject(MyClass myClass)` method here for objects you want
 * to inject into so Dagger knows to wire it up.
 *
 * Annotated with @Singleton so modules can include @Singletons that will
 * be retained at an application level (as this an instance of this components
 * is owned by the Application object).
 *
 * If you need to call a provider directly from the component (in a test
 * for example) you can add a method with the type you are looking to fetch
 * (`MyType myType()`) to this interface.
 *
 * To read more about Dagger visit: https://google.github.io/dagger/users-guide
 **/

@Singleton
@Component(modules = {
        AppDependencyModule.class
})
public interface AppDependencyComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        Builder appDependencyModule(AppDependencyModule testDependencyModule);

        AppDependencyComponent build();
    }

    void inject(Collect collect);

    void inject(SmsService smsService);

    void inject(SmsSender smsSender);

    void inject(SmsSentBroadcastReceiver smsSentBroadcastReceiver);

    void inject(SmsNotificationReceiver smsNotificationReceiver);

    void inject(InstanceUploaderAdapter instanceUploaderAdapter);

    void inject(DataManagerList dataManagerList);

    void inject(PropertyManager propertyManager);

    void inject(FormEntryActivity formEntryActivity);

    void inject(InstanceServerUploaderTask uploader);

    void inject(ServerPreferencesFragment serverPreferencesFragment);

    void inject(FormDownloader formDownloader);

    void inject(ServerPollingJob serverPollingJob);

    void inject(AuthDialogUtility authDialogUtility);

    void inject(FormDownloadListActivity formDownloadListActivity);

    void inject(InstanceUploaderListActivity activity);

    void inject(GoogleDriveActivity googleDriveActivity);

    void inject(GoogleSheetsUploaderActivity googleSheetsUploaderActivity);

    void inject(QuestionWidget questionWidget);

    void inject(ExStringWidget exStringWidget);

    void inject(ODKView odkView);

    void inject(FormMetadataFragment formMetadataFragment);

    void inject(GeoPointMapActivity geoMapActivity);

    void inject(GeoPolyActivity geoPolyActivity);

    void inject(FormMapActivity formMapActivity);

    void inject(OsmDroidMapFragment mapFragment);

    void inject(GoogleMapFragment mapFragment);

    void inject(MapboxMapFragment mapFragment);

    void inject(MainMenuActivity mainMenuActivity);

    void inject(ShowQRCodeFragment showQRCodeFragment);

    void inject(StorageMigrationService storageMigrationService);

    void inject(AutoSendWorker autoSendWorker);

    void inject(StorageMigrationDialog storageMigrationDialog);

    void inject(AdminPasswordDialogFragment adminPasswordDialogFragment);

    void inject(SplashScreenActivity splashScreenActivity);

    void inject(FormHierarchyActivity formHierarchyActivity);

    void inject(FormManagementPreferences formManagementPreferences);

    void inject(IdentityPreferences identityPreferences);

    SmsManager smsManager();

    SmsSubmissionManagerContract smsSubmissionManagerContract();

    RxEventBus rxEventBus();

    OpenRosaHttpInterface openRosaHttpInterface();

    ReferenceManager referenceManager();

    Analytics analytics();

    GeneralSharedPreferences generalSharedPreferences();

    AdminSharedPreferences adminSharedPreferences();
}
