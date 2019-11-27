package com.example.a2048;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener
{
    GestureDetector detector;

    TextView scoreText;
    Field field;
    ImageView[][] squares;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector = new GestureDetector(this, this);
        field = new Field();
        scoreText = findViewById(R.id.scoreText);
        squares = new ImageView[4][4];

        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
            {
                String id = "image" + i + "x" + j;
                int resID = getResources().getIdentifier(id, "id", getPackageName());
                squares[i][j] = findViewById(resID);
            }

        field.reset();

        refresh();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
    }

    private void refresh()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
            {
                int id = getResources().getIdentifier("square_" + field.getState(i,j), "drawable", getPackageName());
                squares[i][j].setImageResource(id);
            }
        scoreText.setText(field.displayScore());
    }

    public void restart(View view)
    {
        field.reset();
        refresh();
    }

    public void undo(View view)
    {
        field.undo();
        refresh();
    }

    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e)
    {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e)
    {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (velocityX<0 && (Math.abs(velocityX)>Math.abs(velocityY)))              //LEFT
        {
            field.shift(Direction.LEFT);
            refresh();
        } else if (velocityX>0 && (Math.abs(velocityX)>Math.abs(velocityY)))         //RIGHT
        {
            field.shift(Direction.RIGHT);
            refresh();
        }

        if (velocityY<0 && (Math.abs(velocityY)>Math.abs(velocityX)))              //UP
        {
            field.shift(Direction.UP);
            refresh();
        } else if (velocityY>0 && (Math.abs(velocityY)>Math.abs(velocityX)))         //DOWN
        {
            field.shift(Direction.DOWN);
            refresh();
        }
        return true;
    }
}
