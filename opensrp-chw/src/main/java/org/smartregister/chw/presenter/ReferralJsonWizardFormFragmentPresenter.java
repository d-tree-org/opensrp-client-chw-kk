package org.smartregister.chw.presenter;

import android.content.Intent;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;
import com.vijay.jsonwizard.utils.FormUtils;

public class ReferralJsonWizardFormFragmentPresenter extends JsonWizardFormFragmentPresenter {
    private FormUtils formUtils = new FormUtils();

    public ReferralJsonWizardFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void onSaveClick(LinearLayout mainView) {
        validateAndWriteValues();
        checkAndStopCountdownAlarm();
        boolean isFormValid = isFormValid();
        if (isFormValid || Boolean.valueOf(mainView.getTag(com.vijay.jsonwizard.R.id.skip_validation).toString())) {
            Intent returnIntent = new Intent();
            getView().onFormFinish();
            returnIntent.putExtra("json", formUtils.addFormDetails(getView().getCurrentJsonState()));
            returnIntent.putExtra(JsonFormConstants.SKIP_VALIDATION,
                    Boolean.valueOf(mainView.getTag(com.vijay.jsonwizard.R.id.skip_validation).toString()));
            getView().finishWithResult(returnIntent);
        } else {
            if (showErrorsOnSubmit()) {
                launchErrorDialog();
                getView().showToast(getView().getContext().getResources()
                        .getString(com.vijay.jsonwizard.R.string.json_form_error_msg, getInvalidFields().size()));
            } else {
                getView().showSnackBar(getView().getContext().getResources()
                        .getString(com.vijay.jsonwizard.R.string.json_form_error_msg, getInvalidFields().size()));

            }
        }
    }
}
