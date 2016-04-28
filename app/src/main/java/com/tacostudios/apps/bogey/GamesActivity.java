package com.tacostudios.apps.bogey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class GamesActivity extends AppCompatActivity {

    private static DbAdapter adapter;
    Context ctx;
    ListView listview_games;

    GamesAdapter gamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        ctx = this;

        getViewHandles();
        openDatabaseAdapter();
        populateGamesList();
    }

    public void getViewHandles() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview_games = (ListView) findViewById(R.id.games_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(ctx, CreateGameActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openDatabaseAdapter() {
        adapter = new DbAdapter(this);
        adapter.open();
    }

    private void populateGamesList() {
        gamesAdapter = new GamesAdapter(this, adapter);
        listview_games.setAdapter(gamesAdapter);

        listview_games.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ctx, ScorecardActivity.class);
                intent.putExtra(ScorecardActivity.EXTRA_GAME_ID, (int) id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_games, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ctx.deleteDatabase(getDatabasePath("data.db").getPath());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
