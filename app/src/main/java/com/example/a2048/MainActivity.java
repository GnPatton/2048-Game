package com.example.a2048;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends Activity
{
    TextView fieldText;
    Field field;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        field = new Field();
        fieldText = findViewById(R.id.fieldText);
        refresh();
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
}
