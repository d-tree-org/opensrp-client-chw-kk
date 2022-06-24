package org.smartregister.chw.actionhelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;

import java.text.MessageFormat;

import timber.log.Timber;

public class MalnutritionScreeningActionHelper extends HomeVisitActionHelper {

    String growthMonitoringSelectedKey = "";
    String growthMonitoringSelectedValue = "";

    @Override
    public void onPayloadReceived(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            growthMonitoringSelectedKey = JsonFormUtils.getValue(jsonObject, "growth_monitoring");
            growthMonitoringSelectedValue = JsonFormUtils.getCheckBoxValue(jsonObject, "growth_monitoring");
        }catch (JSONException e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (growthMonitoringSelectedKey.equalsIgnoreCase("")) return "";
        return MessageFormat.format(getContext().getString(R.string.growth_monitoring_subtitle), growthMonitoringSelectedValue);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (growthMonitoringSelectedKey.equalsIgnoreCase("")) return BaseAncHomeVisitAction.Status.PENDING;
        if (growthMonitoringSelectedKey.equalsIgnoreCase("growth_monitoring_no")) return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        else if (growthMonitoringSelectedKey.equalsIgnoreCase("growth_monitoring_yes")) return BaseAncHomeVisitAction.Status.COMPLETED;
        return BaseAncHomeVisitAction.Status.PENDING;
    }
}