package org.smartregister.chw.listener;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.GcFinalStepFragment;

/**
 * Author issyzac on 05/07/2023
 */
public class CaregiversMaterialsListener implements CompoundButton.OnCheckedChangeListener {

    View parentView;
    CheckBox yesAll;
    CheckBox yesMost;
    CheckBox yesSome;
    CheckBox none;

    GcFinalStepFragment.SelectedOption selectedOption;

    public CaregiversMaterialsListener(View parentView, GcFinalStepFragment.SelectedOption so){
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