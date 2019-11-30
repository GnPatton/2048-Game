package com.example.a2048;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity implements GestureDetector.OnGestureListener
{
    private GestureDetector detector;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private String login;

    TextView scoreText;
    TextView titleText;
    Field field;
    ImageView[][] squares;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        detector = new GestureDetector(this, this);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        boolean isNewGame = Boolean.valueOf(getIntent().getExtras().get("isNewGame").toString());
        login = getIntent().getExtras().getString("login");

        field = new Field(isNewGame, login, database);

        scoreText = findViewById(R.id.scoreText);
        titleText = findViewById(R.id.titleText);
        squares = new ImageView[4][4];

        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
            {
                String id = "image" + i + "x" + j;
                int resID = getResources().getIdentifier(id, "id", getPackageName());
                squares[i][j] = findViewById(resID);
            }

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
        titleText.setTextSize(60);
        titleText.setText("2048");
        field.reset();
        refresh();
    }

    public void undo(View view)
    {
        field.undo();
        refresh();
    }

    private void createState()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                StringBuilder builder = new StringBuilder();
                for(int i=0; i<4; i++)
                    for(int j=0; j<4; j++)
                    {
                        builder.append(field.getState(i, j));
                        if(i==3 && j== 3)
                            break;
                        else
                            builder.append(",");
                    }

                dbHelper.saveGameState(database, login, builder.toString(), field.getScore());
            }
        }).start();
    }

    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

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
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (velocityX<0 && (Math.abs(velocityX)>Math.abs(velocityY)))              //LEFT
        {
            field.shift(Direction.LEFT);
            refresh();
            createState();
        }
        else if (velocityX>0 && (Math.abs(velocityX)>Math.abs(velocityY)))         //RIGHT
        {
            field.shift(Direction.RIGHT);
            refresh();
            createState();
        }
        if (velocityY<0 && (Math.abs(velocityY)>Math.abs(velocityX)))              //UP
        {
            field.shift(Direction.UP);
            refresh();
            createState();
        }
        else if (velocityY>0 && (Math.abs(velocityY)>Math.abs(velocityX)))         //DOWN
        {
            field.shift(Direction.DOWN);
            refresh();
            createState();
        }
        if(field.isGameOver())
        {
            titleText.setTextSize(40);
            titleText.setText("Game Over");
        }
        return true;
    }
}
