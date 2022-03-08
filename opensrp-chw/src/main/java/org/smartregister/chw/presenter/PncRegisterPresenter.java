package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.presenter.BaseAncRegisterPresenter;
import org.smartregister.util.DateUtil;
import org.smartregister.util.JsonFormUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2022-03-07
 */
public class PncRegisterPresenter extends BaseAncRegisterPresenter {
    public PncRegisterPresenter(BaseAncRegisterContract.View view, BaseAncRegisterContract.Model model, BaseAncRegisterContract.Interactor interactor) {
        super(view, model, interactor);
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode, String table) {

        JSONObject jsonObject = JsonFormUtils.toJSONObject(jsonString);
        JSONArray fields = JsonFormUtils.fields(jsonObject);
        getMiscarriageDateFromDays(fields);

        super.saveForm(jsonObject.toString(), isEditMode, table);
    }

    private void getMiscarriageDateFromDays(JSONArray fields) {
        try {
            JSONObject miscarriageUnknownObject = JsonFormUtils.getFieldJSONObject(fields, "miscarriage_date_unknown");
            JSONArray options = JsonFormUtils.getJSONArray(miscarriageUnknownObject, "options");
            JSONObject option = JsonFormUtils.getJSONObject(options, 0);
            String miscarriageUnKnownString = option != null ? option.getString("value") : null;
            if (StringUtils.isNotBlank(miscarriageUnKnownString) && Boolean.parseBoolean(miscarriageUnKnownString)) {
                String miscarriageDaysString = JsonFormUtils.getFieldValue(fields, "miscarriage_days");
                if (StringUtils.isNotBlank(miscarriageDaysString) && NumberUtils.isNumber(miscarriageDaysString)) {
                    int miscarriageDays = Integer.parseInt(miscarriageDaysString);
                    JSONObject dobJSONObject = JsonFormUtils.getFieldJSONObject(fields, "miscarriage_date");
                    dobJSONObject.put("value", getDate(miscarriageDays, DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT));
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
