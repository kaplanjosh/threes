package threes.simulator;

import java.util.*;

import threes.brains.ZeroMoves;
import threes.engine.*;
import threes.mongo.*;

public class Simulator {
	private Game theGame;
	private List<DecisionPoint> decisions;
	private Mongo m;
	private HashMap<String,Integer> queriedMoves;
	private HashSet<String> deadBoards;
	
	public Simulator(Board b) {
		theGame = new Game(b);
		decisions = new ArrayList<DecisionPoint>();
	}
	
	public List<DecisionPoint> getDecisions() { return decisions; }
	public int getScore() { return theGame.getScore(); }
	public int getMoves() { return decisions.size(); }
	public void setMongo(Mongo mm) { m = mm; }
	public void setQueried(HashMap<String,Integer> q) { queriedMoves = q; }
	public void setDead(HashSet<String> d) { deadBoards = d; }
 	
	public void runSim() { runSim(false); }
	public void runSim(boolean print) {
		boolean gamegoing = true;
		while (gamegoing) {
			//show next tile
			int nextTile = theGame.nextTile();
			//get current state
			String preState = theGame.getSerialized();

			//make a move
//			Game.Direction dir = makeSmartMove();
			Game.Direction dir = makeSmartRandomMove();
//			Game.Direction dir = makeRandomMove();

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
	private Game.Direction makeSmartMove() {
		int moveToMake;
		String board = theGame.getSerialized();
		int next = theGame.nextTile();
		String boardNext = board + next;
		
		
		if (queriedMoves.containsKey(boardNext)) {
			moveToMake = queriedMoves.get(boardNext);
		} else {
			QueryUtil qu = new QueryUtil(m);
			moveToMake = qu.getBoardHistory(board, next);
			queriedMoves.put(boardNext, moveToMake);
		}
/*
		String current = theGame.getSerialized() + theGame.nextTile();
		Board myFutureLeft = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.LEFT, theGame.nextTile());		
		Board myFutureUp = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.UP, theGame.nextTile());	
		Board myFutureRight = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.RIGHT, theGame.nextTile());
		Board myFutureDown = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.DOWN, theGame.nextTile());
		*/
		//query for downstate board
		if (moveToMake == 0) {
			theGame.moveLeft();
			return Game.Direction.LEFT;
		} else if (moveToMake == 1) {
			if (theGame.moveUp())
			return Game.Direction.UP;
		} else if (moveToMake == 2) {
			theGame.moveRight();
			return Game.Direction.RIGHT;
		} else if (moveToMake == 3) {
			theGame.moveDown();
			return Game.Direction.DOWN;
		}
		return makeSmartRandomMove();		
	}
	private Game.Direction makeSmartRandomMove() {
		Board b = theGame.getBoard();
		int nextTile = theGame.nextTile();
		int i = 0;
		Random r = new Random();
		int dir = r.nextInt(4);

		int mask = 0;
		/*
		int flask = 0;

		if (BoardUtil.zeroCount(b) < 2) {
			double[] deads = BoardUtil.getOneLevelPerc(theGame.getSerialized());
			double min = 1;
			for (int j=0; j<4; j++) {
				if (deads[j] < min) {
					min = deads[j];
				}
			}
			
			for (int j=0; j<4; j++) {
				if (deads[j] > min*2) {
					flask += 1<<j;
				}
			}
		}
*/		
		//recurse in zero or one-done boards
		if (BoardUtil.zeroCount(b) <=1 && BoardUtil.countOptions(b) <= 2) {
			mask = ZeroMoves.oneRecurse(b, 5, theGame.nextTile());
		}
		
		while (i<4) {
			Board future = new Board(b.getBoardstate());
			if (dir == 0 && (mask & 1) == 0) { //Left
				future.moveLeft(nextTile);
				if (!deadBoards.contains(future.serialize()+nextTile)) {
					if (BoardUtil.isDeadBoard(future)) {
						deadBoards.add(future.serialize()+nextTile);
					} else {
						if (!b.testRowMove(3, true)) {					
							if (theGame.moveLeft()) {
								return Game.Direction.LEFT;
							}
						}
					}
				}
			} else if (dir == 1 && (mask & 2) == 0) { // Up
				future.moveUp(nextTile);
				if (!deadBoards.contains(future.serialize()+nextTile)) {
					if (BoardUtil.isDeadBoard(future)) {
						deadBoards.add(future.serialize()+nextTile);
					} else {
						if (!b.testColMove(3, true)) {
							if (theGame.moveUp()) {
								return Game.Direction.UP;
							}
						}
					}
				}
			} else if (dir == 2 && (mask & 4) == 0) { // Right
				future.moveRight(nextTile);
				if (!deadBoards.contains(future.serialize()+nextTile)) {
					if (BoardUtil.isDeadBoard(future)) {
						deadBoards.add(future.serialize()+nextTile);
					} else {
						if (theGame.moveRight()) {
							return Game.Direction.RIGHT;
						}
					}
				}
			} else if (dir == 3 && (mask & 8) == 0) { // Down
				future.moveDown(nextTile);
				if (!deadBoards.contains(future.serialize()+nextTile)) {
					if (BoardUtil.isDeadBoard(future)) {
						deadBoards.add(future.serialize()+nextTile);
					} else {
						if (theGame.moveDown()) {
							return Game.Direction.DOWN;
						}
					}
				}
			}
			dir = (dir+1)%4;
			i++;
		}
		return Game.Direction.NONE;
	}
	private Game.Direction makeRandomMove() { 
		return makeRandomMove(0);
	}
	private Game.Direction makeRandomMove(int mask) {
		int i = 0;
		Random r = new Random();
		int dir = r.nextInt(4);
		while (i<4) {
			if (dir == 0 && (mask & 1) == 0) { //Left
				if (theGame.moveLeft()) {
					return Game.Direction.LEFT;
				}
			} else if (dir == 1 && (mask & 2) == 0) { // Up
				if (theGame.moveUp()) {
					return Game.Direction.UP;
				}
			} else if (dir == 2 && (mask & 4) == 0) { // Right
				if (theGame.moveRight()) {
					return Game.Direction.RIGHT;
				}
			} else if (dir == 3 && (mask & 8) == 0) { // Down
				if (theGame.moveDown()) {
					return Game.Direction.DOWN;
				}
			}
			dir = (dir+1)%4;
			i++;
		}
		return Game.Direction.NONE;
	}
	private Game.Direction makeValidMove() {
		String current = theGame.getSerialized() + theGame.nextTile();

		int i = 0;
		Random r = new Random();
		int dir = r.nextInt(4);
		Board myFutureSelf;
		int deadEnds = 0;
		
		while (i<4) {
//			if (!Aggregator.deadSpots.contains(current+dir)) {
				if (dir == 0) { //Left
					myFutureSelf = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.LEFT);
					if (BoardUtil.isDeadBoard(myFutureSelf)) {
						deadEnds++;
					} else {
						if (theGame.moveLeft()) {
							return Game.Direction.LEFT;
						}
					}
				} else if (dir == 1) { // Up
					myFutureSelf = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.UP);
					if (BoardUtil.isDeadBoard(myFutureSelf)) {
						deadEnds++;
					} else {
						if (theGame.moveUp()) {
							return Game.Direction.UP;
						}
					}
				} else if (dir == 2) { // Right
					myFutureSelf = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.UP);
					if (BoardUtil.isDeadBoard(myFutureSelf)) {
						deadEnds++;
					} else {
						if (theGame.moveRight()) {
							return Game.Direction.RIGHT;
						}
					}
				} else if (dir == 3) { // Down
					myFutureSelf = BoardUtil.moveResult(theGame.getBoard(), Game.Direction.UP);
					if (BoardUtil.isDeadBoard(myFutureSelf)) {
						deadEnds++;
					} else {
						if (theGame.moveDown()) {
							return Game.Direction.DOWN;
						}
					}				
				}
//			}
			dir = (dir+1)%4;
			i++;
		}
		if (deadEnds == 4) {
			return makeRandomMove();
		}	
		return Game.Direction.NONE;
	}
}
