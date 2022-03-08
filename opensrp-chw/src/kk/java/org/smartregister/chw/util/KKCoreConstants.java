package org.smartregister.chw.util;

import org.smartregister.chw.core.utils.CoreConstants;

public class KKCoreConstants extends CoreConstants {

    public static final class CHILD_HV{

        public static final String TODDLER_DANGERP_SIGN = "child_hv_toddler_danger_sign";

        public static String getToddlerDangerSign(){
            return Utils.getLocalForm(TODDLER_DANGERP_SIGN, JSON_FORM.locale, JSON_FORM.assetManager);
        }

    }

    public static final class ANC_PREGNANCY_OUTCOME {
        public static final String KK_ANC_PREGNANCY_OUTCOME = "kk_anc_pregnancy_outcome";

        public static String getPregnancyOutcome() {
            return Utils.getLocalForm(KK_ANC_PREGNANCY_OUTCOME, JSON_FORM.locale, JSON_FORM.assetManager);
        }

    }

}
