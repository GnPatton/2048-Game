package com.example.a2048;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class AccountSettingsActivity extends Activity
{
    DBHelper dbHelper;
    SQLiteDatabase database;
    Bitmap bitmap;

    private final int GALLERY_REQUEST_CODE = 100;
    ImageView imageView;
    TextView loginText;
    TextView bestScoreText;
    TextView gamePlayedText;
    String login;
    String MIME;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        login = getIntent().getExtras().getString("login", "");
        Pair<Integer, Integer> games = DBHelper.selectGamesResult(database, login);

        imageView = findViewById(R.id.imageView);
        loginText = findViewById(R.id.loginSettingsText);
        bestScoreText = findViewById(R.id.bestScoreText);
        gamePlayedText = findViewById(R.id.gamesPlayedText);

        loginText.setText(login);
        bestScoreText.setText("Best score: " + games.first);
        gamePlayedText.setText("Game played: " + games.second);

        Bitmap img = DBHelper.getImage(database, login);
        imageView.setImageBitmap(img);

        registerForContextMenu(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode==Activity.RESULT_OK)
            switch (requestCode)
            {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    MIME = getMimeType(selectedImage);
                    try
                    {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, MIME, Toast.LENGTH_SHORT).show();
                    imageView.setImageURI(selectedImage);
                    break;
            }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Pick image");
        menu.add(0, v.getId(), 0, "From gallery");
        menu.add(0, v.getId(), 0, "From camera");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item)
    {
        if(item.getTitle() == "From gallery")
        {
            pickImageFromGallery();
        }
        else
        {
            Toast.makeText(this, "todo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getMimeType(Uri uriImage)
    {
        String strMimeType = null;

        Cursor cursor = getApplicationContext().getContentResolver().query(uriImage, new String[]{MediaStore.MediaColumns.MIME_TYPE}, null, null, null);

        if (cursor!=null && cursor.moveToNext())
        {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
    }

    private void pickImageFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void pickImageFromCamera()
    {

    }

    public void saveChanges(View view)
    {
        DBHelper.updateUserPhoto(database, login, bitmap);
    }
}
