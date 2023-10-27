package org.smartregister.chw.repository;

import static org.smartregister.chw.util.JsonFormUtils.processEdiRegistrationEvent;

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

            // Update client Object
            addorUpdateClient(baseEntityId, clientJson);

            //Process new EDI ID registration event
            processEdiRegistrationEvent(baseEntityId, ediID, clientJson, DrishtiApplication.getInstance().getContext().allSharedPreferences(), false);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}
