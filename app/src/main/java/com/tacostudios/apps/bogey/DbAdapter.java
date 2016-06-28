package com.tacostudios.apps.bogey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DbAdapter {
    Context ctx;
    private DatabaseHelper helper;

    String DB_TABLE_COURSES = "Course";
    String KEY_COURSES_ID = "_id";
    String KEY_COURSES_NAME = "name";
    String KEY_COURSES_DESC = "desc";
    String KEY_COURSES_PARID = "par_id";
    String KEY_COURSES_HDCPID = "hdcp_id";

    String DB_TABLE_GAMES = "Game";
    String KEY_GAMES_ID = "_id";
    String KEY_GAMES_DATE = "date";
    String KEY_GAMES_COURSEID = "course_id";
    String KEY_GAMES_SIDES = "sides";
    String KEY_GAMES_NAME1A = "name_1a";
    String KEY_GAMES_NAME1B = "name_1b";
    String KEY_GAMES_NAME2A = "name_2a";
    String KEY_GAMES_NAME2B = "name_2b";
    String KEY_GAMES_HDCP1A = "hdcp_1a";
    String KEY_GAMES_HDCP1B = "hdcp_1b";
    String KEY_GAMES_HDCP2A = "hdcp_2a";
    String KEY_GAMES_HDCP2B = "hdcp_2b";
    String KEY_GAMES_SCORE1A = "score_id_1a";
    String KEY_GAMES_SCORE1B = "score_id_1b";
    String KEY_GAMES_SCORE2A = "score_id_2a";
    String KEY_GAMES_SCORE2B = "score_id_2b";

    String DB_TABLE_SCORES = "Score";
    String KEY_SCORE_ID = "_id";

    String DB_TABLE_PAR = "Par";
    String KEY_PAR_ID = "_id";

    String DB_TABLE_HDCP = "HDCP";
    String KEY_HDCP_ID = "_id";

    String KEY_HOLE_01 = "hole_01";
    String KEY_HOLE_02 = "hole_02";
    String KEY_HOLE_03 = "hole_03";
    String KEY_HOLE_04 = "hole_04";
    String KEY_HOLE_05 = "hole_05";
    String KEY_HOLE_06 = "hole_06";
    String KEY_HOLE_07 = "hole_07";
    String KEY_HOLE_08 = "hole_08";
    String KEY_HOLE_09 = "hole_09";
    String KEY_HOLE_10 = "hole_10";
    String KEY_HOLE_11 = "hole_11";
    String KEY_HOLE_12 = "hole_12";
    String KEY_HOLE_13 = "hole_13";
    String KEY_HOLE_14 = "hole_14";
    String KEY_HOLE_15 = "hole_15";
    String KEY_HOLE_16 = "hole_16";
    String KEY_HOLE_17 = "hole_17";
    String KEY_HOLE_18 = "hole_18";

    GamesList gamesList;
    CourseList courseList;

    public DbAdapter(Context context) {
        ctx = context;

        gamesList = new GamesList();
        courseList = new CourseList();
    }

    public DbAdapter open() throws SQLiteException {
        helper = new DatabaseHelper(ctx);

        // create database
        try {
            helper.createDatabase();
        } catch (IOException e){
            throw new Error ("Unable to create database");
        }

        // open database
        try {
            helper.openDatabase();
        } catch (SQLiteException e) {
            throw e;
        }

        return this;
    }

    public void close() {
        helper.close();
    }

    public GamesList getGamesList() {
        int num_games;

        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_GAMES,      // table
                new String[] {KEY_GAMES_ID, KEY_GAMES_DATE, KEY_GAMES_SIDES, KEY_GAMES_COURSEID,
                        KEY_GAMES_NAME1A, KEY_GAMES_NAME1B, KEY_GAMES_NAME2A, KEY_GAMES_NAME2B},
                // columns
                null,                // selection
                null,                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );

        cursor.moveToFirst();

        num_games = cursor.getCount();
        gamesList.initialize(num_games);

        for (int game = 0; game < num_games; game++) {
            gamesList.game_id[game] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_ID));

            gamesList.dates[game] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_DATE));
            gamesList.course_id[game] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_COURSEID));

            gamesList.player_names[GamesList.NAME1A][game] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME1A));
            gamesList.player_names[GamesList.NAME1B][game] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME1B));
            gamesList.player_names[GamesList.NAME2A][game] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME2A));
            gamesList.player_names[GamesList.NAME2B][game] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME2B));

            gamesList.sides[game] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_SIDES));

            cursor.moveToNext();
        }

        cursor.close();

        return gamesList;
    }

    public CourseList getCourseList() {
        int num_courses;

        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_COURSES,    // table
                new String[] {KEY_COURSES_ID, KEY_COURSES_NAME, KEY_COURSES_DESC},
                // columns
                null,                // selection
                null,                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );

        cursor.moveToFirst();

        num_courses = cursor.getCount();
        courseList.initialize(num_courses);

        for (int course = 0; course < num_courses; course++) {
            courseList.courseID[course] = cursor.getInt(cursor.getColumnIndex(KEY_COURSES_ID));
            courseList.names[course] = cursor.getString(cursor.getColumnIndex(KEY_COURSES_NAME));
            courseList.desc[course] = cursor.getString(cursor.getColumnIndex(KEY_COURSES_DESC));

            cursor.moveToNext();
        }

        cursor.close();

        return courseList;
    }

    public long createGame (NewGameData gameData) {
        ContentValues values = new ContentValues();

        values.put(KEY_HOLE_01, 0);
        values.put(KEY_HOLE_02, 0);
        values.put(KEY_HOLE_03, 0);
        values.put(KEY_HOLE_04, 0);
        values.put(KEY_HOLE_05, 0);
        values.put(KEY_HOLE_06, 0);
        values.put(KEY_HOLE_07, 0);
        values.put(KEY_HOLE_08, 0);
        values.put(KEY_HOLE_09, 0);
        values.put(KEY_HOLE_10, 0);
        values.put(KEY_HOLE_11, 0);
        values.put(KEY_HOLE_12, 0);
        values.put(KEY_HOLE_13, 0);
        values.put(KEY_HOLE_14, 0);
        values.put(KEY_HOLE_15, 0);
        values.put(KEY_HOLE_16, 0);
        values.put(KEY_HOLE_17, 0);
        values.put(KEY_HOLE_18, 0);

        long score_id[] = new long[NewGameData.NUM_PLAYERS];
        for (int player = 0; player < NewGameData.NUM_PLAYERS; player++) {
            score_id[player] = helper.database.insert(DB_TABLE_SCORES, null, values);
        }


        values = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd-yyyy");
        Date today = Calendar.getInstance().getTime();
        String date = formatter.format(today);
        values.put(KEY_GAMES_DATE, date);

        values.put(KEY_GAMES_COURSEID, gameData.courseID);
        values.put(KEY_GAMES_SIDES, gameData.sides);

        values.put(KEY_GAMES_SCORE1A, score_id[0]);
        values.put(KEY_GAMES_SCORE1B, score_id[1]);
        values.put(KEY_GAMES_SCORE2A, score_id[2]);
        values.put(KEY_GAMES_SCORE2B, score_id[3]);

        values.put(KEY_GAMES_NAME1A, gameData.names[NewGameData.NAME1A]);
        values.put(KEY_GAMES_NAME1B, gameData.names[NewGameData.NAME1B]);
        values.put(KEY_GAMES_NAME2A, gameData.names[NewGameData.NAME2A]);
        values.put(KEY_GAMES_NAME2B, gameData.names[NewGameData.NAME2B]);

        values.put(KEY_GAMES_HDCP1A, gameData.handicaps[NewGameData.NAME1A]);
        values.put(KEY_GAMES_HDCP1B, gameData.handicaps[NewGameData.NAME1B]);
        values.put(KEY_GAMES_HDCP2A, gameData.handicaps[NewGameData.NAME2A]);
        values.put(KEY_GAMES_HDCP2B, gameData.handicaps[NewGameData.NAME2B]);

        long row = helper.database.insert(DB_TABLE_GAMES, null, values);

        return row;
    }

    public String getCourseName (int id) {
        String name;

        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_COURSES,    // table
                new String[] {KEY_COURSES_ID, KEY_COURSES_NAME},
                                     // columns
                KEY_COURSES_ID + "=?",
                                     // selection
                new String[] {"" + id},
                                     // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );
        cursor.moveToFirst();
        name = cursor.getString(cursor.getColumnIndex(KEY_COURSES_NAME));

        return name;
    }

    public GameData getGameData(int game_id) {
        GameData gameData = new GameData();

        Cursor cursor = helper.database.query (
                false,               // distinct
                DB_TABLE_GAMES,      // table
                new String[] {KEY_GAMES_ID, KEY_GAMES_DATE, KEY_GAMES_COURSEID, KEY_GAMES_SIDES,
                        KEY_GAMES_NAME1A, KEY_GAMES_NAME1B, KEY_GAMES_NAME2A, KEY_GAMES_NAME2B,
                        KEY_GAMES_HDCP1A, KEY_GAMES_HDCP1B, KEY_GAMES_HDCP2A, KEY_GAMES_HDCP2B,
                        KEY_GAMES_SCORE1A, KEY_GAMES_SCORE1B, KEY_GAMES_SCORE2A, KEY_GAMES_SCORE2B },
                // columns
                KEY_COURSES_ID + "=?",
                // selection
                new String[] {"" + game_id},
                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );
        cursor.moveToFirst();

        gameData.game_id = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_ID));
        gameData.date = cursor.getString(cursor.getColumnIndex(KEY_GAMES_DATE));
        gameData.course_id = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_COURSEID));
        gameData.sides = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_SIDES));

        gameData.names[0] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME1A));
        gameData.names[1] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME1B));
        gameData.names[2] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME2A));
        gameData.names[3] = cursor.getString(cursor.getColumnIndex(KEY_GAMES_NAME2B));

        gameData.handicaps[0] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_HDCP1A));
        gameData.handicaps[1] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_HDCP1B));
        gameData.handicaps[2] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_HDCP2A));
        gameData.handicaps[3] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_HDCP2B));

        gameData.score_id[0] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_SCORE1A));
        gameData.score_id[1] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_SCORE1B));
        gameData.score_id[2] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_SCORE2A));
        gameData.score_id[3] = cursor.getInt(cursor.getColumnIndex(KEY_GAMES_SCORE2B));

        cursor.close();

        return gameData;
    }

    public int[] getScores(int score_id) {
        int scores[] = new int[18];

        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_SCORES,     // table
                new String[] {KEY_HOLE_01, KEY_HOLE_02, KEY_HOLE_03,
                              KEY_HOLE_04, KEY_HOLE_05, KEY_HOLE_06,
                              KEY_HOLE_07, KEY_HOLE_08, KEY_HOLE_09,
                              KEY_HOLE_10, KEY_HOLE_11, KEY_HOLE_12,
                              KEY_HOLE_13, KEY_HOLE_14, KEY_HOLE_15,
                              KEY_HOLE_16, KEY_HOLE_17, KEY_HOLE_18 },
                // columns
                KEY_SCORE_ID + "=?",
                // selection
                new String[] {"" + score_id},
                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );
        cursor.moveToFirst();

        scores[0] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_01));
        scores[1] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_02));
        scores[2] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_03));
        scores[3] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_04));
        scores[4] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_05));
        scores[5] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_06));
        scores[6] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_07));
        scores[7] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_08));
        scores[8] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_09));
        scores[9] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_10));
        scores[10] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_11));
        scores[11] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_12));
        scores[12] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_13));
        scores[13] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_14));
        scores[14] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_15));
        scores[15] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_16));
        scores[16] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_17));
        scores[17] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_18));

        return scores;
    }

    public int[] getPars(int course_id) {
        int pars[] = new int[18];

        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_COURSES,    // table
                new String[] {KEY_COURSES_ID, KEY_COURSES_PARID},
                // columns
                KEY_COURSES_ID + "=?",
                // selection
                new String[] {"" + course_id},
                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );
        cursor.moveToFirst();

        int par_id = cursor.getInt(cursor.getColumnIndex(KEY_COURSES_PARID));
        cursor.close();

        cursor = helper.database.query (
                false,               // distinct
                DB_TABLE_PAR,        // table
                new String[] {KEY_PAR_ID,
                        KEY_HOLE_01, KEY_HOLE_02, KEY_HOLE_03, KEY_HOLE_04, KEY_HOLE_05, KEY_HOLE_06,
                        KEY_HOLE_07, KEY_HOLE_08, KEY_HOLE_09, KEY_HOLE_10, KEY_HOLE_11, KEY_HOLE_12,
                        KEY_HOLE_13, KEY_HOLE_14, KEY_HOLE_15, KEY_HOLE_16, KEY_HOLE_17, KEY_HOLE_18 },
                // columns
                KEY_PAR_ID + "=?",
                // selection
                new String[] {"" + par_id},
                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
                );
        cursor.moveToFirst();

        pars[0] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_01));
        pars[1] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_02));
        pars[2] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_03));
        pars[3] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_04));
        pars[4] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_05));
        pars[5] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_06));
        pars[6] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_07));
        pars[7] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_08));
        pars[8] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_09));
        pars[9] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_10));
        pars[10] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_11));
        pars[11] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_12));
        pars[12] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_13));
        pars[13] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_14));
        pars[14] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_15));
        pars[15] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_16));
        pars[16] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_17));
        pars[17] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_18));

        return pars;
    }

    public void saveHoleData(int score_id, int player, int hole, int score) {
        ContentValues values = new ContentValues();

        String key_string = getKeyString(hole);
        values.put(key_string, score);

        helper.database.update(DB_TABLE_SCORES, values, "_id=?", new String[] {"" + score_id});
    }

    private String getKeyString(int hole) {
        switch (hole) {
            case 0:
                return KEY_HOLE_01;
            case 1:
                return KEY_HOLE_02;
            case 2:
                return KEY_HOLE_03;
            case 3:
                return KEY_HOLE_04;
            case 4:
                return KEY_HOLE_05;
            case 5:
                return KEY_HOLE_06;
            case 6:
                return KEY_HOLE_07;
            case 7:
                return KEY_HOLE_08;
            case 8:
                return KEY_HOLE_09;
            case 9:
                return KEY_HOLE_10;
            case 10:
                return KEY_HOLE_11;
            case 11:
                return KEY_HOLE_12;
            case 12:
                return KEY_HOLE_13;
            case 13:
                return KEY_HOLE_14;
            case 14:
                return KEY_HOLE_15;
            case 15:
                return KEY_HOLE_16;
            case 16:
                return KEY_HOLE_17;
            case 17:
                return KEY_HOLE_18;
        }

        return "";
    }

    public int[] getHoleRank(int handicap_id) {
        int rank[] = new int[18];

        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_HDCP,       // table
                new String[] {KEY_HOLE_01, KEY_HOLE_02, KEY_HOLE_03,
                        KEY_HOLE_04, KEY_HOLE_05, KEY_HOLE_06,
                        KEY_HOLE_07, KEY_HOLE_08, KEY_HOLE_09,
                        KEY_HOLE_10, KEY_HOLE_11, KEY_HOLE_12,
                        KEY_HOLE_13, KEY_HOLE_14, KEY_HOLE_15,
                        KEY_HOLE_16, KEY_HOLE_17, KEY_HOLE_18 },
                // columns
                KEY_HDCP_ID + "=?",
                // selection
                new String[] {"" + handicap_id},
                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
        );
        cursor.moveToFirst();

        rank[0] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_01));
        rank[1] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_02));
        rank[2] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_03));
        rank[3] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_04));
        rank[4] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_05));
        rank[5] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_06));
        rank[6] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_07));
        rank[7] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_08));
        rank[8] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_09));
        rank[9] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_10));
        rank[10] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_11));
        rank[11] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_12));
        rank[12] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_13));
        rank[13] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_14));
        rank[14] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_15));
        rank[15] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_16));
        rank[16] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_17));
        rank[17] = cursor.getInt(cursor.getColumnIndex(KEY_HOLE_18));

        return rank;
    }

    public int getHandicapID(int course_id) {
        Cursor cursor = helper.database.query(
                false,               // distinct
                DB_TABLE_COURSES,    // table
                new String[] {KEY_COURSES_ID, KEY_COURSES_HDCPID},
                // columns
                KEY_COURSES_ID + "=?",
                // selection
                new String[] {"" + course_id},
                // selectionArgs
                null,                // groupBy
                null,                // having
                null,                // orderBy
                null                 // limit
        );
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(KEY_COURSES_HDCPID));
    }
}
