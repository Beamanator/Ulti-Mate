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
    /*
    New in database version:
    7 -- Added template_name / is_template
    8 -- Added date_started / date_ended
    9 -- Revamp to pulling team and left team
    */
    static final int DATABASE_VERSION = 9;

    // Games table
    public static final String GAMES_TABLE = "games";
    public static final String C_ID = "_id"; // Game ID

    // General Game information
    public static final String C_GAME_NAME = "game_name"; // Name of the game
    public static final String C_GAME_STATUS = "game_status"; // Status of the game
    public static final String C_WINNING_SCORE = "winning_score"; // score needed to win

    // Position 1 Team information
    public static final String C_TEAM_1_NAME = "team_1_name"; // Team Name
    public static final String C_TEAM_1_COLOR = "team_1_color"; // Team Color
    public static final String C_TEAM_1_SCORE = "team_1_score"; // Team Score

    // Position 2 Team information
    public static final String C_TEAM_2_NAME = "team_2_name"; // Team Name
    public static final String C_TEAM_2_COLOR = "team_2_color"; // Team Color
    public static final String C_TEAM_2_SCORE = "team_2_score"; // Team Score

    // Additional Game Information
    public static final String C_SOFT_CAP_TIME = "soft_cap_time"; // time of soft cap
    public static final String C_HARD_CAP_TIME = "hard_cap_time"; // time of hard cap
    public static final String C_INIT_PULL_TEAM_POS = "init_pull_team_pos"; // Team pulling at the beginning
    public static final String C_INIT_LEFT_TEAM_POS = "init_team_left_pos"; // Team on the left at the beginning
    public static final String C_PULL_TEAM_POS = "pull_team_pos"; // Team pulling at current state
    public static final String C_LEFT_TEAM_POS = "team_left_pos"; // Team on the left at current state

    // Template Info
    public static final String C_IS_TEMPLATE = "is_template"; // Boolean indicating whether this is a template
    public static final String C_TEMPLATE_NAME = "template_name"; // Name of the template

    // Date info
    public static final String C_DATE_CREATED = "date_created"; // Date game was created
    public static final String C_DATE_UPDATED = "date_updated"; // Date game was last updated
    public static final String C_DATE_STARTED = "date_started"; // Date game was started
    public static final String C_DATE_ENDED = "date_ended"; // Date game was ended

    String[] allGameColumns = {C_ID,    // 0
            C_GAME_NAME,                // 1
            C_GAME_STATUS,              // 2
            C_WINNING_SCORE,            // 3
            C_TEAM_1_NAME,              // 4
            C_TEAM_1_COLOR,             // 5
            C_TEAM_1_SCORE,             // 6
            C_TEAM_2_NAME,              // 7
            C_TEAM_2_COLOR,             // 8
            C_TEAM_2_SCORE,             // 9
            C_SOFT_CAP_TIME,            // 10
            C_HARD_CAP_TIME,            // 11
            C_INIT_PULL_TEAM_POS,       // 12
            C_INIT_LEFT_TEAM_POS,       // 13
            C_PULL_TEAM_POS,            // 14
            C_LEFT_TEAM_POS,            // 15
            C_IS_TEMPLATE,              // 16
            C_TEMPLATE_NAME,            // 17
            C_DATE_CREATED,             // 18
            C_DATE_UPDATED,             // 19
            C_DATE_STARTED,             // 20
            C_DATE_ENDED};              // 21

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
            + C_INIT_PULL_TEAM_POS + " text, "
            + C_INIT_LEFT_TEAM_POS + " text, "
            + C_PULL_TEAM_POS + " text, "
            + C_LEFT_TEAM_POS + " text, "
            + C_IS_TEMPLATE + " boolean, "
            + C_TEMPLATE_NAME + " text, "
            + C_DATE_CREATED + " integer not null, "
            + C_DATE_UPDATED + " integer not null, "
            + C_DATE_STARTED + " integer not null, "
            + C_DATE_ENDED + " integer not null);";

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
        ContentValues values = getDbValuesFromGame(game);
        return sqlDB.insert(GAMES_TABLE,null,values);
    }

    public long saveGame(Game game) {
        ContentValues values = getDbValuesFromGame(game);
        return sqlDB.update(GAMES_TABLE, values, C_ID + " = " + game.getId(), null);
    }

    public long deleteGame(long idToDelete) {
        return sqlDB.delete(GAMES_TABLE, C_ID + " = " + idToDelete, null);
    }

    private ContentValues getDbValuesFromGame(Game game) {

        ContentValues values = new ContentValues();
        values.put(C_GAME_NAME, game.getGameName());
        values.put(C_GAME_STATUS, game.getStatus().name());
        values.put(C_WINNING_SCORE, game.getWinningScore());
        values.put(C_SOFT_CAP_TIME, game.getSoftCapTime());
        values.put(C_HARD_CAP_TIME, game.getHardCapTime());

        Team team1 = game.getTeam(1);
        Team team2 = game.getTeam(2);
        long createDate = game.getCreateDate();
        createDate = (createDate == 0) ? Calendar.getInstance().getTimeInMillis() : createDate;
        values.put(C_TEAM_1_NAME, team1.getName());
        values.put(C_TEAM_1_COLOR, team1.getColor());
        values.put(C_TEAM_1_SCORE, game.getScore(1));
        values.put(C_TEAM_2_NAME, team2.getName());
        values.put(C_TEAM_2_COLOR, team2.getColor());
        values.put(C_TEAM_2_SCORE, game.getScore(2));
        values.put(C_INIT_PULL_TEAM_POS, game.getInitPullingTeamPos());
        values.put(C_INIT_LEFT_TEAM_POS, game.getInitLeftTeamPos());
        values.put(C_PULL_TEAM_POS, game.getPullingTeamPos());
        values.put(C_LEFT_TEAM_POS, game.getLeftTeamPos());
        values.put(C_IS_TEMPLATE, game.isTemplate());
        values.put(C_TEMPLATE_NAME, game.getTemplateName());
        values.put(C_DATE_CREATED, createDate);
        values.put(C_DATE_UPDATED, Calendar.getInstance().getTimeInMillis());
        values.put(C_DATE_STARTED, game.getStartDate());
        values.put(C_DATE_ENDED, game.getEndDate());

        return values;
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
     * @param statuses  Array of game statuses to get - if null, do not filter on statuses
     * @return          ArrayList of games
     */
    public ArrayList<Game> getRecentGames(int numGames, int offset, Game.Status[] statuses) {
        ArrayList<Game> games = new ArrayList<>();

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

    /**
     * Get games that have been ended, sorted by creation date
     * @param numGames  Number of Games to return
     * @param offset    Offset on the SQL query.
     * @return          ArrayList of games
     */
    public ArrayList<Game> getEndedGames(int numGames, int offset) {
        Game.Status[] statuses = {Game.Status.GAME_OVER};
        return getRecentGames(numGames, offset, statuses);
    }

    /**
     * Get games that are still active, sorted by creation date
     * @param numGames  Number of Games to return.
     * @param offset    Offset on the SQL query.
     * @return          ArrayList of games
     */
    public ArrayList<Game> getActiveGames(int numGames, int offset) {
        Game.Status[] statuses = {Game.Status.NOT_STARTED,
                Game.Status.FIRST_HALF,
                Game.Status.HALFTIME,
                Game.Status.SECOND_HALF,
                Game.Status.SOFT_CAP,
                Game.Status.HARD_CAP,
                Game.Status.IN_PROGRESS
        };
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
        ArrayList<Game> games = new ArrayList<>();

        String selection = C_IS_TEMPLATE + " = 1";
        Cursor cursor = sqlDB.query(GAMES_TABLE, allGameColumns, selection,
                null, null, null, C_TEMPLATE_NAME + " COLLATE NOCASE ASC", null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Game game = cursorToGame(cursor);
            games.add(game);
        }

        cursor.close();

        return games;
    }

    /**
     * Transforms a cursor generated by an SQL query on all columns. Use the comment on the
     * allGameColumns to determine what each index is.
     * @param cursor    Cursor generated by SQL query
     * @return          game object
     */
    private Game cursorToGame(Cursor cursor) {
        // Transform query into game object
        long id = cursor.getLong(0);
        String gameName = cursor.getString(1);
        Game.Status status = Game.Status.valueOf(cursor.getString(2)); // Status of game
        int winningScore = cursor.getInt(3); // score needed to win
        String team1Name = cursor.getString(4); // Team Name
        int team1Color = cursor.getInt(5); // Team Color
        int team1Score = cursor.getInt(6); // Team Score
        String team2Name = cursor.getString(7); // Team Name
        int team2Color = cursor.getInt(8); // Team Color
        int team2Score = cursor.getInt(9); // Team Score
        long softCapTime = cursor.getLong(10); // time of soft cap
        long hardCapTime = cursor.getLong(11); // time of hard cap
        int initPullingTeamId = cursor.getInt(12); // Team pulling at the beginning
        int initLeftTeamId = cursor.getInt(13); // Team on the left at the beginning
        int pullingTeamId = cursor.getInt(14);
        int leftTeamId = cursor.getInt(15);
        boolean isTemplate = (cursor.getInt(16) == 1);
        String templateName = cursor.getString(17);
        long date = cursor.getLong(18); // Date game was created
        // position 18 = update date
        long startDate = cursor.getLong(20); // Date game was started
        long endDate = cursor.getLong(21); // Date game was ended

        // Create game object and return it.
        return new Game(id, gameName, status, winningScore,team1Score,team2Score,
                team1Name, team1Color, team2Name, team2Color, softCapTime, hardCapTime,
                initPullingTeamId, initLeftTeamId, pullingTeamId, leftTeamId,
                isTemplate, templateName, date, startDate, endDate);

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
