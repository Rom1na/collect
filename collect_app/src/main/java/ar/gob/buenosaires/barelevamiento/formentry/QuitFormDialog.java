package ar.gob.buenosaires.barelevamiento.formentry;

import android.content.Context;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.google.common.collect.ImmutableList;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.adapters.IconMenuListAdapter;
import ar.gob.buenosaires.barelevamiento.adapters.model.IconMenuItem;
import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.preferences.AdminKeys;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.utilities.DialogUtils;

import java.util.List;

import ar.gob.buenosaires.barelevamiento.logic.FormController;
import ar.gob.buenosaires.barelevamiento.preferences.AdminKeys;
import ar.gob.buenosaires.barelevamiento.preferences.AdminSharedPreferences;
import ar.gob.buenosaires.barelevamiento.utilities.DialogUtils;

public class QuitFormDialog {

    private QuitFormDialog() {
    }

    public static AlertDialog show(Context context, FormController formController, Listener listener) {
        String title = (formController == null) ? null : formController.getFormTitle();
        if (title == null) {
            title = context.getString(R.string.no_form_loaded);
        }

        List<IconMenuItem> items;
        if ((boolean) AdminSharedPreferences.getInstance().get(AdminKeys.KEY_SAVE_MID)) {
            items = ImmutableList.of(new IconMenuItem(R.drawable.ic_save, R.string.keep_changes),
                    new IconMenuItem(R.drawable.ic_delete, R.string.do_not_save));
        } else {
            items = ImmutableList.of(new IconMenuItem(R.drawable.ic_delete, R.string.do_not_save));
        }

        ListView listView = DialogUtils.createActionListView(context);

        final IconMenuListAdapter adapter = new IconMenuListAdapter(context, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            IconMenuItem item = (IconMenuItem) adapter.getItem(position);
            if (item.getTextResId() == R.string.keep_changes) {
                listener.onSaveChangedClicked();
            } else {
                listener.onIgnoreChangesClicked();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(
                        context.getString(R.string.quit_application, title))
                .setPositiveButton(context.getString(R.string.do_not_exit), (dialog, id) -> {
                    dialog.cancel();
                })
                .setView(listView)
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public interface Listener {
        void onSaveChangedClicked();

        void onIgnoreChangesClicked();
    }
}
