package ar.gob.buenosaires.barelevamiento.formentry.audit;

import android.Manifest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.MainMenuActivity;
import ar.gob.buenosaires.barelevamiento.support.pages.ChangesReasonPromptPage;
import ar.gob.buenosaires.barelevamiento.support.pages.FormEntryPage;
import ar.gob.buenosaires.barelevamiento.support.pages.MainMenuPage;
import ar.gob.buenosaires.barelevamiento.support.pages.SaveOrIgnoreDialog;
import ar.gob.buenosaires.barelevamiento.support.CopyFormRule;
import ar.gob.buenosaires.barelevamiento.support.ResetStateRule;

@RunWith(AndroidJUnit4.class)
public class TrackChangesReasonTest {

    private static final String TRACK_CHANGES_REASON_ON_EDIT_FORM = "track-changes-reason-on-edit.xml";
    private static final String NO_TRACK_CHANGES_REASON_FORM = "no-track-changes-reason.xml";

    @Rule
    public ActivityTestRule<MainMenuActivity> rule = new ActivityTestRule<>(MainMenuActivity.class);

    @Rule
    public RuleChain copyFormChain = RuleChain
            .outerRule(GrantPermissionRule.grant(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
            .around(new ResetStateRule())
            .around(new CopyFormRule(TRACK_CHANGES_REASON_ON_EDIT_FORM))
            .around(new CopyFormRule(NO_TRACK_CHANGES_REASON_FORM));

    @Test
    public void openingAFormToEdit_andChangingAValue_andClickingSaveAndExit_andEnteringReason_andClickingSave_returnsToMainMenu() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .swipeToNextQuestion()
                .clickSaveAndExitWithChangesReasonPrompt()
                .enterReason("Needed to be more exciting and less mysterious")
                .clickSave();
    }

    @Test
    public void openingAFormToEdit_andChangingAValue_andClickingSaveAndExit_andEnteringBlankReason_andClickingSave_remainsOnPrompt() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .swipeToNextQuestion()
                .clickSaveAndExitWithChangesReasonPrompt()
                .enterReason(" ")
                .clickSaveWithValidationError();
    }

    @Test
    public void openingAFormToEdit_andChangingAValue_andClickingSaveAndExit_andPressingBack_returnsToForm() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .swipeToNextQuestion()
                .clickSaveAndExitWithChangesReasonPrompt()
                .closeSoftKeyboard()
                .pressBack(new FormEntryPage("Track Changes Reason", rule))
                .checkIsStringDisplayed(R.string.save_form_as);
    }

    @Test
    public void openingAFormToEdit_andChangingAValue_andClickingSaveAndExit_andClickingCross_returnsToForm() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .swipeToNextQuestion()
                .clickSaveAndExitWithChangesReasonPrompt()
                .closeSoftKeyboard()
                .pressClose(new FormEntryPage("Track Changes Reason", rule))
                .checkIsStringDisplayed(R.string.save_form_as);
    }

    @Test
    public void openingAFormToEdit_andChangingAValue_andClickingSaveAndExit_andRotating_remainsOnPrompt() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .swipeToNextQuestion()
                .clickSaveAndExitWithChangesReasonPrompt()
                .enterReason("Something")
                .rotateToLandscape(new ChangesReasonPromptPage("Track Changes Reason", rule))
                .assertText("Something")
                .closeSoftKeyboard()
                .clickSave();
    }

    @Test
    public void openingAFormToEdit_andChangingAValue_andPressingBack_andClickingSaveChanges_promptsForReason() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .closeSoftKeyboard()
                .pressBack(new SaveOrIgnoreDialog<>("Track Changes Reason", new ChangesReasonPromptPage("Track Changes Reason", rule), rule))
                .clickSaveChanges();
    }

    @Test
    public void openingAFormToEdit_andChangingAValue_andPressingBack_andIgnoringChanges_returnsToMainMenu() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .closeSoftKeyboard()
                .pressBack(new SaveOrIgnoreDialog<>("Track Changes Reason", new MainMenuPage(rule), rule))
                .clickIgnoreChanges();
    }

    @Test
    public void openingAFormToEdit_andNotChangingAValue_andClickingSaveAndExit_returnsToMainMenu() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .closeSoftKeyboard()
                .swipeToNextQuestion()
                .clickSaveAndExit();
    }

    @Test
    public void openingFormToEdit_andChangingValue_andClickingSave_promptsForReason() {
        new MainMenuPage(rule)
                .startBlankForm("Track Changes Reason")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Track Changes Reason")
                .clickGoToStart()
                .inputText("Nothing much!")
                .clickSaveWithChangesReasonPrompt();
    }

    @Test
    public void whenFormDoesNotHaveTrackChangesReason_openingToEdit_andChangingAValue_andClickingSaveAndExit_returnsToMainMenu() {
        new MainMenuPage(rule)
                .startBlankForm("Normal Form")
                .inputText("Nothing much...")
                .swipeToNextQuestion()
                .clickSaveAndExit()
                .clickEditSavedForm()
                .clickOnForm("Normal Form")
                .clickGoToStart()
                .inputText("Nothing much!")
                .swipeToNextQuestion()
                .clickSaveAndExit();
    }
}
