package com.tacostudios.apps.bogey;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    Context ctx;
    public static SQLiteDatabase database;

    // private static String DB_PATH;
    private File dbFile;
    private static final String DB_NAME = "bogey.db";
    private static final int DB_VERSION = 2;

    DatabaseHelper (Context context) {
        super (context, DB_NAME, null, DB_VERSION);
        ctx = context;

        // DB_PATH = ctx.getFilesDir().getPath();
        // DB_PATH = ctx.getDatabasePath(DB_NAME).getPath();
        dbFile = ctx.getDatabasePath(DB_NAME);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        // do nothing here
    }

    @Override
    public void onUpgrade (SQLiteDatabase arg0, int arg1, int arg2) {
        // do nothing here
    }

    @Override
    public synchronized void close () {
        if (database != null) {
            database.close();
        }

        super.close();
    }

    public void openDatabase () throws SQLiteException {
        // String fullPath = DB_PATH + DB_NAME;
        try {
            database = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e){
            throw new Error("Could not open database.");
        }
    }

    public void createDatabase () throws IOException {
        boolean exists = checkDb();
        // String fullPath = DB_PATH + DB_NAME;

        if (exists) {
            // do nothing
            // ctx.deleteDatabase (dbFile.getPath());
        } else {
            // create a database in the default directory
            this.getReadableDatabase();

            try {
                copyDb ();
            } catch (IOException e) {
                throw new Error("Could not copy database.");
            }
        }
    }

    public boolean checkDb() {
        SQLiteDatabase check = null;
        // String fullPath = DB_PATH + DB_NAME;

        try {
            check = SQLiteDatabase.openDatabase (dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // database does not exist
        }

        if (check != null) {
            check.close();
            return true;
        } else {
            return false;
        }
    }

    public void copyDb () throws IOException {
        // String fullPath = DB_PATH + DB_NAME;

        int buffer_length;
        byte buffer[] = new byte[1024];

        // open streams
        InputStream myInput = ctx.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(dbFile);

        // copy
        while ( (buffer_length = myInput.read(buffer)) > 0 ) {
            myOutput.write(buffer, 0, buffer_length);
        }

        // close streams
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

}
