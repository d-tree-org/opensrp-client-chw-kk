package org.smartregister.chw.actionhelper;

import static org.smartregister.chw.util.ChildVisitUtil.getTranslatedOtherFood;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class BreastfeedingHelperYearII extends HomeVisitActionHelper {
    private Context context;

    private String jsonString;
    private int childAgeInMonths;
    private String breastfeed_current;
    private String other_food_child_feeds;

    public BreastfeedingHelperYearII(Context context, int childAge) {
        this.childAgeInMonths = childAge;
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
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            //15, 18, 21, 24
            if (childAgeInMonths == 15 || childAgeInMonths == 18 ||
                childAgeInMonths == 21 || childAgeInMonths == 24){
                Objects.requireNonNull(JsonFormUtils.getFieldJSONObject(fields, "visit_month_15_18_21_24")).put(JsonFormConstants.VALUE, true);
            }else{
                Objects.requireNonNull(JsonFormUtils.getFieldJSONObject(fields, "visit_month_15_18_21_24")).put(JsonFormConstants.VALUE, false);
            }

            return jsonObject.toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return super.getPreProcessed();
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {

        jsonString = jsonPayload;

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
        if(!StringUtils.isBlank(breastfeed_current)){
            return MessageFormat.format("{0}: {1}",
                    context.getString(R.string.newborn_care_breastfeeding_current), breastFeedCurrentText);
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
        if(!StringUtils.isBlank(breastfeed_current)){
            if(breastfeed_current.equalsIgnoreCase("No")){
                status = BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            }else {
                status = BaseAncHomeVisitAction.Status.COMPLETED;
            }
        }
        return status;
    }
}
