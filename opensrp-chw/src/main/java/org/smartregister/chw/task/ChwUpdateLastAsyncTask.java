package org.smartregister.chw.task;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import org.joda.time.DateTime;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.ReferralFollowupActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.holders.RegisterViewHolder;
import org.smartregister.chw.core.task.UpdateLastAsyncTask;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.dao.ChwChildDao;
import org.smartregister.chw.util.TaskUtils;
import org.smartregister.chw.util.Utils;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Task;

public class ChwUpdateLastAsyncTask extends UpdateLastAsyncTask {

    public ChwUpdateLastAsyncTask(Context context, CommonRepository commonRepository, RegisterViewHolder viewHolder, String baseEntityId, View.OnClickListener onClickListener) {
        super(context, commonRepository, viewHolder, baseEntityId, onClickListener);
    }

    private void setDueState() {
        if (ChwChildDao.hasDueTodayVaccines(baseEntityId) || ChwChildDao.hasDueAlerts(baseEntityId)) {
            setVisitButtonDueStatus(context, viewHolder.dueButton);
        } else {
            setVisitButtonNoDueStatus(viewHolder.dueButton);
        }
    }

    @Override
    protected void onPostExecute(Void param) {
        if (commonPersonObject != null) {
            viewHolder.dueButton.setVisibility(View.VISIBLE);
            if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.DUE)) {
                setDueState();
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.OVERDUE)) {
                setVisitButtonOverdueStatus(context, viewHolder.dueButton, childVisit.getNoOfMonthDue());
            } else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.VISIT_DONE)){
                setVisitAboveTwentyFourView(context, viewHolder.dueButton);
            }else if (childVisit.getVisitStatus().equalsIgnoreCase(CoreConstants.VISIT_STATE.EXPIRED)){
                setVisitButtonExpired(viewHolder.dueButton);
            }
        } else {
            viewHolder.dueButton.setVisibility(View.GONE);
        }

        // Set the referral due button if the child has a referral due
        Task task = CoreReferralUtils.getTaskForEntity(baseEntityId, true);
        if (task != null){
            DateTime taskAuthoredOn = task.getAuthoredOn();
            long days = Utils.getTaskDuration(taskAuthoredOn);
            if (task.getStatus() == Task.TaskStatus.READY && days >= 3){
                setReferralDueButton(context, viewHolder.dueButton);
            }
        }

    }

    private void setVisitButtonExpired(Button dueButton){
        dueButton.setTextColor(context.getResources().getColor(R.color.grey));
        dueButton.setText("-");
        dueButton.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        dueButton.setOnClickListener(null);
    }

    private void setVisitButtonNoDueStatus(Button dueButton) {
        dueButton.setBackgroundResource(org.smartregister.chw.core.R.drawable.transparent_white_button);
        dueButton.setOnClickListener(null);
    }

    private void setReferralDueButton(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        dueButton.setText(context.getString(R.string.referral_followup));
        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferralFollowupActivity.startReferralFollowupActivity((ChildRegisterActivity) context, CoreReferralUtils.getTaskForEntity(baseEntityId, true).getIdentifier(), baseEntityId);
            }
        });
    }

}
