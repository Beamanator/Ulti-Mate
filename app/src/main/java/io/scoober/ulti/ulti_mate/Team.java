package io.scoober.ulti.ulti_mate;

import android.support.annotation.ColorInt;

/**
 * Created by Navin on 7/21/2016.
 */
public class Team {

    private String name; // Team Name
    private @ColorInt int color; // Team Color

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Team(String name, int color) {
        this.name = name;
        this.color = color;
    }
}
