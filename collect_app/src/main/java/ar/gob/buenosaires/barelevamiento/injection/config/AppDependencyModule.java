package ar.gob.buenosaires.barelevamiento.injection.config;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.webkit.MimeTypeMap;

import org.javarosa.core.reference.ReferenceManager;
import ar.gob.buenosaires.barelevamiento.analytics.Analytics;
import ar.gob.buenosaires.barelevamiento.analytics.FirebaseAnalytics;
import ar.gob.buenosaires.barelevamiento.backgroundwork.CollectBackgroundWorkManager;
import ar.gob.buenosaires.barelevamiento.dao.FormsDao;
import ar.gob.buenosaires.barelevamiento.dao.InstancesDao;
import ar.gob.buenosaires.barelevamiento.events.RxEventBus;
import ar.gob.buenosaires.barelevamiento.formentry.media.AudioHelperFactory;
import ar.gob.buenosaires.barelevamiento.formentry.media.ScreenContextAudioHelperFactory;
import ar.gob.buenosaires.barelevamiento.geo.MapProvider;
import ar.gob.buenosaires.barelevamiento.jobs.CollectJobCreator;
import ar.gob.buenosaires.barelevamiento.metadata.InstallIDProvider;
import ar.gob.buenosaires.barelevamiento.metadata.SharedPreferencesInstallIDProvider;
import ar.gob.buenosaires.barelevamiento.network.NetworkStateProvider;
import ar.gob.buenosaires.barelevamiento.openrosa.CollectThenSystemContentTypeMapper;
import ar.gob.buenosaires.barelevamiento.openrosa.OpenRosaAPIClient;
import ar.gob.buenosaires.barelevamiento.openrosa.OpenRosaHttpInterface;
import ar.gob.buenosaires.barelevamiento.openrosa.okhttp.OkHttpConnection;
import ar.gob.buenosaires.barelevamiento.openrosa.okhttp.OkHttpOpenRosaServerClientProvider;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.MetaSharedPreferencesProvider;
import ar.gob.buenosaires.barelevamiento.storage.StoragePathProvider;
import ar.gob.buenosaires.barelevamiento.storage.StorageStateProvider;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageEraser;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageMigrationRepository;
import ar.gob.buenosaires.barelevamiento.storage.migration.StorageMigrator;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsSubmissionManager;
import ar.gob.buenosaires.barelevamiento.tasks.sms.contracts.SmsSubmissionManagerContract;
import ar.gob.buenosaires.barelevamiento.utilities.ActivityAvailability;
import ar.gob.buenosaires.barelevamiento.utilities.AdminPasswordProvider;
import ar.gob.buenosaires.barelevamiento.utilities.AndroidUserAgent;
import ar.gob.buenosaires.barelevamiento.utilities.DeviceDetailsProvider;
import ar.gob.buenosaires.barelevamiento.utilities.FormListDownloader;
import ar.gob.buenosaires.barelevamiento.network.ConnectivityProvider;
import ar.gob.buenosaires.barelevamiento.utilities.PermissionUtils;
import ar.gob.buenosaires.barelevamiento.utilities.WebCredentialsUtils;
import ar.gob.buenosaires.utilities.BackgroundWorkManager;
import ar.gob.buenosaires.utilities.UserAgentProvider;

import javax.inject.Singleton;

import ar.gob.buenosaires.barelevamiento.backgroundwork.CollectBackgroundWorkManager;
import ar.gob.buenosaires.barelevamiento.dao.FormsDao;
import ar.gob.buenosaires.barelevamiento.dao.InstancesDao;
import ar.gob.buenosaires.barelevamiento.events.RxEventBus;
import ar.gob.buenosaires.barelevamiento.geo.MapProvider;
import ar.gob.buenosaires.barelevamiento.jobs.CollectJobCreator;
import ar.gob.buenosaires.barelevamiento.network.ConnectivityProvider;
import ar.gob.buenosaires.barelevamiento.network.NetworkStateProvider;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.MetaSharedPreferencesProvider;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsSubmissionManager;
import ar.gob.buenosaires.barelevamiento.tasks.sms.contracts.SmsSubmissionManagerContract;
import ar.gob.buenosaires.barelevamiento.utilities.ActivityAvailability;
import ar.gob.buenosaires.barelevamiento.utilities.AdminPasswordProvider;
import ar.gob.buenosaires.barelevamiento.utilities.AndroidUserAgent;
import ar.gob.buenosaires.barelevamiento.utilities.DeviceDetailsProvider;
import ar.gob.buenosaires.barelevamiento.utilities.FormListDownloader;
import ar.gob.buenosaires.barelevamiento.utilities.PermissionUtils;
import ar.gob.buenosaires.barelevamiento.utilities.WebCredentialsUtils;
import ar.gob.buenosaires.utilities.BackgroundWorkManager;
import ar.gob.buenosaires.utilities.UserAgentProvider;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

import static ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys.KEY_INSTALL_ID;

/**
 * Add dependency providers here (annotated with @Provides)
 * for objects you need to inject
 */
@Module
@SuppressWarnings("PMD.CouplingBetweenObjects")
public class AppDependencyModule {

    @Provides
    SmsManager provideSmsManager() {
        return SmsManager.getDefault();
    }

    @Provides
    SmsSubmissionManagerContract provideSmsSubmissionManager(Application application) {
        return new SmsSubmissionManager(application);
    }

