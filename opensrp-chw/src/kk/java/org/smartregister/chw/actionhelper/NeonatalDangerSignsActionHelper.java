package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;

import timber.log.Timber;

public class NeonatalDangerSignsActionHelper extends HomeVisitActionHelper {
    private Context context;
    private String neoNateDangerSigns;

    public NeonatalDangerSignsActionHelper(Context context) {
        this.context = context;
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            neoNateDangerSigns = JsonFormUtils.getCheckBoxValue(jsonObject, "neonate_danger_signs");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("{0}: {1}", context.getString(R.string.neonatal_danger_signs), neoNateDangerSigns);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(neoNateDangerSigns)) {
            return BaseAncHomeVisitAction.Status.PENDING;
        } else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }

}
