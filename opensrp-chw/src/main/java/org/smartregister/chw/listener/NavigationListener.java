package org.smartregister.chw.listener;

import static org.smartregister.chw.util.KkConstants.KKDrawerMenu.GROUP_SESSION;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import org.smartregister.chw.core.adapter.NavigationAdapter;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.KkConstants;
import org.smartregister.util.Utils;

/**
 * Author issyzac on 18/05/2023
 */
public class NavigationListener extends org.smartregister.chw.core.listener.NavigationListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter adapter) {
        super(activity, adapter);
        this.activity = activity;
        this.navigationAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof String) {
            String tag = (String) v.getTag();
            switch (tag) {
                case GROUP_SESSION:
                    startRegisterActivity(getActivity(KkConstants.REGISTERED_ACTIVITIES.GROUP_SESSION));
                    break;
                default:
                    super.onClick(v);
                    break;
            }
            navigationAdapter.setSelectedView(tag);
        }
    }

    private Class getActivity(String key) {
        return navigationAdapter.getRegisteredActivities().get(key);
    }

}
