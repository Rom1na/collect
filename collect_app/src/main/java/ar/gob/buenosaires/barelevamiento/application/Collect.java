/*
 * Copyright (C) 2017 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package ar.gob.buenosaires.barelevamiento.application;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobManagerCreateException;

import net.danlew.android.joda.JodaTimeAndroid;

import ar.gob.buenosaires.barelevamiento.BuildConfig;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.dao.FormsDao;
import ar.gob.buenosaires.barelevamiento.external.ExternalDataManager;
import ar.gob.buenosaires.barelevamiento.injection.config.AppDependencyComponent;
import ar.gob.buenosaires.barelevamiento.injection.config.DaggerAppDependencyComponent;
import ar.gob.buenosaires.barelevamiento.jobs.CollectJobCreator;
import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.logic.PropertyManager;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.AutoSendPreferenceMigrator;
import ar.gob.buenosaires.barelevamiento.preferences.FormMetadataMigrator;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.preferences.PrefMigrator;
import ar.gob.buenosaires.barelevamiento.storage.StoragePathProvider;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsNotificationReceiver;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsSentBroadcastReceiver;
import ar.gob.buenosaires.barelevamiento.utilities.FileUtils;
import ar.gob.buenosaires.barelevamiento.utilities.LocaleHelper;
import ar.gob.buenosaires.barelevamiento.utilities.NotificationUtils;
import ar.gob.buenosaires.utilities.UserAgentProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Locale;

import javax.inject.Inject;

import ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsSender;
import timber.log.Timber;

/**
 * The Open Data Kit Collect application.
 *
 * @author carlhartung
 */
public class Collect extends Application {

    public static final String DEFAULT_FONTSIZE = "21";
    public static final int DEFAULT_FONTSIZE_INT = 21;

    public static String defaultSysLanguage;
    private static Collect singleton;

    @Nullable
    private FormController formController;
    private ExternalDataManager externalDataManager;
    private AppDependencyComponent applicationComponent;

    @Inject
    UserAgentProvider userAgentProvider;

    @Inject
    public CollectJobCreator collectJobCreator;

    public static Collect getInstance() {
        return singleton;
    }

    public static int getQuestionFontsize() {
        // For testing:
        Collect instance = Collect.getInstance();
        if (instance == null) {
            return Collect.DEFAULT_FONTSIZE_INT;
        }

        return Integer.parseInt(String.valueOf(GeneralSharedPreferences.getInstance().get(GeneralKeys.KEY_FONT_SIZE)));
    }

