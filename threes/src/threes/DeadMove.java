package threes;

public class DeadMove {
	public String boardString;
	public int tile;
	public int donotmove;
	
	public DeadMove(String b, int t, String d) {
		boardString = b;
		tile = t;
		donotmove = Integer.parseInt(d);
	}
}
