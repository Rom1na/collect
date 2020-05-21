package ar.gob.buenosaires.barelevamiento.support.pages;

import androidx.test.rule.ActivityTestRule;

import ar.gob.buenosaires.barelevamiento.R;

public class ErrorDialog extends OkDialog {
    ErrorDialog(ActivityTestRule rule) {
        super(rule);
    }

    @Override
    public ErrorDialog assertOnPage() {
        super.assertOnPage();
        checkIsStringDisplayed(R.string.error_occured);
        return this;
    }
}
