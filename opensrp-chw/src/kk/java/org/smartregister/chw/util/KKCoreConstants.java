package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.CoreConstants;

public class KKCoreConstants extends CoreConstants {

    public static final class CHILD_HV{

        public static final String TODDLER_DANGERP_SIGN = "child_hv_toddler_danger_sign";

        public static String getToddlerDangerSign(){
            return Utils.getLocalForm(TODDLER_DANGERP_SIGN, JSON_FORM.locale, JSON_FORM.assetManager);
        }

    }

}
