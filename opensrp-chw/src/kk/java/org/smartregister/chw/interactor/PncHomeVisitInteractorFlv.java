package org.smartregister.chw.interactor;

import androidx.annotation.Nullable;

import org.jeasy.rules.api.Rules;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.PncDangerSignsActionHelper;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.rule.PncVisitAlertRule;
import org.smartregister.chw.core.utils.HomeVisitUtil;
import org.smartregister.chw.core.utils.RecurringServiceUtil;
import org.smartregister.chw.pnc.PncLibrary;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class PncHomeVisitInteractorFlv extends DefaultPncHomeVisitInteractorFlv {

    protected Date deliveryDate;
    private Date lastVisitDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());


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
            this.deliveryDate = DateTime.parse(sdf.format(getPncDeliveryDate())).toDate();
        } catch (Exception e) {
            Timber.e(e);
        }

        Map<String, ServiceWrapper> serviceWrapperMap = getServices();
        try {

            evaluateDangerSignsMother(serviceWrapperMap);
            evaluateMaternalNutrition();

        } catch (BaseAncHomeVisitAction.ValidationException e) {
            Timber.e(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actionList;
    }

    private void evaluateMaternalNutrition() throws BaseAncHomeVisitAction.ValidationException {

        PncVisitAlertRule visitSummary = getVisitSummary(memberObject.getBaseEntityId());

        String visitID = visitID = visitSummary.getVisitID();


        if (visitID == null || visitID.equalsIgnoreCase("3") || visitID.equalsIgnoreCase("35")) return;


        String title = context.getString(R.string.maternal_nutrition);

        BaseAncHomeVisitAction action = getBuilder(title)
                .withOptional(false)
                .withDetails(details)
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KK_PNC_HOME_VISIT.getPncHvMaternalNutrition())
                .build();

        actionList.put(title, action);

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
                .withPayloadType(BaseAncHomeVisitAction.PayloadType.SERVICE)
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.COMBINED)
                .withHelper(new PncDangerSignsActionHelper())
                .build();

        actionList.put(title, action);
    }

    private Map<String, ServiceWrapper> getServices() {
        return RecurringServiceUtil.getRecurringServices(
                memberObject.getBaseEntityId(),
                new DateTime(deliveryDate),
                Constants.SERVICE_GROUPS.PNC
        );
    }

    private PncVisitAlertRule getVisitSummary(String motherBaseID) {
        Rules rules = ChwApplication.getInstance().getRulesEngineHelper().rules(org.smartregister.chw.util.Constants.RULE_FILE.PNC_HOME_VISIT);
        Date lastVisitDate = getLastDateVisit(motherBaseID);

        return HomeVisitUtil.getPncVisitStatus(rules, lastVisitDate, getPncDeliveryDate());
    }

    private Date getLastDateVisit(String motherBaseID) {
        Visit lastVisit = AncLibrary.getInstance().visitRepository().getLatestVisit(motherBaseID, org.smartregister.chw.anc.util.Constants.EVENT_TYPE.PNC_HOME_VISIT);
        if (lastVisit != null) {
            lastVisitDate = lastVisit.getDate();
            return lastVisitDate;
        } else {
            return lastVisitDate = getDeliveryDate(motherBaseID);
        }
    }

    @Nullable
    private Date getDeliveryDate(String motherBaseID) {
        try {
            String deliveryDateString = PncLibrary.getInstance().profileRepository().getDeliveryDate(motherBaseID);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return sdf.parse(deliveryDateString);

        } catch (ParseException e) {
            Timber.e(e);
        }
        return null;
    }
}
