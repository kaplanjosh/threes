package threes.mongo;

import java.util.*;

import org.bson.Document;

//import threes.BoardUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;

public class Analyzer {
/*
	public void findDeadMoves(Mongo m) {
		HashMap<String,HashSet<Integer>> cands = getDeadEndCandidates(m);
		for (String s : cands.keySet()) {
			BoardUtil.getDeadPercentage(s, cands.get(s));
		}
		
/*
		Document deadQuery = buildDeadMoveQuery();
		MongoCursor<Document> cursor = m.runQuery(deadQuery);
		
		HashMap<String,Document> deadMap = new HashMap<String, Document>();
		
		int i = 0;
		while (cursor.hasNext()) {
			Document dead = cursor.next();
			String deadBoard = dead.getString("board");
			int deadTile = dead.getInteger("next");
			String badChoice = dead.getString("choice"); 
			Document deadDoc = toDeadDoc(deadBoard, deadTile, badChoice);
			deadMap.put(deadBoard+deadTile, deadDoc);
			if (++i == 5000) {
				System.out.println(new Date() + " 5k printing");
				List<Document> docList = new ArrayList<Document>(deadMap.values());
				m.insertMany(docList, "deadSpot");
				docList = null;
				deadMap.clear();
				i=0;
			}
        }	
		List<Document> docList = new ArrayList<Document>(deadMap.values());
		m.insertMany(docList, "deadSpot");
	}
*/

	public Document buildDeadMoveQuery() {
//		Document query = new Document();
//		query.put("board","141131402471253a");
//		query.put("moves",0);

		Document clause = new Document("moves",0);
		Document claus2 = new Document("count",new Document("$gt", 5));
		ArrayList<Document> al = new ArrayList<Document>();
		al.add(clause);
		al.add(claus2);
		Document query = new Document("$and",al);

		return query;
	}
	
	public HashMap<String,HashSet<Integer>> getDeadEndCandidates(Mongo m) {
		//query for all rows that have a 0
		Document deadCandidates = buildDeadMoveQuery();
		MongoCursor<Document> cursor = m.runQuery(deadCandidates);
		//put the boardstrs in a set
		HashSet<String> deadBoardStrs = new HashSet<String>();
		while (cursor.hasNext()) {
			Document dead = cursor.next();
			String deadBoard = dead.getString("board");
			deadBoardStrs.add(deadBoard);
        }	
		System.out.println(deadBoardStrs.size());
//		for (String s : deadBoardStrs) System.out.println(s);
		//query for all rows for those boardStrs
		QueryUtil qu = new QueryUtil(m);
		Document inQuery = qu.buildBoardQuery(deadBoardStrs);
		MongoCursor<Document> deadResultQuery = m.runQuery(inQuery);
		//map of set of documents
		HashMap<String,HashSet<Integer>> resultSet = filterAndSortDeadCandidates(deadResultQuery);
		return resultSet;
	}
	private HashMap<String,HashSet<Integer>> filterAndSortDeadCandidates(MongoCursor<Document> deadResultQuery) {
		HashMap<String, HashSet<Document>> sorted = new HashMap<String, HashSet<Document>>();
		//sort
		while(deadResultQuery.hasNext()) {
			Document d = deadResultQuery.next();
			String deadBoard = d.getString("board");
			HashSet<Document> aggregate;
			if (sorted.containsKey(deadBoard)) {
				aggregate = sorted.get(deadBoard);
			} else {
				aggregate = new HashSet<Document>();
				sorted.put(deadBoard,aggregate);
			}
			aggregate.add(d);
		}
		
		HashMap<String, HashSet<Integer>> retVal = new HashMap<String, HashSet<Integer>>();
		//filter
		for (String s : sorted.keySet()) {
			HashSet<Integer> tileZero = new HashSet<Integer>();
			HashSet<Integer> tileGo = new HashSet<Integer>();
			HashSet<Document> docs = sorted.get(s);
			for (Document d : docs) {
				int tile = d.getInteger("next");
				double moves = d.getDouble("moves");
				if (tile >0 && tile <4) {
					if (moves > 0) tileGo.add(tile);
					else tileZero.add(tile);
				}
			}
			for (Integer i : tileGo) {
				tileZero.remove(i);
			}
			if (tileZero.size() > 0)
				retVal.put(s, tileZero);
		}
		return retVal;
	}


	private Document toDeadDoc(String deadBoard, int tile, String move) {
		Document retVal = new Document();
		retVal.put("board", deadBoard);
		retVal.put("next", tile);
		retVal.put("choice", move);

		return retVal;
	}
}
