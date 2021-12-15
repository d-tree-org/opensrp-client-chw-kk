package org.smartregister.chw.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fragment.ChooseLoginMethodFragment;
import org.smartregister.chw.fragment.PinLoginFragment;
import org.smartregister.chw.pinlogin.PinLogger;
import org.smartregister.chw.pinlogin.PinLoginUtil;
import org.smartregister.chw.presenter.LoginPresenter;
import org.smartregister.chw.util.Utils;
import org.smartregister.family.util.Constants;
import org.smartregister.growthmonitoring.service.intent.WeightForHeightIntentService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.task.SaveTeamLocationsTask;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;


public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {
    public static final String TAG = BaseLoginActivity.class.getCanonicalName();
    private static final String WFH_CSV_PARSED = "WEIGHT_FOR_HEIGHT_CSV_PARSED";

    private PinLogger pinLogger = PinLoginUtil.getPinLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasPermissions()) {
            setServerUrl();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();

        if (hasPinLogin()) {
            pinLoginAttempt();
            return;
        }

        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    private void pinLoginAttempt() {
        // if the user has pin
        if (mLoginPresenter.isUserLoggedOut()) {
            if (pinLogger.isPinSet()) {
                Intent intent = new Intent(this, PinLoginActivity.class);
                intent.putExtra(PinLoginActivity.DESTINATION_FRAGMENT, PinLoginFragment.TAG);
                startActivity(intent);
                finish();
            }
        } else {
            goToHome(false);
        }
    }

    private boolean hasPinLogin() {
        return ChwApplication.getApplicationFlavor().hasPinLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (hasPinLogin() && !pinLogger.isFirstAuthentication()) {
            menu.add(getString(R.string.reset_pin_login));
        }
        menu.add(getString(R.string.export_database));
        menu.add(getString(R.string.switch_environment));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.reset_pin_login))) {
            pinLogger.resetPinLogin();
            this.recreate();
            return true;
        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.export_database))) {
            String DBNAME = "drishti.db";
            String COPYDBNAME = "chw";

            Toast.makeText(this, R.string.export_db_notification, Toast.LENGTH_SHORT).show();
            String currentTimeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.ENGLISH).format(new Date());
            if (hasPermissions()) {
                copyDatabase(DBNAME, COPYDBNAME + "-" + currentTimeStamp + ".db", this);
                Toast.makeText(this, R.string.export_db_done_notification, Toast.LENGTH_SHORT).show();
            }
        } else if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.switch_environment))) {
            if (hasPermissions()) {
                this.startActivity(new Intent(this, KkEnvironmentSwitchingActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean hasPermissions(){
        return PermissionUtils.isPermissionGranted(this
                , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                , CoreConstants.RQ_CODE.STORAGE_PERMISIONS);
    }

    public void copyDatabase(String dbName, String copyDbName, Context context) {
        try {
            final String inFileName = context.getDatabasePath(dbName).getPath();
            final String outFileName = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DOWNLOADS + "/" + copyDbName;
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();

        } catch (Exception e) {
            Timber.e("copyDatabase: backup error " + e.toString());
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
            processWeightForHeightZscoreCSV();
        }

        if (hasPinLogin()) {
            startPinHome(remote);
        } else {
            startHome(remote);
        }

        finish();
    }

    private void startHome(boolean remote) {
        Intent intent = new Intent(this, ChwApplication.getApplicationFlavor().launchChildClientsAtLogin() ?
                ChildRegisterActivity.class : FamilyRegisterActivity.class);
        intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
    }

    private void startPinHome(boolean remote) {
        if (remote)
            pinLogger.resetPinLogin();

        if (pinLogger.isFirstAuthentication()) {
            EditText passwordEditText = findViewById(org.smartregister.R.id.login_password_edit_text);
            pinLogger.savePassword(passwordEditText.getText().toString());
        }

        if (pinLogger.isFirstAuthentication()) {
            Intent intent = new Intent(this, PinLoginActivity.class);
            intent.putExtra(PinLoginActivity.DESTINATION_FRAGMENT, ChooseLoginMethodFragment.TAG);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, ChwApplication.getApplicationFlavor().launchChildClientsAtLogin() ?
                    ChildRegisterActivity.class : FamilyRegisterActivity.class);
            intent.putExtra(Constants.INTENT_KEY.IS_REMOTE_LOGIN, remote);
            startActivity(intent);
        }
    }

    private void processWeightForHeightZscoreCSV() {
        AllSharedPreferences allSharedPreferences = ChwApplication.getInstance().getContext().allSharedPreferences();
        if (ChwApplication.getApplicationFlavor().hasChildSickForm() && !allSharedPreferences.getPreference(WFH_CSV_PARSED).equals("true")) {
            WeightForHeightIntentService.startParseWFHZScores(this);
            allSharedPreferences.savePreference(WFH_CSV_PARSED, "true");
        }
    }

    private void setServerUrl() {
        AllSharedPreferences preferences = org.smartregister.util.Utils.getAllSharedPreferences();

        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Kizazi", "env_switch.json");

            if (file.exists()) {
                // if the file is there, then  switching has taken place and the data was cleared from the device
                // Get the environment configurations from the file and set the url based on that
/*                        Yaml yaml = new Yaml();
                        Map<String, Object> envConfig = (Map<String, Object>) yaml.load(new FileInputStream(file));*/
                JSONObject envConfig = getSwitchConfigurationsFromFile(file);

                if (envConfig != null) {

                    if (envConfig.get("env").toString().equalsIgnoreCase("test")) {

                        //preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_staging);
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("enable_production", false).commit();
                        //preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_ADDO_ENVIRONMENT, "test");
                        //setAppNameProductionEnvironment("test");
                    } else {

                        //preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_production);
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("enable_production", true).commit();
                        //preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_ADDO_ENVIRONMENT, "production");
                        //setAppNameProductionEnvironment("production");
                    }
                }

            } else {
                // The file does not exist, no switching environment has taken place. This is the first time the user logged into the device
                //preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_production);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("enable_production", true).commit();
                //preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_ADDO_ENVIRONMENT, "production");
                //setAppNameProductionEnvironment("production");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONObject getSwitchConfigurationsFromFile(File file) {
        JSONObject jsonObject = new JSONObject();
        try {
            FileInputStream stream = new FileInputStream(file);
            String jString = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                jString = Charset.defaultCharset().decode(bb).toString();
            } finally {
                stream.close();
            }
            jsonObject = new JSONObject(jString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}