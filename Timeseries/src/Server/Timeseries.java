/*
 * Submitted by Sarvani vadali
 * DSCS6020 17374 Collect/Store/Retrieve Data
 */

package Server;

import org.json.*;
import org.bson.Document;

import Data.Repository;

// Represents a unique Timeseries object.
public class Timeseries {
	Repository repository;
	long id;
	Buffer buffer;
	
	// Creates new Timeseries object in repository.
	public Timeseries() {
		repository = new Repository("Timeseries");
		id = repository.createTimeseries();
		buffer = null;
	}
	
	// Inserts a data point into current timeseries object.
	public void insert(String jsonStr) {
		try {
			JSONObject obj = new JSONObject(jsonStr);
			String[] offsets = JSONObject.getNames(obj);
			for (int k = 0; k < offsets.length; k++) {
				long time = Long.parseLong(offsets[k]);
				long val = obj.getLong(offsets[k]);
				
				if (null == buffer) {
					buffer = new Buffer(time, this.id);
				} else {
					if (!buffer.isCompatible(time)) {
						flush();
						buffer = new Buffer(time, this.id);
					}
				}
				
				buffer.add(time, val);
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void flush() {
		if (null != buffer) {
			repository.addPoint(buffer.getDocument());
		}
	}
}

// Encapsulates a buffer that will be added to mongoDB in incremental fashion.
class Buffer {
	private long startTime;
	private Document document;
	
	private int count;
	private long total;
	
	
	public Buffer(long startTime, long id) {
		this.startTime = startTime;
		this.document = new Document("ID", id);
		this.document.append("Time", startTime);
		
		count = 0; total = 0;
	}
	
	public void add(long time, long val) {
		if (isCompatible(time)) {
			long offset = time - startTime;
			this.document.append(Long.toString(offset), Long.toString(val));
			count++;
			total += val;
		}
	}
	
	public Boolean isCompatible(long time) {
		long offset = time - startTime;
		if (offset >= 0 && offset <= 59) {
			return true;
		}
		return false;
	}
	
	public Document getDocument() {
		this.document.append("Total", this.total);
		this.document.append("Count", count);
		return this.document;
	}
}
