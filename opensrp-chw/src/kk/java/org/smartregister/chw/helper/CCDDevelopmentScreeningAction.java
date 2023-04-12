package org.smartregister.chw.helper;

import android.content.Context;

import androidx.room.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CCDDevelopmentScreeningAction  extends HomeVisitActionHelper {

    private Context context;
    private Alert alert;
    private String developmentIssuesValue, developmentIssuesKeys;
    private Map<String, Boolean> visitPeriodMap;
    private final String visit_8_visit_10_visit_16 = "visit_8_visit_10_visit_16";

    private String jsonString;
    private ServiceWrapper serviceWrapper;

    public CCDDevelopmentScreeningAction(ServiceWrapper serviceWrapper, Alert alert) {
        this.alert = alert;
        this.serviceWrapper = serviceWrapper;
        initVisitPeriodMap();
    }

    public void initVisitPeriodMap() {
        visitPeriodMap = new HashMap<>();
        visitPeriodMap.put(visit_8_visit_10_visit_16, false);
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

            String visitNumber = getVisitNumber(serviceWrapper.getName());

            if (serviceWrapper != null) {
                if (visitNumber.equals("8") || visitNumber.equals("10") ||
                        visitNumber.equals("16") ) {
                    visitPeriodMap.put(visit_8_visit_10_visit_16, true);
                }
            }

            for (Map.Entry<String, Boolean> entry : visitPeriodMap.entrySet()) {
                if (entry.getValue()) {
                    org.smartregister.chw.anc.util.JsonFormUtils.getFieldJSONObject(fields, entry.getKey()).put("value", "true");
                }
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.getPreProcessed();
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            developmentIssuesValue = JsonFormUtils.getCheckBoxValue(jsonObject, "child_development_issues");
            developmentIssuesKeys = JsonFormUtils.getValue(jsonObject, "child_development_issues");
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("{0}: {1}", context.getString(R.string.ccd_development_screening_subtitle), developmentIssuesValue);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(developmentIssuesKeys)){
            return BaseAncHomeVisitAction.Status.PENDING;
        } else if (StringUtils.isNotBlank(developmentIssuesKeys) && !developmentIssuesKeys.contains("chk_none")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }

    private String getVisitNumber(String serviceName) {
        String[] nameSplit = serviceName.split(" ");
        return nameSplit[nameSplit.length - 1].substring(9);
    }
}
