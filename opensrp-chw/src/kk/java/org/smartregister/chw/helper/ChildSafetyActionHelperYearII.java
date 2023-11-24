package org.smartregister.chw.helper;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;

import java.text.MessageFormat;

import timber.log.Timber;

public class ChildSafetyActionHelperYearII extends HomeVisitActionHelper {
    private String childSafetyCounselling = "";

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
        return MessageFormat.format("{0} : {1}", context.getString(R.string.ccd_child_safety_subtitle_year_ii),childSafetyCounselling.equals("yes") ? context.getString(R.string.yes) : context.getString(R.string.no));
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