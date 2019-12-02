package com.example.a2048;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void login(View view)
    {
        Intent loginPage = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginPage);
    }

    public void leaderBoard(View view)
    {
        Intent leaderBoard = new Intent(getApplicationContext(), LeaderBoardActivity.class);
        startActivity(leaderBoard);
    }
}
