package org.smartregister.chw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.model.MultiSelectListItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kassim Sheghembe on 2023-07-19
 */
public class KKCustomAdapterMultiSelectList extends RecyclerView.Adapter<KKCustomAdapterMultiSelectList.ViewHolder> {

    private final Context context;
    private List<MultiSelectListItemModel> items;

    private int selectedPosition = -1;
    private OnItemSelectedListener listener;

    public KKCustomAdapterMultiSelectList(List<MultiSelectListItemModel> items, Context context) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.simple_multi_choice_item_kk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
/*        ConcatAdapter.Config config = new ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build();
        ConcatAdapter concatAdapter = new ConcatAdapter(config);*/
        holder.bind(items.get(position));
/*        holder.checkBox.setChecked(position == selectedPosition);
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.light_gray));
        } else {
            holder.itemView.setBackgroundColor(context.getColor(R.color.white));
        }*/

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedPosition(int position) {

        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;

        if (previousSelectedPosition != -1) {
            notifyItemChanged(previousSelectedPosition);
        }

        if (selectedPosition != -1) {
            MultiSelectListItemModel selectedItem = items.get(position);
            selectedItem.setSelected(!selectedItem.isSelected());
            notifyItemChanged(selectedPosition);
        }

    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    public List<MultiSelectListItemModel> getSelectedItems() {
        List<MultiSelectListItemModel> selectedItems = new ArrayList<>();
        for (MultiSelectListItemModel item : items) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        AppCompatCheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
            checkBox = itemView.findViewById(R.id.checkBoxItem);

        }

        public void bind(MultiSelectListItemModel item) {
            textView.setText(item.getName());
            checkBox.setChecked(item.isSelected());
            checkBox.setOnClickListener(this);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemSelected(getAbsoluteAdapterPosition());
            }
        }
    }
}
