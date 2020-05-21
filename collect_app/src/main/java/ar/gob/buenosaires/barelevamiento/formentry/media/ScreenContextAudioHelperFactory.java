package ar.gob.buenosaires.barelevamiento.formentry.media;

import android.content.Context;

import ar.gob.buenosaires.barelevamiento.audio.AudioHelper;
import ar.gob.buenosaires.barelevamiento.utilities.ScreenContext;

public class ScreenContextAudioHelperFactory implements AudioHelperFactory {

    public AudioHelper create(Context context) {
        ScreenContext screenContext = (ScreenContext) context;
        return new AudioHelper(screenContext.getActivity(), screenContext.getViewLifecycle());
    }
}
