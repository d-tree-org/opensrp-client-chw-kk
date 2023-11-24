package org.smartregister.chw.actionhelper;

import static org.smartregister.chw.util.ChildVisitUtil.getTranslatedOtherFood;

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
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.util.JsonFormUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2022-02-24
 */
public class NewBornCareBreastfeedingHelper extends HomeVisitActionHelper {
    private Context context;
    private Alert alert;
    private Map<String, List<VisitDetail>> details;
    private final boolean firstBreastFeedingVisitHappened;
    private String jsonString;
    private String breastfed_prev;
    private String time_to_breastfeed;
    private String breastfeed_current;
    private String other_food_child_feeds;
    private final ServiceWrapper serviceWrapperMap;

    public NewBornCareBreastfeedingHelper(Context context, Alert alert, boolean firstBreastFeedingVisitHappened, ServiceWrapper serviceWrapperMap) {
        this.context = context;
        this.alert = alert;
        this.firstBreastFeedingVisitHappened = firstBreastFeedingVisitHappened;
        this.serviceWrapperMap = serviceWrapperMap;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.details = details;
        this.jsonString = jsonString;
    }



    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            if (serviceWrapperMap != null) {
                String serviceName = serviceWrapperMap.getName();
                if ("months".equalsIgnoreCase(getPeriodNoun(serviceName))) {
                    if (getPeriod(serviceName) > 5) {
                        JsonFormUtils.getFieldJSONObject(fields, "visit_months").put(JsonFormConstants.VALUE, "yes");
                    }
                }
            }

            if (firstBreastFeedingVisitHappened) {
                JsonFormUtils.getFieldJSONObject(fields, "first_visit").put(JsonFormConstants.VALUE, "visit_done");
            }

            return jsonObject.toString();
        } catch (Exception e) {
            Timber.e(e);
        }
        return super.getPreProcessed();
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            breastfed_prev = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "breastfed_prev");
            time_to_breastfeed = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "time_to_breastfeed");
            breastfeed_current = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "breastfeed_current");
            other_food_child_feeds = org.smartregister.chw.util.JsonFormUtils.getValue(jsonObject, "other_food_child_feeds");
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @Override
    public String evaluateSubTitle() {
        if (StringUtils.isBlank(breastfeed_current) && StringUtils.isBlank(other_food_child_feeds))
            return "";
        String breastFeedCurrentText = "";
        if ("Yes".equalsIgnoreCase(breastfeed_current)) {
            breastFeedCurrentText = context.getString(R.string.yes);
        } else if ("No".equalsIgnoreCase(breastfeed_current)) {
            breastFeedCurrentText = context.getString(R.string.no);
        }

        return MessageFormat.format("{0}: {1}\n{2}: {3}",
                context.getString(R.string.newborn_care_breastfeeding_current), breastFeedCurrentText,
                context.getString(R.string.newborn_care_breastfeeding_other_food), getTranslatedOtherFood(context, other_food_child_feeds));
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {

        //Is child below six months
        boolean childBelowSixMonths = false;
        if (serviceWrapperMap != null) {
            String serviceName = serviceWrapperMap.getName();
            if ("hours".equalsIgnoreCase(getPeriodNoun(serviceName)) ||
                    "days".equalsIgnoreCase(getPeriodNoun(serviceName)) ||
                    "weeks".equalsIgnoreCase(getPeriodNoun(serviceName))){
                childBelowSixMonths = true;
            }else if ("months".equalsIgnoreCase(getPeriodNoun(serviceName))) {
                if (getPeriod(serviceName) < 6) {
                    childBelowSixMonths = true;
                }
            }
        }

        BaseAncHomeVisitAction.Status status;
        if (firstBreastFeedingVisitHappened) {
            if (StringUtils.isBlank(breastfeed_current) ||
                    StringUtils.isBlank(other_food_child_feeds) ||
                    (!other_food_child_feeds.equalsIgnoreCase("[\"None\"]")
                        && childBelowSixMonths) ||
                    breastfeed_current.equalsIgnoreCase("No")) {
                status = BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            } else {
                status = BaseAncHomeVisitAction.Status.COMPLETED;
            }
        } else {
            if (StringUtils.isBlank(breastfed_prev) ||
                    StringUtils.isBlank(time_to_breastfeed) ||
                    StringUtils.isBlank(breastfeed_current) ||
                    StringUtils.isBlank(other_food_child_feeds) ||
                    (!other_food_child_feeds.equalsIgnoreCase("[\"None\"]") &&
                            childBelowSixMonths) ||
                    breastfeed_current.equalsIgnoreCase("No")) {
                status = BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
            } else {
                status = BaseAncHomeVisitAction.Status.COMPLETED;
            }
        }
        return status;
    }

    private String getPeriodNoun(String serviceName) {
        String[] nameSplit = serviceName.split(" ");
        return nameSplit[nameSplit.length -1];
    }

    private int getPeriod(String serviceName) {
        String[] nameSplit = serviceName.split(" ");
        String periodString = nameSplit[nameSplit.length -2];

        return Integer.parseInt(periodString);
    }
}
