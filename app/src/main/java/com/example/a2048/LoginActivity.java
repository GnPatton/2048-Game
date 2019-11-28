package com.example.a2048;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity
{
    public static final int REGISTRATION_ACTIVITY_CODE = 1;
    private DBHelper dbHelper;
    SQLiteDatabase database;

    EditText loginEditText;
    EditText passwordEditText;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorText = findViewById(R.id.errorText);

        dbHelper = new DBHelper(this);

        database = dbHelper.getReadableDatabase();
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

    public void login(View view)
    {
        Cursor cursor = database.query(DBHelper.TABLE_ACCOUNTS, null, null, null, null, null, null);

        if(cursor.moveToFirst())
        {
            int loginIndex = cursor.getColumnIndex(DBHelper.LOGIN);
            int passwordIndex = cursor.getColumnIndex(DBHelper.PASSWORD);
            do
            {
                String login = cursor.getString(loginIndex);
                String password = cursor.getString(passwordIndex);

                if(loginEditText.getText().toString().equals(login) && passwordEditText.getText().toString().equals(password))
                {
                    Intent game = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(game);
                    break;
                }
            }
            while (cursor.moveToNext());
            errorText.setText("Wrong login or password");
        }
        else
            Toast.makeText(this, "No rows", Toast.LENGTH_SHORT).show();
        cursor.close();
    }
}
