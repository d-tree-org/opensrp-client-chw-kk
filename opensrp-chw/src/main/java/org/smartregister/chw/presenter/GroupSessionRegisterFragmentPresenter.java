package org.smartregister.chw.presenter;

import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.GroupSessionRegisterFragmentContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.interactor.GroupSessionInteractor;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.chw.model.SelectedChildGS;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.family.util.DBConstants;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Author issyzac on 20/05/2023
 */
public class GroupSessionRegisterFragmentPresenter implements GroupSessionRegisterFragmentContract.Presenter, GroupSessionRegisterFragmentContract.Interactor.InteractorCallBack {

    protected Set<View> visibleColumns = new TreeSet<>();
    private final WeakReference<GroupSessionRegisterFragmentContract.View> viewReference;
    private GroupSessionRegisterFragmentContract.Model model;
    private final GroupSessionRegisterFragmentContract.Interactor interactor;
    private RegisterConfiguration configuration;
    private final String viewConfigurationIdentifier;

    public GroupSessionRegisterFragmentPresenter(GroupSessionRegisterFragmentContract.View view, GroupSessionRegisterFragmentContract.Model model,
                                                 String viewConfrigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfrigurationIdentifier;
        this.configuration = model.defaultRegisterConfiguration();
        this.interactor = new GroupSessionInteractor();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            configuration = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (configuration.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }

    }

    @Override
    public void fetchSessionDetails() {
        //Fetch Session From the view
        getView().getSessionDetails();
    }

    @Override
    public void createSessionEvent(String form) {
    }

    @Override
    public void saveGroupSession(GroupSessionModel sessionModel) {

        if (getView() != null) {
            getView().showProgressBar(true);
        }
        boolean sessionTookPlace = sessionModel.isSessionTookPlace();
        String sessionEventType = sessionTookPlace ? "group_session" : "group_session_not_taken";
        JSONObject sessionForm = interactor.getAndPopulateSessionForm(sessionEventType, getView().getContext(), sessionModel);

        // Save the session event
        interactor.saveSessionEvent(sessionForm.toString(), new GroupSessionRegisterFragmentContract.Interactor.InteractorCallBack() {
            @Override
            public void onEventSaved(Event baseEvent) {
                // Session event saved successfully

                //Save the individual child group session events
                if (sessionModel.getSelectedChildren() != null && !sessionModel.getSelectedChildren().isEmpty()) {
                    List<JSONObject> childForms = new ArrayList<>();
                    for (SelectedChildGS selectedChildGS : sessionModel.getSelectedChildren()) {
                        JSONObject childForm = interactor.getAndPopulateChildSessionForm("group_session_child_form", getView().getContext(), sessionModel, selectedChildGS);
                        childForms.add(childForm);
                    }
                    interactor.saveChildSessionEvents(childForms, this);
                } else {
                    getView().finishGroupSession();
                }

            }

            @Override
            public void onEventsSaved(List<Event> baseEvent) {
                Toast.makeText(getView().getContext(), "Saved Event", Toast.LENGTH_SHORT).show();
                getView().finishGroupSession();
            }

            @Override
            public void onEventSaveError(String message) {

            }

            @Override
            public void onRefreshSessionSummaryView(int numberOfSessions) {
            }
        });

    }

    @Override
    public void refreshSessionSummaryView() {
        interactor.refreshSessionSummaryView(this);
    }

    //When the event has already been created
    @Override
    public void onEventsSaved(List<Event> baseEvent) {

        Toast.makeText(getView().getContext(), "Session Saved", Toast.LENGTH_SHORT).show();
        getView().finishGroupSession();

    }

    @Override
    public void onEventSaved(Event baseEvent) {
        if (baseEvent != null) {
            if (getView() != null) {
                getView().showProgressBar(false);
                Toast.makeText(getView().getContext(),
                        String.format(getView().getContext().getString(R.string.session_saved), baseEvent.getBaseEntityId()), Toast.LENGTH_SHORT).show();
                getView().finishGroupSession();
            }
        }
    }

    @Override
    public void onEventSaveError(String message) {
        //todo: Implement event creation failed
    }

    @Override
    public void onRefreshSessionSummaryView(int numberOfSessions) {
        if (getView() != null) {
            getView().refreshSessionSummaryView(numberOfSessions);
        }
    }

    private void setVisibleColumns(Set<View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    @Override
    public void initializeQueries(String mainCondition) {

        /**
         * todo
         * Update to select from the GC table
         */
        String countSelect = model.countSelect("ec_group_session", mainCondition);
        String mainSelect = model.mainSelect("ec_group_session", "", "ec_group_session", mainCondition);

        getView().initializeQueryParams("ec_group_session", countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        //getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        //todo: implement global search
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(org.smartregister.R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s.%s is null",
                /*CoreConstants.TABLE_NAME.CHILD*/"ec_group_session", DBConstants.KEY.DATE_REMOVED);
    }

    @Override
    public String getMainCondition(String tableName) {
        return String.format(" %s is null",
                tableName + "." + DBConstants.KEY.DATE_REMOVED);
    }

    @Override
    public String getDefaultSortQuery() {
        return "ec_group_session"/*CoreConstants.TABLE_NAME.CHILD*/ + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";// AND "+ChildDBConstants.childAgeLimitFilter();
    }

    @Override
    public String getDueFilterCondition() {
        return getMainCondition(); // + " AND " + ChildDBConstants.childDueFilter();
    }

    public String getDueCondition() {
        return ""; // " and " + CoreConstants.TABLE_NAME.CHILD + ".base_entity_id in (select base_entity_id from schedule_service where strftime('%Y-%m-%d') BETWEEN due_date and expiry_date and schedule_name = '" + CoreConstants.SCHEDULE_TYPES.CHILD_VISIT + "' and ifnull(not_done_date,'') = '' and ifnull(completion_date,'') = '' )  ";
    }

    public String getFilterString(String filters) {

        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(" and ( ");
            customFilter.append(MessageFormat.format(" {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.CHILD, org.smartregister.chw.anc.util.DBConstants.KEY.UNIQUE_ID, filters));

            customFilter.append(" ) ");
        }

        //return customFilter.toString();
        return "";
    }


    public void setModel(GroupSessionRegisterFragmentContract.Model model) {
        this.model = model;
    }

    protected GroupSessionRegisterFragmentContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }
}
