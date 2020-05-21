package ar.gob.buenosaires.barelevamiento.externalintents;

import androidx.test.filters.Suppress;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;

import java.io.IOException;

import static ar.gob.buenosaires.barelevamiento.externalintents.ExportedActivitiesUtils.testDirectories;

@Suppress
// Frequent failures: https://github.com/opendatakit/collect/issues/796
public class MainMenuActivityTest {

    @Rule
    public ActivityTestRule<MainMenuActivity> mainMenuActivityRule =
            new ExportedActivityTestRule<>(MainMenuActivity.class);

    @Test
    public void mainMenuActivityMakesDirsTest() throws IOException {
        ExportedActivitiesUtils.testDirectories();
    }

}
