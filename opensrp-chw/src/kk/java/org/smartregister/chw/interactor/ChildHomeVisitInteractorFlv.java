package org.smartregister.chw.interactor;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.ExclusiveBreastFeedingAction;
import org.smartregister.chw.actionhelper.NewBornCareBreastfeedingHelper;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.fragment.BaseAncHomeVisitFragment;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.ChildDao;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
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
            evaluateECD();
            evaluateExclusiveBreastFeeding(serviceWrapperMap);
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

        //final String serviceIteration = serviceWrapper.getName().substring(serviceWrapper.getName().length() - 1);

        String title = getBreastfeedingServiceTittle(serviceWrapper.getName());

        NewBornCareBreastfeedingHelper helper = new NewBornCareBreastfeedingHelper(context, alert, serviceWrapper);
        JSONObject jsonObject = getFormJson(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvBreastfeeding(), memberObject.getBaseEntityId());

        BaseAncHomeVisitAction action = getBuilder(title)
                .withHelper(helper)
                .withDetails(details)
                .withOptional(false)
                .withBaseEntityID(memberObject.getBaseEntityId())
                .withFormName(KkConstants.KKJSON_FORM_CONSTANT.KKCHILD_HOME_VISIT.getChildHvBreastfeeding())
                .withPayloadDetails(serviceWrapper.getName())
                .build();

        actionList.put(title, action);

    }

    private String getBreastfeedingServiceTittle(String serviceName) {

        String[] serviceNameSplit = serviceName.split(" ");
        String period = serviceNameSplit[serviceNameSplit.length - 2];
        String periodNoun = serviceNameSplit[serviceNameSplit.length - 1];

        return context.getString(R.string.essential_newborn_care_breastfeeding) + getTranslatedPeriod(period, periodNoun);
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
