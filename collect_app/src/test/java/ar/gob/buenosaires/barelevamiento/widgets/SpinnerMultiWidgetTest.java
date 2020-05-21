package ar.gob.buenosaires.barelevamiento.widgets;

import android.view.View;

import androidx.annotation.NonNull;

import org.javarosa.core.model.SelectChoice;
import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.widgets.base.GeneralSelectMultiWidgetTest;

import java.util.List;

import ar.gob.buenosaires.barelevamiento.widgets.base.GeneralSelectMultiWidgetTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * @author James Knight
 */
public class SpinnerMultiWidgetTest extends GeneralSelectMultiWidgetTest<SpinnerMultiWidget> {

    @NonNull
    @Override
    public SpinnerMultiWidget createWidget() {
        return new SpinnerMultiWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        List<SelectChoice> selectChoices = getSelectChoices();
        for (SelectChoice selectChoice : selectChoices) {
            when(formEntryPrompt.getSelectChoiceText(selectChoice))
                    .thenReturn(selectChoice.getValue());
        }
    }

    @Test
    public void usingReadOnlyOptionShouldMakeAllClickableElementsDisabled() {
        when(formEntryPrompt.isReadOnly()).thenReturn(true);

        assertThat(getWidget().button.getVisibility(), is(View.GONE));
    }
}
