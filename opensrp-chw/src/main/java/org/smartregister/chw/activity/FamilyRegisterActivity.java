package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreFamilyRegisterActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.FamilyRegisterFragment;
import org.smartregister.chw.listener.ChwBottomNavigationListener;
import org.smartregister.chw.model.FamilyRegisterModel;
import org.smartregister.chw.presenter.FamilyRegisterPresenter;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Map;

import timber.log.Timber;

public class FamilyRegisterActivity extends CoreFamilyRegisterActivity {

    public static void startFamilyRegisterForm(Activity activity) {
        Intent intent = new Intent(activity, FamilyRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTION.START_REGISTRATION);
        activity.startActivity(intent);
    }

    public static void registerBottomNavigation(
            BottomNavigationHelper bottomNavigationHelper, BottomNavigationView bottomNavigationView, Activity activity
    ) {
        Utils.setupBottomNavigation(bottomNavigationHelper, bottomNavigationView, new ChwBottomNavigationListener(activity));
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();
        FamilyRegisterActivity.registerBottomNavigation(bottomNavigationHelper, bottomNavigationView, this);
    }

    @Override
    protected void initializePresenter() {
        presenter = new FamilyRegisterPresenter(this, new FamilyRegisterModel());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        ChwApplication.getInstance().notifyAppContextChange(); // initialize the language (bug in translation)

        action = getIntent().getStringExtra(Constants.ACTIVITY_PAYLOAD.ACTION);
        if (action != null && action.equals(Constants.ACTION.START_REGISTRATION)) {
            startRegistration();
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new FamilyRegisterFragment();
    }

    @Override
    public void startFormActivity(String s, String s1, Map<String, String> map) {
        Timber.v("startFormActivity");
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyFormActivity);
        intent.putExtra("json", jsonForm.toString());
        Form form = new Form();
        form.setName(this.getString(R.string.add_fam));
        form.setActionBarBackground(R.color.family_actionbar);
        form.setGreyOutSaveWhenFormInvalid(true);
        form.setNavigationBackground(R.color.family_navigation);
        form.setHomeAsUpIndicator(R.mipmap.ic_cross_white);
        form.setPreviousLabel(this.getResources().getString(R.string.back));
        intent.putExtra("form", form);
        this.startActivityForResult(intent, 2244);
    }
}