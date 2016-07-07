package io.scoober.ulti.ulti_mate;

import android.support.annotation.ColorInt;

/**
 * Created by Navin on 7/7/2016.
 */

public class GameTemplate {
    private long id;

    private String name; // name of the template
    private String gameName; // name of the game
    private int winningScore; // score needed to win
    private long softCapTime; // time of soft cap (no date)
    private long hardCapTime; // time of hard cap (no date)

    // Team 1 information
    private String team1Name; // Team Name
    private @ColorInt int team1Color; // Team Color

    // Team 2 information
    private String team2Name; // Team Name
    private @ColorInt int team2Color; // Team Color

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getWinningScore() {
        return winningScore;
    }

    public void setWinningScore(int winningScore) {
        this.winningScore = winningScore;
    }

    public long getSoftCapTime() {
        return softCapTime;
    }

    public void setSoftCapTime(long softCapTime) {
        this.softCapTime = softCapTime;
    }

    public long getHardCapTime() {
        return hardCapTime;
    }

    public void setHardCapTime(long hardCapTime) {
        this.hardCapTime = hardCapTime;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }

    public int getTeam1Color() {
        return team1Color;
    }

    public void setTeam1Color(int team1Color) {
        this.team1Color = team1Color;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    public void setTeam2Name(String team2Name) {
        this.team2Name = team2Name;
    }

    public int getTeam2Color() {
        return team2Color;
    }

    public void setTeam2Color(int team2Color) {
        this.team2Color = team2Color;
    }

    public GameTemplate(Game game, String name) {
        this.id = 0;

        this.name = name;
        this.winningScore = game.getWinningScore();
        this.softCapTime = game.getSoftCapTime();
        this.hardCapTime = game.getHardCapTime();
        this.team1Name = game.getTeam1Name();
        this.team1Color = game.getTeam1Color();
        this.team2Name = game.getTeam2Name();
        this.team2Color = game.getTeam2Color();
    }

    public GameTemplate(long id, String name, String gameName, int winningScore, long softCapTime,
                        long hardCapTime, String team1Name, int team1Color, String team2Name, int team2Color) {
        this.id = id;
        this.name = name;
        this.gameName = gameName;
        this.winningScore = winningScore;
        this.softCapTime = softCapTime;
        this.hardCapTime = hardCapTime;
        this.team1Name = team1Name;
        this.team1Color = team1Color;
        this.team2Name = team2Name;
        this.team2Color = team2Color;
    }
}
