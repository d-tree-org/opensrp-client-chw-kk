package org.smartregister.chw.model;

import java.util.List;

/**
 * Author issyzac on 05/07/2023
 */
public class GroupSessionModel {

    private String sessionId;
    private long sessionDate;
    private String sessionPlace;
    private String sessionDuration;
    private List<SelectedChildGS> selectedChildren;
    private String activitiesTookPlace;
    private boolean allTeachingLearningMaterialsUsed;
    private String unguidedFreePlay;
    private boolean anyDifficultActivities;
    private String listOfDifficultActivities;
    private String caregiversEncouragingChildren;
    private String caregiversBroughtMaterials;
    private String topicsCovered;

    public GroupSessionModel(){

    }

    public List<SelectedChildGS> getSelectedChildren() {
        return selectedChildren;
    }

    public long getSessionDate() {
        return sessionDate;
    }

    public String getActivitiesTookPlace() {
        return activitiesTookPlace;
    }

    public String getCaregiversBroughtMaterials() {
        return caregiversBroughtMaterials;
    }

    public String getCaregiversEncouragingChildren() {
        return caregiversEncouragingChildren;
    }

    public String getListOfDifficultActivities() {
        return listOfDifficultActivities;
    }

    public String getSessionDuration() {
        return sessionDuration;
    }

    public String getSessionPlace() {
        return sessionPlace;
    }

    public String getTopicsCovered() {
        return topicsCovered;
    }

    public String getUnguidedFreePlay() {
        return unguidedFreePlay;
    }

    public void setActivitiesTookPlace(String activitiesTookPlace) {
        this.activitiesTookPlace = activitiesTookPlace;
    }

    public void setAllTeachingLearningMaterialsUsed(boolean allTeachingLearningMaterialsUsed) {
        this.allTeachingLearningMaterialsUsed = allTeachingLearningMaterialsUsed;
    }

    public void setAnyDifficultActivities(boolean anyDifficultActivities) {
        this.anyDifficultActivities = anyDifficultActivities;
    }

    public void setCaregiversBroughtMaterials(String caregiversBroughtMaterials) {
        this.caregiversBroughtMaterials = caregiversBroughtMaterials;
    }

    public void setCaregiversEncouragingChildren(String caregiversEncouragingChildren) {
        this.caregiversEncouragingChildren = caregiversEncouragingChildren;
    }

    public void setListOfDifficultActivities(String listOfDifficultActivities) {
        this.listOfDifficultActivities = listOfDifficultActivities;
    }

    public void setSelectedChildren(List<SelectedChildGS> selectedChildren) {
        this.selectedChildren = selectedChildren;
    }

    public void setSessionDate(long sessionDate) {
        this.sessionDate = sessionDate;
    }

    public void setSessionDuration(String sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionPlace(String sessionPlace) {
        this.sessionPlace = sessionPlace;
    }

    public void setTopicsCovered(String topicsCovered) {
        this.topicsCovered = topicsCovered;
    }

    public void setUnguidedFreePlay(String unguidedFreePlay) {
        this.unguidedFreePlay = unguidedFreePlay;
    }
}
