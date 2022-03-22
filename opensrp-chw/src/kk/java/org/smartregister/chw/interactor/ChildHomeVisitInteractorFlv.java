package org.smartregister.chw.interactor;

import android.annotation.SuppressLint;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.actionhelper.DangerSignsAction;
import org.smartregister.chw.actionhelper.NeonatalDangerSignsAction;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.domain.Person;
import org.smartregister.chw.core.model.ChildVisit;
import org.smartregister.chw.core.utils.ChildHomeVisit;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChildHomeVisitInteractorFlv extends DefaultChildHomeVisitInteractorFlv {

    @Override
    protected void bindEvents(Map<String, ServiceWrapper> serviceWrapperMap) throws BaseAncHomeVisitAction.ValidationException {
        ChildHomeVisit lastHomeVisit = ChildUtils.getLastHomeVisit(Constants.TABLE_NAME.CHILD, memberObject.getDob());
        final ChildVisit childVisit = ChildUtils.getChildVisitStatus(context, memberObject.getDob(), lastHomeVisit.getLastHomeVisitDate(), lastHomeVisit.getVisitNotDoneDate(), lastHomeVisit.getDateCreated());
        try {
            evaluateNeonatalDangerSigns(serviceWrapperMap);
        } catch (BaseAncHomeVisitAction.ValidationException e) {
            throw (e);
        } catch (Exception e) {
            Timber.e(e);
        }
    }


    private void evaluateNeonatalDangerSigns(Map<String, ServiceWrapper> serviceWrapperMap) throws Exception {
        ServiceWrapper serviceWrapper = serviceWrapperMap.get("Neonatal Danger Signs");
        if (serviceWrapper == null) return;

        Alert alert = serviceWrapper.getAlert();
        if (alert == null || new LocalDate().isBefore(new LocalDate(alert.startDate()))) return;

        final String serviceIteration = serviceWrapper.getName().substring(serviceWrapper.getName().length() - 1);

        String title = context.getString(R.string.neonatal_danger_signs_month, serviceIteration);

        // Todo -> Compute overdue
        boolean isOverdue = new LocalDate().isAfter(new LocalDate(alert.startDate()).plusDays(14));
        String dueState = !isOverdue ? context.getString(R.string.due) : context.getString(R.string.overdue);

        NeonatalDangerSignsAction helper = new NeonatalDangerSignsAction(context, alert);
        Map<String, List<VisitDetail>> details = getDetails(Constants.EventType.CHILD_HOME_VISIT);

        BaseAncHomeVisitAction neoNatalDangerSignsAction = new BaseAncHomeVisitAction.Builder(context, context.getString(R.string.neonatal_danger_signs_month, serviceIteration))
                .withOptional(false)
                .withDetails(details)
                .withFormName("child_hv_neonatal_danger_signs")
                .withScheduleStatus(!isOverdue ? BaseAncHomeVisitAction.ScheduleStatus.DUE : BaseAncHomeVisitAction.ScheduleStatus.OVERDUE)
                .withSubtitle(MessageFormat.format("{0}{1}", dueState, DateTimeFormat.forPattern("dd MMM yyyy").print(new DateTime(serviceWrapper.getVaccineDate()))))
                .withHelper(helper)
                .build();

        actionList.put(title, neoNatalDangerSignsAction);
    }
}
