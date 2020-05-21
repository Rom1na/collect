package ar.gob.buenosaires.barelevamiento.regression;

import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.support.pages.AdminSettingsPage;
import ar.gob.buenosaires.barelevamiento.support.pages.GeneralSettingsPage;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;
import ar.gob.buenosaires.barelevamiento.support.ResetStateRule;

//Issue NODK-241
@RunWith(AndroidJUnit4.class)
public class UserSettingsTest extends BaseRegressionTest {

    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(new ResetStateRule());

    @Test
    public void typeOption_ShouldNotBeVisible() {
        //TestCase1
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickAdminSettings()
                .openUserSettings()
                .checkIfTextDoesNotExist("Type")
                .checkIfTextDoesNotExist("Submission transport")
                .checkIsStringDisplayed(R.string.server);
    }

    @Test
    public void uncheckedSettings_ShouldNotBeVisibleInGeneralSettings() {
        //TestCase4
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickAdminSettings()
                .openUserSettings()
                .uncheckAllUserSettings()
                .pressBack(new AdminSettingsPage(rule))
                .pressBack(new MainMenuPage(rule))
                .clickOnMenu()
                .clickGeneralSettings()
                .checkIfTextDoesNotExist(R.string.server)
                .checkIfTextDoesNotExist(R.string.client)
                .checkIfTextDoesNotExist(R.string.maps)
                .checkIfTextDoesNotExist(R.string.form_management_preferences)
                .checkIfTextDoesNotExist(R.string.user_and_device_identity_title)
                .pressBack(new MainMenuPage(rule))
                .clickOnMenu()
                .clickAdminSettings()
                .clickGeneralSettings()
                .checkIfServerOptionIsDisplayed()
                .checkIfUserInterfaceOptionIsDisplayed()
                .checkIfMapsOptionIsDisplayed()
                .checkIfFormManagementOptionIsDisplayed()
                .checkIfUserAndDeviceIdentityIsDisplayed();
    }

    @Test
    public void showGuidance_shouldBehidden() {
        //TestCase5
        new MainMenuPage(rule)
                .clickOnMenu()
                .clickAdminSettings()
                .openUserSettings()
                .uncheckUserSettings("guidance_hint")
                .pressBack(new AdminSettingsPage(rule))
                .pressBack(new MainMenuPage(rule))
                .clickOnMenu()
                .clickGeneralSettings()
                .openFormManagement()
                .checkIfTextDoesNotExist(R.string.guidance_hint_title)
                .pressBack(new GeneralSettingsPage(rule))
                .pressBack(new MainMenuPage(rule))
                .clickOnMenu()
                .clickAdminSettings()
                .openUserSettings()
                .uncheckAllUserSettings()
                .pressBack(new AdminSettingsPage(rule))
                .pressBack(new MainMenuPage(rule))
                .clickOnMenu()
                .clickGeneralSettings()
                .openFormManagement()
                .checkIsStringDisplayed(R.string.guidance_hint_title);
    }
}
