package org.smartregister.chw.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.activity.GroupSessionRegisterActivity;
import org.smartregister.chw.listener.CaregiversEncouragingListener;
import org.smartregister.chw.listener.CaregiversMaterialsListener;
import org.smartregister.chw.listener.SessionModelUpdatedListener;
import org.smartregister.chw.model.GroupSessionModel;

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

    private List<Activities> activitiesTookPlace;
    private boolean teachingLearningMaterialsUsed;
    private List<UnguidedFreePlay> unguidedFreePlay;
    private boolean anyDifficultActivities = false;
    private List<Activities> listOfDifficultActivities;
    private String caregiversEncouraging;
    private String caregiversBroughtMaterials;
    private String topicsCovered;

    private JSONObject sessionObject;

    private SessionModelUpdatedListener sessionModelUpdatedListener;
    private GroupSessionModel sessionModel;

    public GcFinalStepFragment(SessionModelUpdatedListener listener, GroupSessionModel model){
        this.sessionModelUpdatedListener = listener;
        this.sessionModel = model;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //todo: to be changed to the same model started on previous steps
        sessionModel = new GroupSessionModel();

        listOfDifficultActivities = new ArrayList<>();
        activitiesTookPlace = new ArrayList<>();
        unguidedFreePlay = new ArrayList<>();

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
                    listOfDifficultActivities.add(Activities.WELCOME_AND_FREE_PLAY);
                else
                    listOfDifficultActivities.remove(Activities.WELCOME_AND_FREE_PLAY);
            }
        });

        difficultOpeningSong = view.findViewById(R.id.difficult_opening_song);
        difficultOpeningSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(Activities.OPENING_SONG);
                else
                    listOfDifficultActivities.remove(Activities.OPENING_SONG);
            }
        });

        difficultReview = view.findViewById(R.id.difficult_review_prevous_week);
        difficultReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(Activities.REVIEW_PREVIOUS_WEEK);
                else
                    listOfDifficultActivities.remove(Activities.REVIEW_PREVIOUS_WEEK);
            }
        });


        difficultActivity1 = view.findViewById(R.id.difficult_activity_1);
        difficultActivity1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(Activities.ACTIVITY_1);
                else
                    listOfDifficultActivities.remove(Activities.ACTIVITY_1);
            }
        });

        difficultActivity2 = view.findViewById(R.id.difficult_activity_2);
        difficultActivity2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(Activities.ACTIVITY_TWO);
                else
                    listOfDifficultActivities.remove(Activities.ACTIVITY_TWO);
            }
        });

        difficultRecap = view.findViewById(R.id.difficult_recap_session);
        difficultRecap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(Activities.RECAP_SESSION);
                else
                    listOfDifficultActivities.remove(Activities.RECAP_SESSION);
            }
        });

        difficultClosingSong = view.findViewById(R.id.difficult_closing_song);
        difficultClosingSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    listOfDifficultActivities.add(Activities.CLOSING_SONG);
                else
                    listOfDifficultActivities.remove(Activities.CLOSING_SONG);
            }
        });

        //Free Play checkboxes
        freePlayMostChildren = view.findViewById(R.id.free_play_most_children);
        freePlayMostChildren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    unguidedFreePlay.add(UnguidedFreePlay.MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS);
                else
                    unguidedFreePlay.remove(UnguidedFreePlay.MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS);
            }
        });

        freePlayOneAdult = view.findViewById(R.id.free_play_one_adult_available);
        freePlayOneAdult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    unguidedFreePlay.add(UnguidedFreePlay.ONE_ADULT_IS_AVAILABLE);
                else
                    unguidedFreePlay.remove(UnguidedFreePlay.ONE_ADULT_IS_AVAILABLE);
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
                    activitiesTookPlace.add(Activities.WELCOME_AND_FREE_PLAY);
                }else{
                    activitiesTookPlace.remove(Activities.WELCOME_AND_FREE_PLAY);
                }

            }
        });
        activityOpeningSong = view.findViewById(R.id.opening_song);
        activityOpeningSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(Activities.OPENING_SONG);
                else
                    activitiesTookPlace.remove(Activities.OPENING_SONG);
            }
        });

        activityReview = view.findViewById(R.id.review_prevous_week);
        activityReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(Activities.REVIEW_PREVIOUS_WEEK);
                else
                    activitiesTookPlace.remove(Activities.REVIEW_PREVIOUS_WEEK);
            }
        });

        activityOne = view.findViewById(R.id.activity_1);
        activityOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(Activities.ACTIVITY_1);
                else
                    activitiesTookPlace.remove(Activities.ACTIVITY_1);
            }
        });

        activityTwo = view.findViewById(R.id.activity_2);
        activityTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(Activities.ACTIVITY_TWO);
                else
                    activitiesTookPlace.remove(Activities.ACTIVITY_TWO);
            }
        });

        activityRecap = view.findViewById(R.id.recap_session);
        activityRecap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(Activities.RECAP_SESSION);
                else
                    activitiesTookPlace.remove(Activities.RECAP_SESSION);
            }
        });

        activityClosingSong = view.findViewById(R.id.closing_song);
        activityClosingSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    activitiesTookPlace.add(Activities.CLOSING_SONG);
                else
                    activitiesTookPlace.remove(Activities.CLOSING_SONG);
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

    }

    private void difficultActivitiesLayoutController(boolean isDifficult){
        difficultActivitiesList.setVisibility(isDifficult ? View.VISIBLE : View.GONE);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_group_session_step_three;
    }

    @Override
    public String getSessionDetails() {
        return null;
    }

    private boolean validateFields(){
        return false;
    }

    private void createSessionObject(){

    }

    public interface SelectedOption {
        void onSelection(String selectedOption);
    }

    enum Activities {
        WELCOME_AND_FREE_PLAY,
        OPENING_SONG,
        REVIEW_PREVIOUS_WEEK,
        ACTIVITY_1,
        ACTIVITY_TWO,
        RECAP_SESSION,
        CLOSING_SONG
    }

    enum UnguidedFreePlay {
        MOST_CHILDREN_ARE_PLAYING_WITH_MATERIALS,
        ONE_ADULT_IS_AVAILABLE
    }

    enum CoveredTopics {
        LANGUAGE,
        COGNITIVE,
        SOCIAL_EMOTIONAL,
        CREATIVITY,
        FORMAL_TEACHING
    }

}
