package threes;
import java.util.*;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.WriteModel;

import threes.simulator.*;
import threes.engine.*;
import threes.mongo.*;
import threes.analysis.*;

public class ThreesSim {
    public static void main(String[] args) {
    	Mongo m = new Mongo();
/*
    	String s;
    	HashSet<String> deads = new HashSet<String>();
    	List<WriteModel<Document>> ld;
    	QueryUtil qu = new QueryUtil(m);

    	s = "244556363261789a";
    	Board b = new Board(s);
//    	System.out.println(BoardUtil.zeroOneRowBoardScore(b));
//    	System.out.println(BoardUtil.countColsMove(b));
//    	System.out.println(BoardUtil.countRowsMove(b));
/*
    	int count = 0;
    	for (int i = 1; i <= 1; i++) {
        	for (int j = 1; j <= 1; j++) {
	        	s = "1" + i + j +"111111111789a";
	    		System.out.println(new Date() + " " + s);
		    	ld = threes.analysis.EndGame.ones(s, 1, 6);
		    	if (ld.size() > 0) 
		    		m.insertBulk("zeroOneRowBoards",ld);
		    	ld = null;
//		    	deads = threes.analysis.EndGame.ones(s, 1, 6);
//		    	count += deads.size();
//	    		System.out.println(new Date() + " adding " + deads.size() + " total " + count);
//		    	deads = null;
        	}
    	}
		System.out.println(new Date() + " done");
//		System.out.println(new Date() + " " + deads.size());
*/    	   	
//   		String s = "133035270483354a";
//   		String s = "132103142471253a";
//   		String s = "213103020267579a";
//   		String s = "000313340475369a";
//   		String s = "15334420456348ab";
//   		String s = "15415200456348ab";
		String s = "00000000800089ab";

		Board b = new Board(s);

		// BoardMaker bm = new BoardMaker(b, 0, 2);
		// bm.setFinalX(2);
		// bm.setFinalY(3);
    	// Date start = new Date();
		// long i = 0;
		// Board bb = bm.getNext();
		// while (bb != null) {
		// 	i++;
		// 	bb = bm.getNext();
		// 	if (bb == null) {
		// 		System.out.println("MAHOOLEEHOO!!! " + i);
		// 		break;
		// 	}
		// }
		// bm.getCurrent().printBoard();
    	// long elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
		// System.out.println(new Date() + " Finished. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
		bigMergeInserts(m);


		/*
     	Aggregator a = new Aggregator(b);
//    	a.setMongo(m);
    	int count = 0;
    	long score = 0;
    	long moves = 0;
    	long highScore = 0;
    	long highMoves = 0;
    	
    	Date start = new Date();

    	for (int i = 0; i < 1; i++) {
    		System.out.println(new Date() + " LOOP #" +(i+1));
	    	a.runLoop(5000,20);
	    	
	    	count += a.getCount();
	    	score += a.getScore();
	    	moves += a.getMoves();
	    	
	    	highScore = Math.max(highScore, a.getBestScore());
	    	highMoves = Math.max(highMoves, a.getBestMoves());

//	    	QueryUtil q = new QueryUtil(m);
//	    	MongoCursor<Document> cursor = q.runIDQuery(a.getKeyStrings());
//	    	HashMap<String,MergedResults> existingMongo = MergedResults.doTheThing(cursor);
//	    	a.mergeAndMongo(existingMongo);
//	WAS:	    	a.mergeAndMongo(q.doTheThing(a.getKeyStrings()));
//	    	m.insertRecords(a.mrMongos);
	
	    	a.cleanup();
    	}
    	long elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
		System.out.println(new Date() + " Finished. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
		System.out.println(new Date() + " Starting score: " + b.boardScore());
		System.out.println(new Date() + " runs: " + count + " score: " + score/count + " moves: " + moves*1.0/count);
		System.out.println(new Date() + " best: " + highScore + ", " + highMoves + " moves");

/*    	
 
    	Simulator s = new Simulator(b);
    	s.setMongo(m);
    	s.runSim(true);
    	System.out.println("Score: " + s.getScore() + " moves: " + s.getMoves());
//
    	Analyzer az = new Analyzer();
    	az.findDeadMoves(m);
/*/
    	m.closeOut();
	}
	private static void bigMergeInserts(Mongo m) {
		String s = "00000000800089ab";		
//		String s = "00000800080009ab";		
//		String s = "00000000088009ab";		
//		String s = "00000000880009ab";		
		Board b = new Board(s);
		
		List<Document> ld = new ArrayList<Document>();

		BoardMaker bm = new BoardMaker(b, 0, 2);
		bm.setFinalX(2);
		bm.setFinalY(3);
    	Date start = new Date();
		int winnerCount = 0;

//		for (int i = 0; i < 500000000; i++) {
//			Board bb = bm.getNext();
		long i = 0;
		Board bb = bm.getNext();
		while (bb != null) {
			ld.add(QueryUtil.stringToIDDocument(bb.serialize()));

			i++;

			if (i%1000000 == 0) {
				long elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
				System.out.println(new Date() + " i: " + i + " Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
				if (i>147000000) {
					m.insertMany(ld,"bigMerge");
				elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
				System.out.println(new Date() + " Mongo'd. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
				}
				ld.clear();
			}
			bb = bm.getNext();
			if (bb == null) {
				System.out.println("MAHOOLEEHOO!!! " + i);
				break;
			}

			/*
			if (BigMerge.easyBigMerge(bb, 12, 4) == 1) {
				bb.printBoard();
				System.out.println();
				winnerCount++;
			}
			*/
		}
		bm.getCurrent().printBoard();
    	long elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
		System.out.println(new Date() + " Finished. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
//		System.out.println("Winner count "+ winnerCount);
		m.insertMany(ld,"bigMerge");
		elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
		System.out.println(new Date() + " Mongo'd. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
		//    	b.printBoard();
//		double merged = BigMerge.easyBigMerge(b, 12, 4);
//		System.out.println("merged? " + merged);
	}
	private static void bigMergeLook(Mongo m) {
		String s = "00000000008809ab";		
		Board b = new Board(s);
		
		List<Document> ld = new ArrayList<Document>();

		BoardMaker bm = new BoardMaker(b, 2, 1);
		bm.setFinalX(3);
		bm.setFinalY(1);
    	Date start = new Date();
		int winnerCount = 0;

		// for (int i = 0; i < 500000000; i++) {
		// 	Board bb = bm.getNext();
		long i = 0;
		Board bb = bm.getNext();
		while (bb != null) {
			// ld.add(QueryUtil.stringToIDDocument(bb.serialize()));
			i++;

			if (BigMerge.easyBigMerge(bb, 12, 4) == 1) {
				bb.printBoard();
				System.out.println();
				winnerCount++;
			}
			// if (i%1000000 == 0) {
			// 	long elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
			// 	System.out.println(new Date() + " i: " + i + " Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
			// 	m.insertMany(ld,"bigMerge");
			// 	elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
			// 	System.out.println(new Date() + " Mongo'd. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
			// 	ld.clear();
			// }
			bb = bm.getNext();
			if (bb == null) {
				System.out.println("MAHOOLEEHOO!!! " + i);
				break;
			}

		}
		bm.getCurrent().printBoard();
    	long elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
		System.out.println(new Date() + " Finished. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
//		System.out.println("Winner count "+ winnerCount);
		m.insertMany(ld,"bigMerge");
		elapsedSeconds = (new Date().getTime() - start.getTime())/1000;
		System.out.println(new Date() + " Mongo'd. Time: " + elapsedSeconds/60 + ":" +elapsedSeconds%60);
		//    	b.printBoard();
//		double merged = BigMerge.easyBigMerge(b, 12, 4);
//		System.out.println("merged? " + merged);
		
	}
}
