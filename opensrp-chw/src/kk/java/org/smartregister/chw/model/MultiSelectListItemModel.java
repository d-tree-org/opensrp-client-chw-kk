package org.smartregister.chw.model;

/**
 * Created by Kassim Sheghembe on 2023-07-19
 */
public class MultiSelectListItemModel {

    private final String name;
    private boolean isSelected;

    public MultiSelectListItemModel(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
