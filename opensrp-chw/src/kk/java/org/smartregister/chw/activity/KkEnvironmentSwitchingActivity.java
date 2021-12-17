package org.smartregister.chw.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.util.FileUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.util.LangUtils;
import org.smartregister.util.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

/**
 * Created by Kassim Sheghembe on 2021-12-14
 */
public class KkEnvironmentSwitchingActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String ROOT_FOLDER = "Kizazi";

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs

        String lang = LangUtils.getLanguage(base.getApplicationContext());
        Configuration newConfiguration = LangUtils.setAppLocale(base, lang);

        super.attachBaseContext(base);

        applyOverrideConfiguration(newConfiguration);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareSwitchFolder();

        getSupportFragmentManager().
                beginTransaction().
                replace(android.R.id.content, new KkEnvironmentSwitchingActivity.MyPreferenceFragment()).
                commit();
    }

    private void updateUrl(String baseUrl) {
        try {

            AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();

            URL url = new URL(baseUrl);

            String base = url.getProtocol() + "://" + url.getHost();
            int port = url.getPort();

            Timber.i("Base URL: %s", base);
            Timber.i("Port: %s", port);

            allSharedPreferences.saveHost(base);
            allSharedPreferences.savePort(port);

            Timber.i("Saved URL: %s", allSharedPreferences.fetchHost(""));
            Timber.i("Port: %s", allSharedPreferences.fetchPort(0));
        } catch (MalformedURLException e) {
            Timber.e("Malformed Url: %s", baseUrl);
        }
    }

    @Override
    public void onClick(View view) {


    }

    private void prepareSwitchFolder() {
        String rootFolder = ROOT_FOLDER;
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    private void createFolders(String rootFolder, Boolean onSdCard) {
        try {
            FileUtils.createDirectory(rootFolder, onSdCard);
        } catch (Exception e) {
            Timber.v(e);
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat implements
            Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {


        public static final String KIZAZI_ENVIRONMENT = "opensrp_kizazi_environment";
        private SwitchPreferenceCompat switchPreferenceCompat;
        private int countClick = 0;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //super.onCreatePreferences(savedInstanceState, rootKey);
            setPreferencesFromResource(R.xml.kk_switch_env_preference, rootKey);
            Preference preference = findPreference("preference");
            if (preference != null) {
                preference.setOnPreferenceClickListener(this);
            }
            switchPreferenceCompat = findPreference("enable_production");
            if (switchPreferenceCompat != null)
                switchPreferenceCompat.setOnPreferenceChangeListener(this);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String newEnvironment = (Boolean) newValue ? "Production" : "test";
            final boolean[] userAgreed = {false};
            if ((Boolean) newValue) {
                confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                    @Override
                    public void onOkButtonClick() {
                        switchEnvironment(newEnvironment);
                        preference.getSharedPreferences().edit().putBoolean("enable_production", (Boolean) newValue).apply();
                        switchPreferenceCompat.setChecked(true);
                        userAgreed[0] = true;
                    }

                    @Override
                    public void onCancelButtonClick() {
                        switchPreferenceCompat.setChecked(false);
                        userAgreed[0] = false;
                    }
                }, newEnvironment);
            } else {
                confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                    @Override
                    public void onOkButtonClick() {
                        switchEnvironment(newEnvironment);
                        preference.getSharedPreferences().edit().putBoolean("enable_production", (Boolean) newValue).apply();
                        switchPreferenceCompat.setChecked(false);
                        userAgreed[0] = true;
                    }

                    @Override
                    public void onCancelButtonClick() {
                        switchPreferenceCompat.setChecked(true);
                        userAgreed[0] = false;
                    }
                }, newEnvironment);
            }
            return userAgreed[0];

        }

        private void confirmSwitchingEnvironment(Context context, final OnDialogButtonClick onDialogButtonClick, String environment) {
            final Boolean[] userResponse = {false};
            final androidx.appcompat.app.AlertDialog alert = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.SettingsAlertDialog).create();
            View title_view = this.getLayoutInflater().inflate(R.layout.custom_dialog_title, null);
            alert.setCustomTitle(title_view);
            alert.setMessage(String.format(getString(R.string.switch_environment_message), environment));
            alert.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), (dialog, which) -> {
                //Toast.makeText(context, context.getResources().getString(R.string.referral_submitted), Toast.LENGTH_LONG).show();
                onDialogButtonClick.onOkButtonClick();
                userResponse[0] = true;
                alert.dismiss();
            });
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), (dialog, which) -> {
                onDialogButtonClick.onCancelButtonClick();
                userResponse[0] = false;
                alert.dismiss();
            });
            alert.show();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equalsIgnoreCase("preference")) {
                if (countClick < 7) {
                    countClick ++;
                }
                if (countClick == 7 ){
                    switchPreferenceCompat.setVisible(true);
                    preference.setVisible(false);
                }
            }

            return false;
        }

        private void switchEnvironment(String newEnvironment) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
            sharedPreferences.edit().putString(KIZAZI_ENVIRONMENT, newEnvironment).apply();
            writeEnvironmentConfiguration(newEnvironment);
            clearApplicationData();
        }

        private void writeEnvironmentConfiguration(String environment) {

            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("env", environment);

                File file = new File(Environment.getExternalStorageDirectory()+ File.separator + ROOT_FOLDER, "env_switch.json");

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(jsonObject.toString());
                bufferedWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void clearApplicationData() {
            File appDir = new File(Environment.getDataDirectory() + File.separator + "data/org.smartregister.chw.kk");
            if(appDir.exists()){
                String[] children = appDir.list();
                for(String s : children){
                    if(!s.equals("lib")){
                        deleteDir(new File(appDir, s));
                        Timber.i("File /data/data/APP_PACKAGE/" + s + " DELETED");
                    }
                }
            }  // TODO else part


            requireActivity().finish();
            System.exit(0);

        }

        public static boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }

            return dir.delete();
        }
    }
}
