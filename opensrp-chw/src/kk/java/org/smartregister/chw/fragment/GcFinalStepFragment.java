package org.smartregister.chw.fragment;

import static org.smartregister.chw.util.KkConstants.GCActivities.ACTIVITY_1;
import static org.smartregister.chw.util.KkConstants.GCActivities.ACTIVITY_2;
import static org.smartregister.chw.util.KkConstants.GCActivities.CLOSING_SONG;
import static org.smartregister.chw.util.KkConstants.GCActivities.OPENING_SONG;
import static org.smartregister.chw.util.KkConstants.GCActivities.RECAP_SESSION;
import static org.smartregister.chw.util.KkConstants.GCActivities.REVIEW_PREVIOUS_WEEK;
import static org.smartregister.chw.util.KkConstants.GCActivities.WELCOME_AND_FREE_PLAY;
import static org.smartregister.chw.util.KkConstants.GCCoveredTopics.TOPIC_COGNITIVE;
import static org.smartregister.chw.util.KkConstants.GCCoveredTopics.TOPIC_CREATIVITY;
import static org.smartregister.chw.util.KkConstants.GCCoveredTopics.TOPIC_FORMAL_TEACHING;
import static org.smartregister.chw.util.KkConstants.GCCoveredTopics.TOPIC_LANGUAGE;
import static org.smartregister.chw.util.KkConstants.GCCoveredTopics.TOPIC_SOCIAL_EMOTIONAL;
import static org.smartregister.chw.util.KkConstants.GCUnguidedFreePlay.MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS;
import static org.smartregister.chw.util.KkConstants.GCUnguidedFreePlay.ONE_ADULT_IS_AVAILABLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.GroupSessionRegisterActivity;
import org.smartregister.chw.listener.CaregiversEncouragingListener;
import org.smartregister.chw.listener.CaregiversMaterialsListener;
import org.smartregister.chw.listener.SessionModelUpdatedListener;
import org.smartregister.chw.model.GroupSessionModel;
import org.smartregister.chw.util.KkConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Author issyzac on 04/07/2023
 */
public class GcFinalStepFragment extends BaseGroupSessionRegisterFragment {

    LinearLayout difficultActivitiesList;

    CheckBox activitiesDifficultYes;
    CheckBox activitiesDifficultNo;

    CheckBox encouragingCaregiversYesAllActivities;
    CheckBox encouragingCaregiversYesMostActivities;
    CheckBox encouragingCaregiversYesSomeActivities;
    CheckBox encouragingCaregiversNoneOrFew;

    CheckBox caregiversBroughtMaterialsYesAll;
    CheckBox caregiversBroughtMaterialsYesMost;
    CheckBox caregiversBroughtMaterialsYesSome;
    CheckBox caregiversBroughtMaterialsNoneOrFew;

    CheckBox activityWelcome;
    CheckBox activityOpeningSong;
    CheckBox activityReview;
    CheckBox activityOne;
    CheckBox activityTwo;
    CheckBox activityRecap;
    CheckBox activityClosingSong;

    CheckBox freePlayMostChildren;
    CheckBox freePlayOneAdult;

    CheckBox difficultWelcome;
    CheckBox difficultOpeningSong;
    CheckBox difficultReview;
    CheckBox difficultActivity1;
    CheckBox difficultActivity2;
    CheckBox difficultRecap;
    CheckBox difficultClosingSong;

    CheckBox materialsScheduledUsedYes;
    CheckBox materialsScheduledUsedNo;

    CheckBox topicLanguage;
    CheckBox topicCognitive;
    CheckBox topicSocialemotional;
    CheckBox topicCreativity;
    CheckBox topicFormalTeaching;

    TextInputEditText etDurationInHours;

    MaterialButton submitButton;

    private List<String> activitiesTookPlace;
    private boolean teachingLearningMaterialsUsed;
    private List<String> unguidedFreePlay;
    private boolean anyDifficultActivities = false;
    private List<String> listOfDifficultActivities;
    private String caregiversEncouraging;
    private String caregiversBroughtMaterials;
    private List<String> topicsCovered;
    private int durationInHours;

    private JSONObject sessionObject;

    private SessionModelUpdatedListener sessionModelUpdatedListener;
    private GroupSessionModel sessionModel;

