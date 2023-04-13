package org.smartregister.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MalnutritionScreeningActionHelper extends HomeVisitActionHelper {

    String growthMonitoringSelectedKey = "";
    String growthMonitoringSelectedValue = "";
    String palmPallorValue = "";
    String palmPallorKey= "";
    String childGrowthMuacKey = "";
    String childGrowthMuacValue = "";

    String jsonString = "";
    ServiceWrapper serviceWrapper;

    public MalnutritionScreeningActionHelper(ServiceWrapper serviceWrapper){
        this.serviceWrapper = serviceWrapper;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonString = jsonString;
        this.context = context;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray fields = org.smartregister.chw.anc.util.JsonFormUtils.fields(jsonObject);

            String servicePronoun = getPeriodNoun(serviceWrapper.getName());
            int period = getPeriod(servicePronoun);

            if (servicePronoun.contains("week")){
                if (period >= 5){
                    org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "child_growth_muac").put(JsonFormConstants.HIDDEN, true);
                }
            }else if (servicePronoun.contains("month")) {
                if (period < 6) {
                    org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "child_growth_muac").put(JsonFormConstants.HIDDEN, true);
                }
            }else{
                org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "child_growth_muac").put(JsonFormConstants.HIDDEN, false);
            }
            return jsonObject.toString();
        }catch (JSONException e){
            Timber.e(e);
        }
        return super.getPreProcessed();
    }

    @Override
    public void onPayloadReceived(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            growthMonitoringSelectedKey = JsonFormUtils.getValue(jsonObject, "growth_monitoring");
            growthMonitoringSelectedValue = JsonFormUtils.getCheckBoxValue(jsonObject, "growth_monitoring");
            palmPallorKey = JsonFormUtils.getValue(jsonObject, "palm_pallor");
            childGrowthMuacKey = JsonFormUtils.getValue(jsonObject, "child_growth_muac");
        }catch (JSONException e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if (growthMonitoringSelectedKey.isEmpty()) return "";

        if (palmPallorKey.contains("palm_pallor_no")) {
            palmPallorValue = context.getString(R.string.no);
        }

        if (palmPallorKey.contains("palm_pallor_yes")) {
            palmPallorValue = context.getString(R.string.yes);
        }

        if (childGrowthMuacKey.contains("Red")) {
            childGrowthMuacValue = context.getString(R.string.palm_pallor_red);
        }

        if (childGrowthMuacKey.contains("Green")) {
            childGrowthMuacValue = context.getString(R.string.palm_pallor_green);
        }

        if (childGrowthMuacKey.contains("Yellow")) {
            childGrowthMuacValue = context.getString(R.string.palm_pallor_yellow);
        }

        return getContext().getString(R.string.malnutrition_screening_subtitle, childGrowthMuacValue, palmPallorValue);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {

        if (growthMonitoringSelectedKey.isEmpty() || palmPallorKey.isEmpty()) {
            return BaseAncHomeVisitAction.Status.PENDING;
        } else if (childGrowthMuacKey.contains("Red") && palmPallorKey.contains("palm_pallor_yes")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }

    private String getPeriodNoun(String serviceName) {
        String[] nameSplit = serviceName.split(" ");
        return nameSplit[nameSplit.length - 1];
    }

    private int getPeriod(String serviceName) {
        String periodString = serviceName.replaceAll("[^0-9]", "");
        return Integer.parseInt(periodString);
    }

}