package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.utils.CoreConstants;

import timber.log.Timber;

public class AddMemberFragment extends org.smartregister.chw.core.fragment.AddMemberFragment {

    private Context context;

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_member, container, false);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            int i = v.getId();
            if (i == R.id.layout_add_anc) {
                ((CoreFamilyProfileActivity) context).startChildForm(CoreConstants.JSON_FORM.getChildRegister(), "", "", "");
                dismiss();
            } else if (i == R.id.layout_add_caregiver) {
                ((CoreFamilyProfileActivity) context).startFormActivity(CoreConstants.JSON_FORM.getFamilyMemberRegister(), null, "");
                dismiss();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
