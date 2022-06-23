package org.smartregister.chw.actionhelper;

import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;

public class MalnutritionScreeningActionHelper extends HomeVisitActionHelper {

    @Override
    public void onPayloadReceived(String s) {

    }

    @Override
    public String evaluateSubTitle() {
        return "";
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return BaseAncHomeVisitAction.Status.COMPLETED;
    }
}
