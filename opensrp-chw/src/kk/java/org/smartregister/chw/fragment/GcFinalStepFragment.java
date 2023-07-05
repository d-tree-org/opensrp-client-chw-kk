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

import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.model.GroupSessionModel;

/**
 * Author issyzac on 04/07/2023
 */
public class GcFinalStepFragment extends BaseGroupSessionRegisterFragment {

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

    GroupSessionModel sessionModel;

    private String activitiesTookPlace;
    private boolean teachingLearningMaterialsUsed;
    private String unguidedFreePlay;
    private boolean anyDifficultActivities = false;
    private String listOfDifficultActivities = "";
    private String caregiversEncouraging;
    private String caregiversBroughtMaterials;
    private String topicsCovered;

    private JSONObject sessionObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //todo: to be changed to the same model started on previous steps
        sessionModel = new GroupSessionModel();

    }

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
        encouragingCaregiversYesAllActivities.setOnCheckedChangeListener(new CaregiversEncouragingListener(view, selectedOption -> caregiversEncouraging = selectedOption));

        encouragingCaregiversYesMostActivities = view.findViewById(R.id.yes_most);
        encouragingCaregiversYesMostActivities.setOnCheckedChangeListener(new CaregiversEncouragingListener(view, selectedOption -> caregiversEncouraging = selectedOption));

        encouragingCaregiversYesSomeActivities = view.findViewById(R.id.yes_some);
        encouragingCaregiversYesSomeActivities.setOnCheckedChangeListener(new CaregiversEncouragingListener(view, selectedOption -> caregiversEncouraging = selectedOption));

        encouragingCaregiversNoneOrFew = view.findViewById(R.id.none_or_very_few);
        encouragingCaregiversNoneOrFew.setOnCheckedChangeListener(new CaregiversEncouragingListener(view, selectedOption -> caregiversEncouraging = selectedOption));


        //Brought Materials Checkboxes
        caregiversBroughtMaterialsYesAll = view.findViewById(R.id.materials_yes_all_or_nearly_all);
        caregiversBroughtMaterialsYesAll.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        caregiversBroughtMaterialsYesMost = view.findViewById(R.id.materials_yes_most);
        caregiversBroughtMaterialsYesMost.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        caregiversBroughtMaterialsYesSome = view.findViewById(R.id.materials_yes_some);
        caregiversBroughtMaterialsYesSome.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        caregiversBroughtMaterialsNoneOrFew = view.findViewById(R.id.materials_none_or_very_few);
        caregiversBroughtMaterialsNoneOrFew.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        difficultActivitiesList = view.findViewById(R.id.llDifficultActivitiesList);
        activitiesDifficultYes = view.findViewById(R.id.difficult_activities_yes);
        activitiesDifficultNo = view.findViewById(R.id.difficult_activities_no);

        activitiesDifficultNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesDifficultYes.setChecked(false);
                    anyDifficultActivities = false;
                }
            }
        });

        activitiesDifficultYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesDifficultNo.setChecked(false);
                    anyDifficultActivities = true;
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

    private boolean validateFields(){
        return false;
    }

    private void createSessionObject(){

    }

    private static class CaregiversMaterialsListener implements CompoundButton.OnCheckedChangeListener {

        View parentView;
        CheckBox yesAll;
        CheckBox yesMost;
        CheckBox yesSome;
        CheckBox none;

        SelectedOption selectedOption;

        CaregiversMaterialsListener(View parentView, SelectedOption so){
            this.parentView = parentView;
            this.selectedOption = so;
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
                    selectedOption.onSelection("Yes all or Nearly all");
                }else if (compoundButton.getId() == R.id.materials_yes_most){
                    yesAll.setChecked(false);
                    yesSome.setChecked(false);
                    none.setChecked(false);
                    selectedOption.onSelection("Yes most");
                }else if (compoundButton.getId() == R.id.materials_yes_some){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    none.setChecked(false);
                    selectedOption.onSelection("Yes some");
                }else if (compoundButton.getId() == R.id.materials_none_or_very_few){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    yesSome.setChecked(false);
                    selectedOption.onSelection("None or very few");
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
        SelectedOption selectedOption;

        CaregiversEncouragingListener(View parentView, SelectedOption selectedOption){
            this.parentView = parentView;
            this.selectedOption = selectedOption;
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
                    selectedOption.onSelection("Yes all or nearly all");
                }else if (compoundButton.getId() == R.id.yes_most){
                    yesAll.setChecked(false);
                    yesSome.setChecked(false);
                    none.setChecked(false);
                    selectedOption.onSelection("Yes most");
                }else if (compoundButton.getId() == R.id.yes_some){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    none.setChecked(false);
                    selectedOption.onSelection("Yes some");
                }else if (compoundButton.getId() == R.id.none_or_very_few){
                    yesAll.setChecked(false);
                    yesMost.setChecked(false);
                    yesSome.setChecked(false);
                    selectedOption.onSelection("None or very few");
                }
            }
        }
    }

    interface SelectedOption {
        void onSelection(String selectedOption);
    }


}
