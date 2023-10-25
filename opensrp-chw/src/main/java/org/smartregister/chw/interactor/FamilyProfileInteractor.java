package org.smartregister.chw.interactor;

import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.core.interactor.CoreFamilyProfileInteractor;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Collections;
import java.util.Date;

import timber.log.Timber;

public class FamilyProfileInteractor extends CoreFamilyProfileInteractor {

    @VisibleForTesting
    FamilyProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyProfileInteractor() {
        super();
    }

    @Override
    public void saveRegistration(FamilyEventClient familyEventClient, String jsonString, boolean isEditMode, FamilyProfileContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            public void run() {
                final boolean isSaved = FamilyProfileInteractor.this.saveRegistration(familyEventClient, jsonString, isEditMode);
                FamilyProfileInteractor.this.appExecutors.mainThread().execute(new Runnable() {
                    public void run() {
                        callBack.onRegistrationSaved(isEditMode, isSaved, familyEventClient);
                    }
                });
            }
        };
        this.appExecutors.diskIO().execute(runnable);
    }

    private boolean saveRegistration(FamilyEventClient familyEventClient, String jsonString, boolean isEditMode) {
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

                //Note: EDI ID registration event is only created during first registration
                //of the client. During editing we will not create another registration event

                //EDI ID registration fields
                JSONObject form = new JSONObject(jsonString);
                JSONArray fields = JsonFormUtils.fields(form);

                String baseEntityId = baseClient.getBaseEntityId();
                String ediId = JsonFormUtils.getFieldValue(fields, "edi_id");

                //Process EDI ID registration event
                org.smartregister.chw.util.JsonFormUtils.processEdiRegistrationEvent(baseEntityId, ediId, clientJson,
                        DrishtiApplication.getInstance().getContext().allSharedPreferences(), isEditMode);

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
            return true;
        } catch (Exception var13) {
            Timber.e(var13);
            return false;
        }
    }
}
