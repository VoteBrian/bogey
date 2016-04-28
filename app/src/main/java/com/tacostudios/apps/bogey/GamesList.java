package com.tacostudios.apps.bogey;

public class GamesList {
    public int[]      game_id;
    public int[]      course_id;
    public String[]   course_names;
    public String[]   dates;
    public int[]      sides;
    public String[][] player_names;

    public static final int FRONT9 = 0;
    public static final int BACK9  = 1;
    public static final int FULL18 = 2;

    public static final int NAME1A = 0;
    public static final int NAME1B = 1;
    public static final int NAME2A = 2;
    public static final int NAME2B = 3;
    public static final int NUM_PLAYERS = 4;

    private int num_games;

    public void initialize(int games) {
        num_games = games;

        game_id = new int[num_games];
        course_id = new int[num_games];
        course_names = new String[num_games];
        dates = new String[num_games];
        sides = new int[num_games];
        player_names = new String[NUM_PLAYERS][num_games];
    }

    public int length() {
        return num_games;
    }
}
