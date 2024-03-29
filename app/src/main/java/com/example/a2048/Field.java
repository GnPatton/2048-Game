package com.example.a2048;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.Random;

public class Field
{
    SQLiteDatabase database;
    String login;
    Sound sound;
    private int score = 0;
    private int previousStateScore = 0;
    private int[][] field;
    private int[][] previousStateField;

    public Field(Context context, boolean isNewGame, String login, SQLiteDatabase database)
    {
        this.database = database;
        this.login = login;

        sound = new Sound(context);
        field = new int[4][4];
        previousStateField = new int[4][4];

        if(isNewGame)
            if(login.equals("test"))
                reset(true);
            else
                reset(false);
        else
            loadGame(login, database);
    }

    private void loadGame(String login, SQLiteDatabase database)
    {
        Pair<int[][], Integer> pair = DBHelper.loadGameState(database, login);
        int[][] prevGame = pair.first;
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                field[i][j] = prevGame[i][j];

        score = pair.second;
    }

    public void reset(boolean winGame)
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                field[i][j] = 0;
        score = 0;
        createInitialCells();

        if(winGame)
        {
            field[3][2] = 1024;
            field[3][3] = 1024;
        }
    }

    public void undo()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                field[i][j] = previousStateField[i][j];

        score = previousStateScore;
    }

    private void copy()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                previousStateField[i][j] = field[i][j];

        previousStateScore = score;
    }

    public int getState(int x, int y)
    {
        return field[x][y];
    }

    public int[] getColumn(int x)
    {
        int[] line = new int[4];

        for(int i=0; i<4; i++)
        {
            line[i] = field[i][x];
        }
        return line;
    }

    public int getScore()
    {
        return score;
    }


    public String displayScore()
    {
        return String.valueOf(score);
    }

    public void setState(int x, int y, int state)
    {
        field[x][y] = state;
    }

    private void createInitialCells()
    {
        for(int i=0; i<Constants.COUNT_INITIAL_CELLS; i++)
        {
            generateNewCell();
        }
    }

    public void generateNewCell()
    {
        int state = (new Random().nextInt(100) <= Constants.CHANCE_OF_LUCKY_SPAWN)
                ? Constants.LUCKY_INITIAL_CELL_STATE : Constants.INITIAL_CELL_STATE;

        int randX, randY;

        randX = new Random().nextInt(4);
        int currentX = randX;

        randY = new Random().nextInt(4);
        int currentY = randY;

        boolean isPlaced = false;
        while(!isPlaced)
        {
            if(field[currentX][currentY] == 0)
            {
                setState(currentX, currentY, state);
                isPlaced = true;
            }
            else
            {
                if(currentX+1 < 4)
                    currentX++;
                else
                {
                    currentX = 0;
                    if(currentY+1 < 4)
                        currentY++;
                    else
                        currentY = 0;
                }

                if(currentX == randX && currentY == randY)
                    break;
            }

        }
    }

    public boolean isGameOver()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<3; j++)
                if(field[i][j]==field[i][j+1] || field[i][j]==0 || field[i][j+1]==0)
                    return false;

        for(int i=0; i<3; i++)
            for(int j=0; j<4; j++)
                if(field[i][j]==field[i+1][j] || field[i][j]==0 || field[i+1][j]==0)
                    return false;

         return true;
    }

    private boolean isWon()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                if(field[i][j] == 2048)
                {
                    DBHelper.createGame(database, login, false, score);
                    return true;
                }
        return false;

    }

    public void shift(Direction direction)
    {
        copy();
        boolean hasChange = false;
        boolean hasMerge = false;

        if(!isWon())
            if(!isGameOver())
            {
                switch(direction)
                {
                    case RIGHT:
                        for(int i=0; i<4; i++)
                        {
                            for(int j=3; j>0; j--)
                            {
                                for(int k=0; k<4; k++)
                                {
                                    if(k<3)
                                    {
                                        if (field[i][k]!=0 && field[i][k+1]==0)
                                        {
                                            int buff;
                                            buff = field[i][k];
                                            field[i][k] = field[i][k+1];
                                            field[i][k+1] = buff;
                                            hasChange = true;
                                            k=0;
                                        }
                                    }
                                    else if(k==3)
                                    {
                                        if(field[i][k] == field[i][k-1] && field[i][k]!=0)
                                        {
                                            field[i][k] *= 2;
                                            field[i][k-1] = 0;
                                            hasChange = true;
                                            hasMerge = true;
                                            score += field[i][k];
                                        }
                                    }

                                    if(field[i][j] == field[i][j-1] && field[i][j] != 0)
                                    {
                                        field[i][j] *= 2;
                                        field[i][j-1] = 0;
                                        score += field[i][j];
                                        j=3;
                                        hasChange = true;
                                        hasMerge = true;
                                    }
                                }
                            }
                        }
                        break;

                    case LEFT:
                        for(int i=0; i<4; i++)
                        {
                            for(int j=0; j<3; j++)
                            {
                                for(int k=3; k>=0; k--)
                                {
                                    if(k>0)
                                    {
                                        if (field[i][k]!=0 && field[i][k-1]==0)
                                        {
                                            int buff;
                                            buff = field[i][k];
                                            field[i][k] = field[i][k-1];
                                            field[i][k-1] = buff;
                                            hasChange = true;
                                            k=3;
                                        }
                                    }
                                    else if(k==0)
                                    {
                                        if(field[i][k] == field[i][k+1] && field[i][k]!=0)
                                        {
                                            field[i][k] *= 2;
                                            field[i][k+1] = 0;
                                            hasChange = true;
                                            hasMerge = true;
                                            score += field[i][k];
                                        }
                                    }

                                    if(field[i][j] == field[i][j+1] && field[i][j+1] != 0)
                                    {
                                        field[i][j] *= 2;
                                        field[i][j+1] = 0;
                                        score += field[i][j];
                                        j=0;
                                        hasChange = true;
                                        hasMerge = true;
                                    }
                                }

                            }
                        }
                        break;

                    case UP:
                        for(int i=0; i<4; i++)
                        {
                            for(int j=0; j<3; j++)
                            {
                                for(int k=0; k<4; k++)
                                {
                                    if(k<3)
                                    {
                                        if (field[k][i]==0 && field[k+1][i]!=0)
                                        {
                                            int buff;
                                            buff = field[k][i];
                                            field[k][i] = field[k+1][i];
                                            field[k+1][i] = buff;
                                            hasChange = true;
                                            k=-1;
                                        }
                                    }
                                    else if(k==3)
                                    {
                                        if(field[k][i] == field[k-1][i] && field[k][i]!=0)
                                        {
                                            field[k][i] *= 2;
                                            field[k-1][i] = 0;
                                            hasChange = true;
                                            hasMerge = true;
                                            score += field[k][i];
                                        }
                                    }

                                    if(field[j][i] == field[j+1][i] && field[j+1][i] != 0)
                                    {
                                        field[j][i] *= 2;
                                        field[j+1][i] = 0;
                                        score += field[j][i];
                                        j=0;
                                        hasChange = true;
                                        hasMerge = true;
                                    }
                                }

                            }
                        }
                        break;

                    case DOWN:
                        for(int i=0; i<4; i++)
                        {
                            for(int j=0; j<3; j++)
                            {
                                for(int k=3; k>=0; k--)
                                {
                                    if(k>0)
                                    {
                                        if (field[k][i]==0 && field[k-1][i]!=0)
                                        {
                                            int buff;
                                            buff = field[k][i];
                                            field[k][i] = field[k-1][i];
                                            field[k-1][i] = buff;
                                            hasChange = true;
                                            k=3;
                                        }
                                    }
                                    else if(k==0)
                                    {
                                        if(field[k][i] == field[k+1][i] && field[k][i]!=0)
                                        {
                                            field[k][i] *= 2;
                                            field[k+1][i] = 0;
                                            hasChange = true;
                                            hasMerge = true;
                                            score += field[k][i];
                                        }
                                    }

                                    if(field[j][i] == field[j+1][i] && field[j+1][i] != 0)
                                    {
                                        field[j][i] *= 2;
                                        field[j+1][i] = 0;
                                        score += field[j][i];
                                        j=0;
                                        hasChange = true;
                                        hasMerge = true;
                                    }
                                }
                            }
                        }
                        break;
                }

                if(hasChange)
                    generateNewCell();

                if(hasMerge && !isWon())
                    sound.playMerge();

                if(hasChange && !hasMerge && !isWon())
                    sound.playSwipe();

                if(isWon())
                    sound.playWin();
                else if(isGameOver())
                    sound.playLose();
            }
    }
}

