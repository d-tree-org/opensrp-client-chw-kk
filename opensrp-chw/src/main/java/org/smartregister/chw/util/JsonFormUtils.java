package org.smartregister.chw.util;

import android.content.Context;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import timber.log.Timber;

import static com.vijay.jsonwizard.utils.NativeFormLangUtils.getTranslatedString;

/**
 * Created by keyman on 13/11/2018.
 */
public class JsonFormUtils extends CoreJsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final int REQUEST_CODE_GET_JSON = 2244;
    public static final int REQUEST_CODE_GET_JSON_WASH = 22444;
    public static final int REQUEST_CODE_GET_JSON_FAMILY_KIT = 22447;
    public static final int REQUEST_CODE_GET_JSON_HOUSEHOLD = 22445;

    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String READ_ONLY = "read_only";
    private static Flavor flavor = new JsonFormUtilsFlv();

    public static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientApplicationVersion(FamilyLibrary.getInstance().getApplicationVersion());
        event.setClientDatabaseVersion(FamilyLibrary.getInstance().getDatabaseVersion());

        return event;
    }

    public static void processEdiRegistrationEvent(String baseEntityId, String ediID, JSONObject clientJson, AllSharedPreferences sharedPreferences, boolean isEditMode){

        try {

            EventClientRepository eventClientRepository = new EventClientRepository();
            String encounterType = isEditMode ? "Update Client Edi ID" : "Register Client Edi ID";

            org.smartregister.domain.Event event = (new org.smartregister.domain.Event())
                    .withBaseEntityId(baseEntityId)
                    .withEventType(encounterType)
                    .withEventDate(new DateTime())
                    .withEntityType("client")
                    .withFormSubmissionId(org.smartregister.util.JsonFormUtils.generateRandomUUIDString());

            //Add EDI ID as observation to the registration event
            org.smartregister.domain.Obs edi_id_obs = new org.smartregister.domain.Obs();
            edi_id_obs.setFieldCode("edi_id");
            edi_id_obs.setFieldType("concept");
            edi_id_obs.setFieldDataType("text");
            edi_id_obs.setParentCode("");
            edi_id_obs.setValue(ediID);
            edi_id_obs.setFormSubmissionField("edi_id");
            edi_id_obs.setHumanReadableValue(ediID);

            event.addObs(edi_id_obs);

            eventClientRepository.addEvent(baseEntityId, eventClientRepository.convertToJson(tagSyncMetadata(sharedPreferences, event)));

            ChwApplication.getInstance().getClientProcessor().processClient(Collections.singletonList(new EventClient(event, eventClientRepository.convert(clientJson, org.smartregister.domain.Client.class))));

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static org.smartregister.domain.Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, org.smartregister.domain.Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientApplicationVersion(FamilyLibrary.getInstance().getApplicationVersion());
        event.setClientDatabaseVersion(FamilyLibrary.getInstance().getDatabaseVersion());

        return event;
    }

    protected static String locationId(AllSharedPreferences allSharedPreferences) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String userLocationId = allSharedPreferences.fetchUserLocalityId(providerId);
        if (StringUtils.isBlank(userLocationId)) {
            userLocationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        }

        return userLocationId;
    }

    public static Pair<Client, Event> processChildRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString) {

        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getString(jsonForm, ENTITY_ID);
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            lastInteractedWith(fields);

            dobUnknownUpdateFromAge(fields);

            processChildEnrollMent(jsonForm, fields);

            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);

            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, getString(jsonForm, ENCOUNTER_TYPE), CoreConstants.TABLE_NAME.CHILD);
            tagSyncMetadata(allSharedPreferences, baseEvent);

            if (baseClient != null || baseEvent != null) {
                String imageLocation = org.smartregister.family.util.JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                org.smartregister.family.util.JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
            }

            JSONObject lookUpJSONObject = getJSONObject(getJSONObject(jsonForm, METADATA), "look_up");
            String lookUpEntityId = "";
            String lookUpBaseEntityId = "";
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, "entity_id");
                lookUpBaseEntityId = getString(lookUpJSONObject, "value");
            }
            if (lookUpEntityId.equals("family") && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Client ss = new Client(lookUpBaseEntityId);
                Context context = ChwApplication.getInstance().getContext().applicationContext();
                addRelationship(context, ss, baseClient,jsonString);
                SQLiteDatabase db = ChwApplication.getInstance().getRepository().getReadableDatabase();
                EventClientRepository eventClientRepository = new EventClientRepository();
                JSONObject clientjson = eventClientRepository.getClient(db, lookUpBaseEntityId);
                baseClient.setAddresses(getAddressFromClientJson(clientjson));
            }


            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public static void addRelationship(Context context, Client parent, Client child,String jsonString) {
        try {
            JSONObject jsonObject=org.smartregister.util.JsonFormUtils.toJSONObject(jsonString);
            JSONArray formFields = org.smartregister.util.JsonFormUtils.fields(jsonObject);
            JSONObject motherEntityId = org.smartregister.util.JsonFormUtils.getFieldJSONObject(formFields,"mother_entity_id");
            child.addRelationship(org.smartregister.chw.anc.util.Constants.RELATIONSHIP.FAMILY,parent.getBaseEntityId());
            child.addRelationship(org.smartregister.chw.anc.util.Constants.RELATIONSHIP.MOTHER,getString(motherEntityId, "value"));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    private static void processChildEnrollMent(JSONObject jsonForm, JSONArray fields) {

        try {

            JSONObject surnam_familyName_SameObject = getFieldJSONObject(fields, "surname_same_as_family_name");
            JSONArray surnam_familyName_Same_options = getJSONArray(surnam_familyName_SameObject, org.smartregister.family.util.Constants.JSON_FORM_KEY.OPTIONS);
            JSONObject surnam_familyName_Same_option = getJSONObject(surnam_familyName_Same_options, 0);
            String surnam_familyName_SameString = surnam_familyName_Same_option != null ? surnam_familyName_Same_option.getString(VALUE) : null;

            if (StringUtils.isNotBlank(surnam_familyName_SameString) && Boolean.valueOf(surnam_familyName_SameString)) {
                String familyId = jsonForm.getJSONObject("metadata").getJSONObject("look_up").getString("value");
                CommonPersonObject familyObject = ChwApplication.getInstance().getContext().commonrepository("ec_family").findByCaseID(familyId);
                if (ChwApplication.getApplicationFlavor().hasSurname()) {
                    String lastname = familyObject.getColumnmaps().get(DBConstants.KEY.LAST_NAME);
                    JSONObject surname_object = getFieldJSONObject(fields, "surname");
                    surname_object.put(VALUE, lastname);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    /**
     * Returns a value from json form field
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getValue(JSONObject jsonObject, String key) {
        try {
            JSONObject formField = com.vijay.jsonwizard.utils.FormUtils.getFieldFromForm(jsonObject, key);
            if (formField != null && formField.has(JsonFormConstants.VALUE)) {
                return formField.getString(JsonFormConstants.VALUE);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    /**
     * Returns a value from a native forms checkbox field and returns an comma separated string
     *
     * @param jsonObject native forms jsonObject
     * @param key        field object key
     * @return value
     */
    public static String getCheckBoxValue(JSONObject jsonObject, String key) {
        try {
            JSONArray jsonArray = jsonObject.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

            JSONObject jo = null;
            int x = 0;
            while (jsonArray.length() > x) {
                jo = jsonArray.getJSONObject(x);
                if (jo.getString(JsonFormConstants.KEY).equalsIgnoreCase(key)) {
                    break;
                }
                x++;
            }

            StringBuilder resBuilder = new StringBuilder();
            if (jo != null) {
                // read all the checkboxes
                JSONArray jaOptions = jo.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                int optionSize = jaOptions.length();
                int y = 0;
                while (optionSize > y) {
                    JSONObject options = jaOptions.getJSONObject(y);
                    if (options.getBoolean(JsonFormConstants.VALUE)) {
                        resBuilder.append(options.getString(JsonFormConstants.TEXT)).append(", ");
                    }
                    y++;
                }

                String res = resBuilder.toString();
                res = (res.length() >= 2) ? res.substring(0, res.length() - 2) : "";
                return res;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
        return "";
    }

    public static JSONObject getJson(Context context, String formName, String baseEntityID) throws Exception {
        String locationId = ChwApplication.getInstance().getContext().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
        JSONObject jsonObject = new JSONObject(getTranslatedString(FormUtils.getInstance(context).getFormJson(formName).toString(), context));
        org.smartregister.chw.anc.util.JsonFormUtils.getRegistrationForm(jsonObject, baseEntityID, locationId);
        return jsonObject;
    }

    public static JSONObject getAutoPopulatedJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver) {
        return flavor.getAutoJsonEditMemberFormString(title, formName, context, client, eventType, familyName, isPrimaryCaregiver);
    }

    public static void populatedJsonForm(@NotNull JSONObject jsonObject, @NotNull Map<String, String> valueMap) throws JSONException {
        Map<String, String> _valueMap = new HashMap<>(valueMap);
        int step = 1;
        while (jsonObject.has("step" + step)) {
            JSONObject jsonStepObject = jsonObject.getJSONObject("step" + step);
            JSONArray array = jsonStepObject.getJSONArray(JsonFormConstants.FIELDS);
            int position = 0;
            while (position < array.length() && _valueMap.size() > 0) {

                JSONObject object = array.getJSONObject(position);
                String key = object.getString(JsonFormConstants.KEY);

                if (_valueMap.containsKey(key)) {
                    object.put(JsonFormConstants.VALUE, _valueMap.get(key));
                    _valueMap.remove(key);
                }

                position++;
            }

            step++;
        }
    }

    public static String getBirthCertificateRegex() {
        List<String> certificateNumbers = ChwChildDao.getRegisteredCertificateNumbers();
        final String regexPrefix = "^(?!.*^(";
        final String regexPostfix = ")$).*^(([0-9]{1,14})|\\s*).";
        String formattedNumbers = "";
        for (String number : certificateNumbers) {
            formattedNumbers = formattedNumbers.concat(String.format("|%s", number));
        }
        return String.format("%s%s%s", regexPrefix, formattedNumbers, regexPostfix);
    }

    public static String getSpinnerFieldValue(String jsonString, String fieldKey) {
        String hintText = "";
        String valueText = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray formFields = org.smartregister.util.JsonFormUtils.fields(jsonObject);
            JSONObject fieldJsonObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(formFields, fieldKey);
            if (fieldJsonObject != null) {
                hintText = fieldJsonObject.getString(JsonFormConstants.HINT);
                valueText = fieldJsonObject.getString(JsonFormConstants.VALUE);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return hintText.equalsIgnoreCase(valueText) ? "" : valueText;
    }

    public interface Flavor {
        JSONObject getAutoJsonEditMemberFormString(String title, String formName, Context context, CommonPersonObjectClient client, String eventType, String familyName, boolean isPrimaryCaregiver);

        void processFieldsForMemberEdit(CommonPersonObjectClient client, JSONObject jsonObject, JSONArray jsonArray, String familyName, boolean isPrimaryCaregiver, Event ecEvent, Client ecClient) throws JSONException;
    }

}
