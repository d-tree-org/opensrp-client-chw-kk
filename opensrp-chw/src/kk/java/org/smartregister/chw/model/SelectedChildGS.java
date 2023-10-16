package org.smartregister.chw.model;

import java.util.List;

/**
 * Created by Kassim Sheghembe on 2023-07-03
 */
public class SelectedChildGS {

    private String childBaseEntityId;
    private ChildStatus childSelectionStatus;
    private boolean cameWithPrimaryCareGiver;
    private String groupPlaced;

    private List<String> accompanyingRelatives = null;

    private List<String> careGiverRepresentatives;

    public SelectedChildGS(String childBaseEntityId, ChildStatus childSelectionStatus, boolean cameWithPrimaryCareGiver, String groupPlaced) {
        this.childBaseEntityId = childBaseEntityId;
        this.childSelectionStatus = childSelectionStatus;
        this.cameWithPrimaryCareGiver = cameWithPrimaryCareGiver;
        this.groupPlaced = groupPlaced;
    }

    public String getChildBaseEntityId() {
        return childBaseEntityId;
    }

    public void setChildBaseEntityId(String childBaseEntityId) {
        this.childBaseEntityId = childBaseEntityId;
    }

    public ChildStatus getChildSelectionStatus() {
        return childSelectionStatus;
    }

    public void setChildSelectionStatus(ChildStatus childSelectionStatus) {
        this.childSelectionStatus = childSelectionStatus;
    }

    public boolean cameWithPrimaryCareGiver() {
        return cameWithPrimaryCareGiver;
    }

    public void setCameWithPrimaryCareGiver(boolean cameWithPrimaryCareGiver) {
        this.cameWithPrimaryCareGiver = cameWithPrimaryCareGiver;
    }

    public String getGroupPlaced() {
        return groupPlaced;
    }

    public void setGroupPlaced(String groupPlaced) {
        this.groupPlaced = groupPlaced;
    }

    public List<String> getAccompanyingRelatives() {
        return accompanyingRelatives;
    }

    public void setAccompanyingRelatives(List<String> accompanyingRelatives) {
        this.accompanyingRelatives = accompanyingRelatives;
    }

    public List<String> getCareGiverRepresentatives() {
        return accompanyingRelatives;
    }

    public void setCareGiverRepresentatives(List<String> careGiverRepresentatives) {
        this.careGiverRepresentatives = careGiverRepresentatives;
    }

    public enum ChildStatus {
        SELECTED,
        UNSELECTED
    }
}
