package threes.analysis;

import java.util.*;


import threes.*;
import threes.engine.*;

public class BoardMaker {
    private Board seedBoard;
    private Board currentBoard;
    private final int[] maxCounts = { 12, 7, 7, 7, 7, 7, 5, 4, 3, 0, 0, 0 };
    private int[] actualCounts     =  { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int startX;
    private int startY;
    private int lastX;
    private int lastY;
    private int finalX = 3;
    private int finalY = 0;
    private final int MAX_NUMBER = 9;

    public BoardMaker(Board b, int startXP, int startYP) {
        seedBoard = b;
        currentBoard = new Board(b);
        startX = startXP;
        startY = startYP;
        lastX = startX;
        lastY = startY;
        incrementActualCounts();
    }
    public void setFinalX(int x) { finalX = x; }
    public void setFinalY(int y) { finalY = y; }
    public Board getCurrent() { return currentBoard; }

    public Board getNext() {
        Board retBoard = new Board(currentBoard);
        //move board next
        if (moveBoardNext(startX, startY)) {
            //return what you had
            return retBoard;        
        } else {
            currentBoard = retBoard;
            return null;
        }
    }
    private boolean moveBoardNext(int x, int y) {
//        System.out.println("move board " + x + "," + y);
        int skip = seedBoard.getTileAt(x, y);
        if (skip != 0) {
            if (x == finalX && y == finalY) return false;
            y++;
            if (y == 4) {
                x++;
                if (x == 4) return false;
                y=0;
            }
            return moveBoardNext(x, y);            
        }
        int was = currentBoard.getTileAt(x, y);
        actualCounts[was]--;
        int nextTile = getNextTile(was);
        if (nextTile == -1) return false;
        if (was > nextTile) {
            if (x == finalX && y == finalY) return false;
            currentBoard.overwriteTile(x,y,nextTile);
            actualCounts[nextTile]++;
            y++;
            if (y == 4) {
                x++;
                if (x == 4) return false;
                y=0;
            }
            return moveBoardNext(x, y);
        } else {
            currentBoard.overwriteTile(x, y, nextTile);
            actualCounts[nextTile]++;
            return true;
        }
    }

    private int getNextTile(int wasTile) {
        boolean done = false;
        int nextTile = wasTile+1;
        while (!done && nextTile != wasTile) {
            if (actualCounts[nextTile] >= maxCounts[nextTile]) {
                nextTile++;
                if (nextTile >= MAX_NUMBER) nextTile = 0;
            } else {
                done = true;
            }
        }
        if (done) return nextTile;
        else return -1;
    }
    private void incrementActualCounts() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int tile = seedBoard.getTileAt(i, j);
                if (tile < MAX_NUMBER)
                    actualCounts[tile]++;
            }
        }
    }
}