package com.tacostudios.apps.bogey;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScorecardActivity extends AppCompatActivity {
    Context ctx;
    public final static String EXTRA_GAME_ID = "com.tacostudios.apps.bogey.GAME_ID";

    DbAdapter adapter;
    GameData gameData;

    // Game Data
    String course_name = "";
    String player_name[];
    int    player_handicaps[];
    int    sides;
    int    hole_start;
    int    hole_end;
    int    num_holes;
    int    scores[][];
    int    pars[];
    int    ranking[];
    int    ranked_indices[];
    int    indiv_strokes[][];
    int    match_strokes[][];
    double indiv_points[][];
    double match_points[][];

    // UI Handles
    TextView v_course_name;
    TextView v_names[];
    TextView v_holes[];
    TextView v_pars[];
    TextView v_ranks[];
    TextView v_scores[][];
    TextView v_raw_scores[][];
    LinearLayout v_data_container;
    RelativeLayout v_scroll;
    TextView v_scroll_names[];
    LinearLayout v_column[];
    LinearLayout v_sum_column;

    android.support.v7.widget.CardView v_score_cards[][];

    ImageView v_indiv_strokes_1[][];
    ImageView v_indiv_strokes_2[][];
    ImageView v_match_strokes_1[][];
    ImageView v_match_strokes_2[][];

    TextView v_indiv_win[][];
    TextView v_match_win[][];

    TextView v_team_totals[];
    TextView v_indiv_totals[];

    TextView v_sum_header;
    TextView v_player_sum[];

    ScrollWheel scrollwheel;


    int curr_hole = 0;


    int gameID = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        setContentView(R.layout.activity_scorecard);

        gameID = getIntent().getIntExtra(EXTRA_GAME_ID, -1);

        openDatabaseAdapter();
        gameData = adapter.getGameData(gameID);

        getViewHandles();

        highlightHole(0, true);
    }

    private void getViewHandles () {
        course_name = adapter.getCourseName(gameData.course_id);
        player_name = new String[NewGameData.NUM_PLAYERS];
        player_handicaps = new int[NewGameData.NUM_PLAYERS];
        scores = new int[NewGameData.NUM_PLAYERS][18];  // TODO: maybe get rid of the 18
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            player_name[player] = gameData.names[player];
            player_handicaps[player] = gameData.handicaps[player];
            scores[player] = adapter.getScores(gameData.score_id[player]);
        }

        sides = gameData.sides;
        switch (sides) {
            case NewGameData.FRONT9:
                hole_start = 0;
                hole_end = 8;
                num_holes = 9;
                break;
            case NewGameData.BACK9:
                hole_start = 9;
                hole_end = 17;
                num_holes = 9;
                break;
            case NewGameData.FULL18:
                hole_start = 0;
                hole_end = 17;
                num_holes = 18;
                break;
        }

        pars = adapter.getPars(gameData.course_id);
        ranking = adapter.getHoleRank(adapter.getHandicapID(gameData.course_id));
        ranked_indices = sortRanking(ranking, sides);

        indiv_strokes = new int[NewGameData.NUM_PLAYERS][num_holes];
        indiv_points  = new double[NewGameData.NUM_PLAYERS][num_holes];
        match_strokes = new int[NewGameData.NUM_PLAYERS][num_holes];
        match_points  = new double[NewGameData.NUM_PLAYERS][num_holes];

        // A vs A
        int delta_a = player_handicaps[NewGameData.NAME1A] - player_handicaps[NewGameData.NAME2A];
        if (delta_a > 0) {
            // player 1A gets individual strokes
            for (int stroke = 0; stroke < delta_a; stroke++) {
                indiv_strokes[NewGameData.NAME1A][ranked_indices[stroke % num_holes]-hole_start] =
                        indiv_strokes[NewGameData.NAME1A][ranked_indices[stroke % num_holes]-hole_start] + 1;
            }
        } else if (delta_a < 0) {
            // player 2A gets individual strokes
            for (int stroke = 0; stroke < Math.abs(delta_a); stroke++) {
                indiv_strokes[NewGameData.NAME2A][ranked_indices[stroke % num_holes]-hole_start] =
                        indiv_strokes[NewGameData.NAME2A][ranked_indices[stroke % num_holes]-hole_start] + 1;
            }
        } else {
            // neither A players get individual strokes
        }

        // B vs B
        int delta_b = player_handicaps[NewGameData.NAME1B] - player_handicaps[NewGameData.NAME2B];
        if (delta_b > 0) {
            // player 1B gets individual strokes
            for (int stroke = 0; stroke < delta_b; stroke++) {
                indiv_strokes[NewGameData.NAME1B][ranked_indices[stroke % num_holes]-hole_start] =
                        indiv_strokes[NewGameData.NAME1B][ranked_indices[stroke % num_holes]-hole_start] + 1;
            }
        } else if (delta_b < 0) {
            // player 2B gets individual strokes
            for (int stroke = 0; stroke < Math.abs(delta_b); stroke++) {
                indiv_strokes[NewGameData.NAME2B][ranked_indices[stroke % num_holes]-hole_start] =
                        indiv_strokes[NewGameData.NAME2B][ranked_indices[stroke % num_holes]-hole_start] + 1;
            }
        } else {
            // neither B players get individual strokes
        }

        // Match Strokes
        int min_hdcp = Math.min(
                Math.min(player_handicaps[NewGameData.NAME1A], player_handicaps[NewGameData.NAME1B]),
                Math.min(player_handicaps[NewGameData.NAME2A], player_handicaps[NewGameData.NAME2B]) );

        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            int delta_match = player_handicaps[player] - min_hdcp;
            for (int stroke = 0; stroke < delta_match; stroke++) {
                match_strokes[player][ranked_indices[stroke % num_holes]-hole_start] =
                        match_strokes[player][ranked_indices[stroke % num_holes]-hole_start] + 1;
            }
        }



        // Course Name
        v_course_name = (TextView) findViewById(R.id.textview_course_name);
        v_course_name.setText(course_name);

        // scrollwheel
        scrollwheel = (ScrollWheel) findViewById (R.id.scrollwheel);

        // Player Names
        v_names = new TextView[NewGameData.NUM_PLAYERS];
        v_names[NewGameData.NAME1A] = (TextView) findViewById(R.id.score_name1a);
        v_names[NewGameData.NAME1B] = (TextView) findViewById(R.id.score_name1b);
        v_names[NewGameData.NAME2A] = (TextView) findViewById(R.id.score_name2a);
        v_names[NewGameData.NAME2B] = (TextView) findViewById(R.id.score_name2b);

        v_scroll_names = new TextView[NewGameData.NUM_PLAYERS];
        v_scroll_names[NewGameData.NAME1A] = (TextView) findViewById(R.id.scroll_name1a);
        v_scroll_names[NewGameData.NAME1B] = (TextView) findViewById(R.id.scroll_name1b);
        v_scroll_names[NewGameData.NAME2A] = (TextView) findViewById(R.id.scroll_name2a);
        v_scroll_names[NewGameData.NAME2B] = (TextView) findViewById(R.id.scroll_name2b);

        // set names
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            v_names[player].setText(player_name[player]);
            v_scroll_names[player].setText(player_name[player]);
        }

        // scrollwheel set names
        scrollwheel.setNames (player_name);

        // Add score columns for each hole to be played
        // handle to container layout
        v_data_container = (LinearLayout) findViewById(R.id.scoreboard_data);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v_team_totals = new TextView[2];
        v_team_totals[0] = (TextView) findViewById(R.id.match_score_1);
        v_team_totals[1] = (TextView) findViewById(R.id.match_score_2);

        v_indiv_totals = new TextView[NewGameData.NUM_PLAYERS];
        v_indiv_totals[0] = (TextView) findViewById(R.id.indiv_score1a);
        v_indiv_totals[1] = (TextView) findViewById(R.id.indiv_score1b);
        v_indiv_totals[2] = (TextView) findViewById(R.id.indiv_score2a);
        v_indiv_totals[3] = (TextView) findViewById(R.id.indiv_score2b);

        v_column = new LinearLayout[num_holes];
        v_holes = new TextView[num_holes];
        v_pars = new TextView[num_holes];
        v_ranks = new TextView[num_holes];
        v_scores = new TextView[num_holes][NewGameData.NUM_PLAYERS];
        v_raw_scores = new TextView[num_holes][NewGameData.NUM_PLAYERS];
        v_indiv_strokes_1 = new ImageView[num_holes][NewGameData.NUM_PLAYERS];
        v_indiv_strokes_2 = new ImageView[num_holes][NewGameData.NUM_PLAYERS];
        v_match_strokes_1 = new ImageView[num_holes][NewGameData.NUM_PLAYERS];
        v_match_strokes_2 = new ImageView[num_holes][NewGameData.NUM_PLAYERS];
        v_indiv_win = new TextView[NewGameData.NUM_PLAYERS][num_holes];
        v_match_win = new TextView[NewGameData.NUM_PLAYERS][num_holes];

        v_score_cards = new android.support.v7.widget.CardView[NewGameData.NUM_PLAYERS][num_holes];

        for (int hole = 0; hole < num_holes; hole++) {
            v_column[hole] = (LinearLayout) inflater.inflate(R.layout.score_column, v_data_container, false);
            v_column[hole].setId(hole);
            v_data_container.addView(v_column[hole]);

            // get handles to views
            v_holes[hole] = (TextView) v_column[hole].findViewById(R.id.column_hole);
            v_pars[hole] = (TextView) v_column[hole].findViewById(R.id.column_par);
            v_ranks[hole] = (TextView) v_column[hole].findViewById(R.id.column_rank);

            v_score_cards[NewGameData.NAME1A][hole] = (android.support.v7.widget.CardView) v_column[hole].findViewById(R.id.column_raw1a);
            v_score_cards[NewGameData.NAME1B][hole] = (android.support.v7.widget.CardView) v_column[hole].findViewById(R.id.column_raw1b);
            v_score_cards[NewGameData.NAME2A][hole] = (android.support.v7.widget.CardView) v_column[hole].findViewById(R.id.column_raw2a);
            v_score_cards[NewGameData.NAME2B][hole] = (android.support.v7.widget.CardView) v_column[hole].findViewById(R.id.column_raw2b);

            v_raw_scores[hole][NewGameData.NAME1A] = (TextView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.score_raw);
            v_raw_scores[hole][NewGameData.NAME1B] = (TextView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.score_raw);
            v_raw_scores[hole][NewGameData.NAME2A] = (TextView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.score_raw);
            v_raw_scores[hole][NewGameData.NAME2B] = (TextView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.score_raw);

            // Handles to stroke indicators
            v_indiv_strokes_1[hole][NewGameData.NAME1A] = (ImageView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.indiv_strokes_1);
            v_indiv_strokes_1[hole][NewGameData.NAME1B] = (ImageView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.indiv_strokes_1);
            v_indiv_strokes_1[hole][NewGameData.NAME2A] = (ImageView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.indiv_strokes_1);
            v_indiv_strokes_1[hole][NewGameData.NAME2B] = (ImageView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.indiv_strokes_1);

            v_indiv_strokes_2[hole][NewGameData.NAME1A] = (ImageView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.indiv_strokes_2);
            v_indiv_strokes_2[hole][NewGameData.NAME1B] = (ImageView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.indiv_strokes_2);
            v_indiv_strokes_2[hole][NewGameData.NAME2A] = (ImageView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.indiv_strokes_2);
            v_indiv_strokes_2[hole][NewGameData.NAME2B] = (ImageView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.indiv_strokes_2);

            v_match_strokes_1[hole][NewGameData.NAME1A] = (ImageView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.match_strokes_1);
            v_match_strokes_1[hole][NewGameData.NAME1B] = (ImageView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.match_strokes_1);
            v_match_strokes_1[hole][NewGameData.NAME2A] = (ImageView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.match_strokes_1);
            v_match_strokes_1[hole][NewGameData.NAME2B] = (ImageView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.match_strokes_1);

            v_match_strokes_2[hole][NewGameData.NAME1A] = (ImageView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.match_strokes_2);
            v_match_strokes_2[hole][NewGameData.NAME1B] = (ImageView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.match_strokes_2);
            v_match_strokes_2[hole][NewGameData.NAME2A] = (ImageView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.match_strokes_2);
            v_match_strokes_2[hole][NewGameData.NAME2B] = (ImageView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.match_strokes_2);

            v_indiv_win[NewGameData.NAME1A][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.indiv_win);
            v_indiv_win[NewGameData.NAME1B][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.indiv_win);
            v_indiv_win[NewGameData.NAME2A][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.indiv_win);
            v_indiv_win[NewGameData.NAME2B][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.indiv_win);

            v_match_win[NewGameData.NAME1A][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw1a).findViewById(R.id.match_win);
            v_match_win[NewGameData.NAME1B][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw1b).findViewById(R.id.match_win);
            v_match_win[NewGameData.NAME2A][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw2a).findViewById(R.id.match_win);
            v_match_win[NewGameData.NAME2B][hole] = (TextView) v_column[hole].findViewById(R.id.column_raw2b).findViewById(R.id.match_win);

            v_column[hole].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // calculate previous hole scoring
                    scoreHole(curr_hole);
                    highlightHole(curr_hole, false);

                    curr_hole = v.getId();

                    //highlight column
                    highlightHole(curr_hole, true);
                }
            });
        }

        // SUM COLUMN
        v_sum_column = (LinearLayout) findViewById(R.id.sum_column);
        v_sum_header = (TextView) v_sum_column.findViewById(R.id.column_hole);

        // player totals
        v_player_sum = new TextView[NewGameData.NUM_PLAYERS];
        v_player_sum[NewGameData.NAME1A] = (TextView) v_sum_column.findViewById(R.id.column_raw1a).findViewById(R.id.score_raw);
        v_player_sum[NewGameData.NAME1B] = (TextView) v_sum_column.findViewById(R.id.column_raw1b).findViewById(R.id.score_raw);
        v_player_sum[NewGameData.NAME2A] = (TextView) v_sum_column.findViewById(R.id.column_raw2a).findViewById(R.id.score_raw);
        v_player_sum[NewGameData.NAME2B] = (TextView) v_sum_column.findViewById(R.id.column_raw2b).findViewById(R.id.score_raw);



        v_sum_header.setText("T");
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            v_player_sum[player].setBackgroundColor(ContextCompat.getColor(ctx, R.color.sum_bg));
        }

        // Get scores
        for (int hole = 0; hole <= hole_end - hole_start; hole++) {
            v_holes[hole].setText(Integer.toString(hole + hole_start + 1));
            v_pars[hole].setText(Integer.toString(pars[hole + hole_start]));
            v_ranks[hole].setText(Integer.toString(ranking[hole + hole_start]));

            for (int player = 0; player < 4; player++) {
                v_raw_scores[hole][player].setText(Integer.toString(scores[player][hole]));
            }
        }

        // Display individual and match strokes
        for (int hole = 0; hole < num_holes; hole++) {
            for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
                switch(indiv_strokes[player][hole]) {
                    case 0:
                        // do nothing
                        break;
                    case 1:
                        v_indiv_strokes_1[hole][player].setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        v_indiv_strokes_1[hole][player].setVisibility(View.VISIBLE);
                        v_indiv_strokes_2[hole][player].setVisibility(View.VISIBLE);
                        break;
                }

                switch(match_strokes[player][hole]) {
                    case 0:
                        // do nothing
                        break;
                    case 1:
                        v_match_strokes_1[hole][player].setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        v_match_strokes_1[hole][player].setVisibility(View.VISIBLE);
                        v_match_strokes_2[hole][player].setVisibility(View.VISIBLE);
                        break;
                }
            }
        }

        v_scroll = (RelativeLayout) findViewById (R.id.scroll_region);
        v_scroll.setOnTouchListener(new View.OnTouchListener() {
            float x0 = 0;
            float y0 = 0;

            int start = 0;

            int num_segments = 20;  // should be multiple of number of players
            int segment0;
            int segment;
            boolean ignore = false;

            private int calcSegment(float x , float y) {
                double angle = Math.toDegrees( Math.atan2( y0 - y, x - x0) );
                if (angle < 0) {
                    angle = angle + 360;
                }
                return (int) Math.floor(angle / 360f * num_segments);
            }

            private double calcRadius(float x, float y) {
                return Math.sqrt( Math.pow((y0-y), 2) + Math.pow((x-x0), 2) );
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // determine middle point on initial touch
                        x0 = v.getWidth() / 2;
                        y0 = v.getHeight() / 2;

                        double radius = calcRadius(event.getX(), event.getY());
                        Log.d("RADIUS", "Radius: " + radius + ", Max: " + y0/2);
                        if ( radius < y0/2 ) {
                            ignore = true;
                        } else {
                            ignore = false;
                        }

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

                        highlightName(start, true);

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!ignore) {
                            segment = calcSegment(event.getX(), event.getY());
                            if (segment == segment0 + 1) {
                                decrementScore(start);
                            } else if (segment == segment0 - 1) {
                                incrementScore(start);
                            } else if ((segment0 == 0) && (segment == num_segments - 1)) {
                                incrementScore(start);
                            } else if ((segment0 == num_segments - 1) && (segment == 0)) {
                                decrementScore(start);
                            }
                            segment0 = segment;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!ignore) {
                            // commit data to database
                            saveHoleData(start);
                        }

                        highlightName(start, false);

                        break;
                }
                return true;
            }
        });

        for (int hole = 0; hole < num_holes; hole++) {
            scoreHole(hole);
        }
    }

    private void openDatabaseAdapter() {
        adapter = new DbAdapter(this);
        adapter.open();
    }

    private void incrementScore(int position) {
        int score = getCurrScore(position);
        v_raw_scores[curr_hole][position].setText(Integer.toString(score + 1));
    }

    private void decrementScore(int position) {
        int score = getCurrScore(position);
        if (score == 0) {
            v_raw_scores[curr_hole][position].setText(Integer.toString(0));
        } else {
            v_raw_scores[curr_hole][position].setText(Integer.toString(score - 1));
        }
    }

    private int getCurrScore (int position) {
        int prev_score;

        // get current score
        try {
            prev_score = Integer.parseInt( v_raw_scores[curr_hole][position].getText().toString() );
        } catch (NumberFormatException nfe) {
            // should be a "-"  treat as 0
            prev_score = 0;
        }

        return prev_score;
    }

    private void highlightName(int player, boolean toggle) {
        if (toggle) {
            v_score_cards[player][curr_hole].setCardElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, Resources.getSystem().getDisplayMetrics()));
        } else {
            v_score_cards[player][curr_hole].setCardElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, Resources.getSystem().getDisplayMetrics()));
        }
    }

    private void highlightHole(int hole, boolean toggle) {
        if (toggle) {
            v_holes[hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.column_highlight));
            v_holes[hole].setTextColor(Color.WHITE);

            v_pars[hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.column_highlight_accent));
            v_ranks[hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.column_highlight_accent));
        } else {
            v_holes[hole].setBackgroundColor(Color.TRANSPARENT);
            v_holes[hole].setTextColor(Color.BLACK);

            v_pars[hole].setBackgroundColor(Color.TRANSPARENT);
            v_ranks[hole].setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void saveHoleData(int player) {
        // get hole values
        int score = getCurrScore(player);

        adapter.saveHoleData(gameData.score_id[player], player, curr_hole, score);
        scores[player][curr_hole] = score;
    }

    private int[] sortRanking(int[] hdcp, int sides) {
        int start  = 0;
        int end    = 0;
        int length = 0;
        int indices[];

        switch(sides) {
            case NewGameData.FRONT9:
                start  = 0;
                end    = 8;
                length = 9;
                break;
            case NewGameData.BACK9:
                start  = 9;
                end    = 17;
                length = 9;
                break;
            case NewGameData.FULL18:
                start  = 0;
                end    = 17;
                length = 18;
                break;
        }

        indices = new int[length];
        int search_index;
        int found_index = 0;

        for (search_index = 1; search_index < 19; search_index++) {
            for (int x = start; x <= end; x++) {
                if (hdcp[x] == search_index) {
                    indices[found_index] = x;
                    found_index++;

                    if (found_index == length) {
                        return indices;
                    }
                }
            }
        }

        return indices;
    }

    private void updateLoss (int player, int hole) {
        indiv_points[player][hole] = 0;
        v_indiv_win[player][hole].setVisibility(View.INVISIBLE);
    }

    private void updateWin(int player, int hole) {
        indiv_points[player][hole] = 1;
        v_indiv_win[player][hole].setVisibility(View.VISIBLE);
        v_indiv_win[player][hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.win));
    }

    private void updateTie(int player, int hole) {
        indiv_points[player][hole] = 0.5;
        v_indiv_win[player][hole].setVisibility(View.VISIBLE);
        v_indiv_win[player][hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.tie));
    }

    private void scoreCompare_Individual(int player1, int player2, int hole) {
        int raw_scores[] = new int[2];

        int FIRST = 0;
        int SECOND = 1;

        // clear existing indicators
        indiv_points[player1][hole] = 0;
        v_indiv_win[player1][hole].setVisibility(View.INVISIBLE);
        indiv_points[player2][hole] = 0;
        v_indiv_win[player2][hole].setVisibility(View.INVISIBLE);

        raw_scores[FIRST] = scores[player1][hole];
        raw_scores[SECOND] = scores[player2][hole];

        if ((raw_scores[FIRST] != 0) && (raw_scores[SECOND] != 0)) {
            // individual scores
            int indiv1 = raw_scores[FIRST] - indiv_strokes[player1][hole];
            int indiv2 = raw_scores[SECOND] - indiv_strokes[player2][hole];

            if (indiv1 < indiv2) {
                // 1A wins
                updateWin(player1, hole);
                updateLoss(player2, hole);
            } else if (indiv1 > indiv2) {
                // 2A wins
                updateLoss(player1, hole);
                updateWin(player2, hole);
            } else {
                // tie
                updateTie(player1, hole);
                updateTie(player2, hole);
            }
        }
    }

    private void scoreCompare_Match(int hole) {
        int hdcp_scores[] = new int[NewGameData.NUM_PLAYERS];

        // first remove any current indicators
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            match_points[player][hole] = 0;
            v_match_win[player][hole].setVisibility(View.INVISIBLE);
        }

        // if any are zero, don't score
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            if (scores[player][hole] == 0) {
                return;
            }

            hdcp_scores[player] = scores[player][hole] - match_strokes[player][hole];
        }

        // compare mins
        int team1 = Math.min(hdcp_scores[NewGameData.NAME1A], hdcp_scores[NewGameData.NAME1B]);
        int team2 = Math.min(hdcp_scores[NewGameData.NAME2A], hdcp_scores[NewGameData.NAME2B]);

        if (team1 < team2) {
            // match point to team 1
            if (hdcp_scores[NewGameData.NAME1A] < hdcp_scores[NewGameData.NAME1B]) {
                // A player wins hole
                match_points[NewGameData.NAME1A][hole] = 1;
                v_match_win[NewGameData.NAME1A][hole].setVisibility(View.VISIBLE);
                v_match_win[NewGameData.NAME1A][hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.match_win));
            } else {
                // B player wins hole
                match_points[NewGameData.NAME1B][hole] = 1;
                v_match_win[NewGameData.NAME1B][hole].setVisibility(View.VISIBLE);
                v_match_win[NewGameData.NAME1B][hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.match_win));
            }
        } else if (team1 > team2) {
            // match point to team 2
            if (hdcp_scores[NewGameData.NAME2A] < hdcp_scores[NewGameData.NAME2B]) {
                // A player wins hole
                match_points[NewGameData.NAME2A][hole] = 1;
                v_match_win[NewGameData.NAME2A][hole].setVisibility(View.VISIBLE);
                v_match_win[NewGameData.NAME2A][hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.match_win));
            } else {
                // B player wins hole
                match_points[NewGameData.NAME2B][hole] = 1;
                v_match_win[NewGameData.NAME2B][hole].setVisibility(View.VISIBLE);
                v_match_win[NewGameData.NAME2B][hole].setBackgroundColor(ContextCompat.getColor(ctx, R.color.match_win));
            }
        } else {
            // tie.  no points
            for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
                match_points[player][hole] = 0;
            }
        }
    }

    private void updateTotals_Team() {
        int sum1 = 0;
        int sum2 = 0;

        for (int hole = 0; hole < num_holes; hole++) {
            sum1 += match_points[NewGameData.NAME1A][hole] + match_points[NewGameData.NAME1B][hole];
            sum2 += match_points[NewGameData.NAME2A][hole] + match_points[NewGameData.NAME2B][hole];
        }

        // update textviews
        v_team_totals[0].setText(Integer.toString(sum1));
        v_team_totals[1].setText(Integer.toString(sum2));
    }

    private void updateTotals_Indiv() {
        double sum[] = new double[NewGameData.NUM_PLAYERS];
        int hole_sum[] = new int[NewGameData.NUM_PLAYERS];

        // init to zero
        for (int x = 0; x < NewGameData.NUM_PLAYERS; x++) {
            sum[x] = 0.0;
            hole_sum[x] = 0;
        }

        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            for (int hole = 0; hole < num_holes; hole++) {
                sum[player] += indiv_points[player][hole];
                hole_sum[player] += scores[player][hole];
            }

            v_indiv_totals[player].setText(String.valueOf(sum[player]));
            v_player_sum[player].setText(String.valueOf(hole_sum[player]));
        }
    }

    private void scoreHole(int hole) {
        scoreCompare_Individual(NewGameData.NAME1A, NewGameData.NAME2A, hole);
        scoreCompare_Individual(NewGameData.NAME1B, NewGameData.NAME2B, hole);

        scoreCompare_Match(hole);

        updateTotals_Team();
        updateTotals_Indiv();
    }

}
