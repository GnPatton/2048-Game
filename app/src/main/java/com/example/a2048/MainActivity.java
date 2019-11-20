package com.example.a2048;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener
{
    GestureDetector detector;

    TextView fieldText;
    Field field;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        detector = new GestureDetector(this, this);
        field = new Field();
        fieldText = findViewById(R.id.fieldText);
        refresh();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return detector.onTouchEvent(event);
    }

    public void next(View view)
    {
        if(count == 0)
        {
            field.setState(2,2,8);
            field.setState(0,1,4);
            field.setState(3,0,2);
            refresh();
            count++;
        }
        else if(count ==1)
        {
            fieldText.setText(Arrays.toString(field.getColumn(0)));
            count++;
        }
        else if(count == 2)
        {
            fieldText.setText(Arrays.toString(field.getLine(0)));
            count = 0;
        }
    }

    private void refresh()
    {
        fieldText.setText(field.displayField());
    }

    public void newCell(View view)
    {
        field.generateNewCell();
        refresh();
        if(field.isGameOver())
            fieldText.setText("Game over");
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
        Toast.makeText(this, "Tap up", Toast.LENGTH_SHORT).show();
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
        Log.d("fling", "VelociryX: " + velocityX);
        Log.d("fling", "VelociryY: " + velocityY);

        if(velocityX<0 && (Math.abs(velocityX) > Math.abs(velocityY)))
            Toast.makeText(this, "LEFT", Toast.LENGTH_SHORT).show();
        else if(velocityX>0 && (Math.abs(velocityX) > Math.abs(velocityY)))
            Toast.makeText(this, "RIGHT", Toast.LENGTH_SHORT).show();

        if(velocityY<0 && (Math.abs(velocityY) > Math.abs(velocityX)))
            Toast.makeText(this, "UP", Toast.LENGTH_SHORT).show();
        else if(velocityY>0 && (Math.abs(velocityY) > Math.abs(velocityX)))
            Toast.makeText(this, "DOWN", Toast.LENGTH_SHORT).show();
        return true;
    }
}
