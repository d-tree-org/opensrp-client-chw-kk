package org.smartregister.chw.interactor;

import static org.smartregister.chw.anc.util.DBConstants.KEY.DOB;
import static org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME;
import static org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME;
import static org.smartregister.chw.anc.util.DBConstants.KEY.RELATIONAL_ID;
import static org.smartregister.chw.anc.util.DBConstants.KEY.SUR_NAME;
import static org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID;

import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.domain.FamilyMember;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.DateUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2022-03-11
 */
public class PncRegisterInteractor extends BaseAncRegisterInteractor {
    private String familyBaseEntityId;
    private final Context context;

    public PncRegisterInteractor(Context context) {
        this.context = context;
    }
    @Override
    public void saveRegistration(String jsonString, boolean isEditMode, BaseAncRegisterContract.InteractorCallBack callBack, String table) {
        super.saveRegistration(jsonString, isEditMode, callBack, table);
        try {
            JSONObject jsonObject = org.smartregister.util.JsonFormUtils.toJSONObject(jsonString);
            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(jsonObject);
            JSONObject familyIdObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.RELATIONAL_ID);
            familyBaseEntityId = familyIdObject != null ? familyIdObject.getString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE) : "";

            if (!isPrimaryCareGiverMother(fields)) {
                String careGiverJsonString = getCareGiverJsonString(fields);
                FamilyEventClient familyEventClient = JsonFormUtils.processFamilyMemberRegistrationForm(FamilyLibrary.getInstance().context().allSharedPreferences(), careGiverJsonString, familyBaseEntityId);
                FamilyMember familyMember = CoreJsonFormUtils.getFamilyMemberFromRegistrationForm(careGiverJsonString, familyBaseEntityId, familyEventClient.getClient().getBaseEntityId());
                updateFamilyRelations(context, familyMember, getLocationID());
                saveFamilyMemberRegistration(familyEventClient, careGiverJsonString, false);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getCareGiverJsonString(JSONArray fields) {

        try {

            JSONObject familyMemberForm = getModel().getFormAsJson(
                    AncLibrary.getInstance().context().applicationContext(),
                    CoreConstants.JSON_FORM.getFamilyMemberRegister(),
                    JsonFormUtils.generateRandomUUIDString(),
                    getLocationID()
            );

            JSONObject famNameObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.FAM_NAME);
            String familyName = famNameObject != null ? famNameObject.optString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE) : "";

            if (familyMemberForm != null) {
                familyMemberForm.put(RELATIONAL_ID, familyBaseEntityId);


                JSONObject stepOne = familyMemberForm.getJSONObject(org.smartregister.chw.anc.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.chw.anc.util.JsonFormUtils.FIELDS);

                String uniqueId = this.getUniqueIdRepository().getNextUniqueId().getOpenmrsId();

                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, UNIQUE_ID, uniqueId.replace("-", ""));

                int x = 0;
                while (x < fields.length()) {
                    JSONObject field = fields.getJSONObject(x);
                    String fieldKey = field.getString("key");
                    switch (fieldKey) {
                        case "caregiver_surname":
                            JSONObject sameAsFamilyNameObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "primary_caregiver_same_as_fam_name");
                            boolean isSurnameSameAsFamilyName = false;
                            if (sameAsFamilyNameObject != null) {
                                JSONObject sameAsFamNameOption = sameAsFamilyNameObject.optJSONArray(DBConstants.KEY.OPTIONS).getJSONObject(0);
                                isSurnameSameAsFamilyName = sameAsFamNameOption.optBoolean(org.smartregister.chw.anc.util.JsonFormUtils.VALUE);
                            }

                            if (isSurnameSameAsFamilyName) {

                                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray,
                                        "surname_calculation", familyName);
                            } else {
                                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray,
                                        SUR_NAME, field.getString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));
                                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray,
                                        "surname_calculation", field.getString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));
                            }
                            break;

                        case "caregiver_first_name":
                            org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, FIRST_NAME, field.getString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));
                            break;

                        case "caregiver_middle_name":
                            org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, MIDDLE_NAME, field.optString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));
                            break;

                        case "primary_caregiver_dob":
                            JSONObject dobUnknownChkObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "dob_unknown");
                            boolean dobUnknown = false;
                            if (dobUnknownChkObject != null) {
                                JSONObject dobUnkownObject = dobUnknownChkObject.optJSONArray(DBConstants.KEY.OPTIONS).getJSONObject(0);
                                dobUnknown = dobUnkownObject.optBoolean(org.smartregister.chw.anc.util.JsonFormUtils.VALUE);
                            }

                            if (dobUnknown) {
                                JSONObject ageObject = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "primary_caregiver_age");
                                String age = ageObject != null ? ageObject.optString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE) : "";
                                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray,
                                        "age", age);
                                int ageInt = Integer.parseInt(age);
                                String dob = org.smartregister.util.Utils.getDob(ageInt, DateUtil.DATE_FORMAT_FOR_TIMELINE_EVENT);
                                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray,
                                        DOB, dob);

                            } else {
                                org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray,
                                        DOB, field.getString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));
                            }

                            break;

                        case "primary_caregiver_sex":
                            org.smartregister.chw.anc.util.JsonFormUtils.updateFormField(jsonArray, "sex", field.getString(org.smartregister.chw.anc.util.JsonFormUtils.VALUE));
                        default:
                            break;
                    }

                    x++;
                }

            }

            return familyMemberForm != null ? familyMemberForm.toString() : null;
        } catch (Exception e) {
            Timber.e(e);
        }

        return null;
    }

    public void saveFamilyMemberRegistration(final FamilyEventClient familyEventClient, final String jsonString, final boolean isEditMode) {
        Runnable runnable = new Runnable() {
            public void run() {
                PncRegisterInteractor.this.saveMemberRegistration(familyEventClient, jsonString, isEditMode);
            }
        };

        this.appExecutors.diskIO().execute(runnable);
    }

    private boolean isPrimaryCareGiverMother(JSONArray fields) {
        String selectedPrimaryCaregiver = null;
        try {
            JSONObject primaryCaregiverObject = JsonFormUtils.getFieldJSONObject(fields, "is_primarycaregiver");
            assert primaryCaregiverObject != null;
            selectedPrimaryCaregiver = (String) primaryCaregiverObject.get("value");
        } catch (JSONException e) {
            Timber.e(e);
        }
        return "Mother".equalsIgnoreCase(selectedPrimaryCaregiver);
    }

    public void updateFamilyRelations(Context context, FamilyMember familyMember, String lastLocationId) throws Exception {

        // update the ec model
        ECSyncHelper syncHelper = this.getSyncHelper();

        // update family record
        Pair<List<Client>, List<Event>> clientEventPair = CoreJsonFormUtils.processFamilyUpdateRelations(CoreChwApplication.getInstance(), context, familyMember, lastLocationId);

        if (clientEventPair != null && clientEventPair.first != null) {
            for (Client c : clientEventPair.first) {
                // merge and add client
                CoreJsonFormUtils.mergeAndSaveClient(syncHelper, c);
            }
        }

        if (clientEventPair != null && clientEventPair.second != null) {
            for (Event event : clientEventPair.second) {
                // add events
                syncHelper.addEvent(event.getBaseEntityId(), new JSONObject(org.smartregister.family.util.JsonFormUtils.gson.toJson(event)));
            }
        }
    }

    private void saveMemberRegistration(FamilyEventClient familyEventClient, String jsonString, boolean isEditMode) {
        try {
            Client baseClient = familyEventClient.getClient();
            Event baseEvent = familyEventClient.getEvent();
            JSONObject eventJson = null;
            JSONObject clientJson = null;
            if (baseClient != null) {
                clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                if (isEditMode) {
                    JsonFormUtils.mergeAndSaveClient(this.getSyncHelper(), baseClient);
                } else {
                    this.getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);
                }
            }

            if (baseEvent != null) {
                eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                this.getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
            }

            String newOpenSRPId;
            if (isEditMode) {
                if (baseClient != null) {
                    newOpenSRPId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                    if (newOpenSRPId != null) {
                        newOpenSRPId.replace("-", "");
                        String currentOpenSRPId = JsonFormUtils.getString(jsonString, "current_opensrp_id").replace("-", "");
                        if (!newOpenSRPId.equals(currentOpenSRPId)) {
                            this.getUniqueIdRepository().open(currentOpenSRPId);
                        }
                    }
                }
            } else if (baseClient != null) {
                newOpenSRPId = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                this.getUniqueIdRepository().close(newOpenSRPId);
            }

            if (baseClient != null || baseEvent != null) {
                newOpenSRPId = JsonFormUtils.getFieldValue(jsonString, "photo");
                JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), newOpenSRPId);
            }

            long lastSyncTimeStamp = this.getAllSharedPreferences().fetchLastUpdatedAtDate(0L);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            org.smartregister.domain.Event domainEvent = (org.smartregister.domain.Event)JsonFormUtils.gson.fromJson(eventJson.toString(), org.smartregister.domain.Event.class);
            org.smartregister.domain.Client domainClient = (org.smartregister.domain.Client)JsonFormUtils.gson.fromJson(clientJson.toString(), org.smartregister.domain.Client.class);
            this.processClient(Collections.singletonList(new EventClient(domainEvent, domainClient)));
            this.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected void processClient(List<EventClient> eventClientList) {
        try {
            this.getClientProcessorForJava().processClient(eventClientList);
        } catch (Exception var3) {
            Timber.e(var3);
        }

    }

    public UniqueIdRepository getUniqueIdRepository() {
        return FamilyLibrary.getInstance().getUniqueIdRepository();
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public ECSyncHelper getSyncHelper() {
        return FamilyLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return FamilyLibrary.getInstance().getClientProcessorForJava();
    }

}
