package org.smartregister.chw.interactor;

import static org.smartregister.chw.anc.util.NCUtils.getClientProcessorForJava;
import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;
import static org.smartregister.util.Utils.getAllSharedPreferences;

import org.json.JSONObject;
import org.smartregister.chw.contract.GroupSessionRegisterFragmentContract;
import org.smartregister.chw.domain.GroupEventClient;
import org.smartregister.chw.util.GroupSessionUtils;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AppExecutors;
import org.smartregister.util.FormUtils;
import org.smartregister.view.LocationPickerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Author issyzac on 12/06/2023
 */
public class GroupSessionInteractor implements GroupSessionRegisterFragmentContract.Interactor {

    AppExecutors appExecutors;
    private static final String GROUP_SESSION_FORM_NAME = "group_session";

    public GroupSessionInteractor() {
        this(new AppExecutors());
    }

    public GroupSessionInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void saveSessionEvents(String form, GroupSessionRegisterFragmentContract.Interactor.InteractorCallBack callBack) {
        try {
            List<GroupEventClient> eventClientList = new ArrayList();

            JSONObject jsonForm = new JSONObject(form);
            String sessionId = jsonForm.getString("entity_id");
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

            /**
             * TODO: Answer if we should process the event here or wait until all the contents have been
             * added to the event
             */
            Event event = JsonFormUtils.gson.fromJson(groupEventJson.toString(), Event.class);
            Client client = JsonFormUtils.gson.fromJson(groupClientJson.toString(), Client.class);
            eventClientList.add(new GroupEventClient(client, event));

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0L);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            processGroupClient(groupEventJson.toString(), groupClientJson.toString());
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());

            callBack.onEventCreated(event);

        }catch (Exception e){
            Timber.e(e);
            callBack.onEventFailed("Failed creating the event");
        }
    }

    private void processGroupClient(String eventJson, String clientJson){
        try {

            List<EventClient> eventClientList = new ArrayList<>();
            org.smartregister.domain.Event domainEvent = JsonFormUtils.gson.fromJson(eventJson, org.smartregister.domain.Event.class);
            org.smartregister.domain.Client domainClient = JsonFormUtils.gson.fromJson(clientJson, org.smartregister.domain.Client.class);
            EventClient ec = new EventClient(domainEvent, domainClient);
            eventClientList.add(ec);
            getClientProcessorForJava().processClient(eventClientList);

        }catch (Exception e){
            Timber.e(e);
        }
    }

}
