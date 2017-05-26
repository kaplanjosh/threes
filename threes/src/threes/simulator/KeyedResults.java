package threes.simulator;


public class KeyedResults {
	private long totalScore;
	private long totalMoves;
	private double count;
	
	public KeyedResults() {
		totalScore=0;
		totalMoves=0;
		count=0;
	}
		
	public void addDecision(DecisionPoint d) {
		count++;
		totalScore += d.finalScore;
		totalMoves += d.movesUntilDone;
	}

	public long getTotalScore() { return totalScore; }
	public long getTotalMoves() { return totalMoves; }
	public int getCount() { return (new Double(count)).intValue(); }
	
	public String toString() {
		String retVal = count + ", " + (totalScore/count) + ", " + (totalMoves/count);
		return retVal;
	}
}
