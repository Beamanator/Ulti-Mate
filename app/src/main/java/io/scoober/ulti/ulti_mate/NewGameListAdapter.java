package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Navin on 7/8/2016.
 */
public class NewGameListAdapter extends ArrayAdapter<Game>{

    public class TemplateViewHolder {
        public TextView templateTitle, gameTitle,teams;
        public ImageButton contextMenuButton;
        public TemplateViewHolder(View itemView) {
            templateTitle = (TextView) itemView.findViewById(R.id.listItemTemplateTitle);
            gameTitle = (TextView) itemView.findViewById(R.id.listItemGameTitle);
            teams = (TextView) itemView.findViewById(R.id.listItemTeams);
            contextMenuButton = (ImageButton) itemView.findViewById(R.id.contextMenuButton);
        }
    }

    public NewGameListAdapter(Context context, List<Game> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TemplateViewHolder holder;

        // analogous to onCreateViewHolder() from RecycleView.Adapter
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.fragment_new_game_template_list_row, parent, false);

            holder = new TemplateViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder= (TemplateViewHolder) convertView.getTag();
        }

        // block below analogous to onBindViewHolder() from RecycleView.Adapter
        Game template = getItem(position);

        // Get view resources to convert string resource id to string
        Resources res = convertView.getResources();

        // set TextView texts
        holder.templateTitle.setText(template.getTemplateName());
        holder.gameTitle.setText(template.getGameName());
        holder.teams.setText(res.getString(R.string.team_list_text,
                template.getTeam1Name(), template.getTeam2Name()));

        // Set onClickListener for contextMenuButton
        Utils.setContextMenuListener(holder.contextMenuButton, convertView, getContext());

        return convertView;
    }
}
