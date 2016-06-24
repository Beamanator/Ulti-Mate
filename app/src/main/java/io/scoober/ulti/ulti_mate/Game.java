package io.scoober.ulti.ulti_mate;

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

    public String getTeam2Name() {
        return team2Name;
    }

    public String getTeam2Color() {
        return team2Color;
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
    }

    /*
    Constructor for building the game from a database
     */
    public Game(long id, String gameName, int winningScore, int team1Score, int team2Score,
                String team1Name, String team1Color, String team2Name, String team2Color,
                long softCapTime, long hardCapTime, String initPullingTeam, String initTeamLeft, long date) {
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
    }
}
