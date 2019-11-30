package com.example.a2048;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
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
    CheckBox rememberMe;

    private final String SHARED_PREFERENCES = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorText = findViewById(R.id.errorText);
        rememberMe = findViewById(R.id.rememberMeCheckBox);
        errorText.setVisibility(View.INVISIBLE);

        dbHelper = new DBHelper(this);

        database = dbHelper.getReadableDatabase();
        dbHelper.selectAll(database);

        String savedLogin = loadData("LOGIN");
        String savedPassword = loadData("PASSWORD");
        boolean savedCheckbox = loadData("CHECKBOX").equals("true");

        if(savedCheckbox)
        {
            loginEditText.setText(savedLogin);
            passwordEditText.setText(savedPassword);
            rememberMe.setChecked(savedCheckbox);
        }
    }

    public void registration(View view)
    {
        Intent registration = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(registration);
    }

    public void login(View view)
    {
        String login = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(dbHelper.loginExists(database, login, password))
        {
            saveData(login, password, rememberMe.isChecked());
            errorText.setVisibility(View.INVISIBLE);
            Intent mainMenu = new Intent(getApplicationContext(), MainMenuActivity.class);
            mainMenu.putExtra("login", login);
            int[][] check = dbHelper.loadGameState(database, login).first;
            if(check[0][0] == 9)
                mainMenu.putExtra("hasGame", false);
            else
                mainMenu.putExtra("hasGame", true);

            startActivity(mainMenu);
        }
        else
        {
            errorText.setText("Wrong login or password");
            errorText.setVisibility(View.VISIBLE);
        }
    }

    private void saveData(String login, String password, boolean checked)
    {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("LOGIN", login);
        editor.putString("PASSWORD", password);
        editor.putString("CHECKBOX", String.valueOf(checked));

        editor.apply();
    }

    private String loadData(String pref)
    {
        SharedPreferences sharedPrefer = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        return sharedPrefer.getString(pref, "");
    }
}
