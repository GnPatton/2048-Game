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
    ContentValues contentValues;
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
        contentValues = new ContentValues();

        database = dbHelper.getReadableDatabase();
    }

    public void confirm(View view)
    {
        if(passwordEditText.getText().length() != 0 || loginEditText.getText().length() != 0)
        {
            if(passwordEditText.getText().toString().equals(repeatPasswordEditText.getText().toString()))
            {
                contentValues.put(DBHelper.LOGIN, loginEditText.getText().toString());
                contentValues.put(DBHelper.PASSWORD, passwordEditText.getText().toString());

                database.insert(DBHelper.TABLE_ACCOUNTS, null, contentValues);
            }
            else
            {
                errorText.setText("Password don't match!");
                errorText.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            errorText.setText("Fill all fields!");
            errorText.setVisibility(View.VISIBLE);
        }
        finish();
    }
}
