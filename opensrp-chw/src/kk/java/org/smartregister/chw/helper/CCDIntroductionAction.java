package org.smartregister.chw.helper;

import android.content.Context;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;

import timber.log.Timber;

public class CCDIntroductionAction extends HomeVisitActionHelper {

    private final Context context;
    private final Alert alert;
    private String childDevelopmentCounselling;

    public CCDIntroductionAction(Context mContext, Alert mAlert){
        this.alert = mAlert;
        this.context = mContext;
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return isOverDue() ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
    }

    private boolean isOverDue(){
        return new LocalDate().isAfter(new LocalDate(alert.startDate()).plusDays(14));
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            childDevelopmentCounselling = JsonFormUtils.getValue(jsonObject, "ccd_development_education");
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("Child behaviour and development : ", childDevelopmentCounselling.equals("yes") ? "Yes" : "No");
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (childDevelopmentCounselling.contains("yes")){
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }else {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }
}
