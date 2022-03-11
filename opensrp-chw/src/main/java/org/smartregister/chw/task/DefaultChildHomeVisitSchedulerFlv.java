package org.smartregister.chw.task;

import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.contract.ScheduleTask;
import org.smartregister.chw.core.domain.BaseScheduleTask;
import org.smartregister.chw.core.rule.VisitAlertRule;
import org.smartregister.chw.core.utils.ChildHomeVisit;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.PersonDao;
import org.smartregister.chw.util.ChildUtils;
import org.smartregister.chw.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DefaultChildHomeVisitSchedulerFlv implements ChildHomeVisitScheduler.Flavor {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Override
    public List<ScheduleTask> generateTasks(String baseEntityID, String eventName, Date eventDate, BaseScheduleTask baseScheduleTask) {
        // recompute the home visit task for this child
        ChildHomeVisit childHomeVisit = ChildUtils.getLastHomeVisit(Constants.TABLE_NAME.CHILD, baseEntityID);
        String yearOfBirth = PersonDao.getDob(baseEntityID);
        long lastVisitDate = childHomeVisit.getLastHomeVisitDate();
        Date dob = null;
        try {
            dob = dateFormat.parse(yearOfBirth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        VisitAlertRule visitAlertRule = new VisitAlertRule(
                ChwApplication.getInstance().getApplicationContext(), dob, lastVisitDate, childHomeVisit.getVisitNotDoneDate());
        CoreChwApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(visitAlertRule, CoreConstants.RULE_FILE.CHILD_HOME_VISIT);


        baseScheduleTask.setScheduleDueDate(visitAlertRule.getDueDate());
        baseScheduleTask.setScheduleNotDoneDate(visitAlertRule.getNotDoneDate());
        baseScheduleTask.setScheduleExpiryDate(visitAlertRule.getExpiryDate());
        baseScheduleTask.setScheduleCompletionDate(visitAlertRule.getCompletionDate());
        baseScheduleTask.setScheduleOverDueDate(visitAlertRule.getOverDueDate());

        return toScheduleList(baseScheduleTask);

    }

    protected List<ScheduleTask> toScheduleList(ScheduleTask task, ScheduleTask... tasks) {
        List<ScheduleTask> res = new ArrayList<>();
        res.add(task);
        if (tasks != null && tasks.length > 0)
            res.addAll(Arrays.asList(tasks));

        return res;
    }
}
