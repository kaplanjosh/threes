package threes.mongo;
import java.util.*;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import org.bson.Document;

public class QueryUtil {
	Mongo m;
	public QueryUtil(Mongo mm) {
		m = mm;
	}
	public MongoCursor<Document> runIDQuery(Set<String> ss) {
		
		Document dbo = buildIDQuery(ss);
		MongoCursor<Document> cursor = m.runQuery(dbo);
		return cursor;
	}
	public List<WriteModel<Document>> buildBulkBoardQuery(HashSet<String> boards) {
		List<WriteModel<Document>> writes = new ArrayList<WriteModel<Document>>();
		for (String s : boards) {
			writes.add(new InsertOneModel<Document>(new Document("board", s)));
		}
		return writes;
	}

	public Document buildBoardQuery(Set<String> ss) {
		return buildListQuery("board",ss);
	}
	public Document buildIDQuery(Set<String> ss) {
		return buildListQuery("_id",ss);
	}
	public Document buildListQuery(String param, Set<String> ss) {
		Document obj = new Document (param, new Document("$in", ss));
		return obj;
	}
	public FindIterable<Document> getRecordsByID(BasicDBObject query) {
		return m.getCol().find(query);
	}
	public int getBoardHistory(String board, int nextTile) {
//System.out.println ("  looking up board " + board + " tile " + nextTile);
		Document d = new Document ();
		d.put("board",board);
		MongoCursor<Document> curs = m.runQuery(d);

		double best = -1;
		int move = -1;

		while (curs.hasNext()) {
			Document res = curs.next();
			int tile = res.getInteger("next");
			if (tile == nextTile) {
				int count = res.getInteger("count");
				Double moves = res.getDouble("moves");
				if (moves < 5) {
					if (count < Math.pow(10, moves)) {
						return -1;
					}
				}
				double avgScore = res.getDouble("score");
				int moveMade = Integer.parseInt(res.getString("choice"));
//				System.out.println ("  match for tile " + tile + " move " + moveMade + " = " + avgScore);
				if (avgScore > best) {
					best = avgScore;
					move = moveMade;
				}
			}
		}
//		System.out.println ("  returning " + move);
		return move;
	}
    public static Document stringToIDDocument(String b) {
        Document d = new Document();
        d.put("_id", b);
        return d;
    }
    public static WriteModel<Document> stringToIDWriteModelDocument(String b) {
        Document d = new Document();
        d.put("_id", b);
        return new InsertOneModel<Document>(d);
    }
}
