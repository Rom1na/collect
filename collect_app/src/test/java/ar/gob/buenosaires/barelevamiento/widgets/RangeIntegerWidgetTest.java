package ar.gob.buenosaires.barelevamiento.widgets;

import androidx.annotation.NonNull;

import org.javarosa.core.model.data.IntegerData;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.widgets.base.RangeWidgetTest;

/**
 * @author James Knight
 */
public class RangeIntegerWidgetTest extends RangeWidgetTest<RangeIntegerWidget, IntegerData> {

    @NonNull
    @Override
    public RangeIntegerWidget createWidget() {
        return new RangeIntegerWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"));
    }

    @NonNull
    @Override
    public IntegerData getNextAnswer() {
        return new IntegerData(random.nextInt());
    }
}
