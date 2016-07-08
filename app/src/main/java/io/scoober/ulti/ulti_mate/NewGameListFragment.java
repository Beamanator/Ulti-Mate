package io.scoober.ulti.ulti_mate;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        launchGameSetup(MainMenuActivity.SetupToLaunch.CREATE_GAME, position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.new_game_template_list_long_press_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int rowPosition = info.position;
        Game template = (Game) getListAdapter().getItem(rowPosition);
        switch(item.getItemId()) {
            case R.id.itemEditTemplateName:
                showEditNameDialog(template, rowPosition);
                return true;
            case R.id.itemEditTemplate:
                launchGameSetup(MainMenuActivity.SetupToLaunch.EDIT_TEMPLATE,rowPosition);
                return true;
            case R.id.itemDeleteTemplate:
                showDeleteConfirmDialog(template, rowPosition);
                return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Launch the game setup activity from the new game activity
     * @param stl       Enum describing which setup to launch
     * @param position  Position of the row of the template on the list
     */
    private void launchGameSetup(MainMenuActivity.SetupToLaunch stl, int position) {
        Game template = (Game) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), GameSetupActivity.class);
        intent.putExtra(MainMenuActivity.TEMPLATE_ID_EXTRA, template.getId());
        intent.putExtra(MainMenuActivity.GAME_SETUP_ARG_EXTRA, stl);
        startActivity(intent);
    }

    private void deleteTemplate(Game template, int rowPosition) {
        Utils.deleteGame(getActivity().getBaseContext(), template);
        templates.remove(rowPosition);
        templateListAdapter.notifyDataSetChanged();
    }

    private void editTemplateName(Game template, String newName, int rowPosition) {
        // update database
        template.setTemplateName(newName);
        Utils.saveGameDetails(getActivity().getBaseContext(), template);

        // update list adapter
        templates.set(rowPosition, template);
        templateListAdapter.notifyDataSetChanged();
    }

    private void showDeleteConfirmDialog(final Game template, final int rowPosition) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_confirm_delete)
                .setMessage(R.string.dialog_delete_template)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTemplate(template, rowPosition);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
                .show();
    }

    private void showEditNameDialog(final Game template, final int rowPosition) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);
        final EditText nameEdit = (EditText) dialogView.findViewById(R.id.templateNameEdit);
        nameEdit.setText(template.getTemplateName());

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_dialog_name_template)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editTemplateName(template, nameEdit.getText().toString(), rowPosition);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
                .show();
    }
}
