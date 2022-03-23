package org.smartregister.chw.presenter;

import static org.smartregister.chw.core.utils.CoreJsonFormUtils.toList;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.presenter.BaseAncRegisterPresenter;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.interactor.FamilyChangeContractInteractor;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.DateUtil;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.LocationPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2022-03-07
 */
public class PncRegisterPresenter extends BaseAncRegisterPresenter {
    protected String familyBaseEntityId;
    public PncRegisterPresenter(BaseAncRegisterContract.View view, BaseAncRegisterContract.Model model, BaseAncRegisterContract.Interactor interactor, String familyBaseEntityId) {
        super(view, model, interactor);
        this.familyBaseEntityId = familyBaseEntityId;
        this.interactor = interactor;
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode, String table) {

        JSONObject jsonObject = JsonFormUtils.toJSONObject(jsonString);
        JSONArray fields = JsonFormUtils.fields(jsonObject);
        updateMiscarriageDateFromDays(fields);
        updateDeliveryDateFromDays(fields);
        super.saveForm(jsonObject.toString(), isEditMode, table);
    }

    private void updateMiscarriageDateFromDays(JSONArray fields) {
        try {
            JSONObject miscarriageUnknownObject = JsonFormUtils.getFieldJSONObject(fields, "miscarriage_date_unknown");
            JSONArray options = JsonFormUtils.getJSONArray(miscarriageUnknownObject, "options");
            JSONObject option = JsonFormUtils.getJSONObject(options, 0);
            String miscarriageUnKnownString = option != null ? option.getString("value") : null;
            if (StringUtils.isNotBlank(miscarriageUnKnownString) && Boolean.parseBoolean(miscarriageUnKnownString)) {
                String miscarriageDaysString = JsonFormUtils.getFieldValue(fields, "miscarriage_days");
                if (StringUtils.isNotBlank(miscarriageDaysString) && NumberUtils.isNumber(miscarriageDaysString)) {
                    int miscarriageDays = Integer.parseInt(miscarriageDaysString);
                    JSONObject miscarriageDateJson = JsonFormUtils.getFieldJSONObject(fields, "miscarriage_date");
                    miscarriageDateJson.put("value", getDate(miscarriageDays, DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT));
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

    }

    private void updateDeliveryDateFromDays(JSONArray fields) {
        try {
            JSONObject deliveryDateUnknownChkBox = JsonFormUtils.getFieldJSONObject(fields, "delivery_date_unknown_chk_box");
            JSONArray options = JsonFormUtils.getJSONArray(deliveryDateUnknownChkBox, "options");
            JSONObject option = JsonFormUtils.getJSONObject(options, 0);
            String deliveryDateUnknownChkBoxString = option != null ? option.getString("value") : null;
            if (StringUtils.isNotBlank(deliveryDateUnknownChkBoxString) && Boolean.parseBoolean(deliveryDateUnknownChkBoxString)) {
                String deliveryDateUnknownString = JsonFormUtils.getFieldValue(fields, "delivery_date_unknown");
                if (StringUtils.isNotBlank(deliveryDateUnknownString) && NumberUtils.isNumber(deliveryDateUnknownString)) {
                    int daysPassedAfterDelivery = Integer.parseInt(deliveryDateUnknownString);
                    JSONObject deliveryDateJSONObject = JsonFormUtils.getFieldJSONObject(fields, "delivery_date");
                    deliveryDateJSONObject.put("value", getDate(daysPassedAfterDelivery, DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT));
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }

    }

    private String getDate(int days, String dateFormatPattern) {
        String pattern = dateFormatPattern;
        if (StringUtils.isBlank(dateFormatPattern)) {
            pattern = DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT;
        }

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -days);
        cal.add(Calendar.YEAR, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(cal.getTime());
    }
}
