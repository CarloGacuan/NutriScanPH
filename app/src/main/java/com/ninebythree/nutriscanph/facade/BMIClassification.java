package com.ninebythree.nutriscanph.facade;

import com.ninebythree.nutriscanph.R;

public class BMIClassification {

    private String[] status = {"Underweight", "Normal", "Pre-obesity", "Obesity class I", "Obesity class II", "Obesity class III"};
    private int[] colorIDs = {R.color.colorPrimaryDark, R.color.green, R.color.red};
    private int[] bgIDs = {R.drawable.profile_circle_brown, R.drawable.profile_circle_green, R.drawable.profile_circle_red};

    // current instance
    private String classification;
    private int colorID, bgID;

    public BMIClassification(double bmiVal) {
        if(bmiVal >= 40) { // Obesity class III
            this.classification = status[5];
            this.colorID = colorIDs[2];
            this.bgID = bgIDs[2];
        } else if(bmiVal >= 35) { // Obesity class II
            this.classification = status[4];
            this.colorID = colorIDs[2];
            this.bgID = bgIDs[2];
        } else if(bmiVal >= 30 ) { // Obesity class I
            this.classification = status[3];
            this.colorID = colorIDs[2];
            this.bgID = bgIDs[2];
        } else if(bmiVal >= 25) { // Pre-obesity
            this.classification = status[2];
            this.colorID = colorIDs[0];
            this.bgID = bgIDs[0];
        } else if(bmiVal >= 18.5) { // Normal
            this.classification = status[1];
            this.colorID = colorIDs[1];
            this.bgID = bgIDs[1];
        } else { // Underweight
            this.classification = status[0];
            this.colorID = colorIDs[0];
            this.bgID = bgIDs[0];
        }
    }

    public String classifyAs() {return classification;}
    public int getColorID() {return colorID;}
    public int getBgID() {return bgID;}

}
