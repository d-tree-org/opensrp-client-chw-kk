package org.smartregister.chw.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.GroupSessionRegisterFragmentContract;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.interactor.GroupSessionInteractor;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.family.util.DBConstants;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import timber.log.Timber;

/**
 * Author issyzac on 20/05/2023
 */
public class GroupSessionRegisterFragmentPresenter implements GroupSessionRegisterFragmentContract.Presenter, GroupSessionRegisterFragmentContract.Interactor.InteractorCallBack {

    protected Set<View> visibleColumns = new TreeSet<>();
    private WeakReference<GroupSessionRegisterFragmentContract.View> viewReference;
    private GroupSessionRegisterFragmentContract.Model model;
    private GroupSessionRegisterFragmentContract.Interactor interactor;
    private RegisterConfiguration configuration;
    private String viewConfigurationIdentifier;

    public GroupSessionRegisterFragmentPresenter(GroupSessionRegisterFragmentContract.View view, GroupSessionRegisterFragmentContract.Model model,
        String viewConfrigurationIdentifier){
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfrigurationIdentifier;
        this.configuration = model.defaultRegisterConfiguration();
        this.interactor = new GroupSessionInteractor();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)){
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
        interactor.createSessionEvent(form, this);
    }

    //When the event has already been created
    @Override
    public void onEventCreated(Event baseEvent) {
        //todo: Dismiss View loader
        //Go elsewhere
    }

    @Override
    public void onEventFailed(String message) {
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

    protected GroupSessionRegisterFragmentContract.View getView(){
        if (viewReference != null){
            return viewReference.get();
        }
        else {
            return null;
        }
    }
}
