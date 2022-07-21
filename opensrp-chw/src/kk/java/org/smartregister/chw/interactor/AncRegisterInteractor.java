package org.smartregister.chw.interactor;

import static org.smartregister.chw.malaria.util.DBConstants.KEY.GEST_AGE;

import android.content.ContentValues;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class AncRegisterInteractor extends BaseAncRegisterInteractor {
    @Override
    public void saveRegistration(String jsonString, boolean isEditMode, BaseAncRegisterContract.InteractorCallBack callBack, String table) {
//        super.saveRegistration(jsonString, isEditMode, callBack, table);
        Runnable runnable = () -> {
            // save it
            String encounterType = "";
            boolean hasChildren = false;

            try {
                JSONObject form = new JSONObject(jsonString);
                encounterType = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

                if (encounterType.equalsIgnoreCase(Constants.EVENT_TYPE.PREGNANCY_OUTCOME)) {

                    saveRegistration(form.toString(), table, form);

                    String motherBaseId = form.optString(Constants.JSON_FORM_EXTRA.ENTITY_TYPE);
                    JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                    JSONObject deliveryDate = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.DELIVERY_DATE);
                    JSONObject famNameObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.FAM_NAME);

                    String familyName = famNameObject != null ? famNameObject.optString(JsonFormUtils.VALUE) : "";
                    String dob = deliveryDate.optString(JsonFormUtils.VALUE);
                    hasChildren = StringUtils.isNotBlank(deliveryDate.optString(JsonFormUtils.VALUE));

                    JSONObject familyIdObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.RELATIONAL_ID);
                    String familyBaseEntityId = familyIdObject.getString(JsonFormUtils.VALUE);

                    Map<String, List<JSONObject>> jsonObjectMap = getChildFieldMaps(fields);

                    generateAndSaveFormsForEachChild(jsonObjectMap, motherBaseId, familyBaseEntityId, dob, familyName);

                } else if (encounterType.equalsIgnoreCase(Constants.EVENT_TYPE.ANC_REGISTRATION)) {

                    JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                    JSONObject lmp = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.LAST_MENSTRUAL_PERIOD);
                    boolean hasLmp = StringUtils.isNotBlank(lmp.optString(JsonFormUtils.VALUE));

                    if (!hasLmp) {
                        JSONObject eddJson = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.EDD);
                        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("dd-MM-yyyy");

                        LocalDate lmpDate = dateTimeFormat.parseLocalDate(eddJson.optString(JsonFormUtils.VALUE)).plusDays(-280);
                        lmp.put(JsonFormUtils.VALUE, dateTimeFormat.print(lmpDate));
                    }

                    saveRegistration(form.toString(), table, form);
                } else {
                    saveRegistration(jsonString, table, form);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

            String finalEncounterType = encounterType;
            boolean finalHasChildren = hasChildren;
            appExecutors.mainThread().execute(() -> {
                try {
                    callBack.onRegistrationSaved(finalEncounterType, isEditMode, finalHasChildren);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
        appExecutors.diskIO().execute(runnable);
    }

    private void saveRegistration(final String jsonString, String table,JSONObject form) throws Exception {
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();
        Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, jsonString, Constants.TABLES.ANC_MEMBERS);

        NCUtils.addEvent(allSharedPreferences, baseEvent);
        NCUtils.startClientProcessing();

        updateAncDetails(jsonString);
    }

    private void updateAncDetails(String jsonString){
        try{
            JSONObject form = new JSONObject(jsonString);
            form.put(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE, CoreConstants.EventType.UPDATE_ANC_REGISTRATION);
            jsonString = form.toString();
            AllSharedPreferences allSharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
            Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, jsonString, Constants.TABLES.ANC_MEMBERS);
            NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
            AllCommonsRepository commonsRepository = CoreChwApplication.getInstance().getAllCommonsRepository(CoreConstants.TABLE_NAME.ANC_MEMBER);

            JSONArray field = org.smartregister.util.JsonFormUtils.fields(form);
            String phoneNumber =  org.smartregister.util.JsonFormUtils.getFieldJSONObject(field, DBConstants.KEY.PHONE_NUMBER).getString(CoreJsonFormUtils.VALUE);
            String baseEntityId = baseEvent.getBaseEntityId();
            if (commonsRepository != null) {
                ContentValues values = new ContentValues();
                values.put(DBConstants.KEY.PHONE_NUMBER, phoneNumber);
                CoreChwApplication.getInstance().getRepository().getWritableDatabase().update(CoreConstants.TABLE_NAME.ANC_MEMBER, values, DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});
            }
        }catch (Exception e){
            Timber.e("Exception From updating the anc member: %S",e);
        }
    }

    private Map<String, List<JSONObject>> getChildFieldMaps(JSONArray fields) {
        Map<String, List<JSONObject>> jsonObjectMap = new HashMap();

        for (int i = 0; i < fields.length(); i++) {
            try {
                JSONObject jsonObject = fields.getJSONObject(i);
                String key = jsonObject.getString(JsonFormUtils.KEY);
                String keySplit = key.substring(key.lastIndexOf("_"));
                if (keySplit.matches(".*\\d.*")) {

                    String formattedKey = keySplit.replaceAll("[^\\d.]", "");
                    if (formattedKey.length() < 10)
                        continue;
                    List<JSONObject> jsonObjectList = jsonObjectMap.get(formattedKey);

                    if (jsonObjectList == null)
                        jsonObjectList = new ArrayList<>();

                    jsonObjectList.add(jsonObject);
                    jsonObjectMap.put(formattedKey, jsonObjectList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObjectMap;
    }
}
