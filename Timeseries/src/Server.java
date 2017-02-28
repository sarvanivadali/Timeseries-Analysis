/*
 * Submitted by Sarvani vadali
 * DSCS6020 17374 Collect/Store/Retrieve Data
 */

import java.io.IOException;

import Server.DatabaseServer;

// Entry into main application
public class Server {
	public static void main(String[] args) throws IOException {
		int port = 9876;
		DatabaseServer.start(port);
	}
}
