package org.smartregister.chw.util;

/**
 * Created by Kassim Sheghembe on 2022-02-17
 */
public class KkConstants extends Constants{

    public static class KKJSON_FORM_CONSTANT {

        public static class KKCHILD_HOME_VISIT {

            public static final String CHILD_HV_BREASTFEEDING = "child_hv_breastfeeding";
            public static final String CHILD_HV_MALARIA_PREVENTION = "child_hv_malaria_prevention";

            public static String getChildHvBreastfeeding() {
                return Utils.getLocalForm(CHILD_HV_BREASTFEEDING);
            }

            public static String getChildHvMalariaPrevention() {
                return Utils.getLocalForm(CHILD_HV_MALARIA_PREVENTION);
            }

        }

    }

}
