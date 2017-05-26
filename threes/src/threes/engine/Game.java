package threes.engine;
import java.util.*;

public class Game {
	public enum Direction {
		LEFT, RIGHT, UP, DOWN, NONE
	}

	private Board theBoard;
	private Stack<Integer> theTiles;
	
	public Game() {
		theBoard = new Board();
		theTiles = new Stack<Integer>();
		ShuffleAndDeal();
	}
	
	public Game(Board b) {
		theBoard = b;
		theTiles = new Stack<Integer>();
		ShuffleAndDeal();
	}
	
	public void printBoard() { theBoard.printBoard(); }
	public int getScore() { return theBoard.boardScore(); }
	public String getSerialized() { return theBoard.serialize(); }
	public int nextTile() { return theTiles.peek(); }
	public Board getBoard() { return theBoard; }
	public boolean moveLeft() { 
		int moveResult = theBoard.moveLeft();
		if (moveResult<=0) return false;
		return addTile(moveResult, Direction.LEFT);
	}
	
	public boolean moveRight() { 
		int moveResult = theBoard.moveRight();
		if (moveResult<=0) return false;
		return addTile(moveResult, Direction.RIGHT);
	}
	
	public boolean moveUp() { 
		int moveResult = theBoard.moveUp();
		if (moveResult<=0) return false;
		return addTile(moveResult, Direction.UP);
	}
	
	public boolean moveDown() { 
		int moveResult = theBoard.moveDown();
		if (moveResult<=0) return false;
		return addTile(moveResult, Direction.DOWN);
	}
	
	public void printTiles() {
		while (!theTiles.isEmpty()) System.out.print(theTiles.pop() + " ");
	}
	
	private boolean addTile(int moveResult, Game.Direction dir) {
		int newTileLoc = Utilities.tileDrop(moveResult);
		Integer top = (Integer)theTiles.pop();

		if (dir == Direction.LEFT)
			theBoard.addTile(newTileLoc, 3, top.intValue());
		if (dir == Direction.RIGHT)
			theBoard.addTile(newTileLoc, 0, top.intValue());
		if (dir == Direction.UP)
			theBoard.addTile(3, newTileLoc, top.intValue());
		if (dir == Direction.DOWN)
			theBoard.addTile(0, newTileLoc, top.intValue());
		
		//check if the stack is empty
		if (theTiles.isEmpty()) ShuffleAndDeal();
		return true;
	}
	private void ShuffleAndDeal() {
		Stack<Integer> ones = new Stack<Integer>();
		Stack<Integer> twos = new Stack<Integer>();
		Stack<Integer> threes = new Stack<Integer>();
		
		for (int i = 0; i < 8; i++) {
			ones.push(new Integer(1));
			twos.push(new Integer(2));
			threes.push(new Integer(3));
		}
		Random rand = new Random();
		int oddball = rand.nextInt(25);

		for (int i = 0; i < 25; i++) {
			if (i==oddball)
				addOddball();
			else {
				boolean found = false;
				while (!found) {
					int nextTile = rand.nextInt(3) + 1;
					if (nextTile == 1) {
						if (!ones.isEmpty()) {
							theTiles.push(ones.pop());
							found = true;
						}
					} else if (nextTile == 2) {
						if (!twos.isEmpty()) {
							theTiles.push(twos.pop());
							found = true;
						}
					} else if (nextTile == 3) {
						if (!threes.isEmpty()) {
							theTiles.push(threes.pop());
							found = true;
						}
					}
				}
			}
		}
	}
	
	private void addOddball() {
		//6 12 24, or top-5 top-4 top-3?
		Random r = new Random();
		int highOddball = 6;

		int coinflip = r.nextInt(2);

		if (coinflip==0) {
			int top = theBoard.getTopTile();
			highOddball = Math.max(highOddball, top-3);
		}
		int mix = r.nextInt(3);
		theTiles.push(new Integer(highOddball-mix));		
	}
}
