/*
 * Copyright (C) 2018 Shobhit Agarwal
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

package org.odk.collect.android.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.odk.collect.android.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import timber.log.Timber;

public class AudioControllerView extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    private static final int SEEK_FORWARD_TIME = 5000; // 5 seconds
    private static final int SEEK_BACKWARD_TIME = 5000; // 5 seconds

    @BindView(R.id.currentDuration)
    TextView currentDurationLabel;

    @BindView(R.id.totalDuration)
    TextView totalDurationLabel;

    @BindView(R.id.playBtn)
    @SuppressFBWarnings("UR")
    ImageButton playButton;

    @BindView(R.id.seekBar)
    @SuppressFBWarnings("UR")
    SeekBar seekBar;

    private State state;
    private MediaPlayer mediaPlayer;
    private final Handler seekHandler = new Handler();

    /**
     * Background Runnable thread
     */
    private final Runnable updateTimeTask = new Runnable() {
        public void run() {
            try {
                if (mediaPlayer.isPlaying()) {
                    updateTimer();
                    seekHandler.postDelayed(this, 100);
                } else {
                    seekBar.setProgress(mediaPlayer.getDuration());
                    seekHandler.removeCallbacks(updateTimeTask);
                }
            } catch (IllegalStateException e) {
                seekHandler.removeCallbacks(updateTimeTask);
                Timber.i(e, "Attempting to update timer when player is stopped");
            }
        }
    };
    private Listener listener;

    public AudioControllerView(Context context) {
        super(context);

        View.inflate(context, R.layout.audio_controller_layout, this);
        ButterKnife.bind(this);
        seekBar.setOnSeekBarChangeListener(this);
        playButton.setImageResource(R.drawable.ic_play_arrow_24dp);
    }

    public void init(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        initMediaPlayer();
    }

    @OnClick(R.id.fastForwardBtn)
    void fastForwardMedia() {
        seekTo(mediaPlayer.getCurrentPosition() + SEEK_FORWARD_TIME);
    }

    @OnClick(R.id.fastRewindBtn)
    void rewindMedia() {
        seekTo(mediaPlayer.getCurrentPosition() - SEEK_BACKWARD_TIME);
    }

    /**
     * Seeks media to the new position and updates timer
     */
    private void seekTo(int newPosition) {
        mediaPlayer.seekTo(Math.min(Math.max(0, newPosition), mediaPlayer.getDuration()));
        updateTimer();
    }

    @OnClick(R.id.playBtn)
    void playClicked() {
        if (state == State.PLAYING) {
            listener.onPauseClicked();
        } else {
            listener.onPlayClicked();
        }
    }

    private void updateTimer() {
        totalDurationLabel.setText(getTime(mediaPlayer.getDuration()));
        currentDurationLabel.setText(getTime(mediaPlayer.getCurrentPosition()));

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
    }

    private void initMediaPlayer() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(player -> {
            Timber.i("Media Prepared");
            updateTimer();
        });
        mediaPlayer.setOnCompletionListener(player -> {
            Timber.i("Completed");
            playButton.setImageResource(R.drawable.ic_play_arrow_24dp);
        });
    }

    /**
     * Update timer on seekbar
     */
    private void updateProgressBar() {
        seekHandler.postDelayed(updateTimeTask, 100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            seekTo(progress);
        }
    }

    /**
     * When user starts moving the progress handler
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        ((SwipableParent) getContext()).allowSwiping(false);

        if (state == State.PLAYING) {
            pause();
        }
    }

    /**
     * When user stops moving the progress handler
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ((SwipableParent) getContext()).allowSwiping(true);

        seekTo(seekBar.getProgress());
        if (state == State.PLAYING) {
            play();
        }
    }

    private void play() {
        playButton.setImageResource(R.drawable.ic_pause_24dp);
        updateProgressBar();
    }

    private void pause() {
        playButton.setImageResource(R.drawable.ic_play_arrow_24dp);
        seekHandler.removeCallbacks(updateTimeTask);
    }

    public void setMedia(File file) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getContext(), Uri.fromFile(file));
            mediaPlayer.prepareAsync();
            state = State.IDLE;
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public void hidePlayer() {
        setVisibility(GONE);
    }

    public void showPlayer() {
        setVisibility(View.VISIBLE);
    }

    /**
     * Converts {@param millis} to mm:ss format
     *
     * @return formatted time as string
     */
    private static String getTime(long millis) {
        return new DateTime(millis, DateTimeZone.UTC).toString("mm:ss");
    }

    public void setPlayState(AudioPlayerViewModel.ClipState playState) {
        switch (playState) {
            case NOT_PLAYING:
                playButton.setImageResource(R.drawable.ic_play_arrow_24dp);
                state = State.IDLE;
                break;
            case PLAYING:
                playButton.setImageResource(R.drawable.ic_pause_24dp);
                state = State.PLAYING;
                break;
            case PAUSED:
                playButton.setImageResource(R.drawable.ic_play_arrow_24dp);
                state = State.PAUSED;
                break;
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private enum State {
        PAUSED, PLAYING, IDLE
    }

    public interface SwipableParent {
        void allowSwiping(boolean allowSwiping);
    }

    public interface Listener {

        void onPlayClicked();

        void onPauseClicked();
    }
}
