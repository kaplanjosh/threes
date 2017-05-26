package threes.simulator;

import threes.engine.Utilities;

public class DecisionPoint {
	public String boardString;
	public int nextTile;
	public int moveMade;
	public int movesUntilDone;
	public int finalScore;
	
	public DecisionPoint() {
		
	}
	public DecisionPoint(String board, int tile, int move) {
		boardString = board; 
		nextTile = tile;
		moveMade = move;
	}
	
	public void setMovesUntilDone (int m) { movesUntilDone = m; } 
	public void setFinalScore (int s) { finalScore = s; } 
	public String getKeyString() {
		String retVal = boardString + nextTile + moveMade;
		return retVal;
	}
	public String toString() {
		return boardString + " " + nextTile + " - " + Utilities.moveIntToStr(moveMade) + " - " + movesUntilDone + ",- " + finalScore;
	}
}
