package ar.gob.buenosaires.barelevamiento.preferences;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.jetbrains.annotations.NotNull;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.CollectAbstractActivity;
import ar.gob.buenosaires.barelevamiento.injection.DaggerUtils;
import ar.gob.buenosaires.barelevamiento.listeners.PermissionListener;
import ar.gob.buenosaires.barelevamiento.logic.PropertyManager;
import ar.gob.buenosaires.barelevamiento.metadata.InstallIDProvider;
import ar.gob.buenosaires.barelevamiento.utilities.PermissionUtils;
import ar.gob.buenosaires.barelevamiento.utilities.ToastUtils;
import ar.gob.buenosaires.barelevamiento.utilities.Validator;

import javax.inject.Inject;

public class FormMetadataFragment extends PreferenceFragmentCompat {

    @Inject
    InstallIDProvider installIDProvider;

    @Inject
    PermissionUtils permissionUtils;

    private Preference emailPreference;
    private EditTextPreference phonePreference;
    private Preference installIDPreference;
    private Preference deviceIDPreference;
    private Preference simSerialPrererence;
    private Preference subscriberIDPreference;


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        DaggerUtils.getComponent(context).inject(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.form_metadata_preferences, rootKey);

        emailPreference = findPreference(GeneralKeys.KEY_METADATA_EMAIL);
        phonePreference = findPreference(GeneralKeys.KEY_METADATA_PHONENUMBER);
        installIDPreference = findPreference(GeneralKeys.KEY_INSTALL_ID);
        deviceIDPreference = findPreference(PropertyManager.PROPMGR_DEVICE_ID);
        simSerialPrererence = findPreference(PropertyManager.PROPMGR_SIM_SERIAL);
        subscriberIDPreference = findPreference(PropertyManager.PROPMGR_SUBSCRIBER_ID);

        FragmentActivity activity = getActivity();
        if (activity instanceof CollectAbstractActivity) {
            ((CollectAbstractActivity) activity).initToolbar(getPreferenceScreen().getTitle());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPrefs();

        if (permissionUtils.isReadPhoneStatePermissionGranted(getActivity())) {
            setupPrefsWithPermissions();
        } else if (savedInstanceState == null) {
            permissionUtils.requestReadPhoneStatePermission(getActivity(), true, new PermissionListener() {
                @Override
                public void granted() {
                    setupPrefsWithPermissions();
                }

                @Override
                public void denied() {
                }
            });
        }
    }

    private void setupPrefs() {
        emailPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String newValueString = newValue.toString();
            if (!newValueString.isEmpty() && !Validator.isEmailAddressValid(newValueString)) {
                ToastUtils.showLongToast(R.string.invalid_email_address);
                return false;
            }

            return true;
        });

        phonePreference.setOnBindEditTextListener(editText -> editText.setInputType(EditorInfo.TYPE_CLASS_PHONE));
        installIDPreference.setSummaryProvider(preference -> installIDProvider.getInstallID());
    }

    private void setupPrefsWithPermissions() {
        PropertyManager propertyManager = new PropertyManager(getActivity());
        deviceIDPreference.setSummaryProvider(new PropertyManagerPropertySummaryProvider(propertyManager, PropertyManager.PROPMGR_DEVICE_ID));
        simSerialPrererence.setSummaryProvider(new PropertyManagerPropertySummaryProvider(propertyManager, PropertyManager.PROPMGR_SIM_SERIAL));
        subscriberIDPreference.setSummaryProvider(new PropertyManagerPropertySummaryProvider(propertyManager, PropertyManager.PROPMGR_SUBSCRIBER_ID));
        phonePreference.setSummaryProvider(new PropertyManagerPropertySummaryProvider(propertyManager, PropertyManager.PROPMGR_PHONE_NUMBER));
    }

    private class PropertyManagerPropertySummaryProvider implements Preference.SummaryProvider<EditTextPreference> {

        private final PropertyManager propertyManager;
        private final String propertyKey;

        PropertyManagerPropertySummaryProvider(PropertyManager propertyManager, String propertyName) {
            this.propertyManager = propertyManager;
            this.propertyKey = propertyName;
        }

        @Override
        public CharSequence provideSummary(EditTextPreference preference) {
            String value = propertyManager.reload(getActivity()).getSingularProperty(propertyKey);
            if (!TextUtils.isEmpty(value)) {
                return value;
            } else {
                return getString(R.string.preference_not_available);
            }
        }
    }
}
