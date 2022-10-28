package org.smartregister.chw.application;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.activity.LoginActivity;
import org.smartregister.chw.util.KkSwitchConstants;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.BaseLoginActivity;

import java.util.List;

import static org.smartregister.util.Utils.isEmptyCollection;

import android.widget.Toast;

/**
 * Created by samuelgithengi on 10/19/18.
 */
public class ChwSyncConfiguration extends SyncConfiguration {
    @Override
    public int getSyncMaxRetries() {
        return BuildConfig.MAX_SYNC_RETRIES;
    }

    @Override
    public SyncFilter getSyncFilterParam() {
        return SyncFilter.PROVIDER;
    }

    @Override
    public String getSyncFilterValue() {
        String providerId = allSharedPreferences().fetchRegisteredANM();
        return providerId;
    }

    private AllSharedPreferences allSharedPreferences(){
        return org.smartregister.Context.getInstance().allSharedPreferences();
    }

    @Override
    public int getUniqueIdSource() {
        return BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    }

    @Override
    public int getUniqueIdBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    }

    @Override
    public int getUniqueIdInitialBatchSize() {
        return BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    }

    @Override
    public boolean isSyncSettings() {
        return BuildConfig.IS_SYNC_SETTINGS;
    }

    @Override
    public SyncFilter getEncryptionParam() {
        return SyncFilter.TEAM_ID;
    }

    @Override
    public boolean updateClientDetailsTable() {
        return false;
    }

    @Override
    public boolean isSyncUsingPost() {
        return ChwApplication.getApplicationFlavor().syncUsingPost();
    }

    @Override
    public List<String> getSynchronizedLocationTags() {
        return ImmutableList.of("MOH Jhpiego Facility Name", "Health Facility", "Facility");
    }

    @Override
    public SyncFilter getSettingsSyncFilterParam() {
        return SyncFilter.TEAM_ID;
    }

    @Override
    public boolean clearDataOnNewTeamLogin() {
        return false;
    }

    @Override
    public String getTopAllowedLocationLevel() {
        return "District";
    }

    @Override
    public String getOauthClientId() {
        /**
         * Check if production, get production client id
         */
        AllSharedPreferences sharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
        if (!sharedPreferences.getPreference(KkSwitchConstants.KIZAZI_ENVIRONMENT).isEmpty()) {
            if (sharedPreferences.getBooleanPreference("enable_production")) {
                return BuildConfig.OAUTH_CLIENT_ID_PROD;
            } else {
                return BuildConfig.OAUTH_CLIENT_ID;
            }
        }
        return BuildConfig.OAUTH_CLIENT_ID_PROD;
    }

    @Override
    public String getOauthClientSecret() {
        AllSharedPreferences sharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
        if (!sharedPreferences.getPreference(KkSwitchConstants.KIZAZI_ENVIRONMENT).isEmpty()) {
            if (sharedPreferences.getBooleanPreference("enable_production")) {
                return BuildConfig.OAUTH_CLIENT_SECRET_PROD;
            } else {
                return BuildConfig.OAUTH_CLIENT_SECRET;
            }
        }
        return BuildConfig.OAUTH_CLIENT_SECRET_PROD;
    }

    @Override
    public int getConnectTimeout() {
        return BuildConfig.MAX_CONNECTION_TIMEOUT * 60000;
    }

    @Override
    public int getReadTimeout() {
        return BuildConfig.MAX_READ_TIMEOUT *  60000;
    }

    @Override
    public Class<? extends BaseLoginActivity> getAuthenticationActivity() {
        return LoginActivity.class;
    }

    @Override
    public boolean validateUserAssignments() {
        return false;
    }
}
