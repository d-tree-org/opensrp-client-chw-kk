package org.smartregister.chw.model;

import org.smartregister.chw.R;
import org.smartregister.chw.core.model.NavigationOption;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.Constants;

import java.util.List;

public class NavigationModelFlv extends DefaultNavigationModelFlv {

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0) {
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_families, R.mipmap.sidemenu_families_active, R.string.menu_all_families, Constants.DrawerMenu.ALL_FAMILIES, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_anc, R.mipmap.sidemenu_anc_active, R.string.menu_anc, Constants.DrawerMenu.ANC, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_pnc, R.mipmap.sidemenu_pnc_active, R.string.menu_pnc, Constants.DrawerMenu.PNC, 0));
            navigationOptions.add(new NavigationOption(R.mipmap.sidemenu_children, R.mipmap.sidemenu_children_active, R.string.menu_child_clients, Constants.DrawerMenu.CHILD_CLIENTS, 0));
            navigationOptions.add(new NavigationOption(R.drawable.ic_group, R.drawable.ic_group_selected, R.string.menu_group_sessions, Constants.DrawerMenu.GROUP_SESSION, 0));
        }

        return navigationOptions;
    }

}
