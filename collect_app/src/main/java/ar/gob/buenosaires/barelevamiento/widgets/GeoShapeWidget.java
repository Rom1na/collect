/*
 * Copyright (C) 2014 GeoODK
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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.GeoPolyActivity;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;

import ar.gob.buenosaires.barelevamiento.activities.GeoPolyActivity;
import ar.gob.buenosaires.barelevamiento.formentry.questions.QuestionDetails;
import ar.gob.buenosaires.barelevamiento.utilities.ApplicationConstants;

import static ar.gob.buenosaires.barelevamiento.utilities.ApplicationConstants.RequestCodes;

/**
 * GeoShapeWidget is the widget that allows the user to get Collect multiple GPS points.
 *
 * @author Jon Nordling (jonnordling@gmail.com)
 */
@SuppressLint("ViewConstructor")
public class GeoShapeWidget extends BaseGeoWidget {

    public GeoShapeWidget(Context context, QuestionDetails questionDetails) {
        super(context, questionDetails);
    }

    public void startGeoActivity() {
        Intent intent = new Intent(getContext(), GeoPolyActivity.class)
            .putExtra(GeoPolyActivity.ANSWER_KEY, answerDisplay.getText().toString())
            .putExtra(GeoPolyActivity.OUTPUT_MODE_KEY, GeoPolyActivity.OutputMode.GEOSHAPE);
        ((Activity) getContext()).startActivityForResult(intent, ApplicationConstants.RequestCodes.GEOSHAPE_CAPTURE);
    }

    public void updateButtonLabelsAndVisibility(boolean dataAvailable) {
        startGeoButton.setText(dataAvailable ? R.string.geoshape_view_change_location : R.string.get_shape);
    }

    @Override
    public String getAnswerToDisplay(String answer) {
        return answer;
    }

    @Override
    public String getDefaultButtonLabel() {
        return getContext().getString(R.string.get_shape);
    }

    @Override
    public IAnswerData getAnswer() {
        String s = answerDisplay.getText().toString();
        return !s.isEmpty()
                ? new StringData(s)
                : null;
    }
}
