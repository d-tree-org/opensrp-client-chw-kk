package org.smartregister.chw.activity;

import static com.vijay.jsonwizard.constants.JsonFormConstants.JSON_FORM_KEY.JSON;
import static org.smartregister.chw.core.utils.CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.REFERRAL_TASK_ID;
import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.getReferralFollowupForm;
import static org.smartregister.chw.core.utils.CoreReferralUtils.setEntityId;
import static org.smartregister.chw.malaria.util.MalariaJsonFormUtils.validateParameters;
import static org.smartregister.chw.util.JsonFormUtils.ENCOUNTER_TYPE;
import static org.smartregister.chw.util.JsonFormUtils.REQUEST_CODE_GET_JSON;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.JsonFormUtils.getFieldJSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.malaria.util.MalariaJsonFormUtils;
import org.smartregister.chw.referral.util.Constants;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.TaskRepository;
import org.smartregister.util.LangUtils;

import timber.log.Timber;

public class ReferralFollowupActivity extends BaseReferralFollowupActivity {
    private static final String CLIENT = "client";
    private boolean isComingFromReferralDetails = false;
    private static final String IS_COMING_FROM_REFERRAL_DETAILS = "IS_COMING_FROM_REFERRAL_DETAILS";

    private String taskId = "";
    private static final String TASK_IDENTIFIER = "taskIdentifier";

    public static final int REQUEST_CODE_REFERRAL_FOLLOWUP = 1205;

    public static void startReferralFollowupActivity(Activity activity, String taskIdentifier, String baseEntityId) {
        Intent intent = new Intent(activity, ReferralFollowupActivity.class);
        intent.putExtra(Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityId);

        Task.TaskPriority referralType = getReferralType(taskIdentifier);
        intent.putExtra(CoreConstants.ACTIVITY_PAYLOAD.REFERRAL_FOLLOWUP_FORM_NAME, getReferralFollowupForm(referralType));
        intent.putExtra(Constants.ActivityPayload.JSON_FORM, getReferralFollowupForm(referralType));
        intent.putExtra(TASK_IDENTIFIER, taskIdentifier);
        intent.putExtra(Constants.ActivityPayload.ACTION, CoreConstants.ACTIVITY_PAYLOAD.FOLLOW_UP_VISIT);
        activity.startActivityForResult(intent, REQUEST_CODE_REFERRAL_FOLLOWUP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            taskId = getIntent().getExtras().getString(TASK_IDENTIFIER);
            isComingFromReferralDetails = getIntent().getBooleanExtra(IS_COMING_FROM_REFERRAL_DETAILS, false);
        }
    }

    public String getTaskIdentifier() {
        return taskId;
    }

    public static Task.TaskPriority getReferralType(String identifier) {
        TaskRepository taskRepository = ChwApplication.getInstance().getTaskRepository();
        Task task = taskRepository.getTaskByIdentifier(identifier);
        return task.getPriority();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_REFERRAL_FOLLOWUP) {
            String jsonString = data.getStringExtra(JSON);
            try {
                JSONObject form = new JSONObject(jsonString);
                Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(form.toString());
                JSONObject jsonForm = registrationFormParams.getMiddle();
                String encounter_type = jsonForm.optString(ENCOUNTER_TYPE);

                if (CoreConstants.EncounterType.REFERRAL_FOLLOW_UP_VISIT.equals(encounter_type)) {
                    completeReferralTask(jsonString);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    protected void completeReferralTask(String jsonString) {
        try {
            JSONObject form = new JSONObject(jsonString);
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(form.toString());
            JSONObject jsonForm = registrationFormParams.getMiddle();
            String encounter_type = jsonForm.optString(ENCOUNTER_TYPE);

            String baseEntityId = jsonForm.optString(CoreConstants.ENTITY_ID);

            if (CoreConstants.EncounterType.REFERRAL_FOLLOW_UP_VISIT.equals(encounter_type)){
                JSONArray fields = registrationFormParams.getRight();

                JSONObject wantToComplete = getFieldJSONObject(fields, "complete_referral");
                JSONObject visit_hf_object = getFieldJSONObject(fields, "did_go_health_facility");
                if (visit_hf_object != null && "yes".equalsIgnoreCase(visit_hf_object.optString(VALUE)) ||
                        wantToComplete != null && "no".equalsIgnoreCase(wantToComplete.optString(VALUE))) {
                    // update task
                    TaskRepository taskRepository = ChwApplication.getInstance().getTaskRepository();
                    Task task = taskRepository.getTaskByIdentifier(getTaskIdentifier());
                    task.setStatus(Task.TaskStatus.COMPLETED);
                    taskRepository.addOrUpdate(task);

                    //Create event
                    createReferralFollowupEvent(jsonString, task.getIdentifier(), context().allSharedPreferences(), baseEntityId);

                }
            }

            // update schedule
            updateReferralFollowUpVisitSchedule(baseEntityId);
            if (isComingFromReferralDetails) {
                Intent intent = new Intent(this, ReferralRegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }

            finish();

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void createReferralFollowupEvent(String jsonString, String referralTaskId, AllSharedPreferences allSharedPreferences, String entityId) throws Exception {
        final Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, setEntityId(jsonString, entityId), CoreConstants.TABLE_NAME.REFERRAL_FOLLOW_UP);
        baseEvent.addIdentifier(REFERRAL_TASK_ID, referralTaskId);
        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
    }

    private void updateReferralFollowUpVisitSchedule(String baseEntityId) {
        ChwApplication.getInstance().getScheduleRepository().deleteScheduleByName(CoreConstants.SCHEDULE_TYPES.REFERRAL_FOLLOWUP_VISIT, baseEntityId);

        // check if there is any task ready/pending
        Task oldestTask = CoreReferralUtils.getTaskForEntity(baseEntityId, false);
        if (oldestTask != null) {
            ChwScheduleTaskExecutor.getInstance().execute(baseEntityId, CoreConstants.EncounterType.REFERRAL_FOLLOW_UP_VISIT, oldestTask.getAuthoredOn().toDate());
        }
    }

    @Override
    public void showProgressDialog(int titleIdentifier) {

    }

    @Override
    public void hideProgressDialog() {
    }
}
