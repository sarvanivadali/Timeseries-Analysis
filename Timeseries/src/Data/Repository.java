/*
 * Submitted by Sarvani vadali
 * DSCS6020 17374 Collect/Store/Retrieve Data
 */

package Data;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

// Encapsulates all the necessary communications
// with MongoDB database
public class Repository {
	MongoClient mongoClient;
	MongoDatabase db;
	
	// Creates new connection to MongoDB
	public Repository(String database) {
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(database);
	}
	
	// Creates new entry in Metadata representing new timeseries.
	public long createTimeseries() {
		// Add metadata about new timeseries.
		MongoCollection<Document> metadata = db.getCollection("Metadata");
		long currentCount = metadata.count();
		metadata.insertOne(
				new Document("ID", currentCount + 1)
					.append("Time", System.currentTimeMillis() / 1000l)
		);
		
		return currentCount + 1;
	}
	
	public void addPoint(Document doc) {
		try {
			MongoCollection<Document> points = db.getCollection("Points");
			points.insertOne(doc);
		} catch(Exception ex) {
			System.out.println("Exception adding point to database. " + ex.getMessage());
		}
	}
}
