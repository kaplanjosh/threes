package threes.mongo;
import java.util.*;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.*;

import org.bson.Document;

public class Mongo {
	private MongoClient mongo;
	private MongoDatabase db;
	private MongoCollection<Document> col;
	
	public Mongo() {
		mongo = new MongoClient("localhost", 27017);
		db = mongo.getDatabase("threesdbtwo");
		col = db.getCollection("javatest");
		
	}
	public void closeOut() {
		mongo.close();
	}
	public MongoCollection<Document> getCol() { return col; }
	
	public MongoCursor<Document> runQuery(Document d) {
		return runQuery(col,d);
	}
	public MongoCursor<Document> runQuery(String colName, Document d) {
		MongoCollection<Document> thisCol = db.getCollection(colName);
		return runQuery(thisCol,d);
	}
	public MongoCursor<Document> runQuery(String colName) {
		MongoCollection<Document> thisCol = db.getCollection(colName);
		return runQuery(thisCol,null);
	}
	private MongoCursor<Document> runQuery(MongoCollection<Document> theCol, Document d) {
		if (d == null)
			return theCol.find().iterator();
		else
			return theCol.find(d).iterator();
	}
	public void insertBulk(String colName,List<WriteModel<Document>> writes) {
		MongoCollection<Document> thisCol = db.getCollection(colName);
		com.mongodb.bulk.BulkWriteResult bulkWriteResult = thisCol.bulkWrite(writes);
	}
	public void insertMany(List<Document> sd, String colName) {
		MongoCollection<Document> thisCol = db.getCollection(colName);
		thisCol.insertMany(sd);
	}
	public List<Document> getRecordByID(String id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		return col.find(query).into(new ArrayList<Document>());
	}
	public void insertRecords(HashMap<String,Document> ld) {
		List<WriteModel<Document>> updateDocuments = new ArrayList<WriteModel<Document>>();
		for (String entityId : ld.keySet()) {

		    //Finder doc
		    Document filterDocument = new Document();
		    filterDocument.append("_id", entityId);

		    //Update doc
		    Document updateDocument = new Document();
		    Document setDocument = ld.get(entityId);

		    updateDocument.append("$set", setDocument);

		    //Update option
		    UpdateOptions updateOptions = new UpdateOptions();
		    updateOptions.upsert(true); //if true, will create a new doc in case of unmatched find
		    updateOptions.bypassDocumentValidation(true); //set true/false

		    //Prepare list of Updates
		    updateDocuments.add(
		            new UpdateOneModel<Document>(
		                    filterDocument,
		                    updateDocument,
		                    updateOptions));

		}
		//Bulk write options
		BulkWriteOptions bulkWriteOptions = new BulkWriteOptions();
		bulkWriteOptions.ordered(false);
		bulkWriteOptions.bypassDocumentValidation(true);

		com.mongodb.bulk.BulkWriteResult bulkWriteResult = null;
		System.out.println(new Date() + " writing to mongo");
		try {
		    //Perform bulk update
		    bulkWriteResult = col.bulkWrite(updateDocuments,bulkWriteOptions);
		} catch (BulkWriteException e) {
		    //Handle bulkwrite exception
		    List<BulkWriteError> bulkWriteErrors = e.getWriteErrors();
		    for (BulkWriteError bulkWriteError : bulkWriteErrors) {
		        int failedIndex = bulkWriteError.getIndex();
		        Document failedEntityId = ld.get(failedIndex);
		        System.out.println("Failed record: " + failedEntityId);
		        //handle rollback
		    }
		}

//		int rowsUpdated = bulkWriteResult.getModifiedCount();
	}
	public void printRecords() {
		Document query = new Document();
		query.put("moves",-1);
		MongoCursor < Document > cursor = col.find().sort(query).iterator();

		while (cursor.hasNext()) {
            Document book = cursor.next();
            System.out.println(book);
        }
	}
	
}
