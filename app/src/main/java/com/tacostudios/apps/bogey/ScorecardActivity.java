package com.tacostudios.apps.bogey;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScorecardActivity extends AppCompatActivity {
    Context ctx;

    DbAdapter adapter;

    TextView textview_course_name;
    TextView textview_names[];
    TextView textview_holes[];
    TextView textview_pars[];
    TextView textview_scores[][];
    TextView tv_raw_scores[][];
    LinearLayout data_container;
    RelativeLayout rl_scroll;
    TextView scroll_names[];
    LinearLayout linearlayout_column[];
    int curr_hole = 0;

    public final static String EXTRA_GAME_ID = "com.tacostudios.apps.bogey.GAME_ID";

    int gameID = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        setContentView(R.layout.activity_scorecard);

        gameID = getIntent().getIntExtra(EXTRA_GAME_ID, -1);

        openDatabaseAdapter();
        getViewHandles();
        populateGameData();

        highlightHole(0);
    }

    private void getViewHandles () {
        textview_course_name = (TextView) findViewById(R.id.textview_course_name);

        // NAMES
        textview_names = new TextView[NewGameData.NUM_PLAYERS];
        textview_names[NewGameData.NAME1A] = (TextView) findViewById(R.id.score_name1a);
        textview_names[NewGameData.NAME1B] = (TextView) findViewById(R.id.score_name1b);
        textview_names[NewGameData.NAME2A] = (TextView) findViewById(R.id.score_name2a);
        textview_names[NewGameData.NAME2B] = (TextView) findViewById(R.id.score_name2b);

        scroll_names = new TextView[NewGameData.NUM_PLAYERS];
        scroll_names[NewGameData.NAME1A] = (TextView) findViewById(R.id.scroll_name1a);
        scroll_names[NewGameData.NAME1B] = (TextView) findViewById(R.id.scroll_name1b);
        scroll_names[NewGameData.NAME2A] = (TextView) findViewById(R.id.scroll_name2a);
        scroll_names[NewGameData.NAME2B] = (TextView) findViewById(R.id.scroll_name2b);

        // Add score columns for each hole to be played
        // handle to container layout
        data_container = (LinearLayout) findViewById(R.id.scoreboard_data);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // TODO: pull from data
        int num_holes = 9;

        linearlayout_column = new LinearLayout[num_holes];
        textview_holes = new TextView[num_holes];
        textview_pars = new TextView[num_holes];
        textview_scores = new TextView[num_holes][NewGameData.NUM_PLAYERS];
        tv_raw_scores = new TextView[num_holes][NewGameData.NUM_PLAYERS];
        for (int hole = 0; hole < num_holes; hole++) {
            linearlayout_column[hole] = (LinearLayout) inflater.inflate(R.layout.score_column, data_container, false);
            linearlayout_column[hole].setId(hole);
            data_container.addView(linearlayout_column[hole]);

            // get handles to views
            textview_holes[hole] = (TextView) linearlayout_column[hole].findViewById(R.id.column_hole);
            textview_holes[hole].setText(Integer.toString(hole+1));

            textview_pars[hole] = (TextView) linearlayout_column[hole].findViewById(R.id.column_par);

            tv_raw_scores[hole][NewGameData.NAME1A] = (TextView) linearlayout_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.score_raw);
            tv_raw_scores[hole][NewGameData.NAME1B] = (TextView) linearlayout_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.score_raw);
            tv_raw_scores[hole][NewGameData.NAME2A] = (TextView) linearlayout_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.score_raw);
            tv_raw_scores[hole][NewGameData.NAME2B] = (TextView) linearlayout_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.score_raw);


            linearlayout_column[hole].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curr_hole = v.getId();

                    //highlight column
                    highlightHole(curr_hole);
                }
            });
        }

        rl_scroll = (RelativeLayout) findViewById (R.id.scroll_region);
        rl_scroll.setOnTouchListener(new View.OnTouchListener() {
            float x0 = 0;
            float y0 = 0;

            int start = 0;

            int num_segments = 20;  // should be multiple of number of players
            int segment0;
            int segment;

            private int calcSegment(float x , float y) {
                double angle = Math.toDegrees( Math.atan2( y0 - y, x - x0) );
                if (angle < 0) {
                    angle = angle + 360;
                }
                return (int) Math.floor(angle / 360f * num_segments);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                double angle;
                int delta;
                double delta_angle;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // determine middle point on initial touch
                        x0 = v.getWidth() / 2;
                        y0 = v.getHeight() / 2;

                        segment0 = calcSegment(event.getX(), event.getY());
                        if ( (segment0 >= 0) && (segment0 < (num_segments/4)) ){
                            start = NewGameData.NAME1B;
                        } else if ( (segment0 >= (num_segments/4)) && (segment0 < (num_segments/2)) ) {
                            start = NewGameData.NAME1A;
                        } else if ( (segment0 >= (num_segments/2)) && (segment0 < (3*num_segments/4)) ) {
                            start = NewGameData.NAME2A;
                        } else {
                            start = NewGameData.NAME2B;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        segment = calcSegment(event.getX(), event.getY());
                        if (segment == segment0 + 1) {
                            decrementScore(start);
                        } else if (segment == segment0 - 1) {
                            incrementScore(start);
                        } else if ((segment0 == 0) && (segment == num_segments-1)) {
                            incrementScore(start);
                        } else if ((segment0 == num_segments-1) && (segment == 0)) {
                            decrementScore(start);
                        }
                        segment0 = segment;
                        break;
                }
                return true;
            }
        });
    }

    private void openDatabaseAdapter() {
        adapter = new DbAdapter(this);
        adapter.open();
    }

    private void populateGameData() {
        GameData gameData = adapter.getGameData(gameID);

        // get name
        String course_name = adapter.getCourseName(gameData.course_id);
        textview_course_name.setText(course_name);

        // get par
        int pars[] = adapter.getPars(gameData.course_id);
        int sides = gameData.sides;

        int par_start = 0;
        int par_end = 0;
        switch (sides) {
            case NewGameData.FRONT9:
                par_start = 0;
                par_end = 8;
                break;
            case NewGameData.BACK9:
                par_start = 9;
                par_end = 17;
                break;
            case NewGameData.FULL18:
                par_start = 0;
                par_end = 17;
                break;
        }

        for (int hole = 0; hole <= par_end-par_start; hole++) {
            textview_holes[hole].setText(Integer.toString(hole + par_start + 1));
            textview_pars[hole].setText(Integer.toString(pars[hole + par_start]));
        }


        textview_names[NewGameData.NAME1A].setText(gameData.names[NewGameData.NAME1A]);
        textview_names[NewGameData.NAME1B].setText(gameData.names[NewGameData.NAME1B]);
        textview_names[NewGameData.NAME2A].setText(gameData.names[NewGameData.NAME2A]);
        textview_names[NewGameData.NAME2B].setText(gameData.names[NewGameData.NAME2B]);

        scroll_names[NewGameData.NAME1A].setText(gameData.names[NewGameData.NAME1A]);
        scroll_names[NewGameData.NAME1B].setText(gameData.names[NewGameData.NAME1B]);
        scroll_names[NewGameData.NAME2A].setText(gameData.names[NewGameData.NAME2A]);
        scroll_names[NewGameData.NAME2B].setText(gameData.names[NewGameData.NAME2B]);

    }

    private void incrementScore(int position) {
        int score = getCurrScore(position);
        tv_raw_scores[curr_hole][position].setText(Integer.toString(score + 1));
    }

    private void decrementScore(int position) {
        int score = getCurrScore(position);
        if (score == 0) {
            tv_raw_scores[curr_hole][position].setText(Integer.toString(0));
        } else {
            tv_raw_scores[curr_hole][position].setText(Integer.toString(score - 1));
        }
    }

    private int getCurrScore (int position) {
        int prev_score;

        // get current score
        try {
            prev_score = Integer.parseInt( tv_raw_scores[curr_hole][position].getText().toString() );
        } catch (NumberFormatException nfe) {
            // should be a "-"  treat as 0
            prev_score = 0;
        }

        return prev_score;
    }

    private void highlightHole(int hole) {
        for (int h = 0; h < 9; h++) {
            linearlayout_column[h].setBackgroundColor(Color.TRANSPARENT);
            for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
                tv_raw_scores[h][player].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textsize_inactive));
            }
        }
        linearlayout_column[hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.column_highlight));
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            tv_raw_scores[hole][player].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textsize_active));
        }
    }
}
