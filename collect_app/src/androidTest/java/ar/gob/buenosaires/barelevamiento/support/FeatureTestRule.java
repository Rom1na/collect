package ar.gob.buenosaires.barelevamiento.support;

import androidx.test.rule.ActivityTestRule;

import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;

public class FeatureTestRule extends ActivityTestRule<MainMenuActivity> {

    public FeatureTestRule() {
        super(MainMenuActivity.class);
    }

    public MainMenuPage mainMenu() {
        return new MainMenuPage(this).assertOnPage();
    }
}
