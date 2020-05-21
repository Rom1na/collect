package ar.gob.buenosaires.barelevamiento.settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;
import ar.gob.buenosaires.barelevamiento.injection.config.AppDependencyModule;
import ar.gob.buenosaires.barelevamiento.metadata.SharedPreferencesInstallIDProvider;
import ar.gob.buenosaires.barelevamiento.support.CopyFormRule;
import ar.gob.buenosaires.barelevamiento.support.ResetStateRule;
import ar.gob.buenosaires.barelevamiento.support.pages.GeneralSettingsPage;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;
import ar.gob.buenosaires.barelevamiento.support.pages.UserAndDeviceIdentitySettingsPage;
import ar.gob.buenosaires.barelevamiento.utilities.DeviceDetailsProvider;

import static ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys.KEY_INSTALL_ID;

@RunWith(AndroidJUnit4.class)
public class FormMetadataTest {

    private final DeviceDetailsProvider deviceDetailsProvider = new FakeDeviceDetailsProvider();
    public ActivityTestRule<MainMenuActivity> rule = new ActivityTestRule<>(MainMenuActivity.class);

    @Rule
    public RuleChain copyFormChain = RuleChain
            .outerRule(GrantPermissionRule.grant(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            ))
            .around(new ResetStateRule(new AppDependencyModule() {
                @Override
                public DeviceDetailsProvider providesDeviceDetailsProvider(Context context) {
                    return deviceDetailsProvider;
                }
            }))
            .around(new CopyFormRule("metadata.xml"))
            .around(rule);

    @Test
    public void settingMetadata_letsThemBeIncludedInAForm() {
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickGeneralSettings()
                .clickUserAndDeviceIdentity()
                .clickFormMetadata()
                .clickUsername()
                .inputText("Chino")
                .clickOKOnDialog()
                .assertPreference(R.string.username, "Chino")
                .clickEmail()
                .inputText("chino@whitepony.com")
                .clickOKOnDialog()
                .assertPreference(R.string.email, "chino@whitepony.com")
                .clickPhoneNumber()
                .inputText("664615")
                .clickOKOnDialog()
                .assertPreference(R.string.phone_number, "664615")
                .pressBack(new UserAndDeviceIdentitySettingsPage(rule))
                .pressBack(new GeneralSettingsPage(rule))
                .pressBack(new MainMenuPage(rule))
                .startBlankForm("Metadata")
                .assertText("Chino", "chino@whitepony.com", "664615");
    }

    @Test
    public void deviceIdentifiersAreDisplayedInSettings() {
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickGeneralSettings()
                .clickUserAndDeviceIdentity()
                .clickFormMetadata()
                .assertPreference(R.string.device_id, deviceDetailsProvider.getDeviceId())
                .assertPreference(R.string.subscriber_id, deviceDetailsProvider.getSubscriberId())
                .assertPreference(R.string.sim_serial_id, deviceDetailsProvider.getSimSerialNumber())
                .assertPreference(R.string.install_id, getInstallID());
    }

    @Test
    public void deviceIdentifiersCanBeIncludedInAForm() {
        new MainMenuPage(rule)
                .startBlankForm("Metadata")
                .scrollToAndAssertText(deviceDetailsProvider.getDeviceId())
                .scrollToAndAssertText(deviceDetailsProvider.getSubscriberId())
                .scrollToAndAssertText(deviceDetailsProvider.getSimSerialNumber());
    }

    private String getInstallID() {
        SharedPreferences sharedPreferences = rule.getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
        return new SharedPreferencesInstallIDProvider(sharedPreferences, KEY_INSTALL_ID).getInstallID();
    }

    class FakeDeviceDetailsProvider implements DeviceDetailsProvider {

        @Override
        public String getDeviceId() {
            return "deviceID";
        }

        @Override
        public String getLine1Number() {
            return "line1Number";
        }

        @Override
        public String getSubscriberId() {
            return "subscriberID";
        }

        @Override
        public String getSimSerialNumber() {
            return "simSerialNumber";
        }
    }
}
