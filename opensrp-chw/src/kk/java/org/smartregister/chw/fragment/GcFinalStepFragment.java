package org.smartregister.chw.fragment;

import android.content.Context;
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
public class GcFinalStepFragment extends BaseGroupSessionRegisterFragment implements CompoundButton.OnCheckedChangeListener {

    LinearLayout difficultActivitiesList;

    CheckBox activitiesDifficultYes;
    CheckBox activitiesDifficultNo;

    CheckBox encouragingCaregiversYesAllActivities;
    CheckBox encouragingCaregiversYesMostActivities;
    CheckBox encouragingCaregiversYesSomeActivities;
    CheckBox encouragingCaregiversNoneOrFew;

    CheckBox caregiversBroughtMaterialsYesAll;
    CheckBox caregiversBroughtMaterialsYesMost;
    CheckBox caregiversBroughtMaterialsYesSome;
    CheckBox caregiversBroughtMaterialsNoneOrFew;

    CheckBox materialsScheduledUsedYes;
    CheckBox materialsScheduledUsedNo;

    CaregiversMaterialsListener caregiversMaterialsListener;
    CaregiversEncouragingListener caregiversEncouragingListener;

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

        caregiversMaterialsListener = new CaregiversMaterialsListener(view);
        caregiversEncouragingListener = new CaregiversEncouragingListener(view);

        //Materials used checkboxes
        materialsScheduledUsedNo = view.findViewById(R.id.teching_materials_used_no);
        materialsScheduledUsedNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    materialsScheduledUsedYes.setChecked(false);
            }
        });

        materialsScheduledUsedYes = view.findViewById(R.id.teching_materials_used_yes);
        materialsScheduledUsedYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        materialsScheduledUsedNo.setChecked(false);
            }
        });

        //Caregivers Encouraging Checkboxes
        encouragingCaregiversYesAllActivities = view.findViewById(R.id.yes_all_or_nearly_all);
        encouragingCaregiversYesAllActivities.setOnCheckedChangeListener(caregiversEncouragingListener);

        encouragingCaregiversYesMostActivities = view.findViewById(R.id.yes_most);
        encouragingCaregiversYesMostActivities.setOnCheckedChangeListener(caregiversEncouragingListener);

        encouragingCaregiversYesSomeActivities = view.findViewById(R.id.yes_some);
        encouragingCaregiversYesSomeActivities.setOnCheckedChangeListener(caregiversEncouragingListener);

        encouragingCaregiversNoneOrFew = view.findViewById(R.id.none_or_very_few);
        encouragingCaregiversNoneOrFew.setOnCheckedChangeListener(caregiversEncouragingListener);


        //Brought Materials Checkboxes
        caregiversBroughtMaterialsYesAll = view.findViewById(R.id.materials_yes_all_or_nearly_all);
        caregiversBroughtMaterialsYesAll.setOnCheckedChangeListener(caregiversMaterialsListener);

        caregiversBroughtMaterialsYesMost = view.findViewById(R.id.materials_yes_most);
        caregiversBroughtMaterialsYesMost.setOnCheckedChangeListener(caregiversMaterialsListener);

        caregiversBroughtMaterialsYesSome = view.findViewById(R.id.materials_yes_some);
        caregiversBroughtMaterialsYesSome.setOnCheckedChangeListener(caregiversMaterialsListener);

        caregiversBroughtMaterialsNoneOrFew = view.findViewById(R.id.materials_none_or_very_few);
        caregiversBroughtMaterialsNoneOrFew.setOnCheckedChangeListener(caregiversMaterialsListener);

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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b){
            if (compoundButton.getId() == R.id.yes_all_or_nearly_all){

            }else if (compoundButton.getId() == R.id.yes_all_or_nearly_all){

            }
            else if (compoundButton.getId() == R.id.yes_all_or_nearly_all){

            }
            else if (compoundButton.getId() == R.id.yes_all_or_nearly_all){

            }
        }
    }

    private static class CaregiversMaterialsListener implements CompoundButton.OnCheckedChangeListener {

        View parentView;
        CheckBox yesAll;
        CheckBox yesMost;
        CheckBox yesSome;
        CheckBox none;

        CaregiversMaterialsListener(View parentView){
            this.parentView = parentView;
            yesAll = (CheckBox) parentView.findViewById(R.id.materials_yes_all_or_nearly_all);
            yesMost = (CheckBox) parentView.findViewById(R.id.materials_yes_most);
            yesSome = (CheckBox) parentView.findViewById(R.id.materials_yes_some);
            none = (CheckBox) parentView.findViewById(R.id.materials_none_or_very_few);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b){
                if (compoundButton.getId() == R.id.materials_yes_all_or_nearly_all){
                    yesMost.setChecked(false);
                    yesSome.setChecked(false);
                    none.setChecked(false);
                }else if (compoundButton.getId() == R.id.materials_yes_most){
                    yesAll.setChecked(false);
                    yesSome.setChecked(false);
                    none.setChecked(false);
                }else if (compoundButton.getId() == R.id.materials_yes_some){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    none.setChecked(false);
                }else if (compoundButton.getId() == R.id.materials_none_or_very_few){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    yesSome.setChecked(false);
                }
            }
        }
    }

    private static class CaregiversEncouragingListener implements CompoundButton.OnCheckedChangeListener {

        View parentView;
        CheckBox yesAll;
        CheckBox yesMost;
        CheckBox yesSome;
        CheckBox none;

        CaregiversEncouragingListener(View parentView){
            this.parentView = parentView;
            yesAll = (CheckBox) parentView.findViewById(R.id.yes_all_or_nearly_all);
            yesMost = (CheckBox) parentView.findViewById(R.id.yes_most);
            yesSome = (CheckBox) parentView.findViewById(R.id.yes_some);
            none = (CheckBox) parentView.findViewById(R.id.none_or_very_few);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b){
                if (compoundButton.getId() == R.id.yes_all_or_nearly_all){
                    yesMost.setChecked(false);
                    yesSome.setChecked(false);
                    none.setChecked(false);
                }else if (compoundButton.getId() == R.id.yes_most){
                    yesAll.setChecked(false);
                    yesSome.setChecked(false);
                    none.setChecked(false);
                }else if (compoundButton.getId() == R.id.yes_some){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    none.setChecked(false);
                }else if (compoundButton.getId() == R.id.none_or_very_few){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    yesSome.setChecked(false);
                }
            }
        }
    }

}
