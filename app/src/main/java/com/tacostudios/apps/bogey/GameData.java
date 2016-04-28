package com.tacostudios.apps.bogey;

public class GameData {
    private static final int NUM_PLAYERS = 4;

    public int game_id;
    public String date;
    public int course_id;
    public String names[];
    public int handicaps[];
    public int sides;
    public int score_id[];

    public GameData() {
        names = new String[NUM_PLAYERS];
        handicaps = new int[NUM_PLAYERS];
        score_id = new int[NUM_PLAYERS];
    }
}
