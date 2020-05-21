package ar.gob.buenosaires.barelevamiento;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ar.gob.buenosaires.barelevamiento.activities.MainActivityTest;
import ar.gob.buenosaires.barelevamiento.utilities.CompressionTest;
import ar.gob.buenosaires.barelevamiento.utilities.PermissionsTest;
import ar.gob.buenosaires.barelevamiento.utilities.StringUtilsTest;
import ar.gob.buenosaires.barelevamiento.activities.MainActivityTest;
import ar.gob.buenosaires.barelevamiento.utilities.CompressionTest;
import ar.gob.buenosaires.barelevamiento.utilities.PermissionsTest;
import ar.gob.buenosaires.barelevamiento.utilities.StringUtilsTest;

/**
 * Suite for running all unit tests from one place
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        //Name of tests which are going to be run by suite
        MainActivityTest.class,
        PermissionsTest.class,
        StringUtilsTest.class,
        CompressionTest.class
})

public class AllTestsSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
