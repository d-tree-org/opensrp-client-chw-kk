package org.smartregister.chw.holders;

import android.view.View;

import com.nerdstone.neatformcore.views.widgets.CheckBoxNFormView;
import org.smartregister.chw.R;

import org.smartregister.chw.core.holders.RegisterViewHolder;

/**
 * Created by Kassim Sheghembe on 2023-06-28
 */
public class SelectChildGSViewHolder extends RegisterViewHolder {
    public CheckBoxNFormView checkBoxNFormView;

    public SelectChildGSViewHolder(View itemView) {
        super(itemView);
        checkBoxNFormView = itemView.findViewById(R.id.chk_select_child);
    }
}
