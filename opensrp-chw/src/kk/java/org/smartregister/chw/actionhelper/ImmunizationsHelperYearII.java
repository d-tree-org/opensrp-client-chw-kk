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
import org.smartregister.chw.dao.ImmunizationDao;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class ImmunizationsHelperYearII extends HomeVisitActionHelper {

    private String clinicCard;
    private String received_ipv;
    private String received_surua_rubella2= "";
    private String received_deworming_tablets= "";
    private String received_vitamin_a_droplets= "";
    private String vaccines_up_to_date = "";
    private String jsonString;
    private int childAgeInMonths;

    public ImmunizationsHelperYearII(int childAgeInMonths) {
        this.childAgeInMonths = childAgeInMonths;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonString = jsonString;
        this.context = context;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            clinicCard = JsonFormUtils.getValue(jsonObject, "clinic_card");
            received_deworming_tablets = JsonFormUtils.getValue(jsonObject, "deworming_tablets");
            received_vitamin_a_droplets = JsonFormUtils.getValue(jsonObject, "vitamin_a_droplets");
            received_ipv = JsonFormUtils.getValue(jsonObject, "received_ipv");
            received_surua_rubella2 = JsonFormUtils.getValue(jsonObject, "received_surua_rubella2");
            vaccines_up_to_date = JsonFormUtils.getValue(jsonObject, "vaccines_up_to_date");

        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        StringBuilder subTitle = new StringBuilder();
        if (vaccines_up_to_date.equalsIgnoreCase("no")) {
            if (received_vitamin_a_droplets.equalsIgnoreCase("no")) {
                subTitle.append("Vitamin A Droplets : ").append(context.getString(R.string.no)).append(" ");
            }

            if (received_deworming_tablets.equalsIgnoreCase("no")) {
                subTitle.append("Deworming Tablets : ").append(context.getString(R.string.no)).append(" ");
            }

            if (received_ipv.equalsIgnoreCase("no")) {
                subTitle.append("IPV : ").append(context.getString(R.string.no)).append(" ");
            }

            if (received_surua_rubella2.equalsIgnoreCase("no")) {
                subTitle.append("Surua - Rubella 2 : ").append(context.getString(R.string.no)).append(" ");
            }
        }
        return subTitle.toString();
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {

        if (vaccines_up_to_date.equalsIgnoreCase("no")) {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        } else if (StringUtils.isBlank(vaccines_up_to_date) || (StringUtils.isBlank(clinicCard)) ) {
            return BaseAncHomeVisitAction.Status.PENDING;
        }else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }

    private String getPeriodNoun(String serviceName) {
        String[] nameSplit = serviceName.split(" ");
        return nameSplit[nameSplit.length - 1];
    }

    private int getPeriod(String serviceName) {
        String[] nameSplit = serviceName.split(" ");
        String periodString = nameSplit[nameSplit.length - 2];

        return Integer.parseInt(periodString);
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            if (this.childAgeInMonths == 15 || this.childAgeInMonths == 18)
                Objects.requireNonNull(JsonFormUtils.getFieldJSONObject(fields, "received_ipv")).put(JsonFormConstants.HIDDEN, false);

            if (this.childAgeInMonths == 18 || this.childAgeInMonths == 19)
                Objects.requireNonNull(JsonFormUtils.getFieldJSONObject(fields, "received_surua_rubella2")).put(JsonFormConstants.HIDDEN, false);

            if (this.childAgeInMonths == 12 || this.childAgeInMonths == 18 || this.childAgeInMonths == 24)
                Objects.requireNonNull(JsonFormUtils.getFieldJSONObject(fields, "received_vitamin_a_droplets")).put(JsonFormConstants.HIDDEN, false);

            if (this.childAgeInMonths == 12 || this.childAgeInMonths == 13 || this.childAgeInMonths == 18 || this.childAgeInMonths == 24)
                Objects.requireNonNull(JsonFormUtils.getFieldJSONObject(fields, "received_deworming_tablets")).put(JsonFormConstants.HIDDEN, false);

            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.getPreProcessed();
    }
}