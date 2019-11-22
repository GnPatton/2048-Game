package com.example.a2048;

import android.util.Log;

import java.util.Random;

public class Field
{
    private int[][] field;
    private boolean gameOver;

    public Field()
    {
        gameOver = false;
        field = new int[4][4];

        reset();
    }

    public void reset()
    {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                field[i][j] = 0;
    }

    public StringBuilder displayField()
    {
        StringBuilder out = new StringBuilder();
        for(int i=0; i<4; i++)
        {
            for(int j=0; j<4; j++)
            {
                out.append(field[i][j]);
                out.append(" ");
            }
            out.append("\n");
        }
        return out;
    }

    public void setState(int x, int y, int state)
    {
        field[x][y] = state;
    }

    public void createInitialCells()
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
                                if(field[i][k] == field[i][k-1])
                                {
                                    field[i][k] *= 2;
                                    field[i][k-1] = 0;
                                    hasChange = true;
                                }
                            }

                            if(field[i][j] == field[i][j-1] && field[i][j] != 0)
                            {
                                field[i][j] *= 2;
                                field[i][j-1] = 0;
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
                                if(field[i][k] == field[i][k+1])
                                {
                                    field[i][k] *= 2;
                                    field[i][k+1] = 0;
                                    hasChange = true;
                                }
                            }

                            if(field[i][j] == field[i][j+1] && field[i][j+1] != 0)
                            {
                                field[i][j] *= 2;
                                field[i][j+1] = 0;
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
                                    k=0;
                                }
                            }
                            else if(k==3)
                            {
                                if(field[k][i] == field[k-1][i])
                                {
                                    field[k][i] *= 2;
                                    field[k-1][i] = 0;
                                    hasChange = true;
                                }
                            }

                            if(field[j][i] == field[j+1][i] && field[j+1][i] != 0)
                            {
                                field[j][i] *= 2;
                                field[j+1][i] = 0;
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
                                if(field[k][i] == field[k+1][i])
                                {
                                    field[k][i] *= 2;
                                    field[k+1][i] = 0;
                                    hasChange = true;
                                }
                            }

                            if(field[j][i] == field[j+1][i] && field[j+1][i] != 0)
                            {
                                field[j][i] *= 2;
                                field[j+1][i] = 0;
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

