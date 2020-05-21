package ar.gob.buenosaires.barelevamiento.formentry.media;

import android.content.Context;

import ar.gob.buenosaires.barelevamiento.audio.AudioHelper;

public interface AudioHelperFactory {

    AudioHelper create(Context context);
}
