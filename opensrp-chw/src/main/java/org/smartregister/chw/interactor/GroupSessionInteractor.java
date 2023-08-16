package org.smartregister.chw.interactor;

import static org.smartregister.chw.anc.util.NCUtils.getClientProcessorForJava;
import static org.smartregister.chw.anc.util.NCUtils.getSyncHelper;
import static org.smartregister.util.Utils.getAllSharedPreferences;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.contract.GroupSessionRegisterFragmentContract;
import org.smartregister.chw.dao.GroupCurriculumDao;
import org.smartregister.chw.domain.GroupEventClient;
import org.smartregister.chw.model.GroupSessionModel;
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

        Runnable runnable = () -> {
            Event event =  saveSessionEvents(form);
            appExecutors.mainThread().execute(() -> callBack.onEventSaved(event));
        };

        appExecutors.diskIO().execute(runnable);

    }

    private Event saveSessionEvents(String form) {
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

            return event;

        }catch (Exception e){
            Timber.e(e);
            return null;
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

    @Override
    public JSONObject getAndPopulateSessionForm(String formName, Context context, GroupSessionModel sessionModel) {

        try {

            JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();

            if (form != null) {
                form.put(org.smartregister.chw.util.JsonFormUtils.ENTITY_ID, sessionModel.getSessionId());

                JSONObject metadata = form.getJSONObject(org.smartregister.chw.util.JsonFormUtils.METADATA);
                String locationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(org.smartregister.chw.util.JsonFormUtils.ENCOUNTER_LOCATION, locationId);

                JSONObject stepOne = form.getJSONObject(org.smartregister.chw.util.JsonFormUtils.STEP1);

                JSONArray fields = stepOne.getJSONArray(org.smartregister.chw.util.JsonFormUtils.FIELDS);

                for(int i = 0; i < fields.length(); i++) {
                    JSONObject field = fields.getJSONObject(i);
                    if (formName.equalsIgnoreCase(GROUP_SESSION_FORM_NAME)) {
                        processPopulatableFields(field, sessionModel);
                    } else {
                        processGroupSessionNotDoneFields(field, sessionModel);
                    }
                }

            }

            return form;

        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    private void processGroupSessionNotDoneFields(JSONObject field, GroupSessionModel sessionModel) throws JSONException {
        switch (field.getString(org.smartregister.chw.util.JsonFormUtils.KEY).toLowerCase()) {
            case KkConstants.GCJsonKeys.GC_SESSION_ID:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getSessionId());
                break;
            case KkConstants.GCJsonKeys.GC_SESSION_NOT_DONE_REASON:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getNoSessionReason());
                break;
            case KkConstants.GCJsonKeys.GC_SESSION_NOT_DONE_OTHER_REASON:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getNoSessionOtherReason());
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshSessionSummaryView(InteractorCallBack callback) {

        Runnable runnable = () -> {
            try {
                int numberOfSessions = GroupCurriculumDao.getNumberGroupCurriculumSessions();
                appExecutors.mainThread().execute(() -> callback.onRefreshSessionSummaryView(numberOfSessions));
            }catch (Exception e){
                Timber.e(e);
            }
        };

        appExecutors.diskIO().execute(runnable);

    }

    private void processPopulatableFields(JSONObject field, GroupSessionModel sessionModel) throws JSONException {

        switch (field.getString(org.smartregister.chw.util.JsonFormUtils.KEY).toLowerCase()) {
            case KkConstants.GCJsonKeys.GC_SESSION_DATE:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getSessionDate());
                break;
            case KkConstants.GCJsonKeys.GC_SESSION_ID:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getSessionId());
                break;
            case KkConstants.GCJsonKeys.GC_SESSION_PLACE:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getSessionPlace());
                break;
            case KkConstants.GCJsonKeys.GC_SESSION_DURATION:
                field.put(org.smartregister.chw.util.JsonFormUtils.VALUE, sessionModel.getDurationInHours());
                break;
            default:
                break;
        }

    }
}
