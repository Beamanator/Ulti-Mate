package io.scoober.ulti.ulti_mate;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 */
public class MyGamesFragment extends ListFragment {

    private List<Game> games;
    private MyGamesListAdapter gamesListAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        games = gameDbAdapter.getRecentGames(20,0);
        gameDbAdapter.close();

        gamesListAdapter = new MyGamesListAdapter(getActivity(),games);

        setListAdapter(gamesListAdapter);

        registerForContextMenu(getListView());
        //TODO Add feature to get more than 20 games
    }

}
