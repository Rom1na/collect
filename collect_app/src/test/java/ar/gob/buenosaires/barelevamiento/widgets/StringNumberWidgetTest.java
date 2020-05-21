package ar.gob.buenosaires.barelevamiento.widgets;

import androidx.annotation.NonNull;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.widgets.base.GeneralStringWidgetTest;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static ar.gob.buenosaires.barelevamiento.utilities.WidgetAppearanceUtils.THOUSANDS_SEP;

/**
 * @author James Knight
 */
public class StringNumberWidgetTest extends GeneralStringWidgetTest<StringNumberWidget, StringData> {

    @NonNull
    @Override
    public StringNumberWidget createWidget() {
        return new StringNumberWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), false);
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(RandomString.make());
    }

    @Test
    public void digitsNumberShouldNotBeLimited() {
        getActualWidget().answerText.setText("123456789123456789123456789123456789");
        assertEquals("123456789123456789123456789123456789", getActualWidget().getAnswerText());
    }

    @Test
    public void separatorsShouldBeAddedWhenEnabled() {
        when(formEntryPrompt.getAppearanceHint()).thenReturn(THOUSANDS_SEP);
        getActualWidget().answerText.setText("123456789123456789123456789123456789");
        assertEquals("123,456,789,123,456,789,123,456,789,123,456,789", getActualWidget().answerText.getText().toString());
    }
}
