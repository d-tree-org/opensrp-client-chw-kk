package org.smartregister.chw.actionhelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2022-03-23
 */
public class ProblemSolvingActionHelper extends HomeVisitActionHelper {

    private String experience_challenges_playing_communicating;

    @Override
    public void onPayloadReceived(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            experience_challenges_playing_communicating = JsonFormUtils.getFieldValue(fields, "experience_challenges_playing_communicating");

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return experience_challenges_playing_communicating.equalsIgnoreCase("Yes") ? getContext().getString(R.string.yes) : getContext().getString(R.string.no) ;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return experience_challenges_playing_communicating.equalsIgnoreCase("Yes") ? BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED : BaseAncHomeVisitAction.Status.COMPLETED;
    }
}
