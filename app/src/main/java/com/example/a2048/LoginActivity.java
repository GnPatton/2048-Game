package com.example.a2048;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivity extends Activity
{
    private DBHelper dbHelper;
    SQLiteDatabase database;

    LoginButton facebookButton;
    CallbackManager callbackManager;

    EditText loginEditText;
    EditText passwordEditText;
    TextView errorText;
    CheckBox rememberMe;
    Bitmap photo;

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
        facebookButton = findViewById(R.id.login_button);

        dbHelper = new DBHelper(this);

        database = dbHelper.getReadableDatabase();
        //dbHelper.insert(database, "kek");
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

        callbackManager = CallbackManager.Factory.create();
        facebookButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException error)
            {

            }
        });
    }

    public void registration(View view)
    {
        Intent registration = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(registration);
    }

    public void getInfo(View view)
    {
        String login = loginEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        login(login, password);
    }

    private void login(String login, String password)
    {
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

            LoginManager.getInstance().logOut();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker()
    {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
        {
            if(currentAccessToken==null)
            {
                Log.d("facebook", "Log out");
            }
            else
            {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken newAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                try
                {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    String login = first_name + " " + last_name;
                    String password = first_name+last_name;

                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                    Log.d("facebook", "Name: " + first_name + " LastName: " + last_name + " id: " + id);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

                    Downloader downloader = new Downloader(LoginActivity.this);
                    try
                    {
                        photo = downloader.execute(image_url).get(2, TimeUnit.SECONDS);
                    }
                    catch (ExecutionException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    } catch (TimeoutException e)
                    {
                        e.printStackTrace();
                    }

                    if(!dbHelper.loginExists(database, login, password))
                    {
                        dbHelper.createAccount(database, login, password, getApplicationContext(), photo);
                        login(login, password);
                    }
                    else
                        login(login, password);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }

//    AsyncTask
//
//    private Bitmap getBitmapFromURL(String src)
//    {
//        try
//        {
//            URL url = new URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            return myBitmap;
//        }
//        catch (IOException e)
//        {
//            return null;
//        }
//    }
}
