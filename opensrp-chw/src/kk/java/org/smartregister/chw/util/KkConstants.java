package org.smartregister.chw.util;

/**
 * Created by Kassim Sheghembe on 2022-02-17
 */
public class KkConstants extends Constants{

    public static class KKJSON_FORM_CONSTANT {

        public static class KKCHILD_HOME_VISIT {

            public static final String CHILD_HV_BREASTFEEDING = "child_hv_breastfeeding";
            public static final String CHILD_HV_ESSENTIAL_CARE_INTRODUCTION = "child_hv_new_born_care_intro";
            public static final String CHILD_HV_MALARIA_PREVENTION = "child_hv_malaria_prevention";
            public static final String CHILD_HV_PLAY_ASSESSMENT_COUNSELLING = "child_hv_play_assessment_counselling";
            public static final String CHILD_HV_PROBLEM_SOLVING = "child_hv_problem_solving";
            public static final String CHILD_HV_CAREGIVER_RESPONSIVENESS = "child_hv_caregiver_responsiveness";
            public static final String CHILD_HV_NEWBORN_CORD_CARE = "child_hv_newborn_cord_care";
            public static final String CHILD_HV_IMMUNIZATIONS = "child_hv_immunizations";

            public static String getChildHvBreastfeeding() {
                return Utils.getLocalForm(CHILD_HV_BREASTFEEDING);
            }

            public static String getChildHvEssentialCareIntroductionForm() {
                return Utils.getLocalForm(CHILD_HV_ESSENTIAL_CARE_INTRODUCTION);
            }

            public static String getChildHvImmunizationsForm() {
                return Utils.getLocalForm(CHILD_HV_IMMUNIZATIONS);
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

        public static class KK_PNC_HOME_VISIT {
            public static final String PNC_HV_DANGER_SIGNS = "kk_pnc_hv_danger_signs";
            public static final String PNC_HV_MATERNAL_NUTRITION = "pnc_hv_maternal_nutrition";
            public static final String PNC_HV_HIV_AIDS_GENERAL_INFO = "pnc_hv_hiv_aids_general_info";
            public static final String PNC_HV_LAM = "pnc_hv_lam";
            public static final String PNC_MALARIA_PREVENTION = "pnc_malaria_prevention";

            public static String getPncHvDangerSigns() {
                return Utils.getLocalForm(PNC_HV_DANGER_SIGNS);
            }

            public static String getPncHvMaternalNutrition() {
                return Utils.getLocalForm(PNC_HV_MATERNAL_NUTRITION);
            }

            public static String getPncHvHivAidsGeneralInfo() {
                return Utils.getLocalForm(PNC_HV_HIV_AIDS_GENERAL_INFO);
            }

            public static String getPncHvLam() {
                return Utils.getLocalForm(PNC_HV_LAM);
            }

            public static String getPncMalariaPrevention() {
                return Utils.getLocalForm(PNC_MALARIA_PREVENTION);
            }

        }
    }

    public static final class EventType {
        public static final String ESSENTIAL_NEW_BORN_CARE_INTRO = "Essential New Born Care: Introduction";
        public static final String IMMUNIZATIONS = "Immunizations";
    }

}
