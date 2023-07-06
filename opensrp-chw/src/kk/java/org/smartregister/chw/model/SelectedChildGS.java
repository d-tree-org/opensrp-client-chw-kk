package org.smartregister.chw.model;

/**
 * Created by Kassim Sheghembe on 2023-07-03
 */
public class SelectedChildGS {

    private String childBaseEntityId;
    private ChildStatus childSelectionStatus;
    private boolean cameWithPrimaryCareGiver;
    private String groupPlaced;

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


    public enum ChildStatus {
        SELECTED,
        UNSELECTED
    }
}
