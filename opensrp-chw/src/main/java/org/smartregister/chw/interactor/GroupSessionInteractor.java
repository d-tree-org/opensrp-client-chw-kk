package org.smartregister.chw.interactor;

import static org.smartregister.chw.anc.util.NCUtils.getClientProcessorForJava;
import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;
import static org.smartregister.util.Utils.getAllSharedPreferences;

import org.json.JSONObject;
import org.smartregister.chw.contract.GroupSessionRegisterFragmentContract;
import org.smartregister.chw.domain.GroupEventClient;
import org.smartregister.chw.util.GroupSessionUtils;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Author issyzac on 12/06/2023
 */
public class GroupSessionInteractor implements GroupSessionRegisterFragmentContract.Interactor {

    private ClientClassification classification;

    @Override
    public void createSessionEvent(String form, GroupSessionRegisterFragmentContract.Interactor.InteractorCallBack callBack) {
        try {
            List<GroupEventClient> eventClientList = new ArrayList();

            String sessionId = UUID.randomUUID().toString();
            String table = KkConstants.TABLES.GROUP_SESSION;

            GroupEventClient groupEventClient = GroupSessionUtils.processGroupSessionEvent(Utils.context().allSharedPreferences(), form, table, sessionId);

            Client groupClient = groupEventClient.getClient();
            Event groupEvent = groupEventClient.getEvent();

            JSONObject groupClientJson = null;
            JSONObject groupEventJson = null;

            //Save event
            if (groupEvent != null) {
                groupEventJson = new JSONObject(JsonFormUtils.gson.toJson(groupEvent));
                getSyncHelper().addEvent(groupEvent.getBaseEntityId(), groupEventJson);
            }

            if (groupClient != null){
                groupClientJson = new JSONObject(JsonFormUtils.gson.toJson(groupClient));
                getSyncHelper().addClient(groupClient.getBaseEntityId(), groupClientJson);
            }

            Event event = (Event)JsonFormUtils.gson.fromJson(groupEventJson.toString(), Event.class);
            Client client = (Client)JsonFormUtils.gson.fromJson(groupClientJson.toString(), Client.class);
            eventClientList.add(new GroupEventClient(client, event));

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0L);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            processGroupClient(eventClientList);
            //TODO: Handle processing
            //getClientProcessorForJava().processEvent(event, client, );
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

            callBack.onEventCreated(event);

        }catch (Exception e){
            Timber.e(e);
            callBack.onEventFailed("Failed creating the event");
        }
    }

    private void processGroupClient(List<GroupEventClient> eventClients){
        try {
            List<EventClient> eventClientList = new ArrayList<>();
            EventClient ec = new EventClient(eventClients.get(0).getEvent(), eventClients.get(0).getClient());
            eventClientList.add(ec);
            getClientProcessorForJava().processClient(eventClientList);

        }catch (Exception e){
            Timber.e(e);
        }
    }
    private ClientClassification getClassification() {
        if (classification == null) {
            //classification = getClientProcessorForJava().assetJsonToJava("ec_client_classification.json", ClientClassification.class);
        }
        return classification;
    }


}
