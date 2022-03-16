package org.smartregister.chw.helper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CCDCommunicationAssessmentAction extends HomeVisitActionHelper {

    private Context context;
    private Alert alert;
    private String developmentIssues;
    private String jsonPayload;
    private int ageInMonth;

    public CCDCommunicationAssessmentAction(Context context, Alert alert, int ageInMonth){
        this.alert = alert;
        this.context = context;
        this.ageInMonth = ageInMonth;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.context = context;
        this.jsonPayload = jsonString;
    }

    @Override
    public String getPreProcessed() {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            JSONObject child_age_in_month = JsonFormUtils.getFieldJSONObject(fields, "child_age_in_months");
            assert child_age_in_month != null;
            child_age_in_month.remove(JsonFormConstants.VALUE);
            child_age_in_month.put(JsonFormConstants.VALUE, ageInMonth);

            return jsonObject.toString();

        }catch (Exception e){
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            //developmentIssues = JsonFormUtils.getCheckBoxValue(jsonObject, "child_development_issues");
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("Communication assessment: {0}", ""/* developmentIssues */);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(developmentIssues)){
            return BaseAncHomeVisitAction.Status.PENDING;
        }else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }
}
