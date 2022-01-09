package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.Constants;

import timber.log.Timber;

public class AddMemberFragment extends org.smartregister.chw.core.fragment.AddMemberFragment {

    private Context context;

    public static AddMemberFragment newInstance() {
        return new AddMemberFragment();
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_member, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.close).setOnClickListener(this);
        view.findViewById(R.id.layout_add_anc).setOnClickListener(this);
        view.findViewById(R.id.layout_add_caregiver).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            int i = v.getId();
            if (i == R.id.close){
                dismiss();
            }
            else if (i == R.id.layout_add_anc) {
                //TODO: Fire up anc reg form
                ((CoreFamilyProfileActivity) context).startFormActivity(Constants.JSON_FORM_CONSTANT.getAncMotherRegistration(), null, "");
                dismiss();
            } else if (i == R.id.layout_add_caregiver) {
                //TODO: Fire up care giver registration form
                ((CoreFamilyProfileActivity) context).startFormActivity(CoreConstants.JSON_FORM.getFamilyMemberRegister(), null, "");
                dismiss();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
