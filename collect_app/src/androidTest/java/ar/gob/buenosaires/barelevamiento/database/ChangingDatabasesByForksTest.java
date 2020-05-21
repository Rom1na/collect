package ar.gob.buenosaires.barelevamiento.database;

import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.dao.FormsDao;
import ar.gob.buenosaires.barelevamiento.dao.InstancesDao;
import ar.gob.buenosaires.barelevamiento.database.helpers.FormsDatabaseHelper;
import ar.gob.buenosaires.barelevamiento.database.helpers.InstancesDatabaseHelper;
import ar.gob.buenosaires.barelevamiento.database.helpers.SqlLiteHelperTest;

import java.io.File;
import java.io.IOException;

import static ar.gob.buenosaires.barelevamiento.test.FileUtils.copyFileFromAssets;

public class ChangingDatabasesByForksTest extends SqlLiteHelperTest {

    @Test
    public void appShouldNotCrashAfterChangingFormsDbByItsFork() throws IOException {
        // getting forms cursor creates the newest version of form.db
        new FormsDao().getFormsCursor();

        // a fork changes our forms.db downgrading it
        copyFileFromAssets("database" + File.separator + "forms_v4.db", FormsDatabaseHelper.getDatabasePath());

        // the change should be detected and handled, app shouldn't crash
        new FormsDao().getFormsCursor();
    }

    @Test
    public void appShouldNotCrashAfterChangingInstancesDbByItsFork() throws IOException {
        // getting instances cursor creates the newest version of instances.db
        new InstancesDao().getSentInstancesCursor();

        // a fork changes our instances.db downgrading it
        copyFileFromAssets("database" + File.separator + "instances_v3.db", InstancesDatabaseHelper.getDatabasePath());

        // the change should be detected and handled, app shouldn't crash
        new InstancesDao().getSentInstancesCursor();
    }
}
