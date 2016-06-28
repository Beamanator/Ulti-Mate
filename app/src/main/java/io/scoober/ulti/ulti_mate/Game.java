package io.scoober.ulti.ulti_mate;

import android.content.Context;

/**
 * Created by Navin on 6/24/2016.
 */
public class Game {
    private long id;
    private long date; // Date game was created

    private String gameName;
    private int winningScore; // score needed to win
    private long softCapTime; // time of soft cap
    private long hardCapTime; // time of hard cap
    private int team1Score; // Team Score
    private int team2Score; // Team Score

    private String initPullingTeam; // Team pulling at the beginning
    private String initTeamLeft; // Team on the left at the beginning

    // Team 1 information
    private String team1Name; // Team Name
    private String team1Color; // Team Color

    // Team 2 information
    private String team2Name; // Team Name
    private String team2Color; // Team Color

    private GameStatus gameStatus;
    public enum GameStatus { NOT_STARTED, PAUSED, FIRST_HALF, HALFTIME, SECOND_HALF,
        SOFT_CAP, HARD_CAP, GAME_OVER}

    public long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public String getGameName() {
        return gameName;
    }

    public int getWinningScore() {
        return winningScore;
    }

    public long getSoftCapTime() {
        return softCapTime;
    }

    public long getHardCapTime() {
        return hardCapTime;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public String getInitPullingTeam() {
        return initPullingTeam;
    }

    public String getInitTeamLeft() {
        return initTeamLeft;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public String getTeam1Color() {
        return team1Color;
    }

    public GameStatus getGameStatus() { return gameStatus; }

    public void setGameStatus(GameStatus status) { this.gameStatus = status; }

    public String getTeam2Name() {
        return team2Name;
    }

    public String getTeam2Color() {
        return team2Color;
    }

    public void setInitPullingTeam(String initPullingTeam) {
        this.initPullingTeam = initPullingTeam;
    }

    public void setInitTeamLeft(String initTeamLeft) {
        this.initTeamLeft = initTeamLeft;
    }

    /*
    Constructor for building the game from the Game Setup Activity
         */
    public Game(String gameName, int winningScore, String team1Name, String team1Color,
                String team2Name, String team2Color, long softCapTime, long hardCapTime) {
        this.gameName = gameName;
        this.winningScore = winningScore;
        this.team1Name = team1Name;
        this.team1Color = team1Color;
        this.team2Name = team2Name;
        this.team2Color = team2Color;
        this.softCapTime = softCapTime;
        this.hardCapTime = hardCapTime;

        this.id=0;
        this.team1Score = 0;
        this.team2Score = 0;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    /*
    Constructor for building the game from a database
     */
    public Game(long id, String gameName, GameStatus status, int winningScore, int team1Score, int team2Score,
                String team1Name, String team1Color, String team2Name, String team2Color,
                long softCapTime, long hardCapTime, String initPullingTeam, String initTeamLeft,
                long date) {
        this.id = id;
        this.gameName = gameName;
        this.winningScore = winningScore;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
        this.team1Name = team1Name;
        this.team1Color = team1Color;
        this.team2Name = team2Name;
        this.team2Color = team2Color;
        this.softCapTime = softCapTime;
        this.hardCapTime = hardCapTime;
        this.initPullingTeam = initPullingTeam;
        this.initTeamLeft = initTeamLeft;
        this.date = date;
        this.gameStatus = status;
    }

    public int incrementScore(int teamNumber) {
        if (teamNumber == 1) {
            team1Score+=1;
            return team1Score;
        } else if(teamNumber == 2) {
            team2Score+=1;
            return team2Score;
        }
        return -1;
    }

    public int decrementScore(int teamNumber) {
        if (teamNumber == 1) {
            team1Score-=1;
            return team1Score;
        } else if(teamNumber == 2) {
            team2Score-=1;
            return team2Score;
        }
        return -1;
    }

    /**
     *Translates GameStatus enum values to text in strings.xml resource file
     * @param status    status enum value that will be translated to a string
     * @param context   base context used to find strings.xml resource file
     * @return          return translated value from strings.xml
     */
    public static String getStatusText(GameStatus status, Context context) {
        switch (status) {
            case NOT_STARTED:
                return context.getString(R.string.status_not_started);
            case PAUSED:
                return context.getString(R.string.status_paused);
            case FIRST_HALF:
                return context.getString(R.string.status_first_half);
            case HALFTIME:
                return context.getString(R.string.status_halftime);
            case SECOND_HALF:
                return context.getString(R.string.status_second_half);
            case SOFT_CAP:
                return context.getString(R.string.status_soft_cap);
            case HARD_CAP:
                return context.getString(R.string.status_hard_cap);
            case GAME_OVER:
                return context.getString(R.string.status_game_over);
        }

        return context.getString(R.string.status_not_started);
    }
}
