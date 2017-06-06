package threes.brains;

import com.mongodb.client.model.geojson.GeoJsonObjectType;

import threes.engine.Board;
import threes.engine.BoardUtil;
import threes.engine.Game;
import threes.engine.Utilities;

public class ScoreMoves {
	public static int oneRecurse(Board b, int depth, int nextTile) {
		int rows = BoardUtil.countRowsMove(b);
		int cols = BoardUtil.countColsMove(b);
		double left = 0;
		double right = 0;
		double up = 0;
		double down = 0;
		
		Board bL = new Board(b);
		int leftMasks = bL.moveLeft();
		if (leftMasks > 0) {
			left = doAllMoves(bL, leftMasks, nextTile, Game.Direction.LEFT, 0, depth);
		}

		Board bR = new Board(b);
		int rightMasks = bR.moveRight();
		if (rightMasks > 0) {
			right = doAllMoves(bR, rightMasks, nextTile, Game.Direction.RIGHT, 0, depth);
		}
		
		Board bU = new Board(b);
		int upMasks = bU.moveUp();
		if (upMasks > 0) {
			up = doAllMoves(bU, upMasks, nextTile, Game.Direction.UP, 0, depth);
		}
		
		Board bD = new Board(b);
		int downMasks = bD.moveDown();
		if (downMasks > 0) {
			down = doAllMoves(bD, downMasks, nextTile, Game.Direction.DOWN, 0, depth);
		}

		double hMax = Math.max(left, right);
		double vMax = Math.max(up, down);

		double allMax = Math.max(hMax,vMax);

		int mask = 0;
		double factor = 1.3;
		if (left * factor < allMax)
			mask += 1;
		if (up * factor < allMax)
			mask += 2;
		if (right * factor < allMax)
			mask += 4;
		if (down * factor < allMax)
			mask += 8;
		return mask;
	}
	
	public static double recurseBoardScore(Board b, int rec, int depth) {
		if (rec > depth) return scoreBoard(b);
		else if (BoardUtil.isDeadBoard(b)) return 0;
		else {
			double topScore = 0;
			Board[] nexts = new Board[4];
			int[] tileMasks = new int[4];

			//move the board all four directions
			for (int i = 0; i < 4; i++) {
				nexts[i] = new Board(b);
				tileMasks[i] = nexts[i].moveDir(i);
			}

			//go through all options, sum up the percentages
			for (int tile=1; tile<=3; tile++) {
				double tileMax = Math.max(doAllMoves(nexts[0], tileMasks[0], tile, Game.Direction.LEFT, rec, depth),
						doAllMoves(nexts[1], tileMasks[1], tile, Game.Direction.UP, rec, depth));
				tileMax = Math.max(tileMax, doAllMoves(nexts[2], tileMasks[2], tile, Game.Direction.RIGHT, rec, depth));
				tileMax = Math.max(tileMax, doAllMoves(nexts[3], tileMasks[3], tile, Game.Direction.DOWN, rec, depth));

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
	}
	
	private static double doAllMoves(Board b, int mask, int tile, Game.Direction dir, int rec, int depth) {
		double scoreCount = 0;
		int tileOptions = 0;
		for (int i=0; i<4; i++) {
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
				scoreCount += recurseBoardScore(b, rec+1, depth);
			}
		}
		return tileOptions > 0 ? scoreCount / tileOptions : 0;
	}
	

	public static int scoreBoard(Board b) {
		int retVal = b.boardScore();
		int maxTile = b.getTopTile();
		int[][] boardstate = b.getBoardstate();
		int[] counts = BoardUtil.countTiles(boardstate,maxTile);

		if (counts[maxTile] == 1) {
			int i=0,j=0;
			outerloop:
			for (; i<4; i++) {
				for (; j<4; j++) {
					if (boardstate[i][j] == maxTile) 
					break outerloop;
				}
			}
			boolean topBottom = false;
			//check if it's on a wall or corner
			if (i == 0 || i == 3) {
				retVal += Math.max(3,maxTile-2);
				topBottom = true;
			}
			if (j == 0 || j == 3) {
				retVal += Math.max(3,maxTile-2);
				if (topBottom) {
					retVal += Math.max(3,maxTile-2)/2;
				}
			}

			//look for next, see if it's nearby
		} else {
			//if they're not close, it's really bad
		}
		
		for (int i = maxTile; i>=5; i--) {
			if (counts[i] == 2) {
				double dist = BoardUtil.tileDistance(boardstate,i);
				if (dist <= 3) 
					retVal += Math.pow(3, i-2) / Math.pow(2, dist);
			}
		}
		//extra points for opens
		if (counts[0] > 0) 
			retVal += Math.pow(3, counts[0]+3);
		//take points away for 1,2,3
		for(int i = 1; i<=3; i++) {
			retVal -= Math.pow(3, counts[i]);
		}
		//double the score for the bottom row
		retVal += BoardUtil.rowScore(b, 3);
		return retVal;
	}
}
