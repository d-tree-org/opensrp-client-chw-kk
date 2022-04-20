package org.smartregister.chw.interactor;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.DangerSignsAction;
import org.smartregister.chw.actionhelper.PncDangerSignsActionHelper;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.RecurringServiceUtil;
import org.smartregister.chw.dao.ChwPNCDao;
import org.smartregister.chw.domain.PNCHealthFacilityVisitSummary;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class PncHomeVisitInteractorFlv extends DefaultPncHomeVisitInteractorFlv {

    protected Date deliberyDate;

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        context = view.getContext();
        actionList = new LinkedHashMap<>();
        this.memberObject = memberObject;
        editMode = view.getEditMode();
        this.view = view;

        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EventType.PNC_HOME_VISIT);
            if (lastVisit != null) {
                details = Collections.unmodifiableMap(VisitUtils.getVisitGroups(AncLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId())));
            }
        }

        try {
            this.deliberyDate = DateTime.parse(getPncDeliveryDate().toString()).toDate();
        } catch (Exception e) {
            Timber.e(e);
        }

        deliberyDate = getPncDeliveryDate();
        Map<String, ServiceWrapper> serviceWrapperMap = getServices();
        try {

            evaluateDangerSignsMother(serviceWrapperMap);

        } catch (BaseAncHomeVisitAction.ValidationException e) {
            Timber.e(e);
        }

        return actionList;
    }

    private Date getPncDeliveryDate() {
        return PNCDao.getPNCDeliveryDate(memberObject.getBaseEntityId());
    }

    private void evaluateDangerSignsMother(Map<String, ServiceWrapper> serviceWrapperMap) throws BaseAncHomeVisitAction.ValidationException {

        ServiceWrapper serviceWrapper = serviceWrapperMap.get("Pnc Danger Signs");

        if (serviceWrapper == null) return;

        Alert alert = serviceWrapper.getAlert();

        if (alert == null || new LocalDate().isBefore(new LocalDate(alert.startDate()))) return;

        final String serviceIteration = serviceWrapper.getName().substring(serviceWrapper.getName().length() - 1);

        String title = context.getString(R.string.postpartum_danger_signs, serviceIteration);

        BaseAncHomeVisitAction action = getBuilder(title)
                .withOptional(false)
                .withDetails(details)
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KK_PNC_HOME_VISIT.getPncHvDangerSigns())
                .withHelper(new PncDangerSignsActionHelper())
                .build();

        actionList.put(context.getString(R.string.pnc_danger_signs_mother), action);
    }

    private Map<String, ServiceWrapper> getServices() {
        return RecurringServiceUtil.getRecurringServices(
                memberObject.getBaseEntityId(),
                new DateTime(deliberyDate),
                Constants.SERVICE_GROUPS.PNC
        );
    }
}
