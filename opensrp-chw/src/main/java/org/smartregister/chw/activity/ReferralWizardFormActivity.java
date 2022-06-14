package org.smartregister.chw.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.chw.fragment.ReferralJsonWizardFormFragment;
import org.smartregister.family.activity.FamilyWizardFormActivity;
import org.smartregister.family.util.Constants;

public class ReferralWizardFormActivity extends FamilyWizardFormActivity {

    private String baseEntityID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseEntityID = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
    }

    @Override
    public void initializeFormFragment() {
        ReferralJsonWizardFormFragment jsonWizardFormFragment = ReferralJsonWizardFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);

        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, jsonWizardFormFragment).commit();
    }

    public String getBaseEntityID() {
        return baseEntityID;
    }
}
