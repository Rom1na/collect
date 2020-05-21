package ar.gob.buenosaires.barelevamiento.formentry;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.formentry.saving.FormSaveViewModel;
import ar.gob.buenosaires.barelevamiento.fragments.dialogs.ProgressDialogFragment;

public class SaveFormProgressDialogFragment extends ProgressDialogFragment {

    private FormSaveViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        viewModel = ViewModelProviders
                .of(requireActivity(), new FormSaveViewModel.Factory())
                .get(FormSaveViewModel.class);

        setCancelable(false);
        setTitle(getString(R.string.saving_form));

        viewModel.getSaveResult().observe(this, result -> {
            if (result != null && result.getState() == FormSaveViewModel.SaveResult.State.SAVING && result.getMessage() != null) {
                setMessage(getString(R.string.please_wait) + "\n\n" + result.getMessage());
            } else {
                setMessage(getString(R.string.please_wait));
            }
        });
    }

    @Override
    protected Cancellable getCancellable() {
        return viewModel;
    }
}
