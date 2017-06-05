package threes.simulator;

import java.util.*;

import threes.brains.ZeroMoves;
import threes.engine.*;
import threes.mongo.*;

public class Simulator {
	private Game theGame;
	private List<DecisionPoint> decisions;
	private Mongo m;
	private MoveMaker mover;
	
	public Simulator(Board b) {
		theGame = new Game(b);
		decisions = new ArrayList<DecisionPoint>();
		mover = new MoveMaker(theGame);
	}
	
	public List<DecisionPoint> getDecisions() { return decisions; }
	public int getScore() { return theGame.getScore(); }
	public int getMoves() { return decisions.size(); }
	public int getTopTile() { return theGame.getBoard().getTopTile(); }
	public void setMongo(Mongo mm) { m = mm; }
	public void setQueried(HashMap<String,Integer> q) { mover.setQueried(q); }
	public void setDead(HashSet<String> d) { mover.setDead(d); }
 	
	public void runSim() { runSim(false); }
	public void runSim(boolean print) {
		boolean gamegoing = true;
		while (gamegoing) {
			//show next tile
			int nextTile = theGame.nextTile();
			//get current state
			String preState = theGame.getSerialized();

			//make a move
			Game.Direction dir = mover.makeMove();

			if (dir == Game.Direction.NONE) { //Game Over
				gameOver();
//				System.out.println("GAME OVER");
//				System.out.println("Final Score: " + theGame.getScore());
				gamegoing = false;
			} else {
				//record board state, next tile, and decision
				decisions.add(writeDecision(preState, dir, nextTile));
				if (print) {
					System.out.println(dir);
					theGame.printBoard();
				}
//	    		System.out.println();
			}
		}
	}
	
	private void gameOver() {
		int count = decisions.size();
		int score = theGame.getScore();
		for (DecisionPoint p : decisions) {
			p.setMovesUntilDone(--count);
			p.setFinalScore(score);
		}
	}
	
	private DecisionPoint writeDecision(Game.Direction dir, int tile) {
		return writeDecision(theGame.getSerialized(),dir,tile);		
	}
	private DecisionPoint writeDecision(String serial, Game.Direction dir, int tile) {
		int moveNum = -1;
		if (dir == Game.Direction.LEFT) { 
			moveNum = 0;
		} else if (dir == Game.Direction.UP) { 
			moveNum = 1;
		} else if (dir == Game.Direction.RIGHT) { 
			moveNum = 2;
		} else if (dir == Game.Direction.DOWN) { 
			moveNum = 3;
		}
		return new DecisionPoint(serial,tile,moveNum);
	}
}
