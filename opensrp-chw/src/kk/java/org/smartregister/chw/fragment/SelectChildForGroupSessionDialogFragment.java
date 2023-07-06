package org.smartregister.chw.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.KKCustomAdapter;

/**
 * Created by Kassim Sheghembe on 2023-06-27
 */
public class SelectChildForGroupSessionDialogFragment extends DialogFragment {

    View separator_2;
    private RecyclerView came_with_pc_lv;
    private RecyclerView who_came_with_the_child_lv;
    private RecyclerView selected_group_lv;

    private TextView who_came_with_child_tv;
    private LinearLayout who_came_with_child_ll;
    private KKCustomAdapter came_with_pc_lv_adapter;
    private KKCustomAdapter who_came_with_the_child_lv_adapter;
    private KKCustomAdapter selected_group_lv_adapter;

    private final String[] items2 = {"Group 1", "Group 2"};
    private final String[] items3 = {"Father of the child/Partner of the mother", "Sibling of the child (less than 5 years old)",
            "Sibling of the child (5 or more years old)", "Grandmother of the child ", "Grandfather of the child",
            "Blood-sister of the mother", "Blood-brother of the mother", "Friend", "Other"};

    private int selectedPosition1 = -1;
    private int selectedPosition2 = -1;
    private int selectedPosition3 = -1;

    private DialogListener dialogListener;
    private String selectedChildBaseEntityId;

    public SelectChildForGroupSessionDialogFragment(String selectedChildBaseEntityId) {
        this.selectedChildBaseEntityId = selectedChildBaseEntityId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.title_dialog_gs);
       /* builder.setSingleChoiceItems(R.array.select_child_option, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogListener != null) {
                    dialogListener.onSelectComeWithPrimaryCareGiver(which == 0, selectedChildBaseEntityId);
                }
                dialog.dismiss();
            }
        });*/
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_childselection, null);
        builder.setView(view);

        separator_2 = view.findViewById(R.id.separator_2);

        came_with_pc_lv = view.findViewById(R.id.came_with_pc_lv);
        who_came_with_the_child_lv = view.findViewById(R.id.who_came_with_the_child_lv);
        selected_group_lv = view.findViewById(R.id.selected_group_lv);
        selected_group_lv.setScrollContainer(false);
        who_came_with_child_ll = view.findViewById(R.id.who_came_with_child_ll);

        who_came_with_child_tv = view.findViewById(R.id.who_came_with_child_tv);

        came_with_pc_lv_adapter = new KKCustomAdapter(getResources().getStringArray(R.array.select_child_option), requireContext());
        who_came_with_the_child_lv_adapter = new KKCustomAdapter(items3, requireContext());
        selected_group_lv_adapter = new KKCustomAdapter(items2, requireContext());

        came_with_pc_lv.setAdapter(came_with_pc_lv_adapter);
        who_came_with_the_child_lv.setAdapter(who_came_with_the_child_lv_adapter);
        selected_group_lv.setAdapter(selected_group_lv_adapter);

        came_with_pc_lv.setLayoutManager(new LinearLayoutManager(requireContext()));
        who_came_with_the_child_lv.setLayoutManager(new LinearLayoutManager(requireContext()));
        selected_group_lv.setLayoutManager(new LinearLayoutManager(requireContext()));

        came_with_pc_lv_adapter.setOnItemSelectedListener(new KKCustomAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                selectedPosition1 = position;
                hideChildCameWith(position == 0);
                came_with_pc_lv_adapter.setSelectedPosition(position);
            }
        });

        selected_group_lv_adapter.setOnItemSelectedListener(position -> {
            selectedPosition2 = position;
            selected_group_lv_adapter.setSelectedPosition(position);
        });

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            if (dialogListener != null) {
                dialogListener.onSelectComeWithPrimaryCareGiver(selectedPosition1 == 0, selectedChildBaseEntityId, items2[selectedPosition2]);
            }
            dialog.dismiss();
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DialogListener");
        }
    }

    private void hideChildCameWith(boolean hide) {
        if (hide) {
            who_came_with_the_child_lv.setVisibility(View.GONE);
            who_came_with_child_tv.setVisibility(View.GONE);
            who_came_with_child_ll.setVisibility(View.GONE);
            separator_2.setVisibility(View.GONE);
        } else {
            who_came_with_the_child_lv.setVisibility(View.VISIBLE);
            who_came_with_child_tv.setVisibility(View.VISIBLE);
            who_came_with_child_ll.setVisibility(View.VISIBLE);
            separator_2.setVisibility(View.VISIBLE);
        }
    }

    public interface DialogListener {
        void onSelectComeWithPrimaryCareGiver(boolean isComeWithPrimaryCareGiver, String selectedChildBaseEntityId, String selectedGroup);
    }

}
