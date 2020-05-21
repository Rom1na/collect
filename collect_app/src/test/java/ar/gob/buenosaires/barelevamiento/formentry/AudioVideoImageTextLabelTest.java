package ar.gob.buenosaires.barelevamiento.formentry;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.audio.AudioButton;
import ar.gob.buenosaires.barelevamiento.audio.AudioHelper;
import ar.gob.buenosaires.barelevamiento.formentry.questions.AudioVideoImageTextLabel;
import ar.gob.buenosaires.barelevamiento.support.TestScreenContextActivity;
import org.robolectric.RobolectricTestRunner;

import ar.gob.buenosaires.barelevamiento.audio.AudioButton;
import ar.gob.buenosaires.barelevamiento.audio.AudioHelper;

import static android.view.View.VISIBLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ar.gob.buenosaires.barelevamiento.support.RobolectricHelpers.createThemedActivity;

@RunWith(RobolectricTestRunner.class)
public class AudioVideoImageTextLabelTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    public AudioHelper audioHelper;

    private TestScreenContextActivity activity;

    @Before
    public void setup() {
        activity = createThemedActivity(TestScreenContextActivity.class);
    }

    @Test
    public void withNullText_hidesTextLabel() {
        AudioVideoImageTextLabel audioVideoImageTextLabel = new AudioVideoImageTextLabel(activity);
        audioVideoImageTextLabel.setText(null, false, 16);

        assertThat(audioVideoImageTextLabel.getLabelTextView().getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void withBlankText_hidesTextLabel() {
        AudioVideoImageTextLabel audioVideoImageTextLabel = new AudioVideoImageTextLabel(activity);
        audioVideoImageTextLabel.setText("", false, 16);

        assertThat(audioVideoImageTextLabel.getLabelTextView().getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void withText_andAudio_showsTextAndAudioButton()  {
        MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
        when(audioHelper.setAudio(any(AudioButton.class), any())).thenReturn(isPlaying);

        AudioVideoImageTextLabel label = new AudioVideoImageTextLabel(activity);
        label.setText("blah", false, 16);
        label.setAudio("file://audio.mp3", audioHelper);

        assertThat(label.getLabelTextView().getVisibility(), equalTo(VISIBLE));
        assertThat(label.getLabelTextView().getText().toString(), equalTo("blah"));
        assertThat(label.findViewById(R.id.audioButton).getVisibility(), equalTo(VISIBLE));
    }

    @Test
    public void withText_andAudio_playingAudio_highlightsText() {
        MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
        when(audioHelper.setAudio(any(AudioButton.class), any())).thenReturn(isPlaying);

        AudioVideoImageTextLabel audioVideoImageTextLabel = new AudioVideoImageTextLabel(activity);
        audioVideoImageTextLabel.setText("blah", false, 16);
        audioVideoImageTextLabel.setAudio("file://audio.mp3", audioHelper);

        int originalTextColor = audioVideoImageTextLabel.getLabelTextView().getCurrentTextColor();

        isPlaying.setValue(true);
        int textColor = audioVideoImageTextLabel.getLabelTextView().getCurrentTextColor();
        assertThat(textColor, not(equalTo(originalTextColor)));

        isPlaying.setValue(false);
        textColor = audioVideoImageTextLabel.getLabelTextView().getCurrentTextColor();
        assertThat(textColor, equalTo(originalTextColor));
    }
}
