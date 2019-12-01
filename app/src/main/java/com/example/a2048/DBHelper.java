package com.example.a2048;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class DBHelper extends SQLiteOpenHelper
{
    private static final String SHARED_PREFERENCES = "sharedPrefs";
    private final String LOGIN_PREF = "LOGIN";
    private final String PASSWORD_PREF = "LOGIN";
    private final String CHECKBOX = "CHECKBOX";

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "gameDB";
    public static final String TABLE_ACCOUNTS = "user";
    public static final String TABLE_GAME = "game";

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String PHOTO = "photo";

    public static final String STATE = "state";
    public static final String SCORE = "score";
    public static final String IS_WIN = "isWin";
    public static final String IS_LOSE = "isLose";
    public static final String USER_LOGIN = "user_login";

    private final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS + "(" + LOGIN +
            " VARCHAR(30) PRIMARY KEY," + PASSWORD + " VARCHAR(50)," + PHOTO  + " BLOB" + ")";

    private final String CREATE_GAME_TABLE = "CREATE TABLE " + TABLE_GAME + "(" + SCORE +
            " INTEGER NOT NULL," + STATE + " VARCHAR(20)," + IS_WIN + " INTEGER NOT NULL," +
            IS_LOSE + " INTEGER NOT NULL," + USER_LOGIN + " VARCHAR(30)," +
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

    public static boolean loginExists(SQLiteDatabase database, String loginIn, String passwordIn)
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

    public void createAccount(SQLiteDatabase database, String login, String password, Context context)
    {
        ContentValues contentValues = new ContentValues();
        Bitmap photo = BitmapFactory.decodeResource(context.getResources(), R.drawable.user);
        byte[] data = getBitmapAsByteArray(photo);

        contentValues.put(LOGIN, login);
        contentValues.put(PASSWORD, password);
        contentValues.put(PHOTO, data);

        database.insert(TABLE_ACCOUNTS, null, contentValues);
        contentValues.clear();

        contentValues.put(SCORE, 0);
        contentValues.put(STATE, "");
        contentValues.put(IS_LOSE, "0");
        contentValues.put(IS_WIN, "0");
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
        Cursor cursor = database.query(DBHelper.TABLE_GAME, null, "user_login=" + "'" + login + "'" + " AND " + "isLose=0" + " AND " + " isWin=0", null, null, null, null);
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
                {
                    out[0][0] = 9; //Костыль пиздец
                    break;
                }
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
            int wonIndex = cursor.getColumnIndex(IS_WIN);
            int lostIndex = cursor.getColumnIndex(IS_LOSE);
            int loginIndex = cursor.getColumnIndex(USER_LOGIN);
            do
            {
                String login = cursor.getString(loginIndex);
                String score = cursor.getString(scoreIndex);
                String state = cursor.getString(stateIndex);
                String wined = cursor.getString(wonIndex);
                String lost = cursor.getString(lostIndex);
                Log.d("select", "Login: " + login + " State: " + state + " Score: " + score + " Wined: " + wined + " Lost: " + lost);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImage(SQLiteDatabase database, String loginIn)
    {
        Cursor cursor = database.query(TABLE_ACCOUNTS, null, null, null, null, null, null);

        if(cursor.moveToFirst())
        {
            int loginIndex = cursor.getColumnIndex(LOGIN);
            int photoIndex = cursor.getColumnIndex(PHOTO);
            do
            {
                String login = cursor.getString(loginIndex);
                byte[] photo = cursor.getBlob(photoIndex);

                if(loginIn.equals(login))
                    return BitmapFactory.decodeByteArray(photo, 0, photo.length);;
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return null;
    }

    public static void updateUserPhoto(SQLiteDatabase database, String login, Bitmap bitmap)
    {
        ContentValues contentValues = new ContentValues();
        byte[] data = getBitmapAsByteArray(bitmap);

        contentValues.put(PHOTO, data);

        database.update(TABLE_ACCOUNTS, contentValues, LOGIN + "=" + "'" + login + "'", null);
    }

    public static void updateUserLogin(SQLiteDatabase database, String oldLogin, String newLogin)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LOGIN, newLogin);

        database.update(TABLE_ACCOUNTS, contentValues, LOGIN + "=" + "'" + oldLogin + "'", null);
        contentValues.clear();

        contentValues.put(USER_LOGIN, newLogin);
        database.update(TABLE_GAME, contentValues, USER_LOGIN + "=" + "'" + oldLogin + "'", null);
    }

    public static void updateUserPassword(SQLiteDatabase database, String loginIn, String newPassword)
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PASSWORD, newPassword);

        database.update(TABLE_ACCOUNTS, contentValues, LOGIN + "=" + "'" + loginIn + "'", null);
    }

    public static Pair<Integer, Integer> selectGamesResult(SQLiteDatabase database, String loginIn)
    {
        Cursor cursor = database.query(TABLE_GAME, null, null, null, null, null, null);
        int maxScore = 0;
        int count = 0;

        if(cursor.moveToFirst())
        {
            int loginIndex = cursor.getColumnIndex(USER_LOGIN);
            int scoreIndex = cursor.getColumnIndex(SCORE);
            int isLostIndex = cursor.getColumnIndex(IS_LOSE);
            int isWonIndex = cursor.getColumnIndex(IS_WIN);
            do
            {
                String login = cursor.getString(loginIndex);
                int score = cursor.getInt(scoreIndex);
                int lost = cursor.getInt(isLostIndex);
                int won = cursor.getInt(isWonIndex);

                if(loginIn.equals(login) && lost==0 && won==1)
                {
                    if(score>maxScore)
                        maxScore = score;
                    count++;
                }

            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return new Pair<>(maxScore, count);
    }

    public static void insert(SQLiteDatabase database, String login)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_LOGIN, login);
        contentValues.put(SCORE, 456);
        contentValues.put(IS_LOSE, 0);
        contentValues.put(IS_WIN, 1);
        contentValues.put(STATE, "");

        database.insert(TABLE_GAME, null, contentValues);
    }



}
