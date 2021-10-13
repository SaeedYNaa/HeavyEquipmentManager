package com.example.heavyequipmentmanager.Engine;

import java.io.Serializable;

public class EngineTool implements Serializable {

    private String name;
    private String treatment;
    private String nextTreatment;
    private Double workingHours;
    private int imageId;

    /**General Information*/
    private String testDate;
    private String ensurenceDate;


    /**For saving state purpose */
    public static boolean written = false;

    public EngineTool(String name, String treatment, String newTreatment, double workingHours, int image){
        this.name = name;
        this.treatment = treatment;
        this.nextTreatment = newTreatment;
        this.workingHours = workingHours;
        this.imageId = image;
    }

    public String getName() {
        return name;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getNextTreatment() {
        return nextTreatment;
    }

    public Double getWorkingHours() {
        return workingHours;
    }

    public int getImageId() {
        return imageId;
    }

    public String getTestDate() {
        return testDate;
    }

    public String getEnsurenceDate() {
        return ensurenceDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public void setNextTreatment(String nextTreatment) {
        this.nextTreatment = nextTreatment;
    }

    public void setWorkingHours(Double workingHours) {
        this.workingHours = workingHours;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public void setEnsurenceDate(String ensurenceDate) {
        this.ensurenceDate = ensurenceDate;
    }
}
