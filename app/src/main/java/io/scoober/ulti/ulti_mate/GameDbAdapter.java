package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Navin on 6/24/2016.
 */
public class GameDbAdapter {

    private static final String DATABASE_NAME = "ultimate.db";
    private static final int DATABASE_VERSION = 1;

    //Games table
    public static final String GAMES_TABLE = "games";
    public static final String C_ID = "_id"; // Game ID
    public static final String C_DATE_CREATED = "date_created"; // Date game was created
    public static final String C_DATE_UPDATED = "date_updated"; // Date game was last updated

    // General Game information
    public static final String C_GAME_NAME = "game_name"; // Name of the game
    public static final String C_WINNING_SCORE = "winning_score"; // score needed to win
    public static final String C_SOFT_CAP_TIME = "soft_cap_time"; // time of soft cap
    public static final String C_HARD_CAP_TIME = "hard_cap_time"; // time of hard cap
    public static final String C_INIT_PULL_TEAM = "init_pull_team"; // Team pulling at the beginning
    public static final String C_INIT_TEAM_LEFT = "init_team_left"; // Team on the left at the beginning

    // Team 1 information
    public static final String C_TEAM_1_NAME = "team_1_name"; // Team Name
    public static final String C_TEAM_1_COLOR = "team_1_color"; // Team Color
    public static final String C_TEAM_1_SCORE = "team_1_score"; // Team Score

    // Team 2 information
    public static final String C_TEAM_2_NAME = "team_2_name"; // Team Name
    public static final String C_TEAM_2_COLOR = "team_2_color"; // Team Color
    public static final String C_TEAM_2_SCORE = "team_2_score"; // Team Score

    private String[] allColumns = {C_ID, C_DATE_CREATED, C_DATE_UPDATED,
    C_GAME_NAME, C_WINNING_SCORE, C_SOFT_CAP_TIME, C_HARD_CAP_TIME, C_INIT_PULL_TEAM, C_INIT_TEAM_LEFT,
    C_TEAM_1_NAME, C_TEAM_1_COLOR, C_TEAM_1_SCORE, C_TEAM_2_NAME, C_TEAM_2_COLOR, C_TEAM_2_SCORE};

    public static final String CREATE_TABLE_GAMES = "create table " + GAMES_TABLE + " ( "
            + C_ID + " integer primary key autoincrement, "
            + C_DATE_CREATED + "text not null, "
            + C_DATE_UPDATED + "text not null, "
            + C_GAME_NAME + " text not null, "
            + C_WINNING_SCORE + " integer not null, "
            + C_SOFT_CAP_TIME + " text, "
            + C_HARD_CAP_TIME + " text, "
            + C_INIT_PULL_TEAM + " text, "
            + C_INIT_TEAM_LEFT + " text, "
            + C_TEAM_1_NAME + " text not null, "
            + C_TEAM_1_COLOR + " text not null, "
            + C_TEAM_1_SCORE + " integer not null, "
            + C_TEAM_2_NAME + " text not null, "
            + C_TEAM_2_COLOR + " text not null, "
            + C_TEAM_2_SCORE + " integer not null);";

    private SQLiteDatabase sqlDB;
    private Context context;

    private GameDbHelper gameDbHelper;

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

    private static class GameDbHelper extends SQLiteOpenHelper {

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
