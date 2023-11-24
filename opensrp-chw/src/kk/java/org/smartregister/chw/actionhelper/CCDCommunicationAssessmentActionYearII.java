package org.smartregister.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class CCDCommunicationAssessmentActionYearII extends HomeVisitActionHelper {
    private Context context;

    private final String bangoKititaPage;

    private String jsonPayload;

    private String communicatesWithChild = "";

    private String communicatesWithChildObservation = "";

    public CCDCommunicationAssessmentActionYearII(Context context, String bangoKititaPage) {
        this.context = context;
        this.bangoKititaPage = bangoKititaPage;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.context = context;
        this.jsonPayload = jsonString;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            if (bangoKititaPage != null) {
                JSONObject bango_kitita_page_ref = JsonFormUtils.getFieldJSONObject(fields, "bango_kitita_page_ref");
                assert bango_kitita_page_ref != null;
                bango_kitita_page_ref.remove(JsonFormConstants.VALUE);
                bango_kitita_page_ref.put(JsonFormConstants.VALUE, bangoKititaPage);
            }
            return jsonObject.toString();
        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            communicatesWithChild = JsonFormUtils.getValue(jsonObject, "communication_with_child");
            communicatesWithChildObservation = JsonFormUtils.getValue(jsonObject, "child_communication_observation");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        if(!StringUtils.isBlank(communicatesWithChild)){
            return MessageFormat.format("{0}: {1}", context.getString(R.string.ccd_communication_with_child), getTranslatedText(communicatesWithChild));
        }else{
            return "";
        }
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (communicatesWithChild.equalsIgnoreCase("no")){
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else if (communicatesWithChild.equalsIgnoreCase("yes") &&
                communicatesWithChildObservation.contains("chk_force_smile")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else if (communicatesWithChild.equals("yes") && !StringUtils.isEmpty(communicatesWithChild)) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }

    private String getTranslatedText(String text) {
        if ("Yes".equalsIgnoreCase(text)) {
            return context.getString(R.string.yes);
        } else if ("No".equalsIgnoreCase(text)) {
            return context.getString(R.string.no);
        }
        return "";
    }
}
