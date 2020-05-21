package ar.gob.buenosaires.barelevamiento.widgets;

import androidx.annotation.NonNull;

import org.javarosa.core.model.data.IntegerData;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.widgets.base.GeneralStringWidgetTest;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static ar.gob.buenosaires.barelevamiento.utilities.WidgetAppearanceUtils.THOUSANDS_SEP;

/**
 * @author James Knight
 */
public class IntegerWidgetTest extends GeneralStringWidgetTest<IntegerWidget, IntegerData> {

    @NonNull
    @Override
    public IntegerWidget createWidget() {
        return new IntegerWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), false);
    }

    @NonNull
    @Override
    public IntegerData getNextAnswer() {
        return new IntegerData(randomInteger());
    }

    private int randomInteger() {
        return Math.abs(random.nextInt()) % 1_000_000_000;
    }

    @Test
    public void digitsAboveLimitOfNineShouldBeTruncatedFromRight() {
        getActualWidget().answerText.setText("123456789123");
        assertEquals("123456789", getActualWidget().getAnswerText());
    }

    @Test
    public void separatorsShouldBeAddedWhenEnabled() {
        when(formEntryPrompt.getAppearanceHint()).thenReturn(THOUSANDS_SEP);
        getActualWidget().answerText.setText("123456789");
        assertEquals("123,456,789", getActualWidget().answerText.getText().toString());
    }
}
