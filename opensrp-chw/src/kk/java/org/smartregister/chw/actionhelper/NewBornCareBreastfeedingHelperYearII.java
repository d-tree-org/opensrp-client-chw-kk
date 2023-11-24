package org.smartregister.chw.actionhelper;

import static org.smartregister.chw.util.ChildVisitUtil.getTranslatedOtherFood;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;

import java.text.MessageFormat;

import timber.log.Timber;

public class NewBornCareBreastfeedingHelperYearII  extends HomeVisitActionHelper {
    private final Context context;

    private String breastfeed_current;

    private String other_food_child_feeds;

    public NewBornCareBreastfeedingHelperYearII(Context context) {
        this.context = context;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            breastfeed_current = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "breastfeed_current");
            other_food_child_feeds = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "other_food_child_feeds");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        String breastFeedCurrentText = getBreastFeedCurrentText(breastfeed_current);
        if(!StringUtils.isBlank(breastfeed_current) && !StringUtils.isBlank(other_food_child_feeds)){
            return MessageFormat.format("{0}: {1}\n{2}: {3}",
                    context.getString(R.string.newborn_care_breastfeeding_current), breastFeedCurrentText,
                    context.getString(R.string.newborn_care_breastfeeding_other_food), getTranslatedOtherFood(context, other_food_child_feeds));
        }else{
            return "";
        }
    }

    private String getBreastFeedCurrentText(String breastfeedCurrent) {
        if ("Yes".equalsIgnoreCase(breastfeedCurrent)) {
            return context.getString(R.string.yes);
        } else if ("No".equalsIgnoreCase(breastfeedCurrent)) {
            return context.getString(R.string.no);
        }
        return "";
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        BaseAncHomeVisitAction.Status status = BaseAncHomeVisitAction.Status.PENDING;
        if(!StringUtils.isBlank(breastfeed_current) && !StringUtils.isBlank(other_food_child_feeds)){
            if(breastfeed_current.equalsIgnoreCase("No")){
                status = BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            }else {
                status = BaseAncHomeVisitAction.Status.COMPLETED;
            }
        }
        return status;
    }
}
