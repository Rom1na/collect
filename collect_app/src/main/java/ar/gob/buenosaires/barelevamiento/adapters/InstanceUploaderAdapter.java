package ar.gob.buenosaires.barelevamiento.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ar.gob.buenosaires.barelevamiento.R;
import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.events.RxEventBus;
import ar.gob.buenosaires.barelevamiento.events.SmsRxEvent;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.provider.InstanceProvider;
import ar.gob.buenosaires.barelevamiento.provider.InstanceProviderAPI.InstanceColumns;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService;
import ar.gob.buenosaires.barelevamiento.tasks.sms.contracts.SmsSubmissionManagerContract;
import ar.gob.buenosaires.barelevamiento.tasks.sms.models.SmsSubmission;
import ar.gob.buenosaires.barelevamiento.views.ProgressBar;

import java.util.Date;

import javax.inject.Inject;

import ar.gob.buenosaires.barelevamiento.application.Collect;
import ar.gob.buenosaires.barelevamiento.events.RxEventBus;
import ar.gob.buenosaires.barelevamiento.events.SmsRxEvent;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys;
import ar.gob.buenosaires.barelevamiento.preferences.GeneralSharedPreferences;
import ar.gob.buenosaires.barelevamiento.provider.InstanceProvider;
import ar.gob.buenosaires.barelevamiento.provider.InstanceProviderAPI;
import ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService;
import ar.gob.buenosaires.barelevamiento.tasks.sms.contracts.SmsSubmissionManagerContract;
import ar.gob.buenosaires.barelevamiento.tasks.sms.models.SmsSubmission;
import ar.gob.buenosaires.barelevamiento.views.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static ar.gob.buenosaires.barelevamiento.preferences.GeneralKeys.KEY_SUBMISSION_TRANSPORT_TYPE;
import static ar.gob.buenosaires.barelevamiento.provider.InstanceProviderAPI.STATUS_SUBMISSION_FAILED;
import static ar.gob.buenosaires.barelevamiento.provider.InstanceProviderAPI.STATUS_SUBMITTED;
import static ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService.RESULT_MESSAGE_READY;
import static ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService.RESULT_OK_OTHERS_PENDING;
import static ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService.RESULT_QUEUED;
import static ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService.RESULT_SENDING;
import static ar.gob.buenosaires.barelevamiento.tasks.sms.SmsService.getDisplaySubtext;

public class InstanceUploaderAdapter extends CursorAdapter {

    @Inject
    RxEventBus eventBus;
    @Inject
    SmsSubmissionManagerContract submissionManager;
    @Inject
    SmsService smsService;

    private final Context context;
    private final CompositeDisposable compositeDisposable;

    public InstanceUploaderAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        Collect.getInstance().getComponent().inject(this);
        compositeDisposable = new CompositeDisposable();
    }

    public void onDestroy() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.form_chooser_list_item_multiple_choice, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long lastStatusChangeDate = getCursor().getLong(getCursor().getColumnIndex(InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE));
        String status = cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS));

        viewHolder.formTitle.setText(cursor.getString(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME)));
        viewHolder.formSubtitle.setText(InstanceProvider.getDisplaySubtext(context, status, new Date(lastStatusChangeDate)));

        long instanceId = cursor.getLong(cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID));

        SmsSubmission model = submissionManager.getSubmissionModel(String.valueOf(instanceId));

        boolean smsTransportEnabled = ((String) GeneralSharedPreferences.getInstance().get(GeneralKeys.KEY_SUBMISSION_TRANSPORT_TYPE)).equalsIgnoreCase(context.getString(R.string.transport_type_value_sms));

        boolean isSmsSubmission = model != null && smsTransportEnabled;

        switch (status) {
            case InstanceProviderAPI.STATUS_SUBMISSION_FAILED:
                viewHolder.statusIcon.setImageResource(R.drawable.form_state_submission_failed);
                break;

            case InstanceProviderAPI.STATUS_SUBMITTED:
                viewHolder.statusIcon.setImageResource(R.drawable.form_state_submitted);
                break;

            default:
                viewHolder.statusIcon.setImageResource(R.drawable.form_state_finalized);
        }

        if (isSmsSubmission) {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            viewHolder.progressBar.setProgressPercent((int) model.getCompletion().getPercentage(), false);

            int smsStatus = submissionManager.checkNextMessageResultCode(String.valueOf(instanceId));

            setSmsSubmissionStateIcons(smsStatus, viewHolder);

            SmsRxEvent currentStatus = new SmsRxEvent();
            currentStatus.setResultCode(smsStatus);
            currentStatus.setLastUpdated(model.getLastUpdated());
            currentStatus.setProgress(model.getCompletion());

            setDisplaySubTextView(currentStatus, viewHolder);

            setupCloseButton(viewHolder, smsStatus);
            viewHolder.closeButton.setOnClickListener(v -> smsService.cancelFormSubmission(String.valueOf(instanceId)));
        }

        compositeDisposable.add(eventBus.register(SmsRxEvent.class)
                .filter(event -> event.getInstanceId().equals(String.valueOf(instanceId)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    viewHolder.progressBar.setProgressPercent((int) event.getProgress().getPercentage(), true);
                    setSmsSubmissionStateIcons(event.getResultCode(), viewHolder);
                    setDisplaySubTextView(event, viewHolder);
                    setupCloseButton(viewHolder, event.getResultCode());
                }));
    }

    private void setupCloseButton(ViewHolder viewHolder, int resultCode) {

        if (resultCode == SmsService.RESULT_QUEUED || resultCode == SmsService.RESULT_OK_OTHERS_PENDING) {
            viewHolder.closeButton.setVisibility(View.VISIBLE);
            viewHolder.checkbox.setVisibility(View.GONE);
        } else {
            viewHolder.closeButton.setVisibility(View.GONE);
            viewHolder.checkbox.setVisibility(View.VISIBLE);
        }
    }

    private void setSmsSubmissionStateIcons(int smsStatus, ViewHolder viewHolder) {
        switch (smsStatus) {
            case Activity.RESULT_OK:
                viewHolder.statusIcon.setImageResource(R.drawable.form_state_submitted);
                break;

            case SmsService.RESULT_QUEUED:
            case SmsService.RESULT_OK_OTHERS_PENDING:
            case SmsService.RESULT_SENDING:
            case SmsService.RESULT_MESSAGE_READY:
                viewHolder.statusIcon.setImageResource(R.drawable.form_state_sending);
                break;

            default:
                viewHolder.statusIcon.setImageResource(R.drawable.form_state_submission_failed);
                break;
        }
    }

    private void setDisplaySubTextView(SmsRxEvent event, ViewHolder viewHolder) {
        String text = SmsService.getDisplaySubtext(event.getResultCode(), event.getLastUpdated(), event.getProgress(), context);
        if (text != null) {
            viewHolder.formSubtitle.setText(text);
        }
    }

    static class ViewHolder {
        @BindView(R.id.form_title)
        TextView formTitle;
        @BindView(R.id.form_subtitle)
        TextView formSubtitle;
        @BindView(R.id.checkbox)
        CheckBox checkbox;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.image)
        ImageView statusIcon;
        @BindView(R.id.close_box)
        ImageView closeButton;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
