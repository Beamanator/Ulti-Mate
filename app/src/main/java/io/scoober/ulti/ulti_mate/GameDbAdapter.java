package io.scoober.ulti.ulti_mate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Navin on 6/24/2016.
 */
public class GameDbAdapter {

    static final String DATABASE_NAME = "ultimate.db";
    static final int DATABASE_VERSION = 7;

    //Games table
    public static final String GAMES_TABLE = "games";
    public static final String C_ID = "_id"; // Game ID

    // General Game information
    public static final String C_GAME_NAME = "game_name"; // Name of the game
    public static final String C_GAME_STATUS = "game_status"; // Status of the game
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

    // Template Info
    public static final String C_IS_TEMPLATE = "is_template"; // Boolean indicating whether this is a template
    public static final String C_TEMPLATE_NAME = "template_name"; // Name of the template

    // Date info
    public static final String C_DATE_CREATED = "date_created"; // Date game was created
    public static final String C_DATE_UPDATED = "date_updated"; // Date game was last updated

    String[] allGameColumns = {C_ID, C_GAME_NAME, C_GAME_STATUS, C_WINNING_SCORE, C_TEAM_1_NAME, C_TEAM_1_COLOR,
            C_TEAM_1_SCORE, C_TEAM_2_NAME, C_TEAM_2_COLOR, C_TEAM_2_SCORE, C_SOFT_CAP_TIME, C_HARD_CAP_TIME,
            C_INIT_PULL_TEAM, C_INIT_TEAM_LEFT, C_IS_TEMPLATE, C_TEMPLATE_NAME,
            C_DATE_CREATED, C_DATE_UPDATED};

    public static final String CREATE_TABLE_GAMES = "create table " + GAMES_TABLE + " ( "
            + C_ID + " integer primary key autoincrement, "
            + C_GAME_NAME + " text not null, "
            + C_GAME_STATUS + " text not null, "
            + C_WINNING_SCORE + " integer not null, "
            + C_TEAM_1_NAME + " text not null, "
            + C_TEAM_1_COLOR + " integer not null, "
            + C_TEAM_1_SCORE + " integer not null, "
            + C_TEAM_2_NAME + " text not null, "
            + C_TEAM_2_COLOR + " integer not null, "
            + C_TEAM_2_SCORE + " integer not null, "
            + C_SOFT_CAP_TIME + " text, "
            + C_HARD_CAP_TIME + " text, "
            + C_INIT_PULL_TEAM + " text, "
            + C_INIT_TEAM_LEFT + " text, "
            + C_IS_TEMPLATE + " boolean, "
            + C_TEMPLATE_NAME + " text, "
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

    public long createGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(C_GAME_NAME, game.getGameName());
        values.put(C_GAME_STATUS, game.getGameStatus().name());
        values.put(C_WINNING_SCORE, game.getWinningScore());
        values.put(C_TEAM_1_NAME, game.getTeam1Name());
        values.put(C_TEAM_1_COLOR, game.getTeam1Color());
        values.put(C_TEAM_1_SCORE, 0);
        values.put(C_TEAM_2_NAME, game.getTeam2Name());
        values.put(C_TEAM_2_COLOR, game.getTeam2Color());
        values.put(C_TEAM_2_SCORE, 0);
        values.put(C_SOFT_CAP_TIME, game.getSoftCapTime());
        values.put(C_HARD_CAP_TIME, game.getHardCapTime());
        values.put(C_IS_TEMPLATE, game.isTemplate());
        values.put(C_TEMPLATE_NAME, game.getTemplateName());
        values.put(C_DATE_CREATED, Calendar.getInstance().getTimeInMillis());
        values.put(C_DATE_UPDATED, Calendar.getInstance().getTimeInMillis());

        long insertId = sqlDB.insert(GAMES_TABLE,null,values);

