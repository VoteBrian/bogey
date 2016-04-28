package com.tacostudios.apps.bogey;

public class NewGameData {
    public int courseID;
    public int sides;
    public String[] names;
    public int[] handicaps;

    public static final int FRONT9 = 0;
    public static final int BACK9  = 1;
    public static final int FULL18 = 2;

    public static final int NAME1A      = 0;
    public static final int NAME1B      = 1;
    public static final int NAME2A      = 2;
    public static final int NAME2B      = 3;
    public static final int NUM_PLAYERS = 4;

    public NewGameData() {
        names = new String[NUM_PLAYERS];
        handicaps = new int[NUM_PLAYERS];
    }
}
