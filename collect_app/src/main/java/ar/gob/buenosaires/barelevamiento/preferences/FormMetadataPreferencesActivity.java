package ar.gob.buenosaires.barelevamiento.preferences;

import android.os.Bundle;

import androidx.annotation.Nullable;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.CollectAbstractActivity;

public class FormMetadataPreferencesActivity extends CollectAbstractActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_layout);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preferences_fragment_container, new FormMetadataFragment())
                .commit();
    }
}
