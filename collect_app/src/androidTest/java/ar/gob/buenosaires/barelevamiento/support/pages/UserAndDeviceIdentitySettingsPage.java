package ar.gob.buenosaires.barelevamiento.support.pages;

import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.rule.ActivityTestRule;

import ar.gob.buenosaires.barelevamiento.R;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;

public class UserAndDeviceIdentitySettingsPage extends Page<UserAndDeviceIdentitySettingsPage> {

    public UserAndDeviceIdentitySettingsPage(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public UserAndDeviceIdentitySettingsPage assertOnPage() {
        checkIsStringDisplayed(R.string.user_and_device_identity_title);
        return this;
    }

    public FormMetadataPage clickFormMetadata() {
        onData(PreferenceMatchers.withKey("form_metadata")).perform(click());
        return new FormMetadataPage(rule);
    }
}
