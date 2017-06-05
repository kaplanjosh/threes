package threes.simulator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import threes.brains.ScoreMoves;
import threes.brains.Strategery;
import threes.brains.ZeroMoves;
import threes.engine.Board;
import threes.engine.BoardUtil;
import threes.engine.Game;
import threes.engine.Utilities;
import threes.mongo.QueryUtil;

public class MoveMaker {
	private Game theGame;
	private HashSet<String> deadBoards;
	private HashMap<String,Integer> queriedMoves;

	public MoveMaker(Game g) {
		theGame=g;
	}
	public void setTheGame(Game g) { theGame=g; }
	public void setDead(HashSet<String> d) { deadBoards = d; }
	public void setQueried(HashMap<String,Integer> q) { queriedMoves = q; }

	public Game.Direction makeMove() {
		return makeSmartRandomMove();
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
			dir = (dir+1)%4;
			i++;
		}
		if (deadEnds == 4) {
			return makeRandomMove();
		}	
		return Game.Direction.NONE;
	}
	
	private Game.Direction makeSmartMove() {
		int moveToMake;
		String board = theGame.getSerialized();
		int next = theGame.nextTile();
		String boardNext = board + next;
		
/*		
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
		*/
		return makeSmartRandomMove();		
	}
	private Game.Direction makeSmartRandomMove() {
		Board b = theGame.getBoard();
		int nextTile = theGame.nextTile();
		int i = 0;
		Random r = new Random();
		int dir = r.nextInt(4);

		int mask = 0;
		
		Game.Direction bigMergeDir = Strategery.bigMerge(b);
		if (bigMergeDir != Game.Direction.NONE) {
			theGame.moveDir(Utilities.moveDirToInt(bigMergeDir));
			return bigMergeDir;
		}

		//recurse in zero or one-done boards
		if (BoardUtil.zeroCount(b) <=1 && BoardUtil.countOptions(b) <= 2) {
			mask = ZeroMoves.oneRecurse(b, 2, theGame.nextTile());
//		} else if (BoardUtil.zeroCount(b) <3){
//			mask = ScoreMoves.oneRecurse(b, 3, theGame.nextTile());
		} else {
			mask = ScoreMoves.oneRecurse(b, 3, theGame.nextTile());
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

	private Game.Direction makeScoredMove(int depth) {
		return Game.Direction.NONE;
	}
	
}
