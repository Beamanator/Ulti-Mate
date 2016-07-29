package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.util.SparseArray;

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

    SparseArray<Team> teams; // SparseArray holds teams mapped by setup position, either 1 or 2
    SparseArray<Integer> scoreMap; // Maps team IDs to their score

    private long softCapTime; // time of soft cap
    private long hardCapTime; // time of hard cap

    private int initPullingTeamPos; // Team pulling at the beginning
    private int pullingTeamPos; // Team pulling at the current game state
    private int initLeftTeamPos; // Team on the left at the beginning
    private int leftTeamPos; // team on the left at the current game state

    private Status status;
    public enum Status { NOT_STARTED, IN_PROGRESS, FIRST_HALF, HALFTIME, SECOND_HALF,
        SOFT_CAP, HARD_CAP, GAME_OVER}

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

    public int getScore(int teamPos) {
        return scoreMap.get(teamPos);
    }

    public void setTeamScore(int teamPos, int score) {
        scoreMap.put(teamPos, score);
    }

    /**
     * Gets the team corresponding to the team position passed in. Options for team position
     * are currently 1 and 2.
     * @param position  Team position based on setup
     * @return          Team object corresponding to the number
     */
    public Team getTeam(int position) {
        return teams.get(position);
    }

    public int getInitPullingTeamPos() {
        return initPullingTeamPos;
    }

    public int getInitLeftTeamPos() {
        return initLeftTeamPos;
    }

    public void setInitPullingTeamPos(int initPullingTeamPos) {
        this.initPullingTeamPos = initPullingTeamPos;
        if (scoreMap.get(1) == 0 && scoreMap.get(2) == 0) {
            this.pullingTeamPos = this.initPullingTeamPos;
        }
    }

    public void setInitLeftTeamPos(int initLeftTeamPos) {
        this.initLeftTeamPos = initLeftTeamPos;
        if (scoreMap.get(1) == 0 && scoreMap.get(2) == 0) {
            this.leftTeamPos = this.initLeftTeamPos;
        }
    }

    public int getLeftTeamPos() {
        return leftTeamPos;
    }

    public void setLeftTeamPos(int leftTeamPos) {
        this.leftTeamPos = leftTeamPos;

        // Set the initial left team position, if it's prior to halftime
        if (scoreMap.get(1) == 0 && scoreMap.get(2) == 0) {
            this.initLeftTeamPos = leftTeamPos;
        } else if (status.compareTo(Status.HALFTIME) < 0) {
            // If it's prior to halftime, then check if the sum scores is even
            this.initLeftTeamPos = ((scoreMap.get(1) + scoreMap.get(2)) % 2 == 0)
                    ? leftTeamPos : getOpposingTeamPos(leftTeamPos);
        }
    }

    public int getPullingTeamPos() {
        return pullingTeamPos;
    }

    public void setPullingTeamPos(int pullingTeamPos) {
        this.pullingTeamPos = pullingTeamPos;

        // Set the initial pulling team position, if the score is 0-0
        if (scoreMap.get(1) == 0 && scoreMap.get(2) == 0) {
            this.initPullingTeamPos = pullingTeamPos;
        }
    }

    public Status getStatus() { return status; }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /*
        Constructor for building the game from the Game Setup Activity.
    */
    public Game(String gameName, int winningScore, Team firstTeam, Team secondTeam, long softCapTime, long hardCapTime) {
        this.gameName = gameName;
        this.winningScore = winningScore;
        this.softCapTime = softCapTime;
        this.hardCapTime = hardCapTime;

        this.teams = new SparseArray<>();
        teams.put(1, firstTeam); // Hard code position 1
        teams.put(2, secondTeam); // Hard code position 2

        // set defaults
        this.scoreMap = new SparseArray<>();
        this.scoreMap.put(1,0);
        this.scoreMap.put(2,0);

        this.initLeftTeamPos = 0;
        this.initPullingTeamPos = 0;
        this.leftTeamPos = 0;
        this.initPullingTeamPos = 0;

        this.startDate = 0;
        this.endDate = 0;
        this.status = Status.NOT_STARTED;
    }

    /*
    Constructor for copying a game object (except ID) - useful for making temporary copies
     */
    public Game(Game game) {
        this(0, game.getGameName(), game.getStatus(), game.getWinningScore(), game.getScore(1),
                game.getScore(2), game.getTeam(1).getName(), game.getTeam(1).getColor(),
                game.getTeam(2).getName(), game.getTeam(2).getColor(), game.getSoftCapTime(),
                game.getHardCapTime(), game.getInitPullingTeamPos(), game.getInitLeftTeamPos(),
                game.getPullingTeamPos(), game.getLeftTeamPos(), game.isTemplate(),
                game.getTemplateName(), game.getCreateDate(), game.getStartDate(), game.getEndDate());
    }

    /*
    Constructor for building the game from a database
     */
    public Game(long id, String gameName, Status status, int winningScore, int team1Score, int team2Score,
                String team1Name, int team1Color, String team2Name, int team2Color,
                long softCapTime, long hardCapTime, int initPullingTeamPos, int initTeamLeftPos,
                int pullingTeamPos, int leftTeamPos, boolean isTemplate, String templateName,
                long createDate, long sDate, long eDate) {
        this.id = id;
        this.gameName = gameName;
        this.winningScore = winningScore;

        Team firstTeam = new Team(team1Name, team1Color);
        Team secondTeam = new Team(team2Name, team2Color);
        this.teams = new SparseArray<>();
        teams.put(1, firstTeam); // Hard code Position 1
        teams.put(2, secondTeam); // Hard code Position 2

        this.scoreMap = new SparseArray<>();
        this.scoreMap.put(1,team1Score); // Hard code Position 1
        this.scoreMap.put(2,team2Score); // Hard code Position 2

        this.softCapTime = softCapTime;
        this.hardCapTime = hardCapTime;
        this.initPullingTeamPos = initPullingTeamPos;
        this.initLeftTeamPos = initTeamLeftPos;
        this.pullingTeamPos = pullingTeamPos;
        this.leftTeamPos = leftTeamPos;
        this.isTemplate = isTemplate;
        this.templateName = templateName;
        this.createDate = createDate;
        this.startDate = sDate;
        this.endDate = eDate;
        this.status = status;
    }

    /**
     * Increment the score of the team corresponding to the given position
     * @param teamPos       Position of the team
     * @return              New Score
     */
    public int incrementScore(int teamPos) {
        int score = scoreMap.get(teamPos);
        score += 1;

        scoreMap.put(teamPos, score);
        Status prevStatus = status;
        status = calcGameStatus();
        pullingTeamPos = calcPullingTeam(true, teamPos);
        leftTeamPos = calcLeftTeam(prevStatus);

        return score;
    }

    /**
     * Decrement the score of the team corresponding to the given position
     * @param teamPos       Position of the team
     * @return              New Score
     */
    public int decrementScore(int teamPos) {
        int score = scoreMap.get(teamPos);
        score -= 1;

        scoreMap.put(teamPos, score);
        Status prevStatus = status;
        status = calcGameStatus();
        pullingTeamPos = calcPullingTeam(false, teamPos);
        leftTeamPos = calcLeftTeam(prevStatus);

        return score;
    }

    private int calcLeftTeam(Status prevStatus) {
        // If the score is 0-0, then return the initial pulling team
        if (scoreMap.get(1) == 0 && scoreMap.get(2) == 0) {
            return initLeftTeamPos;
        }

        // If we're at halftime, then return the opposing team to the initial team
        if (status == Status.HALFTIME) {
            return getOpposingTeamPos(initLeftTeamPos);
        }

        // If we transition below halftime, figure out the pulling team from the sum of the scores
        if (prevStatus == Status.HALFTIME && status == Status.FIRST_HALF) {
            int sumScores = getScore(1) + getScore(2);
            return (sumScores % 2 == 0) ? initLeftTeamPos : getOpposingTeamPos(initLeftTeamPos);
        }

        // Otherwise, return the opposing team to the last team on the left
        return getOpposingTeamPos(leftTeamPos);

    }

    /**
     * Calculates the pulling team
     * @param scoreUp   Boolean indicating whether the score increased
     * @param teamPos   position of Team whose score was modified
     * @return          position Team that will be pulling or 0, if unknown
     */
    private int calcPullingTeam(boolean scoreUp, int teamPos) {
        // If we got back to the original score, then return the initial pulling team
        if (scoreMap.get(1) == 0 && scoreMap.get(2) == 0) {
            return initPullingTeamPos;
        }

        // If we're at halftime, it's the opposite team of the initial pulling team
        if (status == Status.HALFTIME) {
            return getOpposingTeamPos(initPullingTeamPos);
        }

        // If the score went down, the pulling team is unreliable - return null
        if (!scoreUp) {
            return 0;
        }

        // If the score went up, set it to the team that just scored
        return teamPos;
    }

    /**
     * Returns the opposing team position
     * @param teamPos   Position of the original team
     * @return          Team position of opposing team
     */
    public int getOpposingTeamPos(int teamPos) {
        return (teamPos == 1) ? 2 : 1;
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
    private Status calcGameStatus() {

        if (status == Status.GAME_OVER) {
            return Status.GAME_OVER;
        }

        if (!isUsingHalftime()) {
            // User never entered a score / screwed up. No halftime statuses.
            return Status.IN_PROGRESS;
        }

        int halftimeScore = Math.round((float)winningScore /2);

        int team1Score = scoreMap.get(1);
        int team2Score = scoreMap.get(2);
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
