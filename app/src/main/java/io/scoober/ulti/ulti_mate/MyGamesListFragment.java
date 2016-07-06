package io.scoober.ulti.ulti_mate;

import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public class MyGamesListFragment extends ListFragment {

    private List<Game> games;
    private MyGamesListAdapter gamesListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        games = gameDbAdapter.getRecentGames(10,0);
        gameDbAdapter.close();

        gamesListAdapter = new MyGamesListAdapter(getActivity(),games);

        setListAdapter(gamesListAdapter);

        registerForContextMenu(getListView());

        //TODO Add feature to get more than 10 games
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
        launchGameDisplay(MainMenuActivity.DisplayToLaunch.VIEW, position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.my_games_list_long_press_menu, menu);
    }

    public void launchGameDisplay(MainMenuActivity.DisplayToLaunch dtl, int position) {

        Game game = (Game) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), GameDisplayActivity.class);
        intent.putExtra(MainMenuActivity.GAME_ID_EXTRA, game.getId());
        intent.putExtra(MainMenuActivity.GAME_DISPLAY_ARG_EXTRA, dtl);
        startActivity(intent);

    }

    private void deleteGame(Game game, int rowPosition) {
        GameDbAdapter dbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        dbAdapter.open();
        dbAdapter.deleteGame(game.getId());
        dbAdapter.close();

        games.remove(rowPosition);
        gamesListAdapter.notifyDataSetChanged();
    }

    private void showDeleteConfirmDialog(final Game game, final int rowPosition) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_question)
                .setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGame(game, rowPosition);
                    }
                })
                .setNegativeButton(R.string.cancel_button, null)
                .create()
                .show();
    }
}
