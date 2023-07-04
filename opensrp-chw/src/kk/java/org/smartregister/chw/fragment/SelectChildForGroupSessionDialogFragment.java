package org.smartregister.chw.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.smartregister.chw.R;

/**
 * Created by Kassim Sheghembe on 2023-06-27
 */
public class SelectChildForGroupSessionDialogFragment extends DialogFragment {

    private DialogListener dialogListener;
    private String selectedChildBaseEntityId;

    public SelectChildForGroupSessionDialogFragment(String selectedChildBaseEntityId) {
        this.selectedChildBaseEntityId = selectedChildBaseEntityId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.primary_care_give_dialog_gs);
        builder.setSingleChoiceItems(R.array.select_child_option, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogListener != null) {
                    dialogListener.onSelectComeWithPrimaryCareGiver(which == 0, selectedChildBaseEntityId);
                }
                dialog.dismiss();
            }
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

    public interface DialogListener {
        void onSelectComeWithPrimaryCareGiver(boolean isComeWithPrimaryCareGiver, String selectedChildBaseEntityId);
    }

}
