package org.smartregister.chw.actionhelper;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;

import java.text.MessageFormat;

public class MalariaPreventionActionHelperYearII  extends HomeVisitActionHelper {
    private String llitn;
    private String llitn_condition;

    @Override
    public void onPayloadReceived(String jsonPayload) {
        llitn = JsonFormUtils.getSpinnerFieldValue(jsonPayload, "llin_last_night");
        llitn_condition = JsonFormUtils.getSpinnerFieldValue(jsonPayload, "llin_condition");
    }

    @Override
    public String evaluateSubTitle() {
        if (StringUtils.isBlank(llitn) && StringUtils.isBlank(llitn_condition))
            return "";
        String llin_condition_translated = "";

        if ("Good".equalsIgnoreCase(llitn_condition)) {
            llin_condition_translated = context.getString(R.string.malaria_prevention_llin_condition_good);
        } else if ("Fair".equalsIgnoreCase(llitn_condition)) {
            llin_condition_translated = context.getString(R.string.malaria_prevention_llin_condition_fair);
        } else if ("Poor".equalsIgnoreCase(llitn_condition)) {
            llin_condition_translated = context.getString(R.string.malaria_prevention_llin_condition_poor);
        }

        String translated_llitn = "";
        if (llitn.equalsIgnoreCase("Yes")) {
            translated_llitn = context.getString(R.string.yes);
        } else if (llitn.equalsIgnoreCase("No")) {
            translated_llitn = context.getString(R.string.no);
        }

        if (StringUtils.isBlank(llitn)) {
            return MessageFormat.format("{0}: {1}", context.getString(R.string.malaria_prevention_llin_condition), llin_condition_translated);
        } else if (StringUtils.isBlank(llitn_condition)) {
            return MessageFormat.format("{0}: {1}", context.getString(R.string.llin_last_night_under_net), translated_llitn);
        } else {
            return MessageFormat.format("{0}: {1}\n{2}: {3}",
                    context.getString(R.string.llin_last_night_under_net), translated_llitn,
                    context.getString(R.string.malaria_prevention_llin_condition), llin_condition_translated
            );
        }
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(llitn) && StringUtils.isBlank(llitn_condition))
            return BaseAncHomeVisitAction.Status.PENDING;

        if (llitn.equalsIgnoreCase("No") || llitn_condition.equalsIgnoreCase("Poor") || (StringUtils.isBlank(llitn) || StringUtils.isBlank(llitn_condition))) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }
}