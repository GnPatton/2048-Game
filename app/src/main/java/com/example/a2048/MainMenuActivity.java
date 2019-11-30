package com.example.a2048;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainMenuActivity extends Activity
{
    String login;

    TextView greetingText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        login = getIntent().getExtras().getString("login");

        greetingText = findViewById(R.id.greetingText);

        greetingText.setText("Welcome back, " + login);
    }

    public void newGame(View view)
    {
        Intent game = new Intent(getApplicationContext(), GameActivity.class);
        game.putExtra("login", login);
        startActivity(game);
    }
}
