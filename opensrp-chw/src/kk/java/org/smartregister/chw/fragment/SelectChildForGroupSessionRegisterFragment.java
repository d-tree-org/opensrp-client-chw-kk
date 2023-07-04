package org.smartregister.chw.fragment;

import android.widget.Toast;

import com.nerdstone.neatformcore.views.widgets.CheckBoxNFormView;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.SelectChildAdapterGS;
import org.smartregister.chw.model.SelectedChildGS;
import org.smartregister.chw.provider.SelectChildForGroupSessionFragmentProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Kassim Sheghembe on 2023-06-19
 */
public class SelectChildForGroupSessionRegisterFragment extends ChildRegisterFragment implements SelectChildForGroupSessionDialogFragment.DialogListener{

    private HashMap<String, Boolean> selectedChildren = new HashMap<>();
    private SelectChildForGroupSessionFragmentProvider childRegisterProvider;


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
                ((CheckBoxNFormView) view).setChecked(true);
                SelectedChildGS selectedChildGS = new SelectedChildGS(selectedChildBaseEntityId, SelectedChildGS.ChildStatus.SELECTED, false);
                childRegisterProvider.addChildToChildSelectedList(selectedChildBaseEntityId, selectedChildGS);
                view.setBackgroundColor(view.getContext().getColor(R.color.white));
                SelectChildForGroupSessionDialogFragment selectChildForGroupSessionDialogFragment = new SelectChildForGroupSessionDialogFragment(selectedChildBaseEntityId);
                selectChildForGroupSessionDialogFragment.show(getChildFragmentManager(), "SelectChildForGroupSessionDialogFragment");
            } else {
                childRegisterProvider.removeChildFromChildSelectedList(selectedChildBaseEntityId);
                ((CheckBoxNFormView) view).setChecked(false);
            }
        }
        //super.onViewClicked(view);
    }


    @Override
    public void onSelectComeWithPrimaryCareGiver(boolean isComeWithPrimaryCareGiver, String selectedChildBaseEntityId) {
        Toast.makeText(getActivity(), "The Child with id " + selectedChildBaseEntityId + " Come with Primary Care Giver: " + isComeWithPrimaryCareGiver, Toast.LENGTH_SHORT).show();
    }
}
