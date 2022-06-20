package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.activity.BaseAncHomeVisitActivity;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.R;
import org.smartregister.chw.core.task.RunnableTask;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.interactor.AncHomeVisitInteractor;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.Date;
import java.util.UUID;

import timber.log.Timber;

/**
 * @author rkodev
 */
public class AncHomeVisitActivity extends BaseAncHomeVisitActivity {

    public static void startMe(Activity activity, String baseEntityID, Boolean isEditMode) {
        Intent intent = new Intent(activity, AncHomeVisitActivity.class);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.EDIT_MODE, isEditMode);
        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new AncHomeVisitInteractor());
    }

    @Override
    public void submittedAndClose() {
        // recompute schedule
        Runnable runnable = () -> ChwScheduleTaskExecutor.getInstance().execute(memberObject.getBaseEntityId(), CoreConstants.EventType.ANC_HOME_VISIT, new Date());
        org.smartregister.chw.util.Utils.startAsyncTask(new RunnableTask(runnable), null);
        super.submittedAndClose();
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {

        Form form = new Form();
        form.setActionBarBackground(R.color.family_actionbar);
        form.setWizard(false);

        Intent intent = new Intent(this, ReferralWizardFormActivity.class);
        intent.putExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        intent.putExtra(Constants.WizardFormActivity.EnableOnCloseDialog, false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityID);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == org.smartregister.chw.anc.util.Constants.REQUEST_CODE_GET_JSON) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);

                    //check if client is being referred
                    JSONObject form = new JSONObject(jsonString);
                    JSONArray a = form.getJSONObject("step1").getJSONArray("fields");
                    String buttonAction = "";

                    for (int i=0; i<a.length(); i++) {
                        org.json.JSONObject jo = a.getJSONObject(i);
                        if (jo.getString("key").compareToIgnoreCase("save_n_link") == 0 || jo.getString("key").compareToIgnoreCase("save_n_refer") == 0) {
                            if(jo.optString("value") != null && jo.optString("value").compareToIgnoreCase("true") == 0){
                                buttonAction = jo.getJSONObject("action").getString("behaviour");
                            }
                        }
                    }
                    if(!buttonAction.isEmpty()) {
                        // check if other referral exists
                        String entityID = baseEntityID != null ? baseEntityID : memberObject.getBaseEntityId();
                        String businessStatus = buttonAction.equalsIgnoreCase("refer") ? CoreConstants.BUSINESS_STATUS.REFERRED : CoreConstants.BUSINESS_STATUS.LINKED;
                        if (!CoreReferralUtils.hasReferralTask(entityID, businessStatus)) {
                            //Update encounter information to referral
                            JSONObject jsonForm = new JSONObject(jsonString);
                            jsonForm.put(CoreJsonFormUtils.ENCOUNTER_TYPE, CoreConstants.EventType.ANC_REFERRAL);
                            String referralTaskId = UUID.randomUUID().toString();
                            jsonForm.put(org.smartregister.chw.util.Constants.JSON_FORM_CONSTANT.REFERRAL_TASK_ID, referralTaskId);

                            //refer
                            CoreReferralUtils.createReferralEvent(referralTaskId, ChwApplication.getInstance().getContext().allSharedPreferences(),
                                    jsonForm.toString(), CoreConstants.TABLE_NAME.ANC_REFERRAL, entityID);

                            //add referral schedule
                            ChwScheduleTaskExecutor.getInstance().execute(memberObject.getBaseEntityId(), CoreConstants.EventType.ANC_REFERRAL, new Date());

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

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
