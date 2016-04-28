package com.tacostudios.apps.bogey;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateGameActivity extends AppCompatActivity {
    Context ctx;

    DbAdapter adapter;
    CourseAdapter courseAdapter;
    NewGameData gameData;

    Spinner  spinner_course;
    CheckBox checkbox_sides[];
    EditText edittext_names[];
    EditText edittext_handicaps[];
    Button   btn_createGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;

        setContentView(R.layout.activity_create_game);

        openDatabaseAdapter();
        getViewHandles();
        populateCourseList();
    }

    private void getViewHandles() {
        gameData = new NewGameData();

        spinner_course = (Spinner) findViewById(R.id.spinner_course_list);

        checkbox_sides = new CheckBox[2];
        checkbox_sides[NewGameData.FRONT9] = (CheckBox) findViewById(R.id.checkBox_front);
        checkbox_sides[NewGameData.BACK9] = (CheckBox) findViewById(R.id.checkBox_back);

        edittext_names = new EditText[NewGameData.NUM_PLAYERS];
        edittext_names[NewGameData.NAME1A] = (EditText) findViewById(R.id.edit_name1a);
        edittext_names[NewGameData.NAME1B] = (EditText) findViewById(R.id.edit_name1b);
        edittext_names[NewGameData.NAME2A] = (EditText) findViewById(R.id.edit_name2a);
        edittext_names[NewGameData.NAME2B] = (EditText) findViewById(R.id.edit_name2b);

        edittext_handicaps = new EditText[NewGameData.NUM_PLAYERS];
        edittext_handicaps[NewGameData.NAME1A] = (EditText) findViewById(R.id.handicap1A);
        edittext_handicaps[NewGameData.NAME1B] = (EditText) findViewById(R.id.handicap1B);
        edittext_handicaps[NewGameData.NAME2A] = (EditText) findViewById(R.id.handicap2A);
        edittext_handicaps[NewGameData.NAME2B] = (EditText) findViewById(R.id.handicap2B);

        btn_createGame = (Button) findViewById(R.id.btn_create_game);
        btn_createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values and store in gameData struct

                // get course ID
                gameData.courseID = courseAdapter.getCourseID(spinner_course.getSelectedItemPosition());

                // get sides being played
                if ( checkbox_sides[NewGameData.FRONT9].isChecked() && checkbox_sides[NewGameData.BACK9].isChecked() ) {
                    gameData.sides = NewGameData.FULL18;
                } else if ( checkbox_sides[NewGameData.FRONT9].isChecked() ) {
                    gameData.sides = NewGameData.FRONT9;
                } else if ( checkbox_sides[NewGameData.BACK9].isChecked() ) {
                    gameData.sides = NewGameData.BACK9;
                } else {
                    // no boxes checked
                    // user must select a checkbox
                    Snackbar.make(v, "Select which holes you will be playing (i.e. FRONT 9)", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                // get names
                gameData.names[NewGameData.NAME1A] = edittext_names[NewGameData.NAME1A].getText().toString();
                gameData.names[NewGameData.NAME1B] = edittext_names[NewGameData.NAME1B].getText().toString();
                gameData.names[NewGameData.NAME2A] = edittext_names[NewGameData.NAME2A].getText().toString();
                gameData.names[NewGameData.NAME2B] = edittext_names[NewGameData.NAME2B].getText().toString();

                // get handicaps
                gameData.handicaps[NewGameData.NAME1A] = Integer.parseInt(edittext_handicaps[NewGameData.NAME1A].getText().toString());
                gameData.handicaps[NewGameData.NAME1B] = Integer.parseInt(edittext_handicaps[NewGameData.NAME1B].getText().toString());
                gameData.handicaps[NewGameData.NAME2A] = Integer.parseInt(edittext_handicaps[NewGameData.NAME2A].getText().toString());
                gameData.handicaps[NewGameData.NAME2B] = Integer.parseInt(edittext_handicaps[NewGameData.NAME2B].getText().toString());


                // create game in database
                long gameID = adapter.createGame(gameData);

                Log.d("CREATEGAMEACTIVITY", "gameID: " + gameID);

                // launch scorecard activity
                Intent intent = new Intent(ctx, ScorecardActivity.class);
                intent.putExtra(ScorecardActivity.EXTRA_GAME_ID, (int) gameID);
                startActivity(intent);
            }
        });
    }

    private void openDatabaseAdapter() {
        adapter = new DbAdapter(this);
        adapter.open();

        courseAdapter = new CourseAdapter(this, adapter);
    }

    private void populateCourseList() {
        spinner_course.setAdapter(courseAdapter);
    }
}
