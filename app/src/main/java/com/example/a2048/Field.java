package com.example.a2048;

import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.util.Random;

public class Field
{
    private int score = 0;
    private int previousStateScore = 0;
    private int[][] field;
    private int[][] previousStateField;
    private boolean gameOver;

    public Field(boolean isNewGame, String login, SQLiteDatabase database)
    {
        gameOver = false;
        field = new int[4][4];
        previousStateField = new int[4][4];

        if(isNewGame)
            reset();
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

    public void reset()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                field[i][j] = 0;
        score = 0;
        createInitialCells();
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
                {
                    gameOver = true;
                    break;
                }
            }

        }
    }

    public boolean isGameOver()
    {
        return gameOver;
    }

    public void shift(Direction direction)
    {
        copy();
        boolean hasChange = false;

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
                            }
                        }

                    }
                }
                break;
        }
        if(hasChange)
            generateNewCell();
    }
}

