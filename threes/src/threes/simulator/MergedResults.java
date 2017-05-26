package threes.simulator;

import java.util.HashMap;
import java.util.Set;

import threes.engine.*;
import threes.mongo.*;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;

import org.bson.Document;

public class MergedResults {
	public String boardString;
	public int nextTile;
	public String moveMade;
	public int count;
	public double avgScore;
	public double avgMoves;
	
	public MergedResults() { 
		
	}
	public MergedResults(String idStr) {
		boardString = idStr.substring(0, idStr.length()-2);
		nextTile = Integer.parseInt(idStr.substring(idStr.length()-2, idStr.length()-1));
		moveMade = idStr.substring(idStr.length()-1);
		count = 0;
		avgScore = 0;
		avgMoves = 0;
	}
	public MergedResults(int c, double score, double moves) {
		count = c;
		avgScore = score;
		avgMoves = moves;
	}
	
	public String toString() {
		String retVal = count + ", " + avgScore + ", " + avgMoves;
		return retVal;
	}
	public Document toMongoDoc() {
		Document retVal = new Document();
		String idStr = boardString + nextTile + moveMade;
		retVal.put("_id",idStr);
		retVal.put("board", boardString);
		retVal.put("next", nextTile);
		retVal.put("choice", moveMade);
		retVal.put("count", count);
		retVal.put("score", avgScore);
		retVal.put("moves", avgMoves);

		return retVal;
	}

	public void combineResults(KeyedResults other) {
		if (other == null) return;
		double myTotalScore = count * avgScore;
		double myTotalMoves = count * avgMoves;
		count += other.getCount();
		avgScore = (myTotalScore + other.getTotalScore()) / count;
		avgMoves = (myTotalMoves + other.getTotalMoves()) / count;
		
	}

	public void combineResults(MergedResults other) {
		double myTotalScore = count * avgScore;
		double myTotalMoves = count * avgMoves;
		
		double otherTotalScore = other.count * other.avgScore;
		double otherTotalMoves = other.count * other.avgMoves;
		
		count += other.count;
		avgScore = (myTotalScore + otherTotalScore) / count;
		avgMoves = (myTotalMoves + otherTotalMoves) / count;
		
	}
	public static HashMap<String,MergedResults> doTheThing(MongoCursor<Document> cursor) {
		HashMap<String,MergedResults> results = new HashMap<String,MergedResults>();
		
		while (cursor.hasNext()) {
			Document obj = cursor.next();
			int count = obj.getInteger("count");
			Double score = obj.getDouble("score");
			Double moves = obj.getDouble("moves");
			MergedResults mr = new MergedResults(count, score, moves);
			mr.boardString = obj.getString("board");
			mr.nextTile = obj.getInteger("next");
			mr.moveMade = obj.getString("choice");
			results.put(mr.boardString+mr.nextTile+mr.moveMade, mr);
		}
		return results;
	}
}
