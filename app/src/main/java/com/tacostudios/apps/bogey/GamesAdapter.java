package com.tacostudios.apps.bogey;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GamesAdapter extends BaseAdapter {
    GamesList gamesList;

    Context   ctx;
    DbAdapter adapter;

    LayoutInflater inflater;

    class ViewHolder {
        TextView title;
        TextView date;
        TextView[] names;
        TextView side;

        public ViewHolder() {
            names = new TextView[GamesList.NUM_PLAYERS];
        }
    }
    ViewHolder holder;

    int courseID = -1;

    public GamesAdapter (Context context, DbAdapter db) {
        ctx = context;
        adapter = db;

        inflater = LayoutInflater.from(ctx);

        holder = new ViewHolder();
        gamesList = db.getGamesList();
    }

    public int getCount() {
        return gamesList.length();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return gamesList.game_id[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.games_row, parent, false);

        // GET HANDLES
        holder.title = (TextView) convertView.findViewById(R.id.games_title);
        holder.date  = (TextView) convertView.findViewById(R.id.games_date);

        holder.names[GamesList.NAME1A] = (TextView) convertView.findViewById(R.id.txt_name1A);
        holder.names[GamesList.NAME1B] = (TextView) convertView.findViewById(R.id.txt_name1B);
        holder.names[GamesList.NAME2A] = (TextView) convertView.findViewById(R.id.txt_name2A);
        holder.names[GamesList.NAME2B] = (TextView) convertView.findViewById(R.id.txt_name2B);

        holder.side = (TextView) convertView.findViewById(R.id.txt_side);


        // SET TEXT
        holder.title.setText(adapter.getCourseName(gamesList.course_id[position]));

        holder.date.setText(gamesList.dates[position]);

        for (int player = 0; player < GamesList.NUM_PLAYERS; player++) {
            holder.names[player].setText(gamesList.player_names[player][position]);
        }

        switch (gamesList.sides[position]) {
            case GamesList.FRONT9:
                holder.side.setText("F");
                break;
            case GamesList.BACK9:
                holder.side.setText("B");
                break;
            case GamesList.FULL18:
                holder.side.setText("18");
                break;
        }

        return convertView;
    }
}
