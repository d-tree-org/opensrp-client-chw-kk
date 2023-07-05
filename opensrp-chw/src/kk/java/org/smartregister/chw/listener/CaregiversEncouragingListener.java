package org.smartregister.chw.listener;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.smartregister.chw.R;
import org.smartregister.chw.fragment.GcFinalStepFragment;

/**
 * Author issyzac on 05/07/2023
 */
public class CaregiversEncouragingListener implements CompoundButton.OnCheckedChangeListener {

    View parentView;
    CheckBox yesAll;
    CheckBox yesMost;
    CheckBox yesSome;
    CheckBox none;
    GcFinalStepFragment.SelectedOption selectedOption;

    public CaregiversEncouragingListener(View parentView, GcFinalStepFragment.SelectedOption selectedOption){
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