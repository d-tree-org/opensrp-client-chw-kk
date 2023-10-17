package org.smartregister.chw.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.adapter.KKCustomAdapter;
import org.smartregister.chw.adapter.KKCustomAdapterMultiSelectList;
import org.smartregister.chw.model.MultiSelectListItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kassim Sheghembe on 2023-06-27
 */
public class SelectChildForGroupSessionDialogFragment extends DialogFragment {

    private RecyclerView who_came_with_the_child_lv;
    private RecyclerView came_with_pc_lv;
    private RecyclerView lv_caregiver_representative;

    private TextView who_came_with_child_tv;
    private TextView  tv_caregiver_representative;

    private KKCustomAdapter came_with_pc_lv_adapter;
    private KKCustomAdapter selected_group_lv_adapter;
    private KKCustomAdapterMultiSelectList cg_rep_lv_adapter;
    private KKCustomAdapterMultiSelectList who_came_with_the_child_lv_adapter;

    private int selectedPosition1 = -1;
    private int selectedPosition2 = -1;
    private int selectedPosition3 = -1;

    private DialogListener dialogListener;
    private final String selectedChildBaseEntityId;
    private final String primaryCareGiverName;

    private final boolean childrenDividedIntoGroups;

    private EditText cg_rep_lv_other;

    SelectChildForGroupSessionRegisterFragment.DialogDismissListener dialogDismissListener;

    String[] groupItems;

    public SelectChildForGroupSessionDialogFragment(String selectedChildBaseEntityId, String primaryCareGiverName, boolean childrenDividedIntoGroups, SelectChildForGroupSessionRegisterFragment.DialogDismissListener listener) {
        this.selectedChildBaseEntityId = selectedChildBaseEntityId;
        this.primaryCareGiverName = primaryCareGiverName;
        this.dialogDismissListener = listener;
        this.childrenDividedIntoGroups = childrenDividedIntoGroups;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_childselection, null);

        TextView came_with_pc_tv = view.findViewById(R.id.came_with_pc);
        came_with_pc_tv.setText(String.format(getString(R.string.primary_care_give_dialog_gs), primaryCareGiverName));
        builder.setView(view);

        came_with_pc_lv = view.findViewById(R.id.came_with_pc_lv);
        who_came_with_the_child_lv = view.findViewById(R.id.who_came_with_the_child_lv);

        lv_caregiver_representative = view.findViewById(R.id.cg_rep_lv);
        lv_caregiver_representative.setVisibility(View.GONE);
        lv_caregiver_representative.setScrollContainer(false);
        tv_caregiver_representative = view.findViewById(R.id.cg_rep_tv);
        tv_caregiver_representative.setVisibility(View.GONE);

        TextView selected_group = view.findViewById(R.id.selected_group);
        RecyclerView selected_group_lv = view.findViewById(R.id.selected_group_lv);
        selected_group_lv.setScrollContainer(false);

        cg_rep_lv_other = view.findViewById(R.id.cg_rep_lv_other);
        cg_rep_lv_other.setVisibility(View.GONE);

        if (childrenDividedIntoGroups) {
            selected_group_lv.setVisibility(View.VISIBLE);
            selected_group.setVisibility(View.VISIBLE);
        } else {
            selected_group_lv.setVisibility(View.GONE);
            selected_group.setVisibility(View.GONE);
        }

        who_came_with_child_tv = view.findViewById(R.id.who_came_with_child_tv);

        came_with_pc_lv_adapter = new KKCustomAdapter(getResources().getStringArray(R.array.select_child_option), requireContext());
        ArrayList<MultiSelectListItemModel> multiSelectListItems = new ArrayList<>();
         who_came_with_the_child_lv_adapter = new KKCustomAdapterMultiSelectList(setMultiSelectListItems(multiSelectListItems), requireContext());

        groupItems = getResources().getStringArray(R.array.group_session_groups);
        selected_group_lv_adapter = new KKCustomAdapter(groupItems, requireContext());
        came_with_pc_lv.setAdapter(came_with_pc_lv_adapter);
        who_came_with_the_child_lv.setAdapter(who_came_with_the_child_lv_adapter);
        selected_group_lv.setAdapter(selected_group_lv_adapter);

        ArrayList<MultiSelectListItemModel> multiSelectListItems1 = new ArrayList<>();
        cg_rep_lv_adapter = new KKCustomAdapterMultiSelectList(setMultiSelectListItems(multiSelectListItems1), requireContext());
        lv_caregiver_representative.setAdapter(cg_rep_lv_adapter);
        lv_caregiver_representative.setLayoutManager(new LinearLayoutManager(requireContext()));

        came_with_pc_lv.setLayoutManager(new LinearLayoutManager(requireContext()));
        who_came_with_the_child_lv.setLayoutManager(new LinearLayoutManager(requireContext()));
        selected_group_lv.setLayoutManager(new LinearLayoutManager(requireContext()));

        came_with_pc_lv_adapter.setOnItemSelectedListener(position -> {
            selectedPosition1 = position;
            modifyChildCameWith(position == 0);
            came_with_pc_lv_adapter.setSelectedPosition(position);
        });

