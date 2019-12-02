package com.example.a2048;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends Activity
{
    SQLiteDatabase database;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        ArrayList<User> users = dbHelper.selectLeaderBoard(database);

        ListView lv = findViewById(R.id.listView);
        lv.setAdapter(new LeaderBoardAdapter(this, users));
    }
}
