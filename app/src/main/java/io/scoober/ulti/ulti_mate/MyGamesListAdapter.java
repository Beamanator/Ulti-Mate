package io.scoober.ulti.ulti_mate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGamesListAdapter extends ArrayAdapter<Game> {

    public class GameViewHolder {
        public TextView title,score,date,teams;
        public GameViewHolder(View itemView) {
            title = (TextView) itemView.findViewById(R.id.listItemGameTitle);
            score = (TextView) itemView.findViewById(R.id.listItemScores);
            date = (TextView) itemView.findViewById(R.id.listItemGameDate);
            teams = (TextView) itemView.findViewById(R.id.listItemTeams);
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

        // analogous to onBindViewHolder() from RecycleView.Adapter
        Game game = getItem(position);
        //TODO think of internationalization
        holder.title.setText(game.getGameName());
        holder.date.setText(getDateString(game.getDate()));
        holder.score.setText(game.getTeam1Score() + "-" + game.getTeam2Score());
        holder.teams.setText(game.getTeam1Name() + " vs " + game.getTeam2Name());

        return convertView;
    }

//    @Override
//    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.fragment_my_games_list_row, parent, false);
//        return new GameViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(GameViewHolder holder, int position) {
//        Game game = games.get(position);
//        holder.title.setText(game.getGameName());
//        holder.date.setText(getDateString(game.getDate()));
//        holder.score.setText(game.getTeam1Score() + "-" + game.getTeam2Score());
//        holder.teams.setText(game.getTeam1Name() + " vs " + game.getTeam2Name());
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return games.size();
//    }

    //TODO Move to utils
    public String getDateString(long milli) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return sdf.format(new Date(milli));
    }
}
