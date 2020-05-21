/*
 * Copyright (C) 2012 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package ar.gob.buenosaires.barelevamiento.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.DrawActivity;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.formentry.questions.WidgetViewUtils;

import ar.gob.buenosaires.barelevamiento.activities.DrawActivity;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.formentry.questions.WidgetViewUtils;
import ar.gob.buenosaires.barelevamiento.utilities.ApplicationConstants;

import static ar.gob.buenosaires.barelevamiento.formentry.questions.WidgetViewUtils.createSimpleButton;
import static ar.gob.buenosaires.barelevamiento.utilities.ApplicationConstants.RequestCodes;

/**
 * Free drawing widget.
 *
 * @author BehrAtherton@gmail.com
 */
@SuppressLint("ViewConstructor")
public class DrawWidget extends BaseImageWidget {

    Button drawButton;

    public DrawWidget(Context context, QuestionDetails prompt) {
        super(context, prompt);
        imageClickHandler = new DrawImageClickHandler(DrawActivity.OPTION_DRAW, ApplicationConstants.RequestCodes.DRAW_IMAGE, R.string.draw_image);
        setUpLayout();
        addCurrentImageToLayout();
        addAnswerView(answerLayout, WidgetViewUtils.getStandardMargin(context));
    }

    @Override
    protected void setUpLayout() {
        super.setUpLayout();
        drawButton = WidgetViewUtils.createSimpleButton(getContext(), getFormEntryPrompt().isReadOnly(), getContext().getString(R.string.draw_image), getAnswerFontSize(), this);

        answerLayout.addView(drawButton);
        answerLayout.addView(errorTextView);

        errorTextView.setVisibility(View.GONE);
    }

    @Override
    public Intent addExtrasToIntent(Intent intent) {
        return intent;
    }

    @Override
    protected boolean doesSupportDefaultValues() {
        return true;
    }

    @Override
    public void clearAnswer() {
        super.clearAnswer();
        // reset buttons
        drawButton.setText(getContext().getString(R.string.draw_image));
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        drawButton.setOnLongClickListener(l);
        super.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        drawButton.cancelLongPress();
    }

    @Override
    public void onButtonClick(int buttonId) {
        imageClickHandler.clickImage("drawButton");
    }
}
