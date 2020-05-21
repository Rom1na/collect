package ar.gob.buenosaires.barelevamiento.support;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import ar.gob.buenosaires.barelevamiento.utilities.ScreenContext;

public class TestScreenContextActivity extends FragmentActivity implements ScreenContext {

    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    @Override
    public LifecycleOwner getViewLifecycle() {
        return this;
    }
}
