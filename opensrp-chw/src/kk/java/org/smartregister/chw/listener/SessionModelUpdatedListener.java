package org.smartregister.chw.listener;

import org.smartregister.chw.model.GroupSessionModel;

/**
 * Author issyzac on 09/07/2023
 */
public interface SessionModelUpdatedListener {
    public void onSessionModelUpdated(GroupSessionModel updatedSessionModel);
}
