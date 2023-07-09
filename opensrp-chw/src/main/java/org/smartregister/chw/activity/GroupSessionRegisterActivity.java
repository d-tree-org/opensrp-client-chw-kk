package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.GcFinalStepFragment;
import org.smartregister.chw.fragment.GcRegistrationStageFragment;
import org.smartregister.chw.fragment.SelectChildForGroupSessionRegisterFragment;
import org.smartregister.chw.listener.SessionModelUpdatedListener;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.chw.presenter.GroupSessionRegisterPresenter;
import org.smartregister.chw.util.Utils;
import org.smartregister.family.listener.FamilyBottomNavigationListener;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.contract.BaseRegisterContract;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Author issyzac on 18/05/2023
 */
public class GroupSessionRegisterActivity extends BaseRegisterActivity implements BaseRegisterContract.View, SessionModelUpdatedListener {

    public GroupSessionModel sessionModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionModel = new GroupSessionModel();

        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    protected void initializePresenter() {
        presenter = new GroupSessionRegisterPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new GcRegistrationStageFragment(this, sessionModel);
    }

    @Override
    protected Fragment[] getOtherFragments() {
        Fragment[] fragments = new Fragment[2];
        fragments[0] = new SelectChildForGroupSessionRegisterFragment(this, sessionModel);
        fragments[1] = new GcFinalStepFragment(this, sessionModel);
        return fragments;
    }

    @Override
    public void onSessionModelUpdated(GroupSessionModel updatedSessionModel) {
        this.sessionModel = updatedSessionModel;
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {//do nothing
    }

    @Override
    public void startFormActivity(JSONObject form) {

    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Utils.metadata().familyRegister.config);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            bottomNavigationView.getMenu().removeItem(R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(R.id.action_search);
            bottomNavigationView.getMenu().removeItem(R.id.action_library);
            bottomNavigationView.getMenu().removeItem(R.id.action_job_aids);
            bottomNavigationView.getMenu().removeItem(R.id.action_scan_qr);

            bottomNavigationView.inflateMenu(R.menu.gc_bottom_nav);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            FamilyBottomNavigationListener familyBottomNavigationListener = new FamilyBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(familyBottomNavigationListener);

        }
    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

}
