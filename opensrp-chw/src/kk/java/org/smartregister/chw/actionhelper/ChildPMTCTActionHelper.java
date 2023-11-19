package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;

import java.util.List;
import java.util.Map;

public class ChildPMTCTActionHelper extends HomeVisitActionHelper {
    private Context context;
    String hiv_test;
    String disclose_status;
    String taking_art;
    String hiv_status;

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.context = context;
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            hiv_test = JsonFormUtils.getValue(jsonObject, "test_hiv_past_three_months").toLowerCase();
            disclose_status = JsonFormUtils.getValue(jsonObject, "comfortable_disclosing_hiv_status").toLowerCase();
            taking_art = JsonFormUtils.getValue(jsonObject, "already_taking_art").toLowerCase();
            hiv_status = JsonFormUtils.getValue(jsonObject, "hiv_status").toLowerCase();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {

        if (hiv_test.contains("chk_hiv_test_no")){
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }

        if (hiv_test.contains("chk_hiv_test_yes") && disclose_status.contains("chk_hiv_disclosing_status_no")){
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }

        if (hiv_test.contains("chk_hiv_test_yes") && disclose_status.contains("chk_hiv_disclosing_status_yes") && taking_art.contains("chk_taking_art_no")){
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }

        if (hiv_test.contains("chk_hiv_test_yes") && hiv_status.contains("chk_hiv_status_negative")){
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }

        if (hiv_test.contains("chk_hiv_test_yes") && hiv_status.contains("chk_hiv_status_positive") && taking_art.contains("chk_taking_art_yes")){
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }

        return BaseAncHomeVisitAction.Status.PENDING;

    }
}
