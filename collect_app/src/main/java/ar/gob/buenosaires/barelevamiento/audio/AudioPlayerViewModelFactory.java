package ar.gob.buenosaires.barelevamiento.audio;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ar.gob.buenosaires.utilities.Scheduler;

class AudioPlayerViewModelFactory implements ViewModelProvider.Factory {

    private final MediaPlayerFactory mediaPlayerFactory;
    private final Scheduler scheduler;

    AudioPlayerViewModelFactory(MediaPlayerFactory mediaPlayerFactory, Scheduler scheduler) {
        this.mediaPlayerFactory = mediaPlayerFactory;
        this.scheduler = scheduler;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AudioPlayerViewModel(mediaPlayerFactory, scheduler);
    }
}
