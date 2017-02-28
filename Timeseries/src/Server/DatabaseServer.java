/*
 * Submitted by Sarvani vadali
 * DSCS6020 17374 Collect/Store/Retrieve Data
 */

package Server;

import java.net.*;
import java.io.*;

// Database server listens for client (sensor) requests and
// Creates new worker thread for every client.
public class DatabaseServer {
	// Start server on given port.
	public static void start(int port) throws IOException {
		System.out.println("Starting server on port " + port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				new WorkerThread(serverSocket.accept()).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(-1);
		}
	}
}