    public GcFinalStepFragment(SessionModelUpdatedListener listener){
        this.sessionModelUpdatedListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listOfDifficultActivities = new ArrayList<>();
        activitiesTookPlace = new ArrayList<>();
        unguidedFreePlay = new ArrayList<>();
        topicsCovered = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = rootView;
        return rootView;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        //List of difficult Activities
        difficultWelcome = view.findViewById(R.id.difficult_welcome_and_free_play);
        difficultWelcome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(WELCOME_AND_FREE_PLAY);
                else
                    listOfDifficultActivities.remove(WELCOME_AND_FREE_PLAY);
            }
        });

        difficultOpeningSong = view.findViewById(R.id.difficult_opening_song);
        difficultOpeningSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(OPENING_SONG);
                else
                    listOfDifficultActivities.remove(OPENING_SONG);
            }
        });

        difficultReview = view.findViewById(R.id.difficult_review_prevous_week);
        difficultReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(REVIEW_PREVIOUS_WEEK);
                else
                    listOfDifficultActivities.remove(REVIEW_PREVIOUS_WEEK);
            }
        });


        difficultActivity1 = view.findViewById(R.id.difficult_activity_1);
        difficultActivity1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(ACTIVITY_1);
                else
                    listOfDifficultActivities.remove(ACTIVITY_1);
            }
        });

        difficultActivity2 = view.findViewById(R.id.difficult_activity_2);
        difficultActivity2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(ACTIVITY_2);
                else
                    listOfDifficultActivities.remove(ACTIVITY_2);
            }
        });

        difficultRecap = view.findViewById(R.id.difficult_recap_session);
        difficultRecap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(RECAP_SESSION);
                else
                    listOfDifficultActivities.remove(RECAP_SESSION);
            }
        });

        difficultClosingSong = view.findViewById(R.id.difficult_closing_song);
        difficultClosingSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(CLOSING_SONG);
                else
                    listOfDifficultActivities.remove(CLOSING_SONG);
            }
        });

        //Free Play checkboxes
        freePlayMostChildren = view.findViewById(R.id.free_play_most_children);
        freePlayMostChildren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    unguidedFreePlay.add(MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS);
                else
                    unguidedFreePlay.remove(MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS);
            }
        });

        freePlayOneAdult = view.findViewById(R.id.free_play_one_adult_available);
        freePlayOneAdult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    unguidedFreePlay.add(ONE_ADULT_IS_AVAILABLE);
                else
                    unguidedFreePlay.remove(ONE_ADULT_IS_AVAILABLE);
            }
        });

        //Materials used checkboxes
        materialsScheduledUsedNo = view.findViewById(R.id.teching_materials_used_no);
        materialsScheduledUsedNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    materialsScheduledUsedYes.setChecked(false);
                    teachingLearningMaterialsUsed = false;
                }
            }
        });

        materialsScheduledUsedYes = view.findViewById(R.id.teching_materials_used_yes);
        materialsScheduledUsedYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        materialsScheduledUsedNo.setChecked(false);
                        teachingLearningMaterialsUsed = true;
                    }
            }
        });

        //Activities took place
        activityWelcome = view.findViewById(R.id.welcome_and_free_play);
        activityWelcome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesTookPlace.add(WELCOME_AND_FREE_PLAY);
                }else{
                    activitiesTookPlace.remove(WELCOME_AND_FREE_PLAY);
                }

            }
        });
        activityOpeningSong = view.findViewById(R.id.opening_song);
        activityOpeningSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(OPENING_SONG);
                else
                    activitiesTookPlace.remove(OPENING_SONG);
            }
        });

        activityReview = view.findViewById(R.id.review_prevous_week);
        activityReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(REVIEW_PREVIOUS_WEEK);
                else
                    activitiesTookPlace.remove(REVIEW_PREVIOUS_WEEK);
            }
        });

        activityOne = view.findViewById(R.id.activity_1);
        activityOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(ACTIVITY_1);
                else
                    activitiesTookPlace.remove(ACTIVITY_1);
            }
        });

        activityTwo = view.findViewById(R.id.activity_2);
        activityTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(ACTIVITY_2);
                else
                    activitiesTookPlace.remove(ACTIVITY_2);
            }
        });

        activityRecap = view.findViewById(R.id.recap_session);
        activityRecap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(RECAP_SESSION);
                else
                    activitiesTookPlace.remove(RECAP_SESSION);
            }
        });

        activityClosingSong = view.findViewById(R.id.closing_song);
        activityClosingSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(CLOSING_SONG);
                else
                    activitiesTookPlace.remove(CLOSING_SONG);
            }
        });

        //Caregivers Encouraging Checkboxes
        encouragingCaregiversYesAllActivities = view.findViewById(R.id.yes_all_or_nearly_all);
        encouragingCaregiversYesAllActivities.setOnCheckedChangeListener(new CaregiversEncouragingListener(view,
                selectedOption -> caregiversEncouraging = selectedOption));

        encouragingCaregiversYesMostActivities = view.findViewById(R.id.yes_most);
        encouragingCaregiversYesMostActivities.setOnCheckedChangeListener(new CaregiversEncouragingListener(view,
                selectedOption -> caregiversEncouraging = selectedOption));

        encouragingCaregiversYesSomeActivities = view.findViewById(R.id.yes_some);
        encouragingCaregiversYesSomeActivities.setOnCheckedChangeListener(new CaregiversEncouragingListener(view,
                selectedOption -> caregiversEncouraging = selectedOption));

        encouragingCaregiversNoneOrFew = view.findViewById(R.id.none_or_very_few);
        encouragingCaregiversNoneOrFew.setOnCheckedChangeListener(new CaregiversEncouragingListener(view,
                selectedOption -> caregiversEncouraging = selectedOption));

        //Brought Materials Checkboxes
        caregiversBroughtMaterialsYesAll = view.findViewById(R.id.materials_yes_all_or_nearly_all);
        caregiversBroughtMaterialsYesAll.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        caregiversBroughtMaterialsYesMost = view.findViewById(R.id.materials_yes_most);
        caregiversBroughtMaterialsYesMost.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        caregiversBroughtMaterialsYesSome = view.findViewById(R.id.materials_yes_some);
        caregiversBroughtMaterialsYesSome.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        caregiversBroughtMaterialsNoneOrFew = view.findViewById(R.id.materials_none_or_very_few);
        caregiversBroughtMaterialsNoneOrFew.setOnCheckedChangeListener(new CaregiversMaterialsListener(view,
                selectedOption -> caregiversBroughtMaterials = selectedOption));

        difficultActivitiesList = view.findViewById(R.id.llDifficultActivitiesList);
        activitiesDifficultYes = view.findViewById(R.id.difficult_activities_yes);
        activitiesDifficultNo = view.findViewById(R.id.difficult_activities_no);

        activitiesDifficultNo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesDifficultYes.setChecked(false);
                    anyDifficultActivities = false;
                }
            }
        });

        activitiesDifficultYes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    activitiesDifficultNo.setChecked(false);
                    anyDifficultActivities = true;
                }
                difficultActivitiesLayoutController(b);
            }
        });

        //Topics Covered Checkbox
        topicLanguage = view.findViewById(R.id.topic_language);
        topicLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    topicsCovered.add(TOPIC_LANGUAGE);
                }else{
                    activitiesTookPlace.remove(TOPIC_LANGUAGE);
                }

            }
        });
        topicCognitive = view.findViewById(R.id.topic_cognitive);
        topicCognitive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    topicsCovered.add(TOPIC_COGNITIVE);
                }else{
                    topicsCovered.remove(TOPIC_COGNITIVE);
                }
            }
        });

        topicSocialemotional = view.findViewById(R.id.topic_socialemotional);
        topicSocialemotional.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    topicsCovered.add(TOPIC_SOCIAL_EMOTIONAL);
                }else{
                    topicsCovered.remove(TOPIC_SOCIAL_EMOTIONAL);
                }
            }
        });

        topicCreativity = view.findViewById(R.id.topic_creativity);
        topicCreativity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    topicsCovered.add(TOPIC_CREATIVITY);
                }else{
                    topicsCovered.remove(TOPIC_CREATIVITY);
                }
            }
        });

        topicFormalTeaching = view.findViewById(R.id.topic_formal_teaching);
        topicFormalTeaching.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    topicsCovered.add(TOPIC_FORMAL_TEACHING);
                }else{
                    topicsCovered.remove(TOPIC_FORMAL_TEACHING);
                }
            }
        });

        submitButton = view.findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //1 Validate fields

                //2 Reconstruct object
                presenter().fetchSessionDetails();

                //3 Validate Object

                //4 Process to Event
                saveGroupSession();

                //5 Close fragment
            }
        });

        etDurationInHours = view.findViewById(R.id.et_session_duration);

    }

    private void saveGroupSession() {
        presenter().saveGroupSession(sessionModel);
    }

    private void difficultActivitiesLayoutController(boolean isDifficult){
        difficultActivitiesList.setVisibility(isDifficult ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_group_session_step_three;
    }

    @Override
    public void getSessionDetails() {

        sessionModel = GroupSessionRegisterActivity.getSessionModel();

        //Update session Details object with values
        sessionModel.setActivitiesTookPlace(activitiesTookPlace);
        sessionModel.setAllTeachingLearningMaterialsUsed(teachingLearningMaterialsUsed);
        sessionModel.setUnguidedFreePlay(unguidedFreePlay);
        sessionModel.setAnyDifficultActivities(anyDifficultActivities);
        if (anyDifficultActivities)
            sessionModel.setListOfDifficultActivities(listOfDifficultActivities);
        sessionModel.setCaregiversEncouragingChildren(caregiversEncouraging);
        sessionModel.setCaregiversBroughtMaterials(caregiversBroughtMaterials);
        sessionModel.setTopicsCovered(topicsCovered);
        sessionModel.setDurationInHours(etDurationInHours != null ? Integer.parseInt(String.valueOf(etDurationInHours.getText())): 0);
        Toast.makeText(getContext(), "Group Session Information Recorded", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void refreshSessionSummaryView(int numberOfSessions) {

    }

    private boolean validateFields(){
        return false;
    }

    private void createSessionObject(){

    }

    public interface SelectedOption {
        void onSelection(String selectedOption);
    }

}
