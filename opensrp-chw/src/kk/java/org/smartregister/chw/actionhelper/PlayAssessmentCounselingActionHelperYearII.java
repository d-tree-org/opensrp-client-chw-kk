package org.smartregister.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PlayAssessmentCounselingActionHelperYearII extends HomeVisitActionHelper {
    private final Context context;

    private String jsonString;

    private String interaction_with_baby;

    private String play_with_child;

    private final String bangoKititaPage;

    public PlayAssessmentCounselingActionHelperYearII(Context context, String bangoKititaPage) {
        this.context = context;
        this.bangoKititaPage = bangoKititaPage;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            interaction_with_baby = JsonFormUtils.getValue(jsonObject, "interaction_with_baby");
            play_with_child = JsonFormUtils.getValue(jsonObject, "play_with_child");
        }catch(Exception e){
            Timber.e(e);
        }
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonString = jsonString;
        super.onJsonFormLoaded(jsonString, context, details);
    }

    @Override
    public String getPreProcessed() {

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            if (bangoKititaPage != null) {
                JSONObject bango_kitita_page_ref = org.smartregister.chw.util.JsonFormUtils.getFieldJSONObject(fields, "bango_kitita_page_ref");
                assert bango_kitita_page_ref != null;
                bango_kitita_page_ref.remove(JsonFormConstants.VALUE);
                bango_kitita_page_ref.put(JsonFormConstants.VALUE, bangoKititaPage);
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.getPreProcessed();
    }

    @Override
    public String evaluateSubTitle() {
        if(!StringUtils.isBlank(play_with_child)){
            return MessageFormat.format("{0}: {1}", context.getString(R.string.ccd_play_child), getTranslatedText(play_with_child));
        }else{
            return "";
        }
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(interaction_with_baby) || StringUtils.isBlank(play_with_child)){
            return BaseAncHomeVisitAction.Status.PENDING;
        }else if((interaction_with_baby.contains("move_baby_arms_legs_stroke_gently") || interaction_with_baby.contains("get_baby_attention_shaker_toy")) && play_with_child.contains("Yes")){
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }else{
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
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