        return insertId;
    }

    /*
    Performs a database call to get the game with the passed in gameId
     */
    public Game getGame(long gameId) {
        Cursor cursor = sqlDB.query(GAMES_TABLE, allGameColumns,
                C_ID + " = " + gameId, null, null, null, null);
        cursor.moveToFirst();
        Game newGame = cursorToGame(cursor);
        cursor.close();

        return newGame;
    }

    /**
     * Function gets an ArrayList of games from the database, ordered with most recent first.
     * An offset can be used to get items n to n+offset, useful for limiting lists.
     * @param numGames  Number of Games to return
     * @param offset    Offset on the SQL query.
     * @param statuses    Array of game statuses to get - if null, do not filter on statuses
     * @return          ArrayList of games
     */
    public ArrayList<Game> getRecentGames(int numGames, int offset, Game.GameStatus[] statuses) {
        ArrayList<Game> games = new ArrayList<Game>();

        String limit = Integer.toString(numGames);
        if (offset != 0) {
            limit = offset + "," + limit;
        }

        String selection = C_IS_TEMPLATE + " = 0";
        if(statuses != null) {
            selection = selection + " AND " + C_GAME_STATUS + " IN ( \"" +
                    TextUtils.join("\", \"", statuses)+"\" )" ;
        }

        Cursor cursor = sqlDB.query(GAMES_TABLE, allGameColumns, selection, null, null, null, C_DATE_CREATED + " DESC", limit);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Game game = cursorToGame(cursor);
            games.add(game);
        }

        cursor.close();

        return games;
    }

    public ArrayList<Game> getEndedGames(int numGames, int offset) {
        Game.GameStatus[] statuses = {Game.GameStatus.GAME_OVER};
        return getRecentGames(numGames, offset, statuses);
    }

    /**
     * Get the ID of the most recent (non-template) game
     * @return  ID of the most recent game or 0 if there are no games
     */
    public long getRecentGameId() {
        ArrayList<Game> games = getRecentGames(1,0,null);
        if(games.isEmpty()) {
            return 0;
        }

        return games.get(0).getId();
    }

    /**
     * Function to get a list of all templates in the database
     * @return ArrayList of template games
     */
    public ArrayList<Game> getAllTemplates() {
        ArrayList<Game> games = new ArrayList<Game>();

        String selection = C_IS_TEMPLATE + " = 1";
        Cursor cursor = sqlDB.query(GAMES_TABLE, allGameColumns, selection, null, null, null, C_TEMPLATE_NAME + " ASC", null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Game game = cursorToGame(cursor);
            games.add(game);
        }

        cursor.close();

        return games;
    }

    public long saveGame(Game game) {
        ContentValues values = new ContentValues();
        values.put(C_GAME_NAME, game.getGameName());
        values.put(C_GAME_STATUS, game.getGameStatus().name());
        values.put(C_WINNING_SCORE, game.getWinningScore());
        values.put(C_SOFT_CAP_TIME, game.getSoftCapTime());
        values.put(C_HARD_CAP_TIME, game.getHardCapTime());
        values.put(C_TEAM_1_NAME, game.getTeam1Name());
        values.put(C_TEAM_1_COLOR, game.getTeam1Color());
        values.put(C_TEAM_1_SCORE, game.getTeam1Score());
        values.put(C_TEAM_2_NAME, game.getTeam2Name());
        values.put(C_TEAM_2_COLOR, game.getTeam2Color());
        values.put(C_TEAM_2_SCORE, game.getTeam2Score());
        values.put(C_INIT_PULL_TEAM, game.getInitPullingTeam());
        values.put(C_INIT_TEAM_LEFT, game.getInitTeamLeft());
        values.put(C_IS_TEMPLATE, game.isTemplate());
        values.put(C_TEMPLATE_NAME, game.getTemplateName());
        values.put(C_DATE_CREATED, game.getDate());
        values.put(C_DATE_UPDATED, Calendar.getInstance().getTimeInMillis());

        return sqlDB.update(GAMES_TABLE, values, C_ID + " = " + game.getId(), null);
    }

    public long deleteGame(long idToDelete) {
        return sqlDB.delete(GAMES_TABLE, C_ID + " = " + idToDelete, null);
    }

    private Game cursorToGame(Cursor cursor) {
        // Transform query into game object
        long id = cursor.getLong(0);
        String gameName = cursor.getString(1);
        Game.GameStatus status = Game.GameStatus.valueOf(cursor.getString(2)); // Status of game
        int winningScore = cursor.getInt(3); // score needed to win
        String team1Name = cursor.getString(4); // Team Name
        int team1Color = cursor.getInt(5); // Team Color
        int team1Score = cursor.getInt(6); // Team Score
        String team2Name = cursor.getString(7); // Team Name
        int team2Color = cursor.getInt(8); // Team Color
        int team2Score = cursor.getInt(9); // Team Score
        long softCapTime = cursor.getLong(10); // time of soft cap
        long hardCapTime = cursor.getLong(11); // time of hard cap
        String initPullingTeam = cursor.getString(12); // Team pulling at the beginning
        String initTeamLeft = cursor.getString(13); // Team on the left at the beginning
        boolean isTemplate = (cursor.getInt(14) == 1);
        String templateName = cursor.getString(15);
        long date = cursor.getLong(16); // Date game was created

        // Create game object and return it.
        Game newGame = new Game(id, gameName, status, winningScore,team1Score,team2Score,
                team1Name, team1Color, team2Name, team2Color, softCapTime, hardCapTime,
                initPullingTeam, initTeamLeft, isTemplate, templateName, date);

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
