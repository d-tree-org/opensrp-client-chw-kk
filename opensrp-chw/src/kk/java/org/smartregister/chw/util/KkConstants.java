package org.smartregister.chw.util;

/**
 * Created by Kassim Sheghembe on 2022-02-17
 */
public class KkConstants extends Constants{

    public static class KKJSON_FORM_CONSTANT {

        public static class KKCHILD_HOME_VISIT {

            public static final String CHILD_HV_BREASTFEEDING = "child_hv_breastfeeding";
            public static final String CHILD_HV_ESSENTIAL_CARE_INTRODUCTION = "child_hv_new_born_care_intro";

            public static String getChildHvBreastfeeding() {
                return Utils.getLocalForm(CHILD_HV_BREASTFEEDING);
            }

            public static String getChildHvEssentialCareIntroductionForm() {
                return Utils.getLocalForm(CHILD_HV_ESSENTIAL_CARE_INTRODUCTION);
            }
        }

    }
    public static final class EventType {
        public static final String ESSENTIAL_NEW_BORN_CARE_INTRO = "Essential New Born Care: Introduction";
    }

}
