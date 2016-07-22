package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.support.annotation.ColorInt;

import java.util.Calendar;

/**
 * Created by Navin on 6/24/2016.
 */
public class Game {

    public static final int MAX_SCORE = 99;
    public static final int MIN_SCORE = 0;

    private long id;
    private long createDate; // Date game was created
    private long startDate; // Date game was started
    private long endDate; // Date game was stopped

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
    private @ColorInt int team1Color; // Team Color

    // Team 2 information
    private String team2Name; // Team Name
    private @ColorInt int team2Color; // Team Color

    private Status status;
    public enum Status { NOT_STARTED, FIRST_HALF, HALFTIME, SECOND_HALF,
        SOFT_CAP, HARD_CAP, GAME_OVER, IN_PROGRESS}

    // Template info
    private boolean isTemplate;
    private String templateName;

    public long getId() {
        return id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public long getStartDate() { return startDate; }

    public void setStartDate(long date) { this.startDate = date; }

    public long getEndDate() { return endDate; }

    public void setEndDate(long date) { this.endDate = date; }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String name) { this.gameName = name; }

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

    public int getScore(int team) {
        if (team == 1) {
            return team1Score;
        } else if (team == 2) {
            return team2Score;
        } else {
            return -1;
        }
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int score) { this.team1Score = score; }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int score) {this.team2Score = score; }

    public String getInitPullingTeam() {
        return initPullingTeam;
    }

    public String getInitTeamLeft() {
        return initTeamLeft;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String name) { this.team1Name = name; }

    public int getTeam1Color() {
        return team1Color;
    }

    public void setTeam1Color(int team1Color) {
        this.team1Color = team1Color;
    }

    public Status getStatus() { return status; }

    public String getTeam2Name() {
        return team2Name;
    }

    public void setTeam2Name(String name) { this.team2Name = name; }

    public int getTeam2Color() {
        return team2Color;
    }

    public void setTeam2Color(int team2Color) {
        this.team2Color = team2Color;
    }

    public void setInitPullingTeam(String initPullingTeam) {
        this.initPullingTeam = initPullingTeam;
    }

    public void setInitTeamLeft(String initTeamLeft) {
        this.initTeamLeft = initTeamLeft;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /*
        Constructor for building the game from the Game Setup Activity. Defaults are set here
        and will not need to be reset again, as this will populate them in the database.
        */
    public Game(String gameName, int winningScore, String team1Name, int team1Color,
                String team2Name, int team2Color, long softCapTime, long hardCapTime) {
        this.gameName = gameName;
        this.winningScore = winningScore;
        this.team1Name = team1Name;
        this.team1Color = team1Color;
        this.team2Name = team2Name;
        this.team2Color = team2Color;
        this.softCapTime = softCapTime;
        this.hardCapTime = hardCapTime;

        // set defaults
        this.team1Score = 0;
        this.team2Score = 0;
        this.startDate = 0;
        this.endDate = 0;
        this.status = Status.NOT_STARTED;
    }

    /*
    Constructor for building the game from a database
     */
    public Game(long id, String gameName, Status status, int winningScore, int team1Score, int team2Score,
                String team1Name, int team1Color, String team2Name, int team2Color,
                long softCapTime, long hardCapTime, String initPullingTeam, String initTeamLeft,
                boolean isTemplate, String templateName, long createDate, long sDate, long eDate) {
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
        this.isTemplate = isTemplate;
        this.templateName = templateName;
        this.createDate = createDate;
        this.startDate = sDate;
        this.endDate = eDate;
        this.status = status;
    }

    public int incrementScore(int teamNumber) {
        if (teamNumber == 1) {
            this.team1Score+=1;
            status = calculateGameStatus();
            return this.team1Score;
        } else if(teamNumber == 2) {
            this.team2Score+=1;
            status = calculateGameStatus();
            return this.team2Score;
        }
        return -1;
    }

    public int decrementScore(int teamNumber) {
        if (teamNumber == 1) {
            this.team1Score-=1;
            status = calculateGameStatus();
            return this.team1Score;
        } else if(teamNumber == 2) {
            this.team2Score-=1;
            status = calculateGameStatus();
            return this.team2Score;
        }
        return -1;
    }

    private boolean isUsingHalftime() {
        return winningScore > 2;
    }

    public void start() {
        if (isUsingHalftime()) {
            status = Status.FIRST_HALF;
        } else {
            status = Status.IN_PROGRESS;
        }

        setStartDate(Calendar.getInstance().getTimeInMillis());
    }

    public void end() {
        status = Status.GAME_OVER;
        setEndDate(Calendar.getInstance().getTimeInMillis());
    }

    /**
     *Translates status enum values to text in strings.xml resource file
     * @param status    status enum value that will be translated to a string
     * @param context   base context used to find strings.xml resource file
     * @return          return translated value from strings.xml
     */
    public static String getStatusText(Status status, Context context) {
        switch (status) {
            case NOT_STARTED:
                return context.getString(R.string.status_not_started);
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
            case IN_PROGRESS:
                return context.getString(R.string.status_in_progress);
        }

        return context.getString(R.string.status_not_started);
    }

    /**
     * Function to calculate game status and change the gameStatusText. Halftime is defined
     * as (score to win + 1) / 2, unless there is no winning score defined for the game.
     */
    private Status calculateGameStatus() {

        if (status == Status.GAME_OVER) {
            return Status.GAME_OVER;
        }

        if (!isUsingHalftime()) {
            // User never entered a score / screwed up. No halftime statuses.
            return Status.IN_PROGRESS;
        }

        int halftimeScore = Math.round((float)winningScore /2);
        int higherScore = (team1Score > team2Score) ? team1Score : team2Score;

        // If the higher score is less than the halftime score, it must be first half
        if(higherScore < halftimeScore) {
            return Status.FIRST_HALF;
        } else if(higherScore > halftimeScore) {
            // If the higher  score is greater than the halftime score, it must be second half
            return Status.SECOND_HALF;
        } else {
            // If it was halftime or past halftime before, it's still past halftime
            if (status == Status.HALFTIME || status == Status.SECOND_HALF) {
                return Status.SECOND_HALF;
            } else {
                return Status.HALFTIME;
            }
        }

    }

    public boolean isTemplate() {
        return this.isTemplate;
    }

    public void convertToTemplate(String templateName) {
        this.isTemplate = true;
        this.templateName = templateName;
    }
}
