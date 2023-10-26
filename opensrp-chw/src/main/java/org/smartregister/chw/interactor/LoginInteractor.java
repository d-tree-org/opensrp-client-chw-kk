package org.smartregister.chw.interactor;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import org.smartregister.CoreLibrary;
import org.smartregister.chw.contract.LoginJobScheduler;
import org.smartregister.chw.job.IdentifierCleanerWorker;
import org.smartregister.job.DuplicateCleanerWorker;
import org.smartregister.login.interactor.BaseLoginInteractor;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.concurrent.TimeUnit;


/***
 * @author rkodev
 */
public class LoginInteractor extends BaseLoginInteractor implements BaseLoginContract.Interactor {

    /**
     * add all schedule jobs to the schedule instance to enable
     * job start at pin login
     */
    private LoginJobScheduler scheduler = new LoginJobSchedulerProvider();

    public LoginInteractor(BaseLoginContract.Presenter loginPresenter) {
        super(loginPresenter);
    }

    @Override
    protected void scheduleJobsPeriodically() {
        scheduler.scheduleJobsPeriodically();
        //Schedule your jobs here

    }

    @Override
    protected void scheduleJobsImmediately() {
        super.scheduleJobsImmediately();
        scheduler.scheduleJobsImmediately();
        scheduleIdCleanerWorker();

    }

    private void scheduleIdCleanerWorker(){
        WorkRequest identifierCleaner = new PeriodicWorkRequest.Builder(IdentifierCleanerWorker.class, 1, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(CoreLibrary.getInstance().context().applicationContext()).enqueue(identifierCleaner);
    }

}