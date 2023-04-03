package org.smartregister.chw.util;

import org.smartregister.chw.R;

import java.util.HashMap;

/**
 * Created by Kassim Sheghembe on 2023-03-30
 */
public class BangoKititaPages {

    public static final HashMap<String, String> PAGES_COMMUNICATION_ASSESSMENT = new HashMap<>();
    public static final String DAY_8 = "Day 8";
    public static final String WEEK_3 = "Week 3";
    public static final String WEEK_5 = "Week 5";
    public static final String WEEK_8 = "Week 8";
    public static final String MONTH_3 = "Month 3";
    public static final String MONTH_4 = "Month 4";
    public static final String MONTH_5 = "Month 5";
    public static final String MONTH_6 = "Month 6";
    public static final String MONTH_7 = "Month 7";
    public static final String MONTH_8 = "Month 8";
    public static final String MONTH_9 = "Month 9";
    public static final String MONTH_10 = "Month 10";
    public static final String MONTH_11 = "Month 11";
    public static final String MONTH_12 = "Month 12";

    // Pages are comma separated, we can use that to split and construct a page reference as
    // follow if it is a single page reference then .split of the page will have a length of
    // 1 in which case we can format a string as (Page %s) but if the length is 2 we can have
    // the formatting as (Page %s and Page %s) and pass the page numbers
    static {
        PAGES_COMMUNICATION_ASSESSMENT.put(DAY_8, "15");
        PAGES_COMMUNICATION_ASSESSMENT.put(WEEK_3, "23");
        PAGES_COMMUNICATION_ASSESSMENT.put(WEEK_5, "25");
        PAGES_COMMUNICATION_ASSESSMENT.put(WEEK_8, "29");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_3, "35");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_4, "41");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_5, "47");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_6, "53,55");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_7, "63");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_8, "69");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_9, "71,73");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_10, "77,81");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_11, "83,87");
        PAGES_COMMUNICATION_ASSESSMENT.put(MONTH_12, "89,91");
    }

    public static String getBangoKititaPageCommunicatinAssessment(String childAge, android.content.Context context) {
        if (childAge != null) {
            if (!childAge.contains("y")) {
                if (childAge.contains("m")) {
                    // For month the difference between two consecutive months is 1 month so child
                    // age in Month is sufficient to return the page as defined
                    String monthsAge = "Month " + childAge.substring(0, childAge.indexOf("m"));
                    String pageReference = PAGES_COMMUNICATION_ASSESSMENT.get(monthsAge);
                    if (pageReference != null) {
                        String[] pages = pageReference.split(",");
                        return pages.length == 1 ?
                                String.format(context.getString(R.string.bango_kitita_page_number_one_page), pages[0]) :
                                String.format(context.getString(R.string.bango_kitita_page_number_two_pages), pages[0], pages[1]);
                    }
                } else if (childAge.contains("w")) {
                    // For weeks we have to check if it is 3 weeks or 5 weeks or 8 weeks
                    String weekAge = childAge.substring(0, childAge.indexOf("w"));
                    int childAgeInWeeks = Integer.parseInt(weekAge);
                    if (childAgeInWeeks == 2) {
                        return String.format(context.getString(R.string.bango_kitita_page_number_one_page), PAGES_COMMUNICATION_ASSESSMENT.get(DAY_8));
                    }

                    if (childAgeInWeeks >= 3 && childAgeInWeeks < 5) {
                        // Within the 3 weeks visit range
                        return String.format(context.getString(R.string.bango_kitita_page_number_one_page), PAGES_COMMUNICATION_ASSESSMENT.get(WEEK_3));
                    } else if (childAgeInWeeks >= 5 && childAgeInWeeks < 8) {
                        return String.format(context.getString(R.string.bango_kitita_page_number_one_page), PAGES_COMMUNICATION_ASSESSMENT.get(WEEK_5));
                    } else if (childAgeInWeeks >= 8 && childAgeInWeeks < 14){
                        return String.format(context.getString(R.string.bango_kitita_page_number_one_page), PAGES_COMMUNICATION_ASSESSMENT.get(WEEK_8));
                    }
                } else if (childAge.contains("d")) {
                    String dayAge = childAge.substring(0, childAge.indexOf("d"));
                    int childAgeInDays = Integer.parseInt(dayAge);
                    if (childAgeInDays >= 8 && childAgeInDays < 14) {
                        return String.format(context.getString(R.string.bango_kitita_page_number_one_page), PAGES_COMMUNICATION_ASSESSMENT.get(DAY_8));
                    }
                }
            }
        }
        return "";
    }

}
