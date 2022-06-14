package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.ReferralWizardFormActivity;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.presenter.ReferralJsonWizardFormFragmentPresenter;

public class ReferralJsonWizardFormFragment extends JsonWizardFormFragment {

    public static ReferralJsonWizardFormFragment getFormFragment(String stepName) {
        ReferralJsonWizardFormFragment jsonFormFragment = new ReferralJsonWizardFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void customClick(Context context, String behaviour){
        // check if referral exist
        String businessStatus = behaviour.equalsIgnoreCase("refer") ? CoreConstants.BUSINESS_STATUS.REFERRED : CoreConstants.BUSINESS_STATUS.LINKED;
        String baseEntityID = ((ReferralWizardFormActivity) getActivity()).getBaseEntityID();
        String referredTo = behaviour.equalsIgnoreCase("refer") ? "Health Facility" : "Addo";

        if (CoreReferralUtils.hasReferralTask(baseEntityID, businessStatus) && !behaviour.equalsIgnoreCase("save")) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage("This client already has a referral to the "+ referredTo +", do you want to close this referral and open a new one?")
                    .setTitle("Existing Referral")
                    .setCancelable(false)
                    .setPositiveButton(R.string.no_button_label,
                            (dialog1, whichButton) -> {
                                //save
                                save();
                            })
                    .setNegativeButton(R.string.yes_button_label,
                            (dialog1, whichButton) -> {
                                // archive existing referral
                                CoreReferralUtils.archiveTasksForEntity(baseEntityID, businessStatus);
                                //save
                                save();
                            }).create();
            dialog.show();
        }
        else {
            //save
            save();
        }
    }

    @Override
    protected ReferralJsonWizardFormFragmentPresenter createPresenter() {
        return new ReferralJsonWizardFormFragmentPresenter(this, JsonFormInteractor.getInstance());
    }
}
