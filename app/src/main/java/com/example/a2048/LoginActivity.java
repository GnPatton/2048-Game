package com.example.a2048;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void startGame(View view)
    {
        Intent game = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(game);
    }

    public void registration(View view)
    {
        Intent registration = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(registration);
    }
}
