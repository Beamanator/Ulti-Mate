package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.support.annotation.ColorInt;

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
    private @ColorInt int team1Color; // Team Color

    // Team 2 information
    private String team2Name; // Team Name
    private @ColorInt int team2Color; // Team Color

    private GameStatus gameStatus;
    public enum GameStatus { NOT_STARTED, PAUSED, FIRST_HALF, HALFTIME, SECOND_HALF,
        SOFT_CAP, HARD_CAP, GAME_OVER, IN_PROGRESS}

    public long getId() {
        return id;
    }

    public long getDate() {
        return date;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String name) { this.gameName = name; }

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

    public GameStatus getGameStatus() { return gameStatus; }

    public void setGameStatus(GameStatus status) { this.gameStatus = status; }

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

    /*
    Constructor for building the game from the Game Setup Activity
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

        this.id=0;
        this.team1Score = 0;
        this.team2Score = 0;
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    /*
    Constructor for building the game from a database
     */
    public Game(long id, String gameName, GameStatus status, int winningScore, int team1Score, int team2Score,
                String team1Name, int team1Color, String team2Name, int team2Color,
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
            this.team1Score+=1;
            return this.team1Score;
        } else if(teamNumber == 2) {
            this.team2Score+=1;
            return this.team2Score;
        }
        return -1;
    }

    public int decrementScore(int teamNumber) {
        if (teamNumber == 1) {
            this.team1Score-=1;
            return this.team1Score;
        } else if(teamNumber == 2) {
            this.team2Score-=1;
            return this.team2Score;
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
            case IN_PROGRESS:
                return context.getString(R.string.status_in_progress);
        }

        return context.getString(R.string.status_not_started);
    }

    /**
     * Function to calculate game status and change the gameStatusText. Halftime is defined
     * as (score to win + 1) / 2, unless there is no winning score defined for the game.
     * @param dir       Variable to designate score change direction (-1 = minus button,
     *                  0 = initialization, 1 = add button)
     */
    public GameStatus calculateGameStatus(int dir, int teamScored) {
        int t1score = getTeam1Score();
        int t2score = getTeam2Score();
        int winScore = getWinningScore();
        int halftimeScore;
        Game.GameStatus status;

        if (winScore < 2) {
            // User never entered a score / screwed up. No halftime statuses.

            status = GameStatus.IN_PROGRESS;
        } else {
            // Calculate halftime:
            if (winScore % 2 == 0) {
                // winScore is even in this case, so halftime is half the winScore.
                halftimeScore = winScore / 2;
            } else {
                // winScore is odd in this case, so halftime is (winScore + 1) / 2.
                //  ex: game to 13, halftime is at 7
                halftimeScore = (winScore + 1) / 2;
            }

            // Figure out which team is winning.
            if (t1score > t2score) {
                // Team 1 winning
                if (t1score < halftimeScore) {
                    // status: first half
                    status = GameStatus.FIRST_HALF;
                } else if (t1score == halftimeScore) {
                    // if dir is 1, halftime. otherwise, 2nd half
                    if (dir == 1 && teamScored == 1) {
                        status = GameStatus.HALFTIME;
                    } else {
                        status = GameStatus.SECOND_HALF;
                    }
                } else {
                    // status: second half
                    status = GameStatus.SECOND_HALF;
                }
            } else if (t1score == t2score) {
                // Scores equal
                if (t1score < halftimeScore) {
                    // status: first half
                    status = GameStatus.FIRST_HALF;
                } else {
                    // status: second half
                    status = GameStatus.SECOND_HALF;
                }
            } else {
                // Team 2 winning
                if (t2score < halftimeScore) {
                    // status: first half
                    status = GameStatus.FIRST_HALF;
                } else if (t2score == halftimeScore) {
                    // if dir is 1, halftime. otherwise, 2nd half
                    if (dir == 1 && teamScored == 2) {
                        status = GameStatus.HALFTIME;
                    } else {
                        status = GameStatus.SECOND_HALF;
                    }
                } else {
                    // status: second half
                    status = GameStatus.SECOND_HALF;
                }
            }
        }

        return status;
    }
}
