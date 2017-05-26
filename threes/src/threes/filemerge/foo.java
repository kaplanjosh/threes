package threes.filemerge;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class foo {
	public static void bar() {
	
		MongoClient mongo = new MongoClient("localhost", 27017);

		MongoDatabase db = mongo.getDatabase("threesdb");

		MongoCollection<Document> col = db.getCollection("early");
		
		BasicDBObject query = new BasicDBObject();
		query.put("serial", "000100132576369a3L");
		List<Document> foundDocument = col.find(query).into(new ArrayList<Document>());
		for (Document d : foundDocument) {
		    System.out.println(d);
		}
		
		mongo.close();
	}
}
