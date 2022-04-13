package org.smartregister.chw.helper;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChildSafetyActionHelper extends HomeVisitActionHelper {

    private Context context;
    private Alert alert;
    private String childSafetyCounselling = "";
    private String jsonPayload;

    public ChildSafetyActionHelper(Context context, Alert alert){
        this.alert = alert;
        this.context = context;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.context = context;
        this.jsonPayload = jsonString;
    }

    @Override
    public String getPreProcessed() {
        return super.getPreProcessed();
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            childSafetyCounselling = JsonFormUtils.getValue(jsonObject, "child_safety_counselled");
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("Counselled mother for child safety : {0}", childSafetyCounselling);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (childSafetyCounselling.equals("yes"))
            return BaseAncHomeVisitAction.Status.COMPLETED;
        else if (childSafetyCounselling.equals("no"))
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        else
            return BaseAncHomeVisitAction.Status.PENDING;
    }

}
