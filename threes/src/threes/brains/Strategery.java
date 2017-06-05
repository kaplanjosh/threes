package threes.brains;

import threes.engine.Board;
import threes.engine.Game;

public class Strategery {
	public static Game.Direction bigMerge(Board b) {
		int[][] boardstate = b.getBoardstate();
		//starting from largest tile look for merges
		for (int tile = b.getTopTile(); tile >= 7; tile--) {
			for (int i=3; i>=0; i--) {
				for (int j=0; j<4; j++){
					if (boardstate[i][j] == tile) {
						//see if neighbors are mergables
						//check down tile
						if (i!=3 && boardstate[i+1][j] == tile) {
							return Game.Direction.DOWN;
						}
						//right
						if (j!=3 && boardstate[i][j+1] == tile) {
							if (i!=3) {
								//check down tile
								if (boardstate[i+1][j+1] == tile) {
									return Game.Direction.DOWN;
								}
								//check down tile for nextup
								if (boardstate[i+1][j+1] == tile+1) {
									return Game.Direction.RIGHT;
								}
								if (boardstate[i+1][j] == tile+1) {
									return Game.Direction.LEFT;
								}
							}
							return Game.Direction.RIGHT;
						}						
					}
				}
			}
		}
		return Game.Direction.NONE;
	}
}
