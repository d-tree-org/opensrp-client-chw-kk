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
            public static final String PNC_MOTHER_CARE = "pnc_hv_postpartum_care_for_mother";
            public static final String PNC_HV_POSTPARTUM_FAMILY_PLANNING = "pnc_hv_postpartum_family_planning";
            public static final String FOLLOW_UP_HIV_EXPOSED_INFANT = "pnc_hv_hiv_exposed_infant";
            public static final String PNC_HV_POSTPARTUM_PHYSIOLOGICAL_CHANGES = "pnc_hv_postpartum_psychological_changes";
            public static final String PNC_HV_INFECTION_PREVENTION_CONTROL = "pnc_hv_infection_prevention_control";
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

            public static String getPncMotherCare(){
                return Utils.getLocalForm(PNC_MOTHER_CARE);
            }

            public static String getPncHvPostpartumFamilyPlanning(){
                return Utils.getLocalForm(PNC_HV_POSTPARTUM_FAMILY_PLANNING);
            }

            public static String getFollowUpHivExposedInfant(){
                return Utils.getLocalForm(FOLLOW_UP_HIV_EXPOSED_INFANT);
            }

            public static String getPncHvPostpartumPhysiologicalChanges(){
                return Utils.getLocalForm(PNC_HV_POSTPARTUM_PHYSIOLOGICAL_CHANGES);
            }

            public static String getPncHvInfectionPreventionControl() {
                return Utils.getLocalForm(PNC_HV_INFECTION_PREVENTION_CONTROL);
            }

            public static String getPncMalariaPrevention() {
                return Utils.getLocalForm(PNC_MALARIA_PREVENTION);
            }

        }

        public static class GROUP_SESSION_FORM {
            public static final String SESSION_ID = "session_id";
            public static final String SESSION_DATE = "session_date";
            public static final String SESSION_PLACE = "session_place";
            public static final String SESSION_DURATION = "session_duration";
        }

    }

    public static final class EventType {
        public static final String ESSENTIAL_NEW_BORN_CARE_INTRO = "Essential New Born Care: Introduction";
        public static final String IMMUNIZATIONS = "Immunizations";
    }

    public static final class TABLES {
        public static final String CHILD_IMMUNIZATIONS = "ec_child_immunizations";
        public static final String GROUP_SESSION = "ec_group_session";
    }

    public static class GCActivities {
        public static final String WELCOME_AND_FREE_PLAY = "opening_and_free_play";
        public static final String OPENING_SONG = "opening_song";
        public static final String REVIEW_PREVIOUS_WEEK = "review_of_the_week";
        public static final String ACTIVITY_1 = "activity_1";
        public static final String ACTIVITY_2 = "activity_2";
        public static final String RECAP_SESSION = "recap_session";
        public static final String CLOSING_SONG = "closing_song";
    }

    public static class GCUnguidedFreePlay {
        public static final String MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS = "most_children_are_playing_with_materials";
        public static final String ONE_ADULT_IS_AVAILABLE = "one_adult_is_available";
    }

    public static class GCCoveredTopics {
        public final String LANGUAGE = "language";
        public final String COGNITIVE = "cognitive";
        public final String SOCIAL_EMOTIONAL = "social_emotional";
        public final String CREATIVITY = "creativity";
        public final String FORMAL_TEACHING = "formal_teaching";
    }

}
