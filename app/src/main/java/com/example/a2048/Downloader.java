package com.example.a2048;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader extends AsyncTask<String, Integer, Bitmap>
{
    private WeakReference<LoginActivity> contextRef;

    public Downloader(LoginActivity activity)
    {
        contextRef = new WeakReference<>(activity);
    }

    @Override
    protected Bitmap doInBackground(String... urls)
    {
        try
        {
            java.net.URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap)
    {
        LoginActivity context = contextRef.get();
        context.photo = bitmap;
    }
}