        who_came_with_the_child_lv_adapter.setOnItemSelectedListener(position -> {
            selectedPosition3 = position;
            who_came_with_the_child_lv_adapter.setSelectedPosition(position);
        });

        cg_rep_lv_adapter.setOnItemSelectedListener(position -> {
            cg_rep_lv_adapter.setSelectedPosition(position);
            checkForOtherSelectedItem();
        });

        selected_group_lv_adapter.setOnItemSelectedListener(position -> {
            selectedPosition2 = position;
            selected_group_lv_adapter.setSelectedPosition(position);
        });

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            if (selectedPosition1 == -1) {
                Toast.makeText(requireContext(), "Caregiver information missing", Toast.LENGTH_SHORT).show();
                dialogDismissListener.onFailure();
                dialog.dismiss();
            }else if (selectedPosition2 == -1 && childrenDividedIntoGroups) {
                Toast.makeText(requireContext(), "Child missing the group", Toast.LENGTH_SHORT).show();
                dialogDismissListener.onFailure();
                dialog.dismiss();
            }else {
                handleSuccess(dialog);
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialogDismissListener.onFailure();
            dialog.dismiss();
        });

        return builder.create();
    }

    void handleSuccess(DialogInterface dialogInterface){
        if(selectedPosition1 == 0){
            if(who_came_with_the_child_lv_adapter.getSelectedItems().size() > 0){
                dialogListener.onSelectComeWithPrimaryCareGiver(
                        true,
                        selectedChildBaseEntityId,
                        who_came_with_the_child_lv_adapter.getSelectedItems(),
                        !childrenDividedIntoGroups ? "Not in groups" : groupItems[selectedPosition2]
                );
                dialogValidationSuccess(dialogInterface);
            }else{
                dialogValidationFail(dialogInterface,"The companions of caregiver missing");
            }
        }else if(selectedPosition1 == 1){
            if(cg_rep_lv_adapter.getSelectedItems().size() > 0){
                dialogListener.onSelectComeWithoutPrimaryCareGiver(
                        false,
                        selectedChildBaseEntityId,
                        cg_rep_lv_adapter.getSelectedItems(),
                        who_came_with_the_child_lv_adapter.getSelectedItems(),
                        !childrenDividedIntoGroups ? "Not in groups" : groupItems[selectedPosition2]);
            }else{
                dialogValidationFail(dialogInterface,"Caregiver representatives missing");
            }
        }
    }

    void dialogValidationFail(DialogInterface dialogInterface,String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        dialogDismissListener.onFailure();
        dialogInterface.dismiss();
    }

    void dialogValidationSuccess(DialogInterface dialogInterface){
        dialogDismissListener.onSuccess();
        dialogInterface.dismiss();
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

    private void modifyChildCameWith(boolean hide) {
        if (hide) {
            //Child came with the care giver
            tv_caregiver_representative.setVisibility(View.GONE);
            lv_caregiver_representative.setVisibility(View.GONE);
            who_came_with_child_tv.setText(R.string.who_came_with_the_child_yes);
            cg_rep_lv_other.setVisibility(View.GONE);
        } else {
            //Child did not come with the caregiver
            tv_caregiver_representative.setVisibility(View.VISIBLE);
            lv_caregiver_representative.setVisibility(View.VISIBLE);
            who_came_with_child_tv.setText(R.string.who_came_with_the_child_no);
            checkForOtherSelectedItem();
        }
    }

    public interface DialogListener {
        void onSelectComeWithPrimaryCareGiver(
                boolean isComeWithPrimaryCareGiver,
                String selectedChildBaseEntityId, List<MultiSelectListItemModel> selectedAccompanyingCaregivers,
                String selectedGroup);

        void onSelectComeWithoutPrimaryCareGiver(
                boolean isComeWithPrimaryCareGiver,
                String selectedChildBaseEntityId, List<MultiSelectListItemModel> selectedCaregiverRepresentatives,
                List<MultiSelectListItemModel> selectedAccompanyingCaregiverRepresentatives,
                String selectedGroup);
    }

    private ArrayList<MultiSelectListItemModel> setMultiSelectListItems(ArrayList<MultiSelectListItemModel> multiSelectListItems){
        for(String item : getResources().getStringArray(R.array.multi_select_accompany_child_option)){
            multiSelectListItems.add(new MultiSelectListItemModel(item, false));
        }
        return multiSelectListItems;
    }

    public boolean isOtherItemSelected() {
        for (MultiSelectListItemModel item : cg_rep_lv_adapter.getSelectedItems()) {
            if (item.getName().equals(getString(R.string.cg_rep_lv_other)) && item.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void checkForOtherSelectedItem(){
       if(isOtherItemSelected()){
           cg_rep_lv_other.setVisibility(View.VISIBLE);
       }else{
           cg_rep_lv_other.setVisibility(View.GONE);
           cg_rep_lv_other.getText().clear();
       }
    }

}
