package org.smartregister.chw.util;

/**
 * Created by Kassim Sheghembe on 2022-02-17
 */
public class KkConstants extends Constants{

    public static class KKJSON_FORM_CONSTANT {

        public static class KKCHILD_HOME_VISIT {

            public static final String CHILD_HV_BREASTFEEDING = "child_hv_breastfeeding";
            public static final String CHILD_HV_MALARIA_PREVENTION = "child_hv_malaria_prevention";
            public static final String CHILD_HV_PLAY_ASSESSMENT_COUNSELLING = "child_hv_play_assessment_counselling";
            public static final String CHILD_HV_PROBLEM_SOLVING = "child_hv_problem_solving";
            public static final String CHILD_HV_CAREGIVER_RESPONSIVENESS = "child_hv_caregiver_responsiveness";
            public static final String CHILD_HV_NEWBORN_CORD_CARE = "child_hv_newborn_cord_care";
            

            public static String getChildHvBreastfeeding() {
                return Utils.getLocalForm(CHILD_HV_BREASTFEEDING);
            }

            public static String getChildHvMalariaPrevention() {
                return Utils.getLocalForm(CHILD_HV_MALARIA_PREVENTION);
            }

            public static String getChildHvPlayAssessmentCounselling() {
                return Utils.getLocalForm(CHILD_HV_PLAY_ASSESSMENT_COUNSELLING);
            }

            public static String getChildHvProblemSolving() {
                return Utils.getLocalForm(CHILD_HV_PROBLEM_SOLVING);
            }

            public static String getChildHvCaregiverResponsiveness() {
                return Utils.getLocalForm(CHILD_HV_CAREGIVER_RESPONSIVENESS);
            }

            public static String getChildHvNewbornCordCare() {
                return Utils.getLocalForm(CHILD_HV_NEWBORN_CORD_CARE);
            }

        }

    }

}
