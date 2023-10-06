package org.smartregister.chw.repository;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.domain.CleanEDIStatus;
import org.smartregister.domain.Event;
import org.smartregister.domain.Client;
import org.smartregister.domain.Obs;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Author issyzac on 27/09/2023
 */
public class KKEventClientRepository extends EventClientRepository {

    public KKEventClientRepository(){}

    public CleanEDIStatus removeEdiIdsFromClients() throws Exception {

        String username = Context.getInstance().userService().getAllSharedPreferences().fetchRegisteredANM();

        EdiIdCleanUpRepository ediIdCleanUpRepository = new EdiIdCleanUpRepository();

        Map<String, String> clientsWithEdiIds = ediIdCleanUpRepository.clientsWithEdiIds();

        if (clientsWithEdiIds.size() == 0)
            return CleanEDIStatus.CLEANED;

        //TODO: Change to Firebase events
        Timber.e("%d duplicates for provider: %s - %s", clientsWithEdiIds.size(), username, ediIdCleanUpRepository.toString());

        for (Map.Entry<String, String> clientWithEdiId : clientsWithEdiIds.entrySet()) {

            String baseEntityId = clientWithEdiId.getKey();
            String ediId = clientWithEdiId.getValue();

            JSONObject clientJson = getClientByBaseEntityId(baseEntityId);
            JSONObject identifiers = clientJson.getJSONObject(AllConstants.IDENTIFIERS);

            String identifierLabel = identifiers.has("edi_id") ? "edi_id"
                    : identifiers.has("mother_edi_id") ? "mother_edi_id" : "";

            if (!identifierLabel.equals("")){
                identifiers.remove(identifierLabel);
                updateClientObject(clientJson, identifiers, baseEntityId, ediId);
            }

        }

        return CleanEDIStatus.CLEANED;

    }

    private void updateClientObject(JSONObject clientJson, JSONObject identifiers, String baseEntityId, String ediID){
        try {
            clientJson.put(AllConstants.IDENTIFIERS, identifiers);

            // Add events to process this
            addorUpdateClient(baseEntityId, clientJson);


            Event event = (new Event())
                    .withBaseEntityId(baseEntityId)
                    .withEventType("Register Client Edi ID")
                    .withEventDate(new DateTime())
                    .withEntityType("client")
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString());

            //Add EDI ID as observation to the registration event
            Obs edi_id_obs = new Obs();
            edi_id_obs.setFieldCode("edi_id");
            edi_id_obs.setFieldType("concept");
            edi_id_obs.setFieldDataType("text");
            edi_id_obs.setParentCode("");
            edi_id_obs.setValue(ediID);
            edi_id_obs.setFormSubmissionField("edi_id");
            edi_id_obs.setHumanReadableValue(ediID);

            event.addObs(edi_id_obs);

            addEvent(baseEntityId, convertToJson(tagSyncMetadata(DrishtiApplication.getInstance().getContext().allSharedPreferences(), event)));

            ChwApplication.getInstance().getClientProcessor().processClient(Collections.singletonList(new EventClient(event, convert(clientJson, Client.class))));

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

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

    protected static String locationId(AllSharedPreferences allSharedPreferences) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String userLocationId = allSharedPreferences.fetchUserLocalityId(providerId);
        if (StringUtils.isBlank(userLocationId)) {
            userLocationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        }

        return userLocationId;
    }

}
