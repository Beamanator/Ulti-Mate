package io.scoober.ulti.ulti_mate;

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for updating games asynchronously. Currently intended to only
 * be used by the Game Update Notification, which it updates
 */
public class GameUpdateService extends IntentService {

    // IntentService can perform
    public static final String ACTION_INCREMENT_SCORE = MainMenuActivity.PACKAGE_NAME + "action.increment_score";

    // Parameters
    public static final String EXTRA_GAME_ID = MainMenuActivity.PACKAGE_NAME + "extra.game_id";
    public static final String EXTRA_TEAM_POS = MainMenuActivity.PACKAGE_NAME + "extra.team_pos";

    private Game game;

    public GameUpdateService() {
        super("GameUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final long gameId = intent.getLongExtra(EXTRA_GAME_ID, 0);

            // quit if the gameId is 0, as there is no game to update
            if (gameId == 0) {
                return;
            }

            game = Utils.getGameDetails(this, gameId);
            if (ACTION_INCREMENT_SCORE.equals(action)) {
                final int team = intent.getIntExtra(EXTRA_TEAM_POS, 0);
                incrementScore(team);
            }

            Utils.saveGameDetails(this, game);
            GameDisplayActivity.showUpdateNotification(this, gameId);
        }
    }

    /**
     * Increments the score of a given team
     * @param teamPos   Position of the team to increment score
     */
    private void incrementScore(int teamPos) {
        game.incrementScore(teamPos);
        if (game.getStatus() == Game.Status.HALFTIME) {
            GameDisplayFragment.showHalftimeNotification(this, game);
        }
    }
}
