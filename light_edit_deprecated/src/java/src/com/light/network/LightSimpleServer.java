/* 
 * Created on Jul 18, 2007 
 */
package com.light.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple Test Server 
 */
public class LightSimpleServer {
	public static final int DEFAULT_PORT = 9999;

	private int port = 9999;
	private ServerSocket server;

	public LightSimpleServer(int port) {
		this.port = port;
	}
	public void runServer() {
		try {
			server = new ServerSocket(this.port);			
			System.out.println("server bound to port=" + this.port);
		} catch (IOException e) {
			System.out.println("Could not listen on port=" + this.port);
			System.exit(-1);
		}
		try {		
			while (true) {
				LightSimpleServerThread clientThread;
				try {
					// server.accept returns a client connection
					System.out.println("waiting for client connection...");								
					Socket clientSocket = server.accept();
					if  (clientSocket == null) {
						System.out.println("ERROR: invalid socket connection, closing.");
						return;					
					} else {
						System.out.println("connection made=" + clientSocket);
					}
					clientThread = new LightSimpleServerThread(clientSocket);
					Thread t = new Thread(clientThread);
					t.start();
				} catch (IOException e) {
					System.out.println("Accept failed: " + this.port);
					System.exit(-1);
				}
			} // End of While
		} finally {
			try {
				System.out.println("Closing server connection");
				server.close();
			} catch (IOException e1) { }	
		}
	}

	public static void main(String[] args) {
		System.out.println("-- Running Server");
		LightSimpleServer server = new LightSimpleServer(DEFAULT_PORT);
		server.runServer();
		System.out.println("-- Done");

	}

}
//End of File
