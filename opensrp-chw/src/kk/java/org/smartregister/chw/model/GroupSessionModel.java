package org.smartregister.chw.model;

import java.util.List;

/**
 * Author issyzac on 05/07/2023
 */
public class GroupSessionModel {

    private String sessionId;

    private  boolean sessionTookPlace;

    private String noSessionReason;

    private String noSessionOtherReason;
    private String sessionDate;
    private String sessionPlace;
    private String sessionDuration;
    private List<SelectedChildGS> selectedChildren;
    private List<String> activitiesTookPlace;
    private boolean allTeachingLearningMaterialsUsed;
    private List<String> unguidedFreePlay;
    private boolean anyDifficultActivities;
    private List<String> listOfDifficultActivities;
    private String caregiversEncouragingChildren;
    private String caregiversBroughtMaterials;
    private List<String> topicsCovered;
    private int durationInHours;

    private boolean childrenDividedInGroups;

    public GroupSessionModel(){

    }

    public String getSessionId() {
        return sessionId;
    }

    public List<SelectedChildGS> getSelectedChildren() {
        return selectedChildren;
    }

    public String getSessionDate() {
        return sessionDate;
    }

    public List<String> getActivitiesTookPlace() {
        return activitiesTookPlace;
    }

    public String getCaregiversBroughtMaterials() {
        return caregiversBroughtMaterials;
    }

    public String getCaregiversEncouragingChildren() {
        return caregiversEncouragingChildren;
    }

    public List<String> getListOfDifficultActivities() {
        return listOfDifficultActivities;
    }

    public String getSessionDuration() {
        return sessionDuration;
    }

    public String getSessionPlace() {
        return sessionPlace;
    }

    public List<String> getTopicsCovered() {
        return topicsCovered;
    }

    public List<String> getUnguidedFreePlay() {
        return unguidedFreePlay;
    }

    public int getDurationInHours() {
        return durationInHours;
    }

    public void setActivitiesTookPlace(List<String> activitiesTookPlace) {
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

    public void setListOfDifficultActivities(List<String> listOfDifficultActivities) {
        this.listOfDifficultActivities = listOfDifficultActivities;
    }

    public void setSelectedChildren(List<SelectedChildGS> selectedChildren) {
        this.selectedChildren = selectedChildren;
    }

    public void setSessionDate(String sessionDate) {
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

    public void setTopicsCovered(List<String> topicsCovered) {
        this.topicsCovered = topicsCovered;
    }

    public void setUnguidedFreePlay(List<String> unguidedFreePlay) {
        this.unguidedFreePlay = unguidedFreePlay;
    }

    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

    public boolean isChildrenDividedInGroups() {
        return childrenDividedInGroups;
    }

    public void setChildrenDividedInGroups(boolean childrenDividedInGroups) {
        this.childrenDividedInGroups = childrenDividedInGroups;
    }

    public void setSessionTookPlace(boolean sessionTookPlace) {
        this.sessionTookPlace = sessionTookPlace;
    }

    public boolean isSessionTookPlace() {
        return sessionTookPlace;
    }

    public void setNoSessionReason(String noSessionReason) {
        this.noSessionReason = noSessionReason;
    }

    public String getNoSessionReason() {
        return noSessionReason;
    }

    public void setNoSessionOtherReason(String noSessionOtherReason) {
        this.noSessionOtherReason = noSessionOtherReason;
    }

    public String getNoSessionOtherReason() {
        return noSessionOtherReason;
    }

    public boolean isAllTeachingLearningMaterialsUsed() {
        return allTeachingLearningMaterialsUsed;
    }

    public boolean isAnyDifficultActivities() {
        return anyDifficultActivities;
    }

}
