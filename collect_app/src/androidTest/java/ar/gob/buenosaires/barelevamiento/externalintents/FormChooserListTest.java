package ar.gob.buenosaires.barelevamiento.externalintents;

import androidx.test.filters.Suppress;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.activities.FormChooserListActivity;

import java.io.IOException;

import static ar.gob.buenosaires.barelevamiento.externalintents.ExportedActivitiesUtils.testDirectories;

@Suppress
// Frequent failures: https://github.com/opendatakit/collect/issues/796
public class FormChooserListTest {

    @Rule
    public ActivityTestRule<FormChooserListActivity> formChooserListRule =
            new ExportedActivityTestRule<>(FormChooserListActivity.class);

    @Test
    public void formChooserListMakesDirsTest() throws IOException {
        testDirectories();
    }

}
