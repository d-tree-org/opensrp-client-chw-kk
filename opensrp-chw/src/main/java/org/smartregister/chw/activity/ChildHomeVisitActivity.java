package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.interactor.ChildHomeVisitInteractorFlv;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;

import java.util.Date;
import java.util.UUID;

import timber.log.Timber;

public class ChildHomeVisitActivity extends CoreChildHomeVisitActivity {
    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new CoreChildHomeVisitInteractor(new ChildHomeVisitInteractorFlv()));
    }

    @Override
    public void submittedAndClose() {
        super.submittedAndClose();
        ChildProfileActivity.startMe(this, memberObject, ChildProfileActivity.class);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {

        try {
            if (memberObject.getDob() != null) {
                JSONObject stepOne = jsonForm.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                JSONObject min_date = CoreJsonFormUtils.getFieldJSONObject(jsonArray, "birth_cert_issue_date");
                if (min_date != null) {
                    int days = CoreJsonFormUtils.getDayFromDate(memberObject.getDob());

                    min_date.put("min_date", "today-" + days + "d");
                }
            }

            Form form = new Form();
            form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
            form.setWizard(false);

            String entityID = baseEntityID != null ? baseEntityID : memberObject.getBaseEntityId();

            Intent intent = new Intent(this, ReferralWizardFormActivity.class);
            intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
            intent.putExtra(Constants.WizardFormActivity.EnableOnCloseDialog, false);
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, entityID);
            startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e, "Error starting child home visit form activity");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == org.smartregister.chw.anc.util.Constants.REQUEST_CODE_GET_JSON) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String jsonString = data.getStringExtra(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.JSON);

                    //check if client is being referred
                    JSONObject form = new JSONObject(jsonString);
                    JSONArray a = form.getJSONObject("step1").getJSONArray("fields");
                    String buttonAction = "";

                    for (int i = 0; i < a.length(); i++) {
                        org.json.JSONObject jo = a.getJSONObject(i);
                        if (jo.getString("key").compareToIgnoreCase("save_n_link") == 0 || jo.getString("key").compareToIgnoreCase("save_n_refer") == 0) {
                            if (jo.optString("value") != null && jo.optString("value").compareToIgnoreCase("true") == 0) {
                                buttonAction = jo.getJSONObject("action").getString("behaviour");
                            }
                        }
                    }
                    if (!buttonAction.isEmpty()) {
                        // check if other referral exists
                        String entityID = baseEntityID != null ? baseEntityID : memberObject.getBaseEntityId();
                        String businessStatus = buttonAction.equalsIgnoreCase("refer") ? CoreConstants.BUSINESS_STATUS.REFERRED : CoreConstants.BUSINESS_STATUS.LINKED;
                        if (!CoreReferralUtils.hasReferralTask(entityID, businessStatus)) {

                            //Update encounter information to referral
                            JSONObject jsonForm = new JSONObject(jsonString);
                            jsonForm.put(CoreJsonFormUtils.ENCOUNTER_TYPE, CoreConstants.EventType.CHILD_REFERRAL);
                            String referralTaskId = UUID.randomUUID().toString();
                            jsonForm.put(org.smartregister.chw.util.Constants.JSON_FORM_CONSTANT.REFERRAL_TASK_ID, referralTaskId);

                            //refer
                            CoreReferralUtils.createReferralEvent(referralTaskId, ChwApplication.getInstance().getContext().allSharedPreferences(),
                                    jsonForm.toString(), CoreConstants.TABLE_NAME.CHILD_REFERRAL, entityID);

                            //add referral schedule
                            ChwScheduleTaskExecutor.getInstance().execute(memberObject.getBaseEntityId(), CoreConstants.EventType.CHILD_REFERRAL, new Date());

                            Toast.makeText(getContext(), getResources().getString(org.smartregister.chw.R.string.referral_submitted), Toast.LENGTH_LONG).show();
                        }
                    }

                    //end of check referral
                    BaseAncHomeVisitAction ancHomeVisitAction = actionList.get(current_action);
                    if (ancHomeVisitAction != null) {
                        ancHomeVisitAction.setJsonPayload(jsonString);
                    }
                } catch (Exception e) {
                    Timber.e(e);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {

                BaseAncHomeVisitAction ancHomeVisitAction = actionList.get(current_action);
                if (ancHomeVisitAction != null)
                    ancHomeVisitAction.evaluateStatus();
            }

        }

        // update the adapter after every payload
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            redrawVisitUI();
        }
    }
}
