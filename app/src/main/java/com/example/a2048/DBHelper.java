package com.example.a2048;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import static android.content.Context.MODE_PRIVATE;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String SHARED_PREFERENCES = "sharedPrefs";
    private final String LOGIN_PREF = "LOGIN";
    private final String PASSWORD_PREF = "LOGIN";
    private final String CHECKBOX = "CHECKBOX";

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "gameDB";
    public static final String TABLE_ACCOUNTS = "user";
    public static final String TABLE_GAME = "game";

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String PHOTO = "photo";

    public static final String STATE = "state";
    public static final String SCORE = "score";
    public static final String IS_ENDED = "isEnded";
    public static final String USER_LOGIN = "user_login";

    private final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS + "(" + LOGIN +
            " VARCHAR(30) PRIMARY KEY," + PASSWORD + " VARCHAR(50)," + PHOTO  + " BLOB" + ")";

    private final String CREATE_GAME_TABLE = "CREATE TABLE " + TABLE_GAME + "(" + SCORE +
            " INTEGER NOT NULL," + STATE + " VARCHAR(20)," + IS_ENDED + " INTEGER NOT NULL," +
            USER_LOGIN + " VARCHAR(30)," +
            "FOREIGN KEY" + "(" + USER_LOGIN + ")" + " REFERENCES " + TABLE_ACCOUNTS + "(" + LOGIN + ")" + ")";

    public DBHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_GAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        onCreate(db);
    }

    public boolean loginExists(SQLiteDatabase database, String loginIn, String passwordIn)
    {
        Cursor cursor = database.query(TABLE_ACCOUNTS, null, null, null, null, null, null);

        if(cursor.moveToFirst())
        {
            int loginIndex = cursor.getColumnIndex(LOGIN);
            int passwordIndex = cursor.getColumnIndex(PASSWORD);
            do
            {
                String login = cursor.getString(loginIndex);
                String password = cursor.getString(passwordIndex);

                if(loginIn.equals(login) && passwordIn.equals(password))
                    return true;
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    public void createAccount(SQLiteDatabase database, String login, String password)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LOGIN, login);
        contentValues.put(PASSWORD, password);

        database.insert(TABLE_ACCOUNTS, null, contentValues);
        contentValues.clear();

        contentValues.put(SCORE, 0);
        contentValues.put(STATE, "");
        contentValues.put(IS_ENDED, "0");
        contentValues.put(USER_LOGIN, login);

        database.insert(TABLE_GAME, null, contentValues);
    }

    public void saveGameState(SQLiteDatabase database, String login, String state, int score)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(STATE, state);
        contentValues.put(SCORE, score);

        database.update(TABLE_GAME, contentValues, "user_login=" + "'" + login + "'", null);
    }

    public static Pair<int[][], Integer> loadGameState(SQLiteDatabase database, String login)
    {
        Cursor cursor = database.query(DBHelper.TABLE_GAME, null, "user_login=" + "'" + login + "'", null, null, null, null);
        String state="";
        int score=0;
        if(cursor.moveToFirst())
        {
            int stateIndex = cursor.getColumnIndex(STATE);
            int scoreIndex = cursor.getColumnIndex(SCORE);
            do
            {
                state = cursor.getString(stateIndex);
                score = Integer.valueOf(cursor.getString(scoreIndex));
            }
            while(cursor.moveToNext());
        }

        String[] states = state.split("[,]");

        int[][] out = new int[4][4];
        int idx=0;

        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
            {
                if(!state.equals(""))
                {
                    out[i][j] = Integer.valueOf(states[idx]);
                    idx++;
                }
                else
                    out[i][j] = 9; //Костыль пиздец
            }
        cursor.close();
        return new Pair<>(out, score);
    }

    public void selectAll(SQLiteDatabase database)
    {
        Cursor cursor = database.query(DBHelper.TABLE_GAME, null, null, null, null, null, null);

        if(cursor.moveToFirst())
        {
            int scoreIndex = cursor.getColumnIndex(SCORE);
            int stateIndex = cursor.getColumnIndex(STATE);
            int endedIndex = cursor.getColumnIndex(IS_ENDED);
            int loginIndex = cursor.getColumnIndex(USER_LOGIN);
            do
            {
                String login = cursor.getString(loginIndex);
                String score = cursor.getString(scoreIndex);
                String state = cursor.getString(stateIndex);
                String ended = cursor.getString(endedIndex);
                Log.d("huj", "Login: " + login + " Score: " + score + " State: " + state + " isEnded: " + ended);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
    }

}
