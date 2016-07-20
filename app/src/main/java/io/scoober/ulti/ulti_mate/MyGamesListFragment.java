package io.scoober.ulti.ulti_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public class MyGamesListFragment extends android.support.v4.app.ListFragment {
//            implements AbsListView.OnScrollListener {

    private List<Game> games;
    private MyGamesListAdapter gamesListAdapter;

    MainMenuActivity.GamesToShow gamesToShow;
    GameDbAdapter gameDbAdapter;

    // initial number of games to load
    private int gamesToLoad= 10;
    private final int GAME_LOAD_INCREMENT = 10;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // Get arguments to determine which games to load.
        Bundle bundle = getArguments();
        gamesToShow = (MainMenuActivity.GamesToShow)
                bundle.getSerializable(MainMenuActivity.GAMES_TO_SHOW_EXTRA);

        gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());

        games = loadGames(0, 0);

        gamesListAdapter = new MyGamesListAdapter(getActivity(),games);

        setListAdapter(gamesListAdapter);

        registerForContextMenu(getListView());

        //TODO Add feature to get more than 10 games
        ListView listView = getListView();
        setListViewScrollListener(listView);
    }

    private List<Game> loadGames(int firstGame, int additionalGamesToLoad) {
        int offset = firstGame;
        int newGamesToLoad = gamesToLoad + additionalGamesToLoad;
        List<Game> newGames;

        gameDbAdapter.open();
        if (gamesToShow == MainMenuActivity.GamesToShow.ACTIVE) {
            newGames = gameDbAdapter.getActiveGames(newGamesToLoad, offset);
        } else if (gamesToShow == MainMenuActivity.GamesToShow.ENDED) {
            newGames = gameDbAdapter.getEndedGames(newGamesToLoad, offset);
        } else {
            newGames = gameDbAdapter.getEndedGames(newGamesToLoad, 0);
        }
        gameDbAdapter.close();

        // Some new games were loaded, aka not all have been loaded yet
        if (newGames.size() != 0) {
            gamesToLoad = newGamesToLoad;
        }

        return newGames;
    }

    private void setListViewScrollListener(final ListView lv) {
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && (lv.getLastVisiblePosition() - lv.getHeaderViewsCount() -
                        lv.getFooterViewsCount()) >= (gamesListAdapter.getCount() - 1)) {

                    // Now listview has hit the bottom. Load more.
                    List<Game> newGames;

                    newGames = loadGames(gamesToLoad, GAME_LOAD_INCREMENT);
                    gamesListAdapter.addAll(newGames);
                    gamesListAdapter.notifyDataSetChanged();
                }
            }

            // Need to implement onScroll, even if it's unused.
            @Override
            public void onScroll(AbsListView v, int f, int vr, int t) {}
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // give me the position of whatever note I long pressed on
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int rowPosition = info.position;
        Game game = (Game) getListAdapter().getItem(rowPosition);
        switch(item.getItemId()) {
            // if we press edit
            case R.id.edit:
                // do something here
                launchGameDisplay(MainMenuActivity.DisplayToLaunch.EDIT,rowPosition);
                return true;
            case R.id.delete:
                showDeleteConfirmDialog(game, rowPosition);
                return true;
        }

        return super.onContextItemSelected(item);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(gamesToShow == MainMenuActivity.GamesToShow.ACTIVE) {
            launchGameDisplay(MainMenuActivity.DisplayToLaunch.RESUME, position);
        } else if (gamesToShow == MainMenuActivity.GamesToShow.ENDED) {
            launchGameDisplay(MainMenuActivity.DisplayToLaunch.VIEW, position);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.my_games_list_long_press_menu, menu);

        if (gamesToShow == MainMenuActivity.GamesToShow.ACTIVE) {
            MenuItem editItem = menu.findItem(R.id.edit);
            editItem.setVisible(false);
        }
    }

    private void launchGameDisplay(MainMenuActivity.DisplayToLaunch dtl, int position) {

        Game game = (Game) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, dtl);
        startActivity(intent);

    }

    private void deleteGame(Game game, int rowPosition) {
        Utils.deleteGame(getActivity().getBaseContext(), game);
        games.remove(rowPosition);
        gamesListAdapter.notifyDataSetChanged();
    }

    private void showDeleteConfirmDialog(final Game game, final int rowPosition) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_confirm_delete)
                .setMessage(R.string.dialog_delete_game)
                .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGame(game, rowPosition);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
                .show();
    }
}
