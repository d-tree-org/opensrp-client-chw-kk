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

import java.text.MessageFormat;

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
        String referredTo = behaviour.equalsIgnoreCase("refer") ? context.getString(R.string.referred_to_text_value_health_facility) : context.getString(R.string.referred_to_text_value_addo);

        if (CoreReferralUtils.hasReferralTask(baseEntityID, businessStatus) && !behaviour.equalsIgnoreCase("save")) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(MessageFormat.format(context.getString(R.string.existing_referral_dialog_message), referredTo))
                    .setTitle(context.getString(R.string.existing_referral_dialog_title))
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
