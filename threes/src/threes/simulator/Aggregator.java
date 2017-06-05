package threes.simulator;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

import threes.engine.*;
import threes.mongo.*;

public class Aggregator {
	private HashMap<String,KeyedResults> results;
	private Board seedBoard;
	private String fileName;
	private int bestScore;
	private int mostMoves;
	private int topMergeCount;
	public HashMap<String,Document> mrMongos;
	public List<Document> mrMongosNew;
	public List<Document> mrMongosOld;
	public static HashSet<String> deadSpots;
	private int count;
	private long score;
	private long moves;
	private Mongo m;
	private HashMap<String,Integer> queriedMoves;
	
	
	public Aggregator() {
		this(new Board());
	}
	
	public Aggregator(Board b) {
		results = new HashMap<String,KeyedResults>();
		queriedMoves = new HashMap<String,Integer>();
		deadSpots = new HashSet<String>();
		seedBoard = b;
		fileName = null;
		bestScore = 0;
		mostMoves = 0;
		count = 0;
		score = 0;
		moves = 0;
		topMergeCount = 0;
	}
	
	public void setFileName(String s) { fileName = s; }
	public Set<String> getKeyStrings() { return results.keySet(); }
	public void setMongo(Mongo mm) { m = mm; }
	public int getBestScore() { return bestScore; }
	public int getBestMoves() { return mostMoves; }
	public int getCount() { return count; }
	public long getScore() { return score; }
	public long getMoves() { return moves; }
	public void setupDeadPool(MongoCursor<Document> mc) {
		while (mc.hasNext()) {
			Document dead = mc.next();
			String boardie = dead.getString("board");
			int tile = dead.getInteger("next");
			deadSpots.add(boardie+tile+dead.getString("choice"));
		}
	}
	public void cleanup() {
		results.clear();
		bestScore = 0;
		mostMoves = 0;
		count = 0;
		score = 0;
		moves = 0;
	}
	public void runLoop() {
		runLoop(10);
	}
	public void runLoop(int loops) {
		runLoop(loops, 1000);
	}
	public void runLoop(int loops, int logCount) {
		int topStartingTile = seedBoard.getTopTile();
		DecimalFormat df = new DecimalFormat("#.00"); 
		for (int i = 1; i <= loops; i++) {
			if (i%logCount == 0) {
				System.out.println(new Date() + " " + i  
						//+ " rows: " + results.size()
						+ " Avg score: " + df.format(score/count)
						+ " Avg moves: " + df.format(moves*1.0/count)
						+ " Best score: " + bestScore
						+ " Most moves: " + mostMoves
						+ " Big merges: " + topMergeCount);
			}
			
	    	Simulator s = new Simulator(new Board(seedBoard.getBoardstate()));
	    	s.setMongo(m);
	    	s.setQueried(queriedMoves);
	    	s.setDead(deadSpots);
	    	s.runSim();
	    	bestScore = Math.max(bestScore, s.getScore());
	    	mostMoves = Math.max(mostMoves, s.getMoves());
	    	if (s.getTopTile() > topStartingTile) {
	    		topMergeCount++;
	    	}
	    	//recordResults(s.getDecisions());
	    	count++;
	    	score += s.getScore();
	    	moves += s.getMoves();
	    	
	    	s = null;
		}
		System.out.println(new Date() + " Sims complete...count: " + count + " rows: " 
		+ results.size() + " Best score: " + bestScore + " Most moves: " + mostMoves);

//		sortAndMongo();
//		sortAndPrintResults();
//		System.out.println();
//		System.out.println("Writing file...");
//		System.out.println("File written.");
	}

	private void recordResults(List<DecisionPoint> l) {
    	for (DecisionPoint d : l) {
    		String keyStr = d.getKeyString();
    		KeyedResults kr;
    		if (!results.containsKey(keyStr)) {
    			kr = new KeyedResults();
    			results.put(keyStr,kr);
    		} else {
    			kr = results.get(keyStr);
    		}
    		kr.addDecision(d);
    	}		
	}
	public void mergeAndMongo(HashMap<String,MergedResults> oldies) {
//		queriedMoves.clear();		
		System.out.println(new Date() + " Query done, merging " + results.size() + " results");
		mrMongos = new HashMap <String, Document> ();
		mrMongosNew = new ArrayList < Document > ();
		mrMongosOld = new ArrayList < Document > ();
		for (String key : results.keySet()) { 
			MergedResults mr;
			KeyedResults kr = results.get(key);
			if (oldies.containsKey(key)) {
				if (kr.getTotalMoves() == 0) {
					mr = null;
				} else {
					mr = oldies.get(key);
				}
			} else {
				mr = new MergedResults(key);
//				mr.combineResults(kr);
//				mrMongosNew.add(mr.toMongoDoc());
			}
			if (mr != null) {
				mr.combineResults(kr);
				mrMongos.put(key, mr.toMongoDoc());
			}
		}		
		System.out.println(new Date() + " Merge done, " + mrMongos.size() + " records");
	}
	private void sortAndPrintResults() {
//		SortedSet<String> keys = new TreeSet<String>(results.keySet());
		if (fileName == null) {
			fileName = seedBoard.serialize() + "_" + new Date();
		}
		try{
		    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			for (String key : results.keySet()) { 
				   KeyedResults kr = results.get(key);
				   writer.println(key + ", " + kr);
				}		
		    writer.close();
		} catch (IOException e) {
		   System.out.println("oh SNAP with the file. " + e);
		}
	}
}
