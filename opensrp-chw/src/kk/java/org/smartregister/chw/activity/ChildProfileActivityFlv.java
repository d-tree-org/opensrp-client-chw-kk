package org.smartregister.chw.activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChildProfileActivityFlv extends DefaultChildProfileActivityFlv {

    @Override
    public void setLastVisitRowView (String days, RelativeLayout layoutLastVisitRow, View viewLastVisitRow, TextView textViewLastVisit, Context context){
        //Remove the last visit days row
        layoutLastVisitRow.setVisibility(View.GONE);
        viewLastVisitRow.setVisibility(View.GONE);
    }

}
