package org.smartregister.chw.contract;

import org.smartregister.domain.Event;
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

    }

    public interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);
        Presenter presenter();

        String getSessionDetails();

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

        void createSessionEvent(String json, InteractorCallBack callBack);

        interface InteractorCallBack {

            void onEventCreated(Event baseEvent);

            void onEventFailed(String message);

        }
    }

}
