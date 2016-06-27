package io.scoober.ulti.ulti_mate;

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
public class MyGamesFragment extends Fragment {

    private RecyclerView.Adapter gamesListAdapter;
    private List<Game> games;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyGamesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_games_list, container, false);

        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        games = gameDbAdapter.getRecentGames(20,0);
        gameDbAdapter.close();

        gamesListAdapter = new MyGamesListAdapter(games);


        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(gamesListAdapter);

        //TODO Add feature to get more than 20 games
        return view;
    }

}
