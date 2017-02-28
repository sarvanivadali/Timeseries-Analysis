/*
 * Submitted by Sarvani vadali
 * DSCS6020 17374 Collect/Store/Retrieve Data
 */

package Server;

import java.net.*;
import java.io.*;

// Each WorkerThread is responsible for creating new
// Timeseries object and storing it in database.
public class WorkerThread extends Thread {
	private Socket socket = null;

	public WorkerThread(Socket socket) {
		super("WorkerThread");
		this.socket = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Each connection represents new Timeseries object.
			// Create new Timeseries object.
			Timeseries timeseries = new Timeseries();
			
			String inputLine;
			while ((inputLine = in .readLine()) != null) {
				// Insert new data point
				timeseries.insert(inputLine);
			}
			
			timeseries.flush();
			
			socket.close();
		} catch(Exception ex) {
			System.out.println("Exception retrievng data from client.");
			System.out.println(ex.getMessage());
		}
	}
}