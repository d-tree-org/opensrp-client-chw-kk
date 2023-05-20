package org.smartregister.chw.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.chw.fragment.GroupSessionRegisterFragment;
import org.smartregister.chw.presenter.GroupSessionRegisterPresenter;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;
import java.util.Map;

/**
 * Author issyzac on 18/05/2023
 */
public class GroupSessionRegisterActivity extends BaseRegisterActivity {
    @Override
    protected void initializePresenter() {
        presenter = new GroupSessionRegisterPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new GroupSessionRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String formName, String entityId, Map<String, String> metaData) {
        //Stub
    }

    @Override
    public void startFormActivity(JSONObject form) {
        //Stub
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        //Stub
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    public void startRegistration() {
        //Stub
    }
}
