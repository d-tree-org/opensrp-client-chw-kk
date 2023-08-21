package org.smartregister.chw.contract;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.chw.model.SelectedChildGS;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

/**
 * Author issyzac on 20/05/2023
 */
public interface GroupSessionRegisterFragmentContract {

    public interface Presenter extends BaseRegisterFragmentContract.Presenter {

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        String getMainCondition();

        String getMainCondition(String tableName);

        String getDefaultSortQuery();

        String getDueFilterCondition();

        void fetchSessionDetails();

        void createSessionEvent(String form);

        void saveGroupSession(GroupSessionModel sessionModel);

        void refreshSessionSummaryView();
    }

    public interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);
        Presenter presenter();

        void getSessionDetails();

        void goToStepTwo();

        void goToFinalStep();

        void refreshSessionSummaryView(int numberOfSessions);
        void showProgressBar(boolean status);
        void finishGroupSession();
    }

    public interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String familyTableName, String familyMemberTableName, String mainCondition);

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

        //JSONArray getJsonArray(Response<String> response);

    }

    public interface Interactor {

        void saveSessionEvent(String json, InteractorCallBack callBack);
        void saveChildSessionEvents(List<JSONObject> jsonChildForms, InteractorCallBack callBack);
        JSONObject getAndPopulateSessionForm(String formName, Context context, GroupSessionModel sessionModel);

        void refreshSessionSummaryView(InteractorCallBack callback);

        JSONObject getAndPopulateChildSessionForm(String group_session_child, Context context, GroupSessionModel sessionModel, SelectedChildGS selectedChildGS);

        interface InteractorCallBack {
            void onEventSaved(Event baseEvent);
            void onEventsSaved(List<Event> baseEvent);
            void onEventSaveError(String message);
            void onRefreshSessionSummaryView(int numberOfSessions);

        }
    }

}
