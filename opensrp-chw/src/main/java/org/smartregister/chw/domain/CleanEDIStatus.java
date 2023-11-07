package org.smartregister.chw.domain;

/**
 * Author issyzac on 27/09/2023
 */
public enum CleanEDIStatus {

    CLEANED ("CLEANED"),
    PENDING ("PENDING");

    private String value;

    private CleanEDIStatus(String value){
        this.value = value;
    }

    public String value(){
        return this.value;
    }

}
