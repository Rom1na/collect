package ar.gob.buenosaires.barelevamiento.formentry;

import android.Manifest;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import ar.gob.buenosaires.barelevamiento.activities.FormEntryActivity;
import ar.gob.buenosaires.barelevamiento.support.CopyFormRule;
import ar.gob.buenosaires.barelevamiento.support.ResetStateRule;
import ar.gob.buenosaires.barelevamiento.support.pages.FormEntryPage;
import ar.gob.buenosaires.barelevamiento.test.FormLoadingUtils;

import java.util.Collections;

public class RankingWidgetWithCSVTest {

    private static final String TEST_FORM = "ranking_widget.xml";

    @Rule
    public RuleChain copyFormChain = RuleChain
            .outerRule(GrantPermissionRule.grant(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            )
            .around(new ResetStateRule())
            .around(new CopyFormRule(TEST_FORM, Collections.singletonList("fruits.csv")));

    @Rule
    public IntentsTestRule<FormEntryActivity> activityTestRule = FormLoadingUtils.getFormActivityTestRuleFor(TEST_FORM);

    @Test
    public void rankingWidget_shouldDisplayItemsFromSearchFunc() {
        new FormEntryPage("ranking_widget", activityTestRule)
                .clickRankingButton()
                .assertText("Mango", "Oranges", "Strawberries");
    }
}
