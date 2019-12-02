package com.example.a2048;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class LeaderBoardAdapter extends ArrayAdapter<User>
{
    Activity context;
    ArrayList<User> users;

    public LeaderBoardAdapter(Activity context, ArrayList<User> users)
    {
        super(context, R.layout.leaderboard_row, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View row = layoutInflater.inflate(R.layout.leaderboard_row, parent, false);
        ImageView image = row.findViewById(R.id.userImage);
        TextView login = row.findViewById(R.id.loginText1);
        TextView score = row.findViewById(R.id.scoreText1);

        Bitmap bitmap = BitmapFactory.decodeByteArray(users.get(position).photo, 0, users.get(position).photo.length);
        image.setImageBitmap(bitmap);

        login.setText(this.users.get(position).login);
        score.setText(String.valueOf(this.users.get(position).bestScore));

        return row;
    }
}
