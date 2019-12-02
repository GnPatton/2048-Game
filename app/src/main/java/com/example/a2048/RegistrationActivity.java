package com.example.a2048;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity
{
    private DBHelper dbHelper;

    SQLiteDatabase database;

    TextView errorText;
    EditText loginEditText;
    EditText passwordEditText;
    EditText repeatPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        errorText = findViewById(R.id.errorText);
        errorText.setVisibility(View.INVISIBLE);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);

        dbHelper = new DBHelper(this);

        database = dbHelper.getReadableDatabase();
    }

    public void confirm(View view)
    {
        String login = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(password.length() != 0 && login.length() != 0)
        {
            if(password.equals(repeatPasswordEditText.getText().toString()))
            {
                if(!dbHelper.loginExists(database, login, password))
                {
                    dbHelper.createAccount(database, loginEditText.getText().toString(), passwordEditText.getText().toString(), getApplicationContext());
                    finish();
                }
                else
                {
                    errorText.setText("This login already exists!");
                    errorText.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                errorText.setText("Passwords don't match!");
                errorText.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            errorText.setText("Fill all fields!");
            errorText.setVisibility(View.VISIBLE);
        }
    }
}
