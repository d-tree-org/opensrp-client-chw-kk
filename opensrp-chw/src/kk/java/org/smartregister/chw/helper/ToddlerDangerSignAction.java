package org.smartregister.chw.helper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;

import timber.log.Timber;

public class ToddlerDangerSignAction extends HomeVisitActionHelper {

    private final Context context;
    private String toddler_danger_sign;
    private String toddler_danger_sign_key;
    private final Alert alert;

    public ToddlerDangerSignAction(Context context, Alert alert){
        this.context = context;
        this.alert = alert;
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return isOverDue() ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
    }

    private boolean isOverDue() {
        return new LocalDate().isAfter(new LocalDate(alert.startDate()).plusDays(14));
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            toddler_danger_sign_key = JsonFormUtils.getValue(jsonObject, "child_danger_signs");
            toddler_danger_sign  = JsonFormUtils.getCheckBoxValue(jsonObject, "child_danger_signs");
        }catch (JSONException e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("{0}: {1}", context.getString(R.string.ccd_toddler_danger_signs_subtitle), toddler_danger_sign);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(toddler_danger_sign_key)){
            return BaseAncHomeVisitAction.Status.PENDING;
        } else if(toddler_danger_sign_key.contains("chk_none")){
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else{
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }
}