    /**
     * Predicate that tests whether a directory path might refer to an
     * ODK Tables instance data directory (e.g., for media attachments).
     */
    public static boolean isODKTablesInstanceDataDirectory(File directory) {
        /*
         * Special check to prevent deletion of files that
         * could be in use by ODK Tables.
         */
        String dirPath = directory.getAbsolutePath();
        StoragePathProvider storagePathProvider = new StoragePathProvider();
        if (dirPath.startsWith(storagePathProvider.getStorageRootDirPath())) {
            dirPath = dirPath.substring(storagePathProvider.getStorageRootDirPath().length());
            String[] parts = dirPath.split(File.separatorChar == '\\' ? "\\\\" : File.separator);
            // [appName, instances, tableId, instanceId ]
            if (parts.length == 4 && parts[1].equals("instances")) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public FormController getFormController() {
        return formController;
    }

    public void setFormController(@Nullable FormController controller) {
        formController = controller;
    }

    public ExternalDataManager getExternalDataManager() {
        return externalDataManager;
    }

    public void setExternalDataManager(ExternalDataManager externalDataManager) {
        this.externalDataManager = externalDataManager;
    }

    /*
        Adds support for multidex support library. For more info check out the link below,
        https://developer.android.com/studio/build/multidex.html
    */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        setupDagger();

        NotificationUtils.createNotificationChannel(singleton);

        registerReceiver(new SmsSentBroadcastReceiver(), new IntentFilter(SmsSender.SMS_SEND_ACTION));
        registerReceiver(new SmsNotificationReceiver(), new IntentFilter(SmsNotificationReceiver.SMS_NOTIFICATION_ACTION));

        try {
            JobManager
                    .create(this)
                    .addJobCreator(collectJobCreator);
        } catch (JobManagerCreateException e) {
            Timber.e(e);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        FormMetadataMigrator.migrate(prefs);
        PrefMigrator.migrateSharedPrefs();
        AutoSendPreferenceMigrator.migrate();

        reloadSharedPreferences();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        JodaTimeAndroid.init(this);

        defaultSysLanguage = Locale.getDefault().getLanguage();
        new LocaleHelper().updateLocale(this);

        initializeJavaRosa();

        if (BuildConfig.BUILD_TYPE.equals("odkCollectRelease")) {
            Timber.plant(new CrashReportingTree());
        } else {
            Timber.plant(new Timber.DebugTree());
        }

        setupOSMDroid();

        // Force inclusion of scoped storage strings so they can be translated
        Timber.i("%s %s", getString(R.string.scoped_storage_banner_text),
                                   getString(R.string.scoped_storage_learn_more));
    }

    protected void setupOSMDroid() {
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(userAgentProvider.getUserAgent());
    }

    private void setupDagger() {
        applicationComponent = DaggerAppDependencyComponent.builder()
                .application(this)
                .build();

        applicationComponent.inject(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //noinspection deprecation
        defaultSysLanguage = newConfig.locale.getLanguage();
        boolean isUsingSysLanguage = GeneralSharedPreferences.getInstance().get(GeneralKeys.KEY_APP_LANGUAGE).equals("");
        if (!isUsingSysLanguage) {
            new LocaleHelper().updateLocale(this);
        }
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                return;
            }

            Crashlytics.log(priority, tag, message);

            if (t != null && priority == Log.ERROR) {
                Crashlytics.logException(t);
            }
        }
    }

    public void initializeJavaRosa() {
        PropertyManager mgr = new PropertyManager(this);

        // Use the server username by default if the metadata username is not defined
        if (mgr.getSingularProperty(PropertyManager.PROPMGR_USERNAME) == null || mgr.getSingularProperty(PropertyManager.PROPMGR_USERNAME).isEmpty()) {
            mgr.putProperty(PropertyManager.PROPMGR_USERNAME, PropertyManager.SCHEME_USERNAME, (String) GeneralSharedPreferences.getInstance().get(GeneralKeys.KEY_USERNAME));
        }

        FormController.initializeJavaRosa(mgr);
    }

    // This method reloads shared preferences in order to load default values for new preferences
    private void reloadSharedPreferences() {
        GeneralSharedPreferences.getInstance().reloadPreferences();
        AdminSharedPreferences.getInstance().reloadPreferences();
    }

    public AppDependencyComponent getComponent() {
        return applicationComponent;
    }

    public void setComponent(AppDependencyComponent applicationComponent) {
        this.applicationComponent = applicationComponent;
        applicationComponent.inject(this);
    }

    /**
     * Gets a unique, privacy-preserving identifier for the current form.
     *
     * @return md5 hash of the form title, a space, the form ID
     */
    public static String getCurrentFormIdentifierHash() {
        FormController formController = getInstance().getFormController();
        if (formController != null) {
            return formController.getCurrentFormIdentifierHash();
        }

        return "";
    }

    /**
     * Gets a unique, privacy-preserving identifier for a form based on its id and version.
     * @param formId id of a form
     * @param formVersion version of a form
     * @return md5 hash of the form title, a space, the form ID
     */
    public static String getFormIdentifierHash(String formId, String formVersion) {
        String formIdentifier = new FormsDao().getFormTitleForFormIdAndFormVersion(formId, formVersion) + " " + formId;
        return FileUtils.getMd5Hash(new ByteArrayInputStream(formIdentifier.getBytes()));
    }
}