    @Provides
    Context context(Application application) {
        return application;
    }

    @Provides
    public InstancesDao provideInstancesDao() {
        return new InstancesDao();
    }

    @Provides
    public FormsDao provideFormsDao() {
        return new FormsDao();
    }

    @Provides
    @Singleton
    RxEventBus provideRxEventBus() {
        return new RxEventBus();
    }

    @Provides
    MimeTypeMap provideMimeTypeMap() {
        return MimeTypeMap.getSingleton();
    }

    @Provides
    @Singleton
    UserAgentProvider providesUserAgent() {
        return new AndroidUserAgent();
    }

    @Provides
    @Singleton
    OpenRosaHttpInterface provideHttpInterface(MimeTypeMap mimeTypeMap, UserAgentProvider userAgentProvider) {
        return new OkHttpConnection(
                new OkHttpOpenRosaServerClientProvider(new OkHttpClient()),
                new CollectThenSystemContentTypeMapper(mimeTypeMap),
                userAgentProvider.getUserAgent()
        );
    }

    @Provides
    public OpenRosaAPIClient provideCollectServerClient(OpenRosaHttpInterface httpInterface, WebCredentialsUtils webCredentialsUtils) {
        return new OpenRosaAPIClient(httpInterface, webCredentialsUtils);
    }

    @Provides
    WebCredentialsUtils provideWebCredentials() {
        return new WebCredentialsUtils();
    }

    @Provides
    FormListDownloader provideDownloadFormListDownloader(
            Application application,
            OpenRosaAPIClient openRosaAPIClient,
            WebCredentialsUtils webCredentialsUtils,
            FormsDao formsDao) {
        return new FormListDownloader(
                application,
                openRosaAPIClient,
                webCredentialsUtils,
                formsDao
        );
    }

    @Provides
    @Singleton
    public Analytics providesAnalytics(Application application, GeneralSharedPreferences generalSharedPreferences) {
        com.google.firebase.analytics.FirebaseAnalytics firebaseAnalyticsInstance = com.google.firebase.analytics.FirebaseAnalytics.getInstance(application);
        return new FirebaseAnalytics(firebaseAnalyticsInstance, generalSharedPreferences);
    }

    @Provides
    public PermissionUtils providesPermissionUtils() {
        return new PermissionUtils();
    }

    @Provides
    public ReferenceManager providesReferenceManager() {
        return ReferenceManager.instance();
    }

    @Provides
    public AudioHelperFactory providesAudioHelperFactory() {
        return new ScreenContextAudioHelperFactory();
    }

    @Provides
    public ActivityAvailability providesActivityAvailability(Context context) {
        return new ActivityAvailability(context);
    }

    @Provides
    @Singleton
    public StorageMigrationRepository providesStorageMigrationRepository() {
        return new StorageMigrationRepository();
    }

    @Provides
    StorageMigrator providesStorageMigrator(StoragePathProvider storagePathProvider, StorageStateProvider storageStateProvider, StorageMigrationRepository storageMigrationRepository, ReferenceManager referenceManager, BackgroundWorkManager backgroundWorkManager, Analytics analytics) {
        StorageEraser storageEraser = new StorageEraser(storagePathProvider);

        return new StorageMigrator(storagePathProvider, storageStateProvider, storageEraser, storageMigrationRepository, GeneralSharedPreferences.getInstance(), referenceManager, backgroundWorkManager, analytics);
    }

    @Provides
    InstallIDProvider providesInstallIDProvider(Context context) {
        SharedPreferences prefs = new MetaSharedPreferencesProvider(context).getMetaSharedPreferences();
        return new SharedPreferencesInstallIDProvider(prefs, GeneralKeys.KEY_INSTALL_ID);
    }

    @Provides
    public DeviceDetailsProvider providesDeviceDetailsProvider(Context context) {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        return new DeviceDetailsProvider() {

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getDeviceId() {
                return telMgr.getDeviceId();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getLine1Number() {
                return telMgr.getLine1Number();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getSubscriberId() {
                return telMgr.getSubscriberId();
            }

            @Override
            @SuppressLint({"MissingPermission", "HardwareIds"})
            public String getSimSerialNumber() {
                return telMgr.getSimSerialNumber();
            }
        };
    }

    @Provides
    @Singleton
    GeneralSharedPreferences providesGeneralSharedPreferences(Context context) {
        return new GeneralSharedPreferences(context);
    }

    @Provides
    @Singleton
    AdminSharedPreferences providesAdminSharedPreferences(Context context) {
        return new AdminSharedPreferences(context);
    }

    @Provides
    @Singleton
    public MapProvider providesMapProvider() {
        return new MapProvider();
    }

    @Provides
    public StorageStateProvider providesStorageStateProvider() {
        return new StorageStateProvider();
    }

    @Provides
    public StoragePathProvider providesStoragePathProvider() {
        return new StoragePathProvider();
    }

    @Provides
    public AdminPasswordProvider providesAdminPasswordProvider() {
        return new AdminPasswordProvider(AdminSharedPreferences.getInstance());
    }

    @Provides
    public CollectJobCreator providesCollectJobCreator() {
        return new CollectJobCreator();
    }

    @Provides
    public BackgroundWorkManager providesBackgroundWorkManager() {
        return new CollectBackgroundWorkManager();
    }

    @Provides
    public NetworkStateProvider providesConnectivityProvider() {
        return new ConnectivityProvider();
    }
}
