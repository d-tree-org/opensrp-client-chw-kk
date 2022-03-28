package org.smartregister.chw.interactor;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.CareGiverResponsivenessActionHelper;
import org.smartregister.chw.actionhelper.MalariaPreventionActionHelper;
import org.smartregister.chw.actionhelper.NewBornCareBreastfeedingHelper;
import org.smartregister.chw.actionhelper.PlayAssessmentCounselingActionHelper;
import org.smartregister.chw.actionhelper.ProblemSolvingActionHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.chw.util.Utils;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class ChildHomeVisitInteractorFlv extends DefaultChildHomeVisitInteractorFlv {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Override
    public LinkedHashMap<String, BaseAncHomeVisitAction> calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) throws BaseAncHomeVisitAction.ValidationException {
        actionList = new LinkedHashMap<>();
        context = view.getContext();
        this.memberObject = memberObject;
        editMode = view.getEditMode();
        try {
            this.dob = DateTime.parse(memberObject.getDob()).toDate(); //ChildDao.getChild(memberObject.getBaseEntityId()).getDateOfBirth();
        } catch (Exception e) {
            Timber.e(e);
        }
        this.view = view;
        // get the preloaded data
        if (view.getEditMode()) {
            Visit lastVisit = getVisitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.EventType.CHILD_HOME_VISIT);
            if (lastVisit != null) {
                details = VisitUtils.getVisitGroups(getVisitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        Map<String, ServiceWrapper> serviceWrapperMap = getServices();

        try {
            Constants.JSON_FORM.setLocaleAndAssetManager(ChwApplication.getCurrentLocale(), ChwApplication.getInstance().getApplicationContext().getAssets());
        } catch (Exception e) {
            Timber.e(e);
        }

        bindEvents(serviceWrapperMap);

        return actionList;
    }

    @Override
    protected void bindEvents(Map<String, ServiceWrapper> serviceWrapperMap) throws BaseAncHomeVisitAction.ValidationException {
        try {
            evaluateBreastFeeding(serviceWrapperMap);
            evaluateMalariaPrevention(serviceWrapperMap);
            evaluateChildPlayAssessmentCounseling(serviceWrapperMap);
            evaluateProblemSolving(serviceWrapperMap);
            evaluateCareGiverResponsiveness();
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    protected void evaluateBreastFeeding(Map<String, ServiceWrapper> serviceWrapperMap) throws Exception {

        ServiceWrapper serviceWrapper = serviceWrapperMap.get("Essential care breastfeeding");
        if (serviceWrapper == null) return;

        Alert alert = serviceWrapper.getAlert();
        if (alert == null || new LocalDate().isBefore(new LocalDate(alert.startDate()))) return;

        final String serviceName = serviceWrapper.getName();

        // Check if it is a dummy -5 weeks service that is there to re-set milestone to 0 before start 1 months recurring
        if ("Essential care breastfeeding -5 weeks".equalsIgnoreCase(serviceName)) return;

        // Get the very first breastfeeding visit
        boolean firstBreastFeedingHappened;
        List<Visit> breastFeedingServiceVisits = getVisitRepository().getVisits(memberObject.getBaseEntityId(), "Essential New Born Care: Breastfeeding");

        firstBreastFeedingHappened = breastFeedingServiceVisits.size() > 0;

        String title = getBreastfeedingServiceTittle(serviceWrapper.getName());

        NewBornCareBreastfeedingHelper helper = new NewBornCareBreastfeedingHelper(context, alert, firstBreastFeedingHappened, serviceWrapper);
        JSONObject jsonObject = getFormJson(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvBreastfeeding(), memberObject.getBaseEntityId());

        BaseAncHomeVisitAction action = getBuilder(title)
                .withHelper(helper)
                .withDetails(details)
                .withOptional(false)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withPayloadType(BaseAncHomeVisitAction.PayloadType.SERVICE)
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvBreastfeeding())
                .withPayloadDetails(serviceWrapper.getName())
                .build();

        actionList.put(title, action);

    }

    protected void evaluateMalariaPrevention(Map<String, ServiceWrapper> serviceWrapperMap) throws Exception {

        ServiceWrapper serviceWrapper = serviceWrapperMap.get("Malaria Prevention");
        if (serviceWrapper == null) return;

        Alert alert = serviceWrapper.getAlert();
        if (alert == null || new LocalDate().isBefore(new LocalDate(alert.startDate()))) return;

        final String serviceName = serviceWrapper.getName();

        String title = getMalariaPreventionServiceTittle(serviceWrapper.getName());

        MalariaPreventionActionHelper helper = new MalariaPreventionActionHelper(context, alert);

        BaseAncHomeVisitAction action = getBuilder(title)
                .withHelper(helper)
                .withDetails(details)
                .withOptional(false)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withPayloadType(BaseAncHomeVisitAction.PayloadType.SERVICE)
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvMalariaPrevention())
                .withPayloadDetails(serviceWrapper.getName())
                .build();

        actionList.put(title, action);

    }

    protected void evaluateChildPlayAssessmentCounseling(Map<String, ServiceWrapper> serviceWrapperMap) throws Exception {

        ServiceWrapper serviceWrapper = serviceWrapperMap.get("Play Assessment and Counselling");
        if (serviceWrapper == null) return;

        Alert alert = serviceWrapper.getAlert();
        if (alert == null || new LocalDate().isBefore(new LocalDate(alert.startDate()))) return;

        final String serviceName = serviceWrapper.getName();

        // Check if it is a dummy -5 weeks service that is there to re-set milestone to 0 before start 1 months recurring
        if ("Play Assessment and Counselling -5 weeks".equalsIgnoreCase(serviceName)) return;

        String[] serviceNameSplit = serviceName.split(" ");
        String period = serviceNameSplit[serviceNameSplit.length - 2];
        String periodNoun = serviceNameSplit[serviceNameSplit.length - 1];

        String title = context.getString(R.string.child_play_and_assessment_counselling) + getTranslatedPeriod(period, periodNoun);

        PlayAssessmentCounselingActionHelper helper = new PlayAssessmentCounselingActionHelper(serviceWrapper);

        BaseAncHomeVisitAction action = getBuilder(title)
                .withHelper(helper)
                .withDetails(details)
                .withOptional(false)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withPayloadType(BaseAncHomeVisitAction.PayloadType.SERVICE)
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvPlayAssessmentCounselling())
                .withPayloadDetails(serviceWrapper.getName())
                .build();

        actionList.put(title, action);

    }

    private void evaluateProblemSolving(Map<String, ServiceWrapper> serviceWrapperMap) throws BaseAncHomeVisitAction.ValidationException{

        ServiceWrapper serviceWrapper = serviceWrapperMap.get("Problem Solving");
        if (serviceWrapper == null) return;

        Alert alert = serviceWrapper.getAlert();
        if (alert == null || new LocalDate().isBefore(new LocalDate(alert.startDate()))) return;

        boolean isOverdue = new LocalDate().isAfter(new LocalDate(alert.startDate()).plusDays(14));
        String dueState = !isOverdue ? context.getString(R.string.due) : context.getString(R.string.overdue);

        ProblemSolvingActionHelper actionHelper = new ProblemSolvingActionHelper(alert, context);

        String title = context.getString(R.string.ccd_problem_solving);
        BaseAncHomeVisitAction action = getBuilder(title)
                .withHelper(actionHelper)
                .withScheduleStatus(!isOverdue ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.OVERDUE)
                .withDetails(details)
                .withOptional(false)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withPayloadType(BaseAncHomeVisitAction.PayloadType.SERVICE)
                .withSubtitle(MessageFormat.format("{0}{1}", dueState, DateTimeFormat.forPattern("dd MMM yyyy").print(new DateTime(serviceWrapper.getVaccineDate()))))
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvProblemSolving())
                .build();

        actionList.put(title, action);
    }

    protected void evaluateCareGiverResponsiveness() throws BaseAncHomeVisitAction.ValidationException {

        CareGiverResponsivenessActionHelper actionHelper = new CareGiverResponsivenessActionHelper();

        String title = "CCD: Caregiver Responsiveness to Child Behavior/Cues";

        BaseAncHomeVisitAction action = getBuilder(title)
                .withHelper(actionHelper)
                //.withScheduleStatus(!isOverdue ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.OVERDUE)
                .withDetails(details)
                .withOptional(false)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withProcessingMode(BaseAncHomeVisitAction.ProcessingMode.SEPARATE)
                .withPayloadType(BaseAncHomeVisitAction.PayloadType.SERVICE)
                //.withSubtitle(MessageFormat.format("{0}{1}", dueState, DateTimeFormat.forPattern("dd MMM yyyy").print(new DateTime(serviceWrapper.getVaccineDate()))))
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvCaregiverResponsiveness())
                .build();

        actionList.put(title, action);

    }

    private String getBreastfeedingServiceTittle(String serviceName) {

        String[] serviceNameSplit = serviceName.split(" ");
        String period = serviceNameSplit[serviceNameSplit.length - 2];
        String periodNoun = serviceNameSplit[serviceNameSplit.length - 1];

        return context.getString(R.string.essential_newborn_care_breastfeeding) + getTranslatedPeriod(period, periodNoun);
    }

    private String getMalariaPreventionServiceTittle(String serviceName) {

        String[] serviceNameSplit = serviceName.split(" ");
        String period = serviceNameSplit[serviceNameSplit.length - 2];
        String periodNoun = serviceNameSplit[serviceNameSplit.length - 1];

        return context.getString(R.string.malaria_prevention_service) + getTranslatedPeriod(period, periodNoun);
    }

    private String getTranslatedPeriod(String period, String periodNoun) {
        String translatedText = "";

        if ("hours".equalsIgnoreCase(periodNoun)) {
            translatedText = context.getString(R.string.hour_of, period);
        } else if ("days".equalsIgnoreCase(periodNoun)) {
            translatedText = context.getString(R.string.day_of, period);
        } else if ("weeks".equalsIgnoreCase(periodNoun)) {
            translatedText = context.getString(R.string.week_of, period);
        } else {
            translatedText = context.getString(R.string.month_of, period);
        }
        return translatedText;
    }
}
