package ar.gob.buenosaires.barelevamiento.externalintents;

import android.app.Activity;
import androidx.test.rule.ActivityTestRule;

class ExportedActivityTestRule<A extends Activity> extends ActivityTestRule<A> {

    ExportedActivityTestRule(Class<A> activityClass) {
        super(activityClass);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        ExportedActivitiesUtils.clearDirectories();
    }

}
