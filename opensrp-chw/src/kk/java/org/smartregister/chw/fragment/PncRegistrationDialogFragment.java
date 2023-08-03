package org.smartregister.chw.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.chw.activity.PncRegisterActivity;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.KKCoreConstants;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Kassim Sheghembe on 2023-08-01
 */
public class PncRegistrationDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText lmp_date_pnc_registration;
    private final String baseEntityId;
    private final String familyBaseEntityId;
    private final String familyName;
    private Triple dateTriple;
    PncRegistrationDialogFragment(String baseEntityId, String familyBaseEntityId, String familyName) {
        this.baseEntityId = baseEntityId;
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;
    }

    public static PncRegistrationDialogFragment newInstance(String baseEntityId, String familyBaseEntityId, String familyName) {
        PncRegistrationDialogFragment fragment = new PncRegistrationDialogFragment(baseEntityId, familyBaseEntityId, familyName);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.kk_pnc_enrollment_dialog_fragment, null);
        lmp_date_pnc_registration = view.findViewById(R.id.lmp_date_pnc_registration);

        lmp_date_pnc_registration.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), PncRegistrationDialogFragment.this, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        builder.setView(view);

        builder.setPositiveButton(R.string.confirm_register_new_pnc, (dialog, which) -> {
            CoreConstants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(), ChwApplication.getInstance().getApplicationContext().getAssets());
            String lmpDate = null;
            if (dateTriple != null) {
                int month = (int) dateTriple.getMiddle() + 1;
                int day = (int) dateTriple.getRight();
                int year = (int) dateTriple.getLeft();
                lmpDate = String.format(Locale.getDefault(),"%d-%d-%d", day, month, year);
            }
            PncRegisterActivity.startPncRegistrationActivity(
                    (FamilyOtherMemberProfileActivity) requireActivity() ,
                    this.baseEntityId,
                    null,
                    KKCoreConstants.ANC_PREGNANCY_OUTCOME.getPregnancyOutcome(),
                    AncLibrary.getInstance().getUniqueIdRepository().getNextUniqueId().getOpenmrsId(),
                    familyBaseEntityId,
                    familyName,
                    lmpDate);
        });

        return builder.create();
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        dateTriple = Triple.of(year, month, dayOfMonth);
        String lmpDateString = String.format("%s/%s/%s", dayOfMonth, month + 1, year);
        lmp_date_pnc_registration.setText(lmpDateString);
    }

}
