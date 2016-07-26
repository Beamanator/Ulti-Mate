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

public class MyGamesListAdapter extends ArrayAdapter<Game> {

    public class GameViewHolder {
        public TextView title,score,date,teams;
        public ImageButton contextMenuButton;
        public GameViewHolder(View itemView) {
            title = (TextView) itemView.findViewById(R.id.listItemGameTitle);
            score = (TextView) itemView.findViewById(R.id.listItemScores);
            date = (TextView) itemView.findViewById(R.id.listItemGameDate);
            teams = (TextView) itemView.findViewById(R.id.listItemTeams);
            contextMenuButton = (ImageButton) itemView.findViewById(R.id.contextMenuButton);
        }
    }

    public MyGamesListAdapter(Context context, List<Game> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GameViewHolder holder;
        // analogous to onCreateViewHolder() from RecycleView.Adapter
        if (convertView == null) {
            // Inflate view
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.fragment_my_games_list_row, parent, false);

            holder = new GameViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder= (GameViewHolder) convertView.getTag();
        }

        // block below analogous to onBindViewHolder() from RecycleView.Adapter
        Game game = getItem(position);

        // Get view resources to convert string resource id to string
        Resources res = convertView.getResources();

        // set TextView texts
        holder.title.setText(game.getGameName());
        holder.date.setText(Utils.getDateString(game.getCreateDate()));
        holder.score.setText(res.getString(R.string.score_list_text,
                game.getScore(1), game.getScore(2)));
        holder.teams.setText(res.getString(R.string.team_list_text,
                game.getTeam(1).getName(), game.getTeam(2).getName()));

        // Set onClickListener for contextMenuButton
        Utils.setContextMenuListener(holder.contextMenuButton, convertView, getContext());

        return convertView;
    }

//    @Override
//    public TemplateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.fragment_my_games_list_row, parent, false);
//        return new TemplateViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(TemplateViewHolder holder, int position) {
//        Game game = games.get(position);
//        holder.title.setText(game.getGameName());
//        holder.date.setText(getDateString(game.getCreateDate()));
//        holder.score.setText(game.getTeam1Score() + "-" + game.getTeam2Score());
//        holder.teams.setText(game.getTeam1Name() + " vs " + game.getTeam2Name());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return games.size();
//    }


}
