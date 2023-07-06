package org.smartregister.chw.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;

/**
 * Created by Kassim Sheghembe on 2023-07-06
 */
public class KKCustomAdapter extends RecyclerView.Adapter<KKCustomAdapter.ViewHolder>{
    private String[] items;
    private int selectedPosition = -1;
    private OnItemSelectedListener listener;
    private Context context;

    public KKCustomAdapter(String[] items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void setSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;
        selectedPosition = position;

        if (previousSelectedPosition != -1) {
            notifyItemChanged(previousSelectedPosition);
        }

        if (selectedPosition != -1) {
            notifyItemChanged(selectedPosition);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.simple_single_choice_item_kk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(items[position]);
        holder.radioButton.setChecked(position == selectedPosition);
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(context.getColor(R.color.primary_light));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        AppCompatRadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
            radioButton = itemView.findViewById(R.id.radioItem);

            textView.setOnClickListener(this);
            radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemSelected(getAbsoluteAdapterPosition());
            }
        }
    }
}
