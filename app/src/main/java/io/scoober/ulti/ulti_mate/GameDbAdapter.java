package io.scoober.ulti.ulti_mate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Navin on 6/24/2016.
 */
public class GameDbAdapter {

    static final String DATABASE_NAME = "ultimate.db";
    static final int DATABASE_VERSION = 3;

    //Games table
    public static final String GAMES_TABLE = "games";
    public static final String C_ID = "_id"; // Game ID

    // General Game information
    public static final String C_GAME_NAME = "game_name"; // Name of the game
    public static final String C_WINNING_SCORE = "winning_score"; // score needed to win

    // Team 1 information
    public static final String C_TEAM_1_NAME = "team_1_name"; // Team Name
    public static final String C_TEAM_1_COLOR = "team_1_color"; // Team Color
    public static final String C_TEAM_1_SCORE = "team_1_score"; // Team Score

    // Team 2 information
    public static final String C_TEAM_2_NAME = "team_2_name"; // Team Name
    public static final String C_TEAM_2_COLOR = "team_2_color"; // Team Color
    public static final String C_TEAM_2_SCORE = "team_2_score"; // Team Score

    // Additional Game Information
    public static final String C_SOFT_CAP_TIME = "soft_cap_time"; // time of soft cap
    public static final String C_HARD_CAP_TIME = "hard_cap_time"; // time of hard cap
    public static final String C_INIT_PULL_TEAM = "init_pull_team"; // Team pulling at the beginning
    public static final String C_INIT_TEAM_LEFT = "init_team_left"; // Team on the left at the beginning

    // Date info
    public static final String C_DATE_CREATED = "date_created"; // Date game was created
    public static final String C_DATE_UPDATED = "date_updated"; // Date game was last updated

    String[] allColumns = {C_ID, C_GAME_NAME, C_WINNING_SCORE, C_TEAM_1_NAME, C_TEAM_1_COLOR,
            C_TEAM_1_SCORE, C_TEAM_2_NAME, C_TEAM_2_COLOR, C_TEAM_2_SCORE, C_INIT_PULL_TEAM,
            C_INIT_TEAM_LEFT, C_SOFT_CAP_TIME, C_HARD_CAP_TIME, C_DATE_CREATED, C_DATE_UPDATED};

    public static final String CREATE_TABLE_GAMES = "create table " + GAMES_TABLE + " ( "
            + C_ID + " integer primary key autoincrement, "
            + C_GAME_NAME + " text not null, "
            + C_WINNING_SCORE + " integer not null, "
            + C_TEAM_1_NAME + " text not null, "
            + C_TEAM_1_COLOR + " text, "
            + C_TEAM_1_SCORE + " integer not null, "
            + C_TEAM_2_NAME + " text not null, "
            + C_TEAM_2_COLOR + " text, "
            + C_TEAM_2_SCORE + " integer not null, "
            + C_SOFT_CAP_TIME + " text, "
            + C_HARD_CAP_TIME + " text, "
            + C_INIT_PULL_TEAM + " text, "
            + C_INIT_TEAM_LEFT + " text, "
            + C_DATE_CREATED + " text not null, "
            + C_DATE_UPDATED + " text not null);";

    SQLiteDatabase sqlDB;
    Context context;

    GameDbHelper gameDbHelper;

    public GameDbAdapter(Context ctx) {
        context = ctx;
    }

    public GameDbAdapter open() throws android.database.SQLException {
        gameDbHelper = new GameDbHelper(context);
        sqlDB = gameDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        gameDbHelper.close();
    }

    public Game createGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(C_DATE_CREATED, Calendar.getInstance().getTimeInMillis());
        values.put(C_DATE_UPDATED, Calendar.getInstance().getTimeInMillis());
        values.put(C_GAME_NAME, game.getGameName());
        values.put(C_WINNING_SCORE, game.getWinningScore());
        values.put(C_SOFT_CAP_TIME, game.getSoftCapTime());
        values.put(C_HARD_CAP_TIME, game.getHardCapTime());
        values.put(C_TEAM_1_NAME, game.getTeam1Name());
        values.put(C_TEAM_1_COLOR, game.getTeam1Color());
        values.put(C_TEAM_1_SCORE, 0);
        values.put(C_TEAM_2_NAME, game.getTeam2Name());
        values.put(C_TEAM_2_COLOR, game.getTeam2Color());
        values.put(C_TEAM_2_SCORE, 0);

        long insertId = sqlDB.insert(GAMES_TABLE,null,values);

        return getGame(insertId);
    }

    /*
    Performs a database call to get the game with the passed in gameId
     */
    public Game getGame(long gameId) {
        Cursor cursor = sqlDB.query(GAMES_TABLE, allColumns,
                C_ID + " = " + gameId, null, null, null, null);
        cursor.moveToFirst();
        Game newGame = cursorToGame(cursor);
        cursor.close();

        return newGame;
    }

    private Game cursorToGame(Cursor cursor) {
        // Transform query into game object
        long id = cursor.getLong(0);
        String gameName = cursor.getString(1);
        int winningScore = cursor.getInt(2); // score needed to win
        String team1Name = cursor.getString(3); // Team Name
        String team1Color = cursor.getString(4); // Team Color
        int team1Score = cursor.getInt(5); // Team Score
        String team2Name = cursor.getString(6); // Team Name
        String team2Color = cursor.getString(7); // Team Color
        int team2Score = cursor.getInt(8); // Team Score
        long softCapTime = cursor.getLong(9); // time of soft cap
        long hardCapTime = cursor.getLong(10); // time of hard cap
        String initPullingTeam = cursor.getString(11); // Team pulling at the beginning
        String initTeamLeft = cursor.getString(12); // Team on the left at the beginning
        long date = cursor.getLong(13); // Date game was created

        // Create game object and return it.
        Game newGame = new Game(id, gameName, winningScore,team1Score,team2Score,
                team1Name, team1Color, team2Name, team2Color,
                softCapTime, hardCapTime, initPullingTeam, initTeamLeft, date);

        return newGame;

    }

    static class GameDbHelper extends SQLiteOpenHelper {

        GameDbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //create note table
            db.execSQL(CREATE_TABLE_GAMES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(GameDbHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + GAMES_TABLE);
            onCreate(db);
        }
    }
}