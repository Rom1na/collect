package ar.gob.buenosaires.barelevamiento.support;

import androidx.fragment.app.FragmentActivity;

import ar.gob.buenosaires.barelevamiento.audio.AudioControllerView;
import ar.gob.buenosaires.barelevamiento.audio.AudioControllerView;

public class SwipableParentActivity extends FragmentActivity implements AudioControllerView.SwipableParent {

    private boolean swipingAllowed;

    @Override
    public void allowSwiping(boolean allowSwiping) {
        swipingAllowed = allowSwiping;
    }

    public boolean isSwipingAllowed() {
        return swipingAllowed;
    }
}
