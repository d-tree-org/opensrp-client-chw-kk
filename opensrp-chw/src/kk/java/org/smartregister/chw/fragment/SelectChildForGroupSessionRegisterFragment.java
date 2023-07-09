package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.nerdstone.neatformcore.views.widgets.CheckBoxNFormView;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.GroupSessionRegisterActivity;
import org.smartregister.chw.adapter.SelectChildAdapterGS;
import org.smartregister.chw.listener.SessionModelUpdatedListener;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.chw.model.SelectedChildGS;
import org.smartregister.chw.provider.SelectChildForGroupSessionFragmentProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2023-06-19
 */
public class SelectChildForGroupSessionRegisterFragment extends ChildRegisterFragment implements SelectChildForGroupSessionDialogFragment.DialogListener{

    private SelectChildForGroupSessionFragmentProvider childRegisterProvider;
    private MaterialButton nextButton;

    private SessionModelUpdatedListener sessionModelUpdatedListener;
    private GroupSessionModel groupSessionModel;

    List<SelectedChildGS> selectedChildren = new ArrayList<>();

    public SelectChildForGroupSessionRegisterFragment(SessionModelUpdatedListener listener){
        this.sessionModelUpdatedListener = listener;
    }

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        childRegisterProvider = new SelectChildForGroupSessionFragmentProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new SelectChildAdapterGS(null, (RecyclerViewProvider) childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(10);
        clientsView.setBackgroundColor(clientsView.getContext().getColor(R.color.chw_primary));
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void onViewClicked(android.view.View view) {
        if (view.getTag() instanceof org.smartregister.commonregistry.CommonPersonObjectClient
                && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            CommonPersonObjectClient client = (org.smartregister.commonregistry.CommonPersonObjectClient) view.getTag();
            String selectedChildBaseEntityId = client.getCaseId();
            boolean isChildSelected = ((CheckBoxNFormView) view).isChecked();
            if (isChildSelected) {
                SelectedChildGS selectedChildGS = new SelectedChildGS(selectedChildBaseEntityId, SelectedChildGS.ChildStatus.SELECTED, false, ".");
                childRegisterProvider.addChildToChildSelectedList(selectedChildBaseEntityId, selectedChildGS);
                view.setBackgroundColor(view.getContext().getColor(R.color.white));
                SelectChildForGroupSessionDialogFragment selectChildForGroupSessionDialogFragment = new SelectChildForGroupSessionDialogFragment(selectedChildBaseEntityId, new DialogDismissListener() {
                    @Override
                    public void onSuccess() {
                        ((CheckBoxNFormView) view).setChecked(true);
                    }

                    @Override
                    public void onFailure() {
                        ((CheckBoxNFormView) view).setChecked(false);
                    }
                });
                selectChildForGroupSessionDialogFragment.show(getChildFragmentManager(), "SelectChildForGroupSessionDialogFragment");
            } else {
                childRegisterProvider.removeChildFromChildSelectedList(selectedChildBaseEntityId);
                ((CheckBoxNFormView) view).setChecked(false);
            }
        }
        //super.onViewClicked(view);
    }


    @Override
    public void onSelectComeWithPrimaryCareGiver(boolean isComeWithPrimaryCareGiver, String selectedChildBaseEntityId, String groupPlaced) {
        Timber.d("The Child with id %s Come with Primary Care Giver: %s", selectedChildBaseEntityId, isComeWithPrimaryCareGiver);
        childRegisterProvider.updateChildSelectionStatus(selectedChildBaseEntityId, isComeWithPrimaryCareGiver, groupPlaced);
    }

    @Override
    protected int getToolBarTitle() {
        return R.string.select_child_for_gs;
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);

        this.view = view;

        dueOnlyLayout = view.findViewById(R.id.due_only_layout);
        dueOnlyLayout.setVisibility(android.view.View.VISIBLE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);

        dueOnlyLayout.setVisibility(android.view.View.GONE);

        nextButton = view.findViewById(R.id.buttonNext);

    }

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        android.view.View view = rootView;

        nextButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                HashMap<String, SelectedChildGS> childrenList = childRegisterProvider.getSelectedChildList();
                groupSessionModel = GroupSessionRegisterActivity.getSessionModel();
                boolean atLeastOneChildSelected = false;

                for (SelectedChildGS child : childrenList.values()){
                    atLeastOneChildSelected = true;
                    selectedChildren.add(child);
                }

                if(groupSessionModel != null && atLeastOneChildSelected){
                    groupSessionModel.setSelectedChildren(selectedChildren);
                    sessionModelUpdatedListener.onSessionModelUpdated(groupSessionModel);
                    ((BaseRegisterActivity) requireActivity()).switchToFragment(2);
                }
            }
        });

        return view;

    }

    private void displayToast(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_base_select_child_for_gs;
    }

    public interface DialogDismissListener {
        public void onSuccess();
        public void onFailure();
    }

}
