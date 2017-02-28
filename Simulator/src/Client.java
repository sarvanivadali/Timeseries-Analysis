/*
 * Submitted by Sarvani vadali
 * DSCS6020 17374 Collect/Store/Retrieve Data
 */

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

// Entry point into Simulator application.
public class Client {
	public static void main(String[] args) {
		int numberOfThreads = 5; // Default number of Threads (each simulating a sensor).
		int numberOfDataPoints = 3600; // 1 Hour worth of data points (every second). 
		
		System.out.println("Starting " + numberOfThreads + " with " + numberOfDataPoints + " data points each");
		
		if (args.length > 1) {
			numberOfThreads = Integer.parseInt(args[0]);
			numberOfDataPoints = Integer.parseInt(args[1]);
		}
		
		System.out.println("Starting simulation");
		
		ExecutorService executor = Executors.newCachedThreadPool();
	    for (int i = 0; i < 8; i++) {
	    	executor.execute(new Simulator(numberOfDataPoints));
	    }
		
	    System.out.println("Finished simulation");
	}
}

// Class that represents a given sensor simulator.
class Simulator extends Thread {
	private int numDataPoints;
	
	public Simulator(int numDataPoints) {
		super("Simulator");
		
		this.numDataPoints = numDataPoints;
	}
	
	public void run() {
		try {
			// Connect to server on a specific port (can be parameterized).
			Socket socket = new Socket("localhost", 9876);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			int count = numDataPoints;
			long startTime = System.currentTimeMillis() / 1000l;
			Random rand = new Random();
			
			while(count > 0) {
				int val = rand.nextInt((110 - 40) + 1) + 40;
				JSONObject obj = new JSONObject("{'" + startTime + "':'" + val + "'}");
				
				// Send data point to sever.
				out.println(obj.toString());
				
				count--; startTime++;
			}
			
			socket.close();
		} catch (Exception ex) {
			System.out.println("Failed to run simulator " + ex.getMessage());
		}
	}
}
