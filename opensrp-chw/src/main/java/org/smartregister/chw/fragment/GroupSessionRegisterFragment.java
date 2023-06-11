package org.smartregister.chw.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.Loader;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.R;
import org.smartregister.chw.contract.GroupSessionRegisterFragmentContract;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.core.fragment.BaseChwRegisterFragment;
import org.smartregister.chw.core.provider.CoreChildRegisterProvider;
import org.smartregister.chw.model.GroupSessionRegisterFragmentModel;
import org.smartregister.chw.presenter.GroupSessionRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.fragment.NoMatchDialogFragment;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Set;

import timber.log.Timber;

/**
 * Author issyzac on 20/05/2023
 */

public class GroupSessionRegisterFragment extends BaseChwRegisterFragment implements GroupSessionRegisterFragmentContract.View {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";
    private static final String DUE_FILTER_TAG = "PRESSED";
    protected View view;
    protected View dueOnlyLayout;
    protected boolean dueFilterActive = false;


    //New sessions implementation
    private TextInputEditText etSessionDate;
    private AppCompatSpinner spTypeOfPlace;
    private TextInputLayout etGps;
    private TextInputLayout etDuration;

    private static final String[] places = {"Session place","Hospital", "Health Center", "School"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = rootView;

        etSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSessionDate();
            }
        });

        return view;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        this.view = view;

        dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);

        dueOnlyLayout.setVisibility(View.GONE);

        // Update Search bar
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setVisibility(View.GONE);

        etSessionDate = view.findViewById(R.id.editTextSessionDate);
        spTypeOfPlace = view.findViewById(R.id.spinnerTypeOfPlace);
        etGps = view.findViewById(R.id.editTextGps);
        etDuration = view.findViewById(R.id.editTextDuration);

        setupSpinner();

    }

    private void selectSessionDate(){
        DialogFragment newFragment = new DatePickerFragment(this.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                etSessionDate.setText(i2+"/"+i1+"/"+i);
            }
        });
        Activity activity = (BaseRegisterActivity) getActivity();
        newFragment.show(activity.getFragmentManager(), "datePicker");
    }

    private void setupSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, places);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOfPlace.setAdapter(adapter);
        spTypeOfPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_group_session_register;
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.menu_group_sessions;
    }

    @Override
    public void showNotFoundPopup(String uniqueId) {
        if (getActivity() == null) {
            return;
        }
        NoMatchDialogFragment.launchDialog((BaseRegisterActivity) getActivity(), DIALOG_TAG, uniqueId);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null){
            return;
        }
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new GroupSessionRegisterFragmentPresenter(this, new GroupSessionRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {
        //// todo
    }

    @Override
    protected void onResumption() {
        if (dueFilterActive && dueOnlyLayout != null) {
            dueFilter(dueOnlyLayout);
        } else {
            super.onResumption();
        }
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        //todo: change to list the available group sessions
        CoreChildRegisterProvider childRegisterProvider = new CoreChildRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public GroupSessionRegisterFragmentContract.Presenter presenter() {
        return (GroupSessionRegisterFragmentContract.Presenter) presenter;
    }

    @Override
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    protected void startRegistration() {
        //((CoreChildRegisterActivity) getActivity()).startFormActivity(CoreConstants.JSON_FORM.getChildRegister(), null, "");
    }

    @Override
    protected void onViewClicked(android.view.View view) {
        if (getActivity() == null) {
            return;
        }

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            if (view.getTag() instanceof CommonPersonObjectClient) {
                goToDetailActivity((CommonPersonObjectClient) view.getTag(), false);
            }
        } else if (view.getId() == R.id.due_only_layout) {
            //toggleFilterSelection(view);
        }
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) && dueFilterActive && dueOnlyLayout != null) {
            dueFilter(dueOnlyLayout);
            Utils.showShortToast(getActivity(), getString(R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncInProgress(fetchStatus);
        }
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        if (!SyncStatusBroadcastReceiver.getInstance().isSyncing() && (FetchStatus.fetched.equals(fetchStatus) || FetchStatus.nothingFetched.equals(fetchStatus)) && (dueFilterActive && dueOnlyLayout != null)) {
            dueFilter(dueOnlyLayout);
            Utils.showShortToast(getActivity(), getString(R.string.sync_complete));
            refreshSyncProgressSpinner();
        } else {
            super.onSyncComplete(fetchStatus);
        }

        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        super.refreshSyncProgressSpinner();
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    public void goToDetailActivity(CommonPersonObjectClient patient,
                                        boolean launchDialog) {
        if (launchDialog) {
            Timber.i(patient.name);
        }

        /*
        Intent intent = new Intent(getActivity(), CoreChildProfileActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
         */
    }

    protected String getDueFilterCondition() {
        return presenter().getDueFilterCondition();
    }

    private void dueFilter(View dueOnlyLayout) {
        filter(searchText(), "", getDueFilterCondition());
        dueOnlyLayout.setTag(DUE_FILTER_TAG);
        switchViews(dueOnlyLayout, true);
    }

    protected void filterAndSortExecute()
    {
        filterandSortExecute(countBundle());
    }

    protected void filter(String filterString, String joinTableString, String mainConditionString) {
        filters = filterString;
        joinTable = joinTableString;
        mainCondition = mainConditionString;
        filterAndSortExecute();
    }

    private String searchText() {
        return (getSearchView() == null) ? "" : getSearchView().getText().toString();
    }

    protected TextView getDueOnlyTextView(View dueOnlyLayout) {
        return dueOnlyLayout.findViewById(R.id.due_only_text_view);
    }

    private void switchViews(View dueOnlyLayout, boolean isPress) {
        if (isPress) {
            getDueOnlyTextView(dueOnlyLayout).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_on, 0);
        } else {
            getDueOnlyTextView(dueOnlyLayout).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_due_filter_off, 0);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*if (id == LOADER_ID) {// Returns a new CursorLoader
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    String query = filterandSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }*/// An invalid id was passed in
        return null;
    }


    @Override
    public void countExecute() {
        Cursor c = null;
        try {
            c = commonRepository().rawCustomQueryForAdapter(getCountSelect());
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private String getCountSelect() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);

        String query = countSelect;
        try {
            if (StringUtils.isNotBlank(filters))
                query = sqb.addCondition(((GroupSessionRegisterFragmentPresenter) presenter()).getFilterString(filters));

            if (dueFilterActive)
                query = sqb.addCondition(((GroupSessionRegisterFragmentPresenter) presenter()).getDueCondition());
            query = sqb.Endquery(query);
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

}
