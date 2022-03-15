package org.smartregister.chw.interactor;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.contract.BaseAncRegisterContract;
import org.smartregister.chw.anc.interactor.BaseAncRegisterInteractor;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class FamilyRegisterInteractor extends org.smartregister.family.interactor.FamilyRegisterInteractor implements BaseAncRegisterContract.InteractorCallBack {

    public FamilyRegisterInteractor(){
        super();
    }

    @Override
    public void saveRegistration(List<FamilyEventClient> familyEventClientList, String jsonString, boolean isEditMode, FamilyRegisterContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {

                try {

                    JSONObject form = new JSONObject(jsonString);
                    JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
                    JSONObject consentField = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, "fam_consent");
                    assert consentField != null;
                    JSONArray consent = consentField.getJSONArray("value");

                    //Only store client information when the conset has been given
                    if (consent.get(0).equals("fam_consent_yes")){
                        final boolean isSaved = FamilyRegisterInteractor.this.saveRegistration(familyEventClientList, jsonString, isEditMode);

                        FamilyRegisterInteractor.this.appExecutors.mainThread().execute(new Runnable() {
                            public void run() {
                                callBack.onRegistrationSaved(isEditMode, false, familyEventClientList);
                            }
                        });
                    }else {
                        FamilyRegisterInteractor.this.appExecutors.mainThread().execute(new Runnable() {
                            public void run() {
                                callBack.onRegistrationSaved(isEditMode, false, familyEventClientList);
                            }
                        });
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        this.appExecutors.diskIO().execute(runnable);
    }

    private void saveANCClient(String jsonString, boolean isEditMode, String baseEntityId, String relationalId, boolean isSaved, List<FamilyEventClient> familyEventClientList, FamilyRegisterContract.InteractorCallBack callBack){
        try {
            JSONObject form = new JSONObject(jsonString);
            String encounterType = form.optString(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE);

            JSONArray fields = org.smartregister.util.JsonFormUtils.fields(form);
            JSONObject relational = org.smartregister.util.JsonFormUtils.getFieldJSONObject(fields, DBConstants.KEY.RELATIONAL_ID);
            assert relational != null;
            relational.put("value", relationalId);
            form.put(DBConstants.KEY.RELATIONAL_ID, relational);
            form.put(Constants.JSON_FORM_EXTRA.ENTITY_TYPE, baseEntityId);
            form.put(Constants.JSON_FORM_EXTRA.ENCOUNTER_TYPE, Constants.EVENT_TYPE.ANC_REGISTRATION);
            form.put("relational_id", relationalId);
            new BaseAncRegisterInteractor().saveRegistration(form.toString(), false, new BaseAncRegisterContract.InteractorCallBack() {
                @Override
                public void onRegistrationSaved(String encounterType, boolean isEdit, boolean hasChildren) {
                    Timber.d("Registration Saved!");
                    FamilyRegisterInteractor.this.appExecutors.mainThread().execute(new Runnable() {
                        public void run() {
                            callBack.onRegistrationSaved(isEditMode, isSaved, familyEventClientList);
                        }
                    });
                }
            }, null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean saveRegistration(List<FamilyEventClient> familyEventClientList, String jsonString, boolean isEditMode) {
        try {
            List<EventClient> eventClientList = new ArrayList();

            for(int i = 0; i < familyEventClientList.size(); ++i) {
                FamilyEventClient familyEventClient = (FamilyEventClient)familyEventClientList.get(i);
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

                String imageLocation;
                String familyMemberStep;
                if (isEditMode) {
                    if (baseClient != null) {
                        imageLocation = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey).replace("-", "");
                        familyMemberStep = JsonFormUtils.getString(jsonString, "current_opensrp_id").replace("-", "");
                        if (!imageLocation.equals(familyMemberStep)) {
                            this.getUniqueIdRepository().open(familyMemberStep);
                        }
                    }
                } else if (baseClient != null) {
                    imageLocation = baseClient.getIdentifier(Utils.metadata().uniqueIdentifierKey);
                    if (StringUtils.isNotBlank(imageLocation) && !imageLocation.contains("_family")) {
                        this.getUniqueIdRepository().close(imageLocation);
                    }
                }

                if (baseClient != null || baseEvent != null) {
                    imageLocation = null;
                    if (i == 0) {
                        familyMemberStep = Utils.getCustomConfigs("family_form_image_step");
                        imageLocation = StringUtils.isBlank(familyMemberStep) ? JsonFormUtils.getFieldValue(jsonString, "photo") : JsonFormUtils.getFieldValue(jsonString, familyMemberStep, "photo");
                    } else if (i == 1) {
                        familyMemberStep = Utils.getCustomConfigs("family_member_form_image_step");
                        imageLocation = StringUtils.isBlank(familyMemberStep) ? JsonFormUtils.getFieldValue(jsonString, "step2", "photo") : JsonFormUtils.getFieldValue(jsonString, familyMemberStep, "photo");
                    }

                    if (StringUtils.isNotBlank(imageLocation)) {
                        JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
                    }
                }

                org.smartregister.domain.Event domainEvent = (org.smartregister.domain.Event)JsonFormUtils.gson.fromJson(eventJson.toString(), org.smartregister.domain.Event.class);
                org.smartregister.domain.Client domainClient = (org.smartregister.domain.Client)JsonFormUtils.gson.fromJson(clientJson.toString(), org.smartregister.domain.Client.class);
                eventClientList.add(new EventClient(domainEvent, domainClient));
            }

            long lastSyncTimeStamp = this.getAllSharedPreferences().fetchLastUpdatedAtDate(0L);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            this.processClient(eventClientList);
            this.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
            return true;
        } catch (Exception var13) {
            Timber.e(var13);
            return false;
        }
    }

    @Override
    public void onRegistrationSaved(String encounterType, boolean isEdit, boolean hasChildren) {
        //ANC Registration Completed
    }
}
