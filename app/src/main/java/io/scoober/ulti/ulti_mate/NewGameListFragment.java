package io.scoober.ulti.ulti_mate;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGameListFragment extends ListFragment{

    private List<Game> templates;
    private NewGameListAdapter templateListAdapter;

    public NewGameListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        GameDbAdapter gameDbAdapter = new GameDbAdapter(getActivity().getBaseContext());
        gameDbAdapter.open();
        templates = gameDbAdapter.getAllTemplates();
        gameDbAdapter.close();

        templateListAdapter = new NewGameListAdapter(getActivity(), templates);

        setListAdapter(templateListAdapter);

        registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //TODO Add long press menu
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //TODO add edit and delete
        return super.onContextItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //TODO
    }
}
