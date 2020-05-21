package ar.gob.buenosaires.barelevamiento.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.activities.FormEntryActivity;
import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.dao.helpers.ContentResolverHelper;
import ar.gob.buenosaires.barelevamiento.exception.GDriveConnectionException;
import ar.gob.buenosaires.barelevamiento.formentry.ODKView;
import ar.gob.buenosaires.barelevamiento.fragments.dialogs.ProgressDialogFragment;
import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.network.NetworkStateProvider;
import ar.gob.buenosaires.barelevamiento.utilities.FileUtils;
import ar.gob.buenosaires.barelevamiento.utilities.ImageConverter;
import ar.gob.buenosaires.barelevamiento.utilities.MediaUtils;
import ar.gob.buenosaires.barelevamiento.utilities.ToastUtils;
import ar.gob.buenosaires.barelevamiento.widgets.BaseImageWidget;
import ar.gob.buenosaires.barelevamiento.widgets.QuestionWidget;

import java.io.File;
import java.lang.ref.WeakReference;

import ar.gob.buenosaires.barelevamiento.network.NetworkStateProvider;
import timber.log.Timber;

public class MediaLoadingTask extends AsyncTask<Uri, Void, File> {

    private WeakReference<FormEntryActivity> formEntryActivity;
    private WeakReference<NetworkStateProvider> connectivityProvider;

    public MediaLoadingTask(FormEntryActivity formEntryActivity, NetworkStateProvider connectivityProvider) {
        onAttach(formEntryActivity);
        this.connectivityProvider = new WeakReference<>(connectivityProvider);
    }

    public void onAttach(FormEntryActivity formEntryActivity) {
        this.formEntryActivity = new WeakReference<>(formEntryActivity);
    }

    public void onDetach() {
        formEntryActivity = null;
        connectivityProvider = null;
    }

    @Override
    protected File doInBackground(Uri... uris) {

        File instanceFile;
        FormController formController = Collect.getInstance().getFormController();

        if (formController != null) {
            instanceFile = formController.getInstanceFile();
            if (instanceFile != null) {
                String instanceFolder = instanceFile.getParent();
                String extension = ContentResolverHelper.getFileExtensionFromUri(formEntryActivity.get(), uris[0]);
                String destMediaPath = instanceFolder + File.separator + System.currentTimeMillis() + extension;

                try {
                    File chosenFile = MediaUtils.getFileFromUri(formEntryActivity.get(), uris[0], MediaStore.Images.Media.DATA, connectivityProvider.get());
                    if (chosenFile != null) {
                        final File newFile = new File(destMediaPath);
                        FileUtils.copyFile(chosenFile, newFile);
                        QuestionWidget questionWidget = formEntryActivity.get().getWidgetWaitingForBinaryData();

                        // apply image conversion if the widget is an image widget
                        if (questionWidget instanceof BaseImageWidget) {
                            ImageConverter.execute(newFile.getPath(), questionWidget, formEntryActivity.get());
                        }

                        return newFile;
                    } else {
                        Timber.e("Could not receive chosen file");
                        formEntryActivity.get().runOnUiThread(() -> ToastUtils.showShortToastInMiddle(R.string.error_occured));
                        return null;
                    }
                } catch (GDriveConnectionException e) {
                    Timber.e("Could not receive chosen file due to connection problem");
                    formEntryActivity.get().runOnUiThread(() -> ToastUtils.showLongToastInMiddle(R.string.gdrive_connection_exception));
                    return null;
                }
            }
        }
        return null;

    }

    @Override
    protected void onPostExecute(File result) {
        Fragment prev = formEntryActivity.get().getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.COLLECT_PROGRESS_DIALOG_TAG);
        if (prev != null && !formEntryActivity.get().isInstanceStateSaved()) {
            ((DialogFragment) prev).dismiss();
        }

        ODKView odkView = formEntryActivity.get().getCurrentViewIfODKView();
        if (odkView != null) {
            odkView.setBinaryData(result);
        }
    }
}
