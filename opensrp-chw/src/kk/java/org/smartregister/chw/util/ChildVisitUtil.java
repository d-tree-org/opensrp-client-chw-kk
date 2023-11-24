package org.smartregister.chw.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.chw.R;
import org.smartregister.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Author: Isaya Mollel (@issyzac)
 */

public class ChildVisitUtil {

    /**
     * First Visit should be within 24 hours after birth
     *
     * @return TRUE if Anytime between 24 hours and the third day
     */
    public static boolean isFirstVisit(Date dob){
        LocalDate dateOfBirth = DateUtil.getLocalDate(dob.toString());
        LocalDate today = DateUtil.today();
        int daysDifference = DateUtil.dayDifference(dateOfBirth, today);
        if (daysDifference < 3 ){
            return true;
        }else
            return false;
    }

    /**
     * Third day after birth
     * @return TRUE if Anytime between the 3rd and the 8th day
     */
    public static boolean isSecondVisit(Date dob){
        LocalDate dateOfBirth = DateUtil.getLocalDate(dob.toString());
        LocalDate today = DateUtil.today();
        int daysDifference = DateUtil.dayDifference(dateOfBirth, today);
        if (daysDifference >= 3 && daysDifference < 8){
            return true;
        }else
            return false;
    }

    /**
     * Eighth day after birth
     * @return TRUE if Anytime between the 8th day and the 3rd week
     */
    public static boolean isThirdVisit(Date dob){
        LocalDate dateOfBirth = DateUtil.getLocalDate(dob.toString());
        LocalDate today = DateUtil.today();
        int daysDifference = DateUtil.dayDifference(dateOfBirth, today);
        int weekDifference = DateUtil.weekDifference(dateOfBirth, today);
        if (daysDifference >= 8 && weekDifference < 3 ){
            return true;
        }else
            return false;
    }

    /**
     * Third Week after birth
     * @return TRUE if Anytime between the 3rd and the 5th week
     */
    public static boolean isFourthVisit(Date dob){
        LocalDate dateOfBirth = DateUtil.getLocalDate(dob.toString());
        LocalDate today = DateUtil.today();
        int weekDifference = DateUtil.weekDifference(dateOfBirth, today);
        if (weekDifference >= 3 && weekDifference < 5 ){
            return true;
        }else
            return false;
    }

    /**
     * 5th week after birth
     * @return TRUE if Anytime between the 5th and the 7th week
     */
    public static boolean isFifthVisit(Date dob){
        LocalDate dateOfBirth = DateUtil.getLocalDate(dob.toString());
        LocalDate today = DateUtil.today();
        int weekDifference = DateUtil.weekDifference(dateOfBirth, today);
        if (weekDifference >= 5 && weekDifference < 7 ){
            return true;
        }else
            return false;
    }


    /**
     * Sixth week onwards needs to be once a month
     * @return TRUE if Anytime above the 7th week
     */
    public static boolean isMonthlyVisit(Date dob){
        LocalDate dateOfBirth = DateUtil.getLocalDate(dob.toString());
        LocalDate today = DateUtil.today();
        int weekDifference = DateUtil.weekDifference(dateOfBirth, today);
        if (weekDifference > 7){
            return true;
        }else
            return false;
    }

    public static String getTranslatedOtherFood(Context context, String other_food_child_feeds){
        try {
            if (StringUtils.isNotBlank(other_food_child_feeds)) {
                JSONArray arrayOfOtherFoods = new JSONArray(other_food_child_feeds);
                List<String> arrayOfOtherFoodName = new ArrayList<>();
                for (int i=0; i < arrayOfOtherFoods.length(); i++) {
                    arrayOfOtherFoodName.add(getTranslatedOtherFoodChoice(context,(String) arrayOfOtherFoods.get(i)));
                }

                return  String.join(", ", arrayOfOtherFoodName);
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return "";
    }

    private static String getTranslatedOtherFoodChoice(Context context, String choice) {
        if (choice.equalsIgnoreCase("infant_formula")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_infant_formula);
        } else if (choice.equalsIgnoreCase("plain_water")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_plain_water);
        } else if (choice.equalsIgnoreCase("juice")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_juice);
        } else if (choice.equalsIgnoreCase("clear_broth_soup")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_clear_broth_soup);
        } else if (choice.equalsIgnoreCase("milk_from_other_animals")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_milk_from_other_animals);
        } else if (choice.equalsIgnoreCase("soft_food")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_soft_food);
        } else if (choice.equalsIgnoreCase("something_else")) {
            return context.getString(R.string.newborn_care_breastfeeding_other_food_something_else);
        }
        return context.getString(R.string.newborn_care_breastfeeding_other_food_none);
    }
}
