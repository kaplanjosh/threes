package threes.analysis;

import java.util.*;


import threes.*;
import threes.engine.*;

public class BigMerge {
    //method to figure out if any move gets you to a big merge
    //b is the board
    //bigMerge is the tile number you're trying to make
    public static double easyBigMerge(Board b, int bigMergeTile) {
        return recurseBoard(b, bigMergeTile, 1, true);
    }

    public static double easyBigMerge(Board b, int bigMergeTile, int depth) {
        return recurseBoard(b, bigMergeTile, depth, true);
    }

	public static double recurseBoard(Board b, int bigMergeTile, int depth, boolean allOrNone) {
        double topScore = 0;
        Board[] nexts = new Board[4];
        int[] tileMasks = new int[4];

        //move the board all four directions
        for (int i = 0; i < 4; i++) {
            nexts[i] = new Board(b);
            tileMasks[i] = nexts[i].moveDir(i);
            //check if this move made the big merge
            if (nexts[i].getTopTile() >= bigMergeTile) return 1;
        }
        //if we're at the bottom, we didn't make a merge.
        if (depth == 1) return 0;

        //go through all options, sum up the percentages
        for (int tile=1; tile<=3; tile++) {

            double tileMax = Math.max(doAllMoves(nexts[0], tileMasks[0], tile, Game.Direction.LEFT, bigMergeTile, depth, allOrNone),
                    doAllMoves(nexts[1], tileMasks[1], tile, Game.Direction.UP, bigMergeTile, depth, allOrNone));
            tileMax = Math.max(tileMax, doAllMoves(nexts[2], tileMasks[2], tile, Game.Direction.RIGHT, bigMergeTile, depth, allOrNone));
            tileMax = Math.max(tileMax, doAllMoves(nexts[3], tileMasks[3], tile, Game.Direction.DOWN, bigMergeTile, depth, allOrNone));

            topScore += tileMax;
            //topScore += Math.max(
            //		Math.max(
            //				doAllMoves(nexts[0], tileMasks[0], tile, Game.Direction.LEFT, rec, depth),
            //				doAllMoves(nexts[1], tileMasks[1], tile, Game.Direction.UP, rec, depth)),
            //		Math.max(
            //				doAllMoves(nexts[2], tileMasks[2], tile, Game.Direction.RIGHT, rec, depth),
            //				doAllMoves(nexts[3], tileMasks[3], tile, Game.Direction.DOWN, rec, depth)));
        }
        return topScore/3;
    }
    

    private static double doAllMoves(Board b, int mask, int tile, Game.Direction dir, int bigMergeTile, int depth, boolean allOrNone) {
        //average merge score for eah possible tile landing
        double scoreCount = 0;

        //how many tile choices were there? Need the average of all these random options
        int tileOptions = 0;

        //on each row
        for (int i=0; i<4; i++) {
            //see if there is a space to put the tile
			if ((mask & 1<<i) > 0) {                
				tileOptions++;
				if (dir == Game.Direction.LEFT)
					b.overwriteTile(i, 3, tile);
				else if (dir == Game.Direction.RIGHT)
					b.overwriteTile(i, 0, tile);
				else if (dir == Game.Direction.UP)
					b.overwriteTile(3, i, tile);
				else if (dir == Game.Direction.DOWN)
					b.overwriteTile(0, i, tile);
				scoreCount += recurseBoard(b, bigMergeTile, depth-1, allOrNone);
			}
        }
        
        if (allOrNone) {
            if (tileOptions > 0 && scoreCount == tileOptions) return 1;
            else return 0;
        } else {
            return tileOptions > 0 ? scoreCount / tileOptions : 0;
        }
    }
}