package com.example.a2048;

import java.util.Random;

public class Field
{
    private int[][] field;
    private boolean gameOver;

    public Field()
    {
        gameOver = false;
        field = new int[4][4];

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

    public int getState(int x, int y)
    {
        return field[x][y];
    }

    public void setState(int x, int y, int state)
    {
        field[x][y] = state;
    }

    public int[] getColumn(int x)
    {
        return field[x];
    }

    public int[] getLine(int x)
    {
        int[] line = new int[4];

        for(int i=0; i<4; i++)
        {
            line[i] = field[i][x];
        }
        return line;
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
            if(getState(currentX, currentY) == 0)
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

}

