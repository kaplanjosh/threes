package threes.engine;
import java.util.Random;

public class Board {
	private int[][] boardstate;
	
	public Board() {
		boardstate = new int[4][4];
		initialize();
	}
	public Board(Board b2) {
		boardstate = new int[4][4];
		int[][] oldstate = b2.getBoardstate();
		for (int i=0; i<4; i++) {
			System.arraycopy(oldstate[i], 0, boardstate[i], 0, 4);
		}
	}
	public Board(String s) {
		boardstate = Board.deserializeBoard(s);
	}
	public Board(int[][] vals) {
		boardstate = new int[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				boardstate[i][j] = vals[i][j];
			}
		}
	}
	public int moveDir(int dir) {
		if (dir == 0) return moveLeft();
		if (dir == 1) return moveUp();
		if (dir == 2) return moveRight();
		if (dir == 3) return moveDown();
		return -1;
	}
	public int moveLeft() {
		return moveRows(true);
	}
	public int moveLeft(int tile) {
		int locations = moveRows(true);
		int drop = Utilities.tileDrop(locations);
		if (locations <= 0) {
			return -1;
		}
		addTile(drop, 3, tile);
		return drop;
	}
	public int moveRight() {
		return moveRows(false);
	}
	public int moveRight(int tile) {
		int locations = moveRows(false);
		int drop = Utilities.tileDrop(locations);
		if (locations <= 0) {
			return -1;
		}
		addTile(drop, 0, tile);
		return drop;
	}		
	public int moveUp() {
		return moveCols(true);
	}
	public int moveUp(int tile) {
		int locations = moveCols(true);
		int drop = Utilities.tileDrop(locations);
		if (locations <= 0) {
			return -1;
		}
		addTile(3, drop, tile);
		return drop;
	}
	public int moveDown() {
		return moveCols(false);
	}
	public int moveDown(int tile) {
		int locations = moveCols(false);
		int drop = Utilities.tileDrop(locations);
		if (locations <= 0) {
			return -1;
		}
		addTile(0, drop, tile);
		return drop;
	}		
	public boolean addTile(int row, int col, int tile) {
		if (boardstate[row][col] == 0) {
			boardstate[row][col] = tile;
			return true;
		} else return false;		
	}
	public boolean overwriteTile(int row, int col, int tile) {
		boardstate[row][col] = tile;
		return true;
	}	
	public String serialize() {
		return serializeBoard();
	}
	
	public int[][] getBoardstate() { return boardstate; }
	public int getTopTile() {
		int retVal = 0;
		for (int i = 0; i < 4; i++) {
			for (int j=0; j<4; j++) {
				retVal = Math.max(retVal, boardstate[i][j]);
			}
		}		
		return retVal;
	}
	
	public void printBoard() {
		for (int i = 0; i < 4; i++) {
			for (int j=0; j<4; j++) {
				System.out.print(getTileVal(boardstate[i][j]));
				System.out.print(' ');
			}
			System.out.print('\n');
		}
	}
	public int boardScore() {
		int retVal = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int tile = boardstate[i][j];
				if (tile < 3) retVal += tile;
				else retVal += Math.pow(3, tile-2);
			}
		}
		return retVal;
	}
	
	public boolean testRowMove(int row, boolean left) {
		int col = left ? 0 : 3;
		return moveRowFrom(row,col,left,true) > 0;
	}
	public boolean testColMove(int col, boolean up) {
		int row = up ? 0 : 3;
		return moveColFrom(row,col,up,true) > 0;
	}
	private int moveRows(boolean left) {
		int retVal=0;
		int col = left ? 0 : 3;
		for (int i = 0; i < 4; i++) {
			int moved = moveRowFrom(i,col,left);
			if (moved>=0) {
				retVal += moved * Math.pow(2,i);
			}
		}
		return retVal;
	}

	private int moveRowFrom(int row, int col, boolean left) {
		return moveRowFrom(row,col,left,false);
	}
	private int moveRowFrom(int row, int col, boolean left, boolean test) {
		if (left && col == 3) return -1;
		if (!left && col == 0) return -1;
		int flip = left ? 1 : -1;
		if (boardstate[row][col] == 0) {
			//see if there's anything to scoot
			if (!isEmptyRow(row, col, left)) {
				if (!test)
					scoot(row,col,left);
				return 1;
			} else return 0;
		} else if (boardstate[row][col] == 1) {
			if (boardstate[row][col+flip] == 2) {
				//merge 3
				if (!test) {
					boardstate[row][col] = 3;
					scoot (row,col+flip,left);
				}
				return 1;
			} else {
				return moveRowFrom(row,col+flip,left,test);
			}
		} else if (boardstate[row][col] == 2) {
			if (boardstate[row][col+flip] == 1) {
				//merge 3
				if (!test) {
					boardstate[row][col] = 3;
					scoot (row,col+flip,left);
				}
				return 1;
			} else {
				return moveRowFrom(row,col+flip,left,test);
			}
		} else if (boardstate[row][col] == boardstate[row][col+flip]) {
			//merge n
			if (!test) {
				boardstate[row][col]++;
				scoot (row,col+flip,left);
			}
			return 1;
		} else {
			return moveRowFrom(row,col+flip,left,test);
		}
	}

	private int moveCols(boolean up) {
		int retVal=0;
		int row = up ? 0 : 3;
		for (int j = 0; j < 4; j++) {
			int moved = moveColFrom(row,j,up);
			if (moved>=0) {
				retVal += moved * Math.pow(2,j);
			}
		}
		return retVal;
	}

	private int moveColFrom(int row, int col, boolean up) {
		return moveColFrom(row,col,up,false);
	}
	private int moveColFrom(int row, int col, boolean up, boolean test) {
		if (up && row == 3) return -1;
		if (!up && row == 0) return -1;
		int flip = up ? 1 : -1;
		if (boardstate[row][col] == 0) {
			//see if there's anything to scoot
			if (!isEmptyCol(row, col, up)) {
				if (!test)
					slide(row,col,up);
				return 1;
			} else return 0;
		} else if (boardstate[row][col] == 1) {
			if (boardstate[row+flip][col] == 2) {
				//merge 3
				if (!test) {
					boardstate[row][col] = 3;
					slide (row+flip,col,up);
				}
				return 1;
			} else {
				return moveColFrom(row+flip,col,up,test);
			}
		} else if (boardstate[row][col] == 2) {
			if (boardstate[row+flip][col] == 1) {
				//merge 3
				if (!test) {
					boardstate[row][col] = 3;
					slide (row+flip,col,up);
				}
				return 1;
			} else {
				return moveColFrom(row+flip,col,up,test);
			}
		} else if (boardstate[row][col] == boardstate[row+flip][col]) {
			//merge n
			if (!test) {
				boardstate[row][col]++;
				slide (row+flip,col,up);
			}
			return 1;
		} else {
			return moveColFrom(row+flip,col,up,test);
		}
	}

	private void scoot(int row, int col, boolean left) {
		if (left) {
			for (int j = col; j < 3; j++) {
				boardstate[row][j] = boardstate[row][j+1];
			}
			boardstate[row][3] = 0;
		} else {
			for (int j = col; j > 0; j--) {
				boardstate[row][j] = boardstate[row][j-1];
			}
			boardstate[row][0] = 0;
		}
	}

	private void slide(int row, int col, boolean up) {
		if (up) {
			for (int i = row; i < 3; i++) {
				boardstate[i][col] = boardstate[i+1][col];
			}
			boardstate[3][col] = 0;
		} else {
			for (int i = row; i > 0; i--) {
				boardstate[i][col] = boardstate[i-1][col];
			}
			boardstate[0][col] = 0;
		}
	}
	
	private boolean isEmptyRow (int row, int col, boolean left) {
		int retTest = 0;
		if (left) {
			for (int i = col; i < 4; i++) {
				retTest+= boardstate[row][i];
			}
		} else {
			for (int i = col; i >=0; i--) {
				retTest+= boardstate[row][i];
			}			
		}
		return retTest == 0;
	}
	
	private boolean isEmptyCol (int row, int col, boolean up) {
		int retTest = 0;
		if (up) {
			for (int i = row; i < 4; i++) {
				retTest+= boardstate[i][col];
			}
		} else {
			for (int i = row; i >=0; i--) {
				retTest+= boardstate[i][col];
			}			
		}
		return retTest == 0;
	}


	private String serializeBoard() {
		String boardString ="";
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				boardString += Integer.toHexString(boardstate[i][j]);
			}
		}
		return boardString;
	}
	private static int[][] deserializeBoard(String s) {
		if (s.length()!=16) {
			return null;
		} 
		int [][] boardArray = new int[4][4];

		int i = 0;
		int j = 0;
		for (int ij = 0; ij < 16; ij++) {
			
			boardArray[i][j] = Integer.parseInt(s.substring(ij,ij+1),16);
			j++;
			if (j==4) {
				i++;
				j=0;
			}
		}
		return boardArray;
	}
	private void initialize() {
		int emptyCount = 0;
		int oneCount = 0;
		int twoCount = 0;
		int threeCount = 0;
		int numCount = 0;
		boolean hasFour = false;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Random rand = new Random();
				boolean nailedIt = false;
				while (!nailedIt) {
					int k = rand.nextInt(2);
					if (emptyCount < 7 && k == 0) {
						boardstate[i][j] = 0;
						emptyCount++;
						nailedIt = true;
					} else {
						int  n = rand.nextInt(3) +1;
	
						if (numCount < 9) {
							if (n == 1 && oneCount < 4) {
								if (oneCount == 3 && hasFour) break;
								if (oneCount == 3) hasFour=true;
								boardstate[i][j] = 1;
								oneCount++;
								numCount++;
								nailedIt = true;					
							} else if (n == 2 && twoCount < 4) {
								if (twoCount == 3 && hasFour) break;
								if (twoCount == 3) hasFour=true;
								boardstate[i][j] = 2;
								twoCount++;
								numCount++;
								nailedIt = true;					
							} else if (n == 3 && threeCount < 4) {
								if (threeCount == 3 && hasFour) break;
								if (threeCount == 3) hasFour=true;
								boardstate[i][j] = 3;
								numCount++;
								threeCount++;
								nailedIt = true;
							}
						}
//						System.out.println("i " + i + " j " + j + " n "+ n + " empty " + emptyCount + " num " + numCount);
					}
				}
			}
		}
	}
	
		
	private int getTileVal(int tile) {
		if (tile < 4) {
			return tile;
		} else {
			int i = 0;
			i += 3 * Math.pow(2,tile-3);
			return i;
		}
	}
}
