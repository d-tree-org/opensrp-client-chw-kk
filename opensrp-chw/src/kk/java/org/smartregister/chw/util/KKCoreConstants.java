package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.CoreConstants;

public class KKCoreConstants extends CoreConstants {

    public static final class CHILD_HV{

        public static final String TODDLER_DANGERP_SIGN = "child_hv_toddler_danger_sign";

        public static String getToddlerDangerSign(){
            return Utils.getLocalForm(TODDLER_DANGERP_SIGN, JSON_FORM.locale, JSON_FORM.assetManager);
        }

    }

    public static final class ChildVisitEvents {
        public static final String TODDLER_DANGER_SIGN = "Toddler danger sign";
        public static final String CCD_INTRODUCTION = "Home Visit - CCD Introduction";
        public static final String CCD_DEVELOPMENT_SCREENING = "Home Visit - CCD Development Screening";
        public static final String CCD_CHILD_DISCIPLINE = "Home Visit - CCD Child Discipline";
    }
}
