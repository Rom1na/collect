package ar.gob.buenosaires.barelevamiento;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.location.Location;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import ar.gob.buenosaires.barelevamiento.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import ar.gob.buenosaires.barelevamiento.activities.FormEntryActivity;
import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;
import ar.gob.buenosaires.barelevamiento.support.CopyFormRule;
import ar.gob.buenosaires.barelevamiento.support.ResetStateRule;
import ar.gob.buenosaires.barelevamiento.utilities.GeoUtils;

import ar.gob.buenosaires.barelevamiento.support.CopyFormRule;
import ar.gob.buenosaires.barelevamiento.support.ResetStateRule;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;

import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

public class FormMapTest {
    private static final String SINGLE_GEOPOINT_FORM = "single-geopoint.xml";
    private static final String NO_GEOPOINT_FORM = "basic.xml";

    @Rule public IntentsTestRule<MainMenuActivity> rule = new IntentsTestRule<>(MainMenuActivity.class);

    @Rule public RuleChain copyFormChain = RuleChain
            .outerRule(GrantPermissionRule.grant(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            .around(new ResetStateRule())
            .around(new CopyFormRule(SINGLE_GEOPOINT_FORM))
            .around(new CopyFormRule(NO_GEOPOINT_FORM));

    @Before public void stubGeopointIntent() {
        Location location = new Location("gps");
        location.setLatitude(125.1);
        location.setLongitude(10.1);
        location.setAltitude(5);

        Intent intent = new Intent();
        intent.putExtra(FormEntryActivity.LOCATION_RESULT, GeoUtils.formatLocationResultString(location));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

        intending(hasComponent("ar.gob.buenosaires.barelevamiento.activities.GeoPointActivity"))
                .respondWith(result);
    }

    @Test public void gettingBlankFormList_showsMapIcon_onlyForFormsWithGeometry() {
        new MainMenuPage(rule)
                .clickFillBlankForm()
                .checkMapIconDisplayedForForm("Single geopoint")
                .checkMapIconNotDisplayedForForm("basic");
    }

    @Test public void clickingOnMapIcon_opensMapForForm() {
        new MainMenuPage(rule)
                .clickFillBlankForm()
                .clickOnMapIconForForm("Single geopoint")
                .assertText("Single geopoint");
    }

    @Test public void fillingBlankForm_addsInstanceToMap() {
        String oneInstanceString = ApplicationProvider.getApplicationContext().getResources().getString(R.string.geometry_status, 1, 1);

        new MainMenuPage(rule)
                .clickFillBlankForm()
                .clickOnMapIconForForm("Single geopoint")
                .clickFillBlankFormButton("Single geopoint")
                .inputText("Foo")
                .swipeToNextQuestion()
                .clickWidgetButton()
                .swipeToNextQuestion()
                .clickSaveAndExitBackToMap()
                .assertText(oneInstanceString);
    }
}
