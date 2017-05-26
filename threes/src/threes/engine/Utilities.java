package threes.engine;

import java.util.Random;

public class Utilities {
	public static int tileDrop(int resultVector) {
		Random r = new Random();
		int drop = r.nextInt(4);
		int infinite = 0;
		boolean dropped = false;
		//check until we get a valid column
		while (!dropped && infinite < 6) {
			int dropPow = new Double(Math.pow(2, drop)).intValue();
			int yes = dropPow & resultVector;
			if (yes > 0) {
				dropped = true;
			} else {
				drop = (drop+1) % 4;
			}
			
			infinite++;
		}
		if (infinite > 4) return -1;
		return drop;
	}
	public static String moveIntToStr(int move) {
		String moveStr = "";
		if (move == 0) {
			moveStr = "L";
		} else if (move == 1) {
			moveStr = "U";
		} else if (move == 2) {
			moveStr = "R";
		} else if (move == 3) {
			moveStr = "D";
		}
		return moveStr;		
	}
	public static int moveStrToInt(String move) {
		int moveInt = -1;
		if (move.equalsIgnoreCase("L")) {
			moveInt = 0;
		} else if (move.equalsIgnoreCase("U")) {
			moveInt = 1;
		} else if (move.equalsIgnoreCase("R")) {
			moveInt = 2;
		} else if (move.equalsIgnoreCase("D")) {
			moveInt = 3;
		}
		return moveInt;		
	}
	public static int moveDirToInt(Game.Direction move) {
		int moveInt = -1;
		if (move == Game.Direction.LEFT) {
			moveInt = 0;
		} else if (move == Game.Direction.UP) {
			moveInt = 1;
		} else if (move == Game.Direction.RIGHT) {
			moveInt = 2;
		} else if (move == Game.Direction.DOWN) {
			moveInt = 3;
		}
		return moveInt;		
	}
}
