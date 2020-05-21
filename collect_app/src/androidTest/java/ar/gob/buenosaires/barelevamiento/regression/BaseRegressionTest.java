package ar.gob.buenosaires.barelevamiento.regression;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;

public class BaseRegressionTest {

    @Rule
    public ActivityTestRule<MainMenuActivity> rule = new ActivityTestRule<>(MainMenuActivity.class);
}