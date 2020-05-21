package ar.gob.buenosaires.barelevamiento.widgets;

import android.app.Application;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.form.api.FormEntryCaption;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import ar.gob.buenosaires.barelevamiento.analytics.Analytics;
import ar.gob.buenosaires.barelevamiento.audio.AudioButton;
import ar.gob.buenosaires.barelevamiento.audio.AudioHelper;
import ar.gob.buenosaires.barelevamiento.audio.Clip;
import ar.gob.buenosaires.barelevamiento.formentry.media.AudioHelperFactory;
import ar.gob.buenosaires.barelevamiento.formentry.questions.AudioVideoImageTextLabel;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.injection.config.AppDependencyModule;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.support.MockFormEntryPromptBuilder;
import ar.gob.buenosaires.barelevamiento.support.RobolectricHelpers;
import ar.gob.buenosaires.barelevamiento.utilities.WidgetAppearanceUtils;
import ar.gob.buenosaires.barelevamiento.widgets.base.GeneralSelectOneWidgetTest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ar.gob.buenosaires.barelevamiento.support.CollectHelpers.setupFakeReferenceManager;
import static ar.gob.buenosaires.barelevamiento.support.RobolectricHelpers.populateRecyclerView;

/**
 * @author James Knight
 */

public class SelectOneWidgetTest extends GeneralSelectOneWidgetTest<AbstractSelectOneWidget> {

    @NonNull
    @Override
    public SelectOneWidget createWidget() {
        return new SelectOneWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"), false);
    }

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    public AudioHelper audioHelper;

    @Mock
    public Analytics analytics;

    @Before
    public void setup() throws Exception {
        overrideDependencyModule();
        when(audioHelper.setAudio(any(AudioButton.class), any())).thenReturn(new MutableLiveData<>());
    }

    @Test
    public void whenChoicesHaveAudio_audioButtonUsesIndexAsClipID() throws Exception {
        formEntryPrompt = new MockFormEntryPromptBuilder()
                .withIndex("i am index")
                .withSelectChoices(asList(
                        new SelectChoice("1", "1"),
                        new SelectChoice("2", "2")
                ))
                .withSpecialFormSelectChoiceText(asList(
                        new Pair<>(FormEntryCaption.TEXT_FORM_AUDIO, REFERENCES.get(0).first),
                        new Pair<>(FormEntryCaption.TEXT_FORM_AUDIO, REFERENCES.get(1).first)
                ))
                .build();

        populateRecyclerView(getActualWidget());
        verify(audioHelper).setAudio(any(AudioButton.class), eq(new Clip("i am index 0", REFERENCES.get(0).second)));
        verify(audioHelper).setAudio(any(AudioButton.class), eq(new Clip("i am index 1", REFERENCES.get(1).second)));
    }

    @Test
    public void whenChoicesHaveAudio_logsAudioChoiceEvent() throws Exception {
        formEntryPrompt = new MockFormEntryPromptBuilder()
                .withIndex("i am index")
                .withSelectChoices(asList(
                        new SelectChoice("1", "1"),
                        new SelectChoice("2", "2")
                ))
                .withSpecialFormSelectChoiceText(asList(
                        new Pair<>(FormEntryCaption.TEXT_FORM_AUDIO, REFERENCES.get(0).first),
                        new Pair<>(FormEntryCaption.TEXT_FORM_AUDIO, REFERENCES.get(1).first)
                ))
                .build();

        populateRecyclerView(getActualWidget());
        verify(analytics).logEvent("Prompt", "AudioChoice", "formAnalyticsID");
    }

    private void overrideDependencyModule() throws Exception {
        ReferenceManager referenceManager = setupFakeReferenceManager(REFERENCES);
        RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule() {

            @Override
            public ReferenceManager providesReferenceManager() {
                return referenceManager;
            }

            @Override
            public AudioHelperFactory providesAudioHelperFactory() {
                return context -> audioHelper;
            }

            @Override
            public Analytics providesAnalytics(Application application, GeneralSharedPreferences generalSharedPreferences) {
                return analytics;
            }
        });
    }

    @Test
    public void usingReadOnlyOptionShouldMakeAllClickableElementsDisabled() {
        // No appearance
        formEntryPrompt = new MockFormEntryPromptBuilder()
                .withIndex("i am index")
                .withSelectChoices(asList(
                        new SelectChoice("1", "1"),
                        new SelectChoice("2", "2")
                ))
                .withReadOnly(true)
                .build();

        populateRecyclerView(getActualWidget());

        AudioVideoImageTextLabel avitLabel = (AudioVideoImageTextLabel) ((LinearLayout) ((RecyclerView) getWidget().answerLayout.getChildAt(0)).getLayoutManager().getChildAt(0)).getChildAt(0);
        assertThat(avitLabel.isEnabled(), is(Boolean.FALSE));

        resetWidget();

        // No-buttons appearance
        formEntryPrompt = new MockFormEntryPromptBuilder(formEntryPrompt)
                .withAppearance(WidgetAppearanceUtils.NO_BUTTONS)
                .build();

        populateRecyclerView(getActualWidget());

        FrameLayout view = (FrameLayout) ((RecyclerView) getWidget().answerLayout.getChildAt(0)).getLayoutManager().getChildAt(0);
        assertThat(view.isEnabled(), is(Boolean.FALSE));
    }

    private static final List<Pair<String, String>> REFERENCES = asList(
            new Pair<>("ref", "file://audio.mp3"),
            new Pair<>("ref1", "file://audio1.mp3")
    );
}
