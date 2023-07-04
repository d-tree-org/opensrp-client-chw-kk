package org.smartregister.chw.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.smartregister.chw.R;

/**
 * Author issyzac on 04/07/2023
 */
public class GcFinalStepFragment extends BaseGroupSessionRegisterFragment {

    LinearLayout difficultActivitiesList;

    CheckBox activitiesDifficultYes;
    CheckBox activitiesDifficultNo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = rootView;

        return rootView;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        difficultActivitiesList = view.findViewById(R.id.llDifficultActivitiesList);
        activitiesDifficultYes = view.findViewById(R.id.difficult_activities_yes);
        activitiesDifficultNo = view.findViewById(R.id.difficult_activities_no);

        activitiesDifficultNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesDifficultYes.setChecked(false);
                }
            }
        });

        activitiesDifficultYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesDifficultNo.setChecked(false);
                }
                difficultActivitiesLayoutController(b);
            }
        });

    }

    private void difficultActivitiesLayoutController(boolean isDifficult){
        difficultActivitiesList.setVisibility(isDifficult ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_group_session_step_three;
    }

    @Override
    public String getSessionDetails() {
        return null;
    }
}
