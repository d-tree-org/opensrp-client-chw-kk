package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.PncHomeVisitActivity;
import org.smartregister.chw.activity.PncMemberProfileActivity;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.chw.core.provider.ChwPncRegisterProvider;
import org.smartregister.chw.model.ChwPncRegisterFragmentModel;
import org.smartregister.chw.presenter.PncRegisterFragmentPresenter;
import org.smartregister.chw.provider.PncRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

public class PncRegisterFragment extends CorePncRegisterFragment {

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        PncRegisterProvider provider = new PncRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        PncHomeVisitActivity.startMe(getActivity(), new MemberObject(client), false);
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        MemberObject memberObject = new MemberObject(client);
        PncMemberProfileActivity.startMe(getActivity(), memberObject.getBaseEntityId());
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PncRegisterFragmentPresenter(this, new ChwPncRegisterFragmentModel(), null);
    }

}
