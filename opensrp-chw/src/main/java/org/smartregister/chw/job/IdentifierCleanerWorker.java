package org.smartregister.chw.job;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.smartregister.CoreLibrary;
import org.smartregister.chw.domain.CleanEDIStatus;
import org.smartregister.chw.repository.KKEventClientRepository;

import timber.log.Timber;

/**
 * Author issyzac on 27/09/2023
 */
public class IdentifierCleanerWorker extends Worker {

    private Context context;
    public static final String TAG = "IdentifierCleanerWorker";
    KKEventClientRepository repository;

    public IdentifierCleanerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        repository = new KKEventClientRepository();
    }

    @NonNull
    @Override
    public Result doWork() {
        CleanEDIStatus cleanEDIStatus = null;
        try {
            cleanEDIStatus = DoClean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timber.i("Doing some cleaning work");

        // Remove worker if everything has been cleaned
        if (cleanEDIStatus != null && cleanEDIStatus.equals(CleanEDIStatus.CLEANED))
            WorkManager.getInstance(context).cancelWorkById(this.getId());

        return Result.success();
    }

    private CleanEDIStatus DoClean() throws Exception {
        // Call repository to clean the IDs
        return repository.removeEdiIdsFromClients();
    }

}
