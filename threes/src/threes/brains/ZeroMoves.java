package threes.brains;

import threes.*;
import threes.engine.*;

public class ZeroMoves {
	public static int oneRecurse(Board b, int depth, int nextTile) {
		int rows = BoardUtil.countRowsMove(b);
		int cols = BoardUtil.countColsMove(b);
		double left = 0;
		double right = 0;
		double up = 0;
		double down = 0;
		
		if (rows == 1) { //do zeroOneRow
			Board bL = new Board(b);
			int row = bL.moveLeft(nextTile);
			Board bR = new Board(b);
			bR.moveRight(nextTile);
			left = zeroOneRowBoardScore(bL, depth);
			right = zeroOneRowBoardScore(bR, depth);
		}
		if (cols == 1) {
			Board bU = new Board(b);
			bU.moveUp(nextTile);
			Board bD = new Board(b);
			bD.moveDown(nextTile);
			up = zeroOneRowBoardScore(bU, depth);
			down = zeroOneRowBoardScore(bD, depth);
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
	public static double zeroOneRowBoardScore(Board b, int depth) {
		return zeroOneRowBoardScore(b,0,depth,false);
	}
	public static double zeroOneRowBoardScore(Board b, int rec, int depth) {
		return zeroOneRowBoardScore(b,rec,depth,false);
	}
	public static double zeroOneRowBoardScore(Board b, int rec, int depth, boolean print) {
		if (rec > depth) return 1;
		else if (BoardUtil.isDeadBoard(b)) return 0;
		else if (BoardUtil.countOptions(b) >= 2) return BoardPoints(b); 
//		else if (BoardUtil.countOptions(b) >= 2) return 1; //7248 23.7439
//		else if (BoardUtil.countOptions(b) >= 2) return BoardPoints(b); 7120 18
//		else if (BoardUtil.countOptions(b) >= 2) return 1.5; 7185 20.6
		else {
			double odds = 0;
			//go through all options, sum up the percentages
			//one
			Board bL = new Board(b);
			int row = Utilities.tileDrop(bL.moveLeft());
			if (row == -1) return zeroOneColBoardScore(b, rec, depth);
			Board bR = new Board(b);
			bR.moveRight();

			for (int i=1; i<=3; i++) {
				bL.overwriteTile(row, 3, i);
				bR.overwriteTile(row, 0, i);
				odds += Math.max(zeroOneRowBoardScore(bL,rec+1,depth), zeroOneRowBoardScore(bR,rec+1,depth)) * .32;
				if (print) {
					System.out.println(i +" " + odds);
				}
			}
			for (int i=4; i<=6; i++) {
				bL.overwriteTile(row, 3, i);
				bR.overwriteTile(row, 0, i);
				odds += Math.max(zeroOneRowBoardScore(bL,rec+1,depth), zeroOneRowBoardScore(bR,rec+1,depth)) * .0133;
				if (print) {
					System.out.println(i +" " + odds);
				}
			}
			return odds;
		}
	}
	public static double zeroOneColBoardScore(Board b, int depth) {
		return zeroOneColBoardScore(b, 0, depth, false);
	}
	public static double zeroOneColBoardScore(Board b, int rec, int depth) {
		return zeroOneColBoardScore(b, rec, depth, false);
	}
	public static double zeroOneColBoardScore(Board b, int rec, int depth, boolean print) {
		if (rec > depth) return 1;
		else if (BoardUtil.isDeadBoard(b)) return 0;
		else if (BoardUtil.countOptions(b) >= 2) return BoardPoints(b); 
		else {
			double odds = 0;
			//go through all options, sum up the percentages
			//one
			Board bU = new Board(b);
			int col = Utilities.tileDrop(bU.moveUp());
			if (col == -1) return zeroOneRowBoardScore(b, rec, depth);
			Board bD = new Board(b);
			bD.moveDown();

			for (int i=1; i<=3; i++) {
				bU.overwriteTile(3, col, i);
				bD.overwriteTile(0, col, i);
				odds += Math.max(zeroOneColBoardScore(bU,rec+1, depth), zeroOneColBoardScore(bD,rec+1,depth)) * .32;
				if (print) {
					System.out.println(i +" " + odds);
				}
			}
			for (int i=4; i<=6; i++) {
				bU.overwriteTile(3, col, i);
				bD.overwriteTile(0, col, i);
				odds += Math.max(zeroOneColBoardScore(bU,rec+1,depth), zeroOneColBoardScore(bD,rec+1,depth)) * .0133;
				if (print) {
					System.out.println(i +" " + odds);
				}
			}
			return odds;
		}
	}
	public static double BoardPoints(Board b) {
		int rows = BoardUtil.countRowsMove(b);
		int cols = BoardUtil.countColsMove(b);
//	7213 21.86
		/*
		if (rows * cols > 0) { 
			return 1.2;
		} else {
			return 1.5;
		}
		*/
//	7160 19.85
		/*
		if (rows * cols > 0) { 
			return 1.5;
		} else {
			return 1;
		}
		*/
//	7207 21.14
		/*
		if (rows * cols > 0) { 
			return 1;
		} else {
			return 1.5;
		}
//	7191 20.4962
		if (rows * cols > 0) { 
			return 1;
		} else {
			return 1 + (rows+cols)/2;
		}
//	7206 21.36
		if (rows * cols > 0) { 
			return 0.75;
		} else {
			return 1;
		}
//	7244 23.56
		if (rows * cols > 0) { 
			return 1;
		} else {
			return 1 + (rows+cols)/4;
		}
//	7253 23.95
//		return 1;
// 0.8 7228 22.8
// 1.0 7248 23.8
// 1.1 7257 24.0
// 1.2 7244 23.42
		if (rows * cols > 0) { 
	 		if (rows == 1 && cols == 1) {
				return 1.1;
	 		} else
	 			return 1 + (Math.min(rows,cols)-1) * .5 + (Math.max(rows, cols)-2)*.25;
		} else {
			return 1 + (rows+cols)/4;
		}
//.75 7248 23.7
// .5 7257 24.0
//.33 7250 23.67
//.25 7242 23.48
		*/
		if (rows * cols > 0) { 
	 		if (rows == 1 && cols == 1) {
				return 1.1;
	 		} else
	 			return 1 + (Math.min(rows,cols)-1) * .5 + (Math.max(rows, cols)-2)*.25;
		} else {
			return 1 + (rows+cols)/4;
		}
//	9931 20.3
//	9894 19.97
//		5 deep
//		9533 20.78
//		9514 20.59
//		2 deep
//		9526 20.84
//		9533 21.07
//		return 1;
			
/*
 		if (rows == 1 && cols == 1) {
			return 1.3;
		} else if (rows == 2 && cols == 1) {
			return 1.7;
		} else if (rows == 3 && cols == 1) {
			return 2.4;
		} else if (rows == 4 && cols == 1) {
			return 3.1;
		} else if (rows == 1 && cols == 2) {
			return 1.7;
		} else if (rows == 2 && cols == 2) {
			return 2;
		} else if (rows == 3 && cols == 2) {
			return 3;
		} else if (rows == 4 && cols == 2) {
			return 3.7;
		} else if (rows == 1 && cols == 3) {
			return 2.4;
		} else if (rows == 2 && cols == 3) {
			return 3;
		} else if (rows == 3 && cols == 3) {
			return 4;
		} else if (rows == 4 && cols == 3) {
			return 4.3;
		} else if (rows == 1 && cols == 4) {
			return 3.1;
		} else if (rows == 2 && cols == 4) {
			return 3.7;
		} else if (rows == 3 && cols == 4) {
			return 4.3;
		} else if (rows == 4 && cols == 4) {			
			return 5;
		}
//		return 1;  
*/		
	}
}