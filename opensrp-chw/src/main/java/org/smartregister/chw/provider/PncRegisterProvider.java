package org.smartregister.chw.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.provider.AncRegisterProvider;
import org.smartregister.chw.core.provider.ChwPncRegisterProvider;
import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.chw.core.utils.VisitSummary;
import org.smartregister.chw.util.TaskUtils;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.Set;

/**
 * @author issyzac 6/8/22
 */
public class PncRegisterProvider extends ChwPncRegisterProvider {

    private Context context;
    private View.OnClickListener onClickListener;

    public PncRegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {
        super(context, commonRepository, visibleColumns, onClickListener, paginationClickListener);
        this.context = context;
        this.onClickListener = onClickListener;
    }


    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.getView(cursor, client, viewHolder);
    }

    @Override
    protected void updateDueColumn(Context context, RegisterViewHolder viewHolder, PncVisitAlertRule pncVisitAlertRule, String baseEntityId) {
        super.updateDueColumn(context, viewHolder, pncVisitAlertRule, baseEntityId);
        viewHolder.dueButton.setVisibility(View.VISIBLE);
        if (TaskUtils.hasReferralDue(TaskUtils.getClientReferralTasks(baseEntityId))) {
            setVisitButtonDueStatus(context, viewHolder.dueButton);
        }
    }

    private void setVisitButtonDueStatus(Context context, Button dueButton) {
        dueButton.setTextColor(context.getResources().getColor(R.color.white));
        dueButton.setText(context.getString(R.string.referral_followup));
        dueButton.setBackgroundResource(R.drawable.overdue_red_btn_selector);
        dueButton.setOnClickListener(onClickListener);
    }


}
