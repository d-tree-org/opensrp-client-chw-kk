package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.AncRegisterActivity;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.activity.ReferralFollowupActivity;
import org.smartregister.chw.core.interactor.CoreChildProfileInteractor;
import org.smartregister.chw.core.provider.ChwAncRegisterProvider;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.chw.util.TaskUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Task;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

/**
 * @author issyzac 6/8/22
 */
public class AncRegisterProvider extends ChwAncRegisterProvider {

    private View.OnClickListener onClickListener;

    public AncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.onClickListener = onClickListener;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
    }

    @Override
    protected void updateDueColumn(Context context, RegisterViewHolder viewHolder, VisitSummary visitSummary, String baseEntityId) {
        super.updateDueColumn(context, viewHolder, visitSummary, baseEntityId);
        viewHolder.dueButton.setVisibility(View.VISIBLE);

        //Check if user has a referral task open
        Task task = CoreReferralUtils.getTaskForEntity(baseEntityId, true);
        if (task != null && task.getStatus() == Task.TaskStatus.READY){
            setReferralDueButton(context, viewHolder.dueButton, baseEntityId);
        }

    }

    private void setReferralDueButton(Context context, Button dueButton, String baseEntityId) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        dueButton.setText(context.getString(R.string.referral_followup));
        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReferralFollowupActivity.startReferralFollowupActivity((AncRegisterActivity) context, CoreReferralUtils.getTaskForEntity(baseEntityId, true).getIdentifier(), baseEntityId);
            }
        });
    }

}
