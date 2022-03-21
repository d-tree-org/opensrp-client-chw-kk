package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2022-03-21
 */
public class PlayAssessmentCounselingActionHelper extends HomeVisitActionHelper {

    private Context context;
    private String interaction_with_baby;
    private String play_with_child;

    public PlayAssessmentCounselingActionHelper(Context context) {
        this.context = context;

    }


    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            interaction_with_baby = JsonFormUtils.getValue(jsonObject, "interaction_with_baby");
            play_with_child = JsonFormUtils.getValue(jsonObject, "play_with_child");

        } catch (JSONException e) {
            Timber.e(e);
        }

    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(interaction_with_baby) || StringUtils.isBlank(play_with_child)) {
            return BaseAncHomeVisitAction.Status.PENDING;
        } else if ((interaction_with_baby.contains("move_baby_arms_legs_stroke_gently") ||
                interaction_with_baby.contains("get_baby_attention_shaker_toy")) && play_with_child.equalsIgnoreCase("Yes")) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }

    }
}
