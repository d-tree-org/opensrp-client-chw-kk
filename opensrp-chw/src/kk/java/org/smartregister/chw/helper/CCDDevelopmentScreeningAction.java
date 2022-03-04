package org.smartregister.chw.helper;

import android.content.Context;

import androidx.room.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;

import timber.log.Timber;

public class CCDDevelopmentScreeningAction  extends HomeVisitActionHelper {

    private Context context;
    private Alert alert;
    private String developmentIssues;

    public CCDDevelopmentScreeningAction(Context context, Alert alert){
        this.alert = alert;
        this.context = context;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
            developmentIssues = JsonFormUtils.getCheckBoxValue(jsonObject, "child_development_issues");
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("Development issues: {0}", developmentIssues);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(developmentIssues)){
            return BaseAncHomeVisitAction.Status.PENDING;
        }else {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }
    }
}
