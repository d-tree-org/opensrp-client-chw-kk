package org.smartregister.chw.fragment;

import androidx.recyclerview.widget.DividerItemDecoration;

import org.smartregister.chw.R;
import org.smartregister.chw.provider.ChildRegisterProvider;
import org.smartregister.chw.provider.SelectChildForGroupSessionFragmentProvider;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Set;

/**
 * Created by Kassim Sheghembe on 2023-06-19
 */
public class SelectChildForGroupSessionRegisterFragment extends ChildRegisterFragment{


    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        SelectChildForGroupSessionFragmentProvider childRegisterProvider = new SelectChildForGroupSessionFragmentProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(10);
        clientsView.setBackgroundColor(clientsView.getContext().getColor(R.color.chw_primary));
        clientsView.setAdapter(clientAdapter);
    }

}
