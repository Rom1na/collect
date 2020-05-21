package ar.gob.buenosaires.barelevamiento.widgets;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import net.bytebuddy.utility.RandomString;

import org.javarosa.core.model.data.StringData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.DrawActivity;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.support.MockFormEntryPromptBuilder;
import ar.gob.buenosaires.barelevamiento.widgets.base.FileWidgetTest;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import ar.gob.buenosaires.barelevamiento.widgets.base.FileWidgetTest;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static ar.gob.buenosaires.barelevamiento.support.CollectHelpers.overrideReferenceManager;
import static ar.gob.buenosaires.barelevamiento.support.CollectHelpers.setupFakeReferenceManager;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author James Knight
 */
@RunWith(RobolectricTestRunner.class)
public class DrawWidgetTest extends FileWidgetTest<DrawWidget> {

    @Mock
    File file;

    private String fileName;

    @NonNull
    @Override
    public DrawWidget createWidget() {
        return new DrawWidget(activity, new QuestionDetails(formEntryPrompt, "formAnalyticsID"));
    }

    @NonNull
    @Override
    public StringData getNextAnswer() {
        return new StringData(fileName);
    }

    @Override
    public Object createBinaryData(StringData answerData) {
        return file;
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        fileName = RandomString.make();
    }

    @Override
    protected void prepareForSetAnswer() {

        when(file.exists()).thenReturn(true);
        when(file.getName()).thenReturn(fileName);
    }

    @Test
    public void buttonsShouldLaunchCorrectIntents() {
        stubAllRuntimePermissionsGranted(true);

        Intent intent = getIntentLaunchedByClick(R.id.simple_button);
        assertComponentEquals(activity, DrawActivity.class, intent);
        assertExtraEquals(DrawActivity.OPTION, DrawActivity.OPTION_DRAW, intent);
    }

    @Test
    public void usingReadOnlyOptionShouldMakeAllClickableElementsDisabled() {
        when(formEntryPrompt.isReadOnly()).thenReturn(true);

        assertThat(getWidget().drawButton.getVisibility(), is(View.GONE));
    }

    @Test
    public void whenPromptHasDefaultAnswer_showsInImageView() throws Exception {
        String defaultImagePath = File.createTempFile("blah", ".bmp").getAbsolutePath();
        overrideReferenceManager(setupFakeReferenceManager(asList(
                new Pair<>("jr://images/referenceURI", defaultImagePath)
        )));

        formEntryPrompt = new MockFormEntryPromptBuilder()
                .withAnswerDisplayText("jr://images/referenceURI")
                .build();

        DrawWidget widget = createWidget();
        ImageView imageView = widget.getImageView();
        assertThat(imageView, notNullValue());
        Drawable drawable = imageView.getDrawable();
        assertThat(drawable, notNullValue());

        String loadedImagePath = shadowOf(((BitmapDrawable) drawable).getBitmap()).getCreatedFromPath();
        assertThat(loadedImagePath, equalTo(defaultImagePath));
    }
}