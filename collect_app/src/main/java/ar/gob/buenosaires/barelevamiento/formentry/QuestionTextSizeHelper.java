package ar.gob.buenosaires.barelevamiento.formentry;

import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;

import ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys;

public class QuestionTextSizeHelper {

    public float getHeadline6() {
        return getBaseFontSize() - 1; // 20sp by default
    }

    public float getSubtitle1() {
        return getBaseFontSize() - 5; // 16sp by default
    }

    private int getBaseFontSize() {
        return Integer.parseInt(String.valueOf(GeneralSharedPreferences.getInstance().get(GeneralKeys.KEY_FONT_SIZE)));
    }
}
