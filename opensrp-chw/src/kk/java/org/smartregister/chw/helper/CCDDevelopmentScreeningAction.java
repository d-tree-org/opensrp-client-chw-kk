package org.smartregister.chw.helper;

import android.content.Context;

import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.domain.Alert;

import timber.log.Timber;

public class CCDDevelopmentScreeningAction  extends HomeVisitActionHelper {

    private Context context;

    private Alert alert;

    public CCDDevelopmentScreeningAction(Context context, Alert alert){
        this.alert = alert;
        this.context = context;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try{
            JSONObject jsonObject = new JSONObject(jsonPayload);
        }catch (Exception e){
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return null;
    }
}
