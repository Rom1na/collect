package ar.gob.buenosaires.barelevamiento.widgets;

import android.view.View;

import androidx.annotation.NonNull;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.MultipleItemsData;
import org.javarosa.core.model.data.SelectMultiData;
import org.javarosa.core.model.data.helper.Selection;
import org.junit.Test;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.widgets.base.SelectWidgetTest;

import java.util.ArrayList;
import java.util.List;

import ar.gob.buenosaires.barelevamiento.widgets.base.SelectWidgetTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class RankingWidgetTest extends SelectWidgetTest<RankingWidget, MultipleItemsData> {

    @NonNull
    @Override
    public RankingWidget createWidget() {
        return new RankingWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"));
    }

    @NonNull
    @Override
    public MultipleItemsData getNextAnswer() {
        List<SelectChoice> selectChoices = getSelectChoices();

        List<Selection> selections = new ArrayList<>();
        for (SelectChoice selectChoice : selectChoices) {
            selections.add(new Selection(selectChoice));
        }

        return new SelectMultiData(selections);
    }

    @Test
    public void usingReadOnlyOptionShouldMakeAllClickableElementsDisabled() {
        when(formEntryPrompt.isReadOnly()).thenReturn(true);

        assertThat(getWidget().showRankingDialogButton.getVisibility(), is(View.GONE));
    }
}
