package org.smartregister.chw.adapter;

import android.database.Cursor;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.holders.SelectChildGSViewHolder;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.RecyclerViewProvider;

/**
 * Created by Kassim Sheghembe on 2023-06-28
 */
public class SelectChildAdapterGS extends RecyclerViewPaginatedAdapter<SelectChildGSViewHolder> {
    public SelectChildAdapterGS(Cursor cursor, RecyclerViewProvider<RecyclerView.ViewHolder> listItemProvider, CommonRepository commonRepository) {
        super(cursor, listItemProvider, commonRepository);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        super.onBindViewHolder(viewHolder, cursor);
    }
}
