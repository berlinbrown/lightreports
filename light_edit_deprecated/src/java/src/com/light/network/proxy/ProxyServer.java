// http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm

package com.light.network.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyServer {

	public static final String _serverConnectHost_ = "localhost";
	
	/**
	 * The remote server port that the client will connect to and this class
	 * will bind to.
	 */
	private final int clientBindPort;
	
	/**
	 * The server port that the this local server will connect to.
	 */
	private final int serverConnectPort;
	
	private ServerSocket serverSocket;
	
	public ProxyServer(final int bindLocalPort, final int serverPort) {
		this.clientBindPort = bindLocalPort;
		this.serverConnectPort = serverPort;			
	}
	
	public ProxyServer() {
		this.clientBindPort = 7781;
		this.serverConnectPort = 8080;
	}
	
	public static final MainServerThread createProxyServer() {
		final ProxyServer server = new ProxyServer();		
		return server.new MainServerThread();
	}	
	
	public class MainServerThread implements Runnable {
		
		private boolean isRunning = false;
				
		public void initServerSocket() {
			
			System.out.println(";;;;;;;;;");
			System.out.println(";;;; Launching Octane Proxy Server");
			System.out.println(";;;; <Bound on Local Port> : " + clientBindPort);
			System.out.println(";;;;;;;;;");
			
			try {
				// Bind to the following port and wait for connections from the client.
				serverSocket = new ServerSocket(clientBindPort);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ProxyServerError(ProxyServerError.ERROR, "Could not create server socket =>" + e);
			}
		} // End of the Method
		
		public void mainServerLoop() {
			
			System.out.println(";;INFO: Entering server loop...");			
			while(true) { }
		}
		
		private void runServerAcceptClients() {
			
			isRunning = true;
			 byte[] reply = new byte[4096];
			
			while (isRunning) {
				
				Socket client = null;
				Socket socketToServer = null;
				// Accept client requests, wait for a connection
		        try {
					client = serverSocket.accept();
					System.out.println("INFO: accepting connection from client, client=" + client);
			        final InputStream streamFromClient = client.getInputStream();			        
			        final OutputStream streamToClient  = client.getOutputStream();
			        
			        ///////////////////
			        // Make a connection to the real server.
			        // If we cannot connect to the server, send an error to the
			        // client, disconnect, and continue waiting for connections.
			        ///////////////////
			        try {
			        	socketToServer = new Socket(_serverConnectHost_, serverConnectPort);
			        } catch (IOException e) {
			            PrintWriter out = new PrintWriter(streamToClient);
			            out.print("Proxy server cannot connect to ");			                
			            out.flush();
			            client.close();
			            // Move to wait for connections.
			            continue;
			        }
			        
			        //////////////////////
			        // Get server streams.
			        //////////////////////
			        final InputStream inputStreamToServer = socketToServer.getInputStream();
			        final OutputStream outputStreamToServer = socketToServer.getOutputStream();
			        final ProxyServerClientThread clientThread = new ProxyServerClientThread(streamFromClient, outputStreamToServer);
			        new Thread(clientThread).start();
			        
			        //////////////////			           
			        // Read the server's responses
			        // and pass them back to the client.
			        ////////////////
					// Keep a record of all transmissions.
					final ByteArrayOutputStream baosLogger = new ByteArrayOutputStream();
			        int bytesRead;
			        try {
			          while ((bytesRead = inputStreamToServer.read(reply)) != -1) {
			            streamToClient.write(reply, 0, bytesRead);
			            baosLogger.write(reply, 0, bytesRead);
			            streamToClient.flush();
			          }
			        } catch (IOException e) { }

			        // The server closed its connection to us, so we close our
			        // connection to our client.
			        streamToClient.close();
			        
			        // Print the data stream (client to server communcation)
			        baosLogger.flush();
					final String printMsgContent = new String(baosLogger.toByteArray());	
					System.out.println("<INFO> [From Server to Client] " + printMsgContent);
			        
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						// Closing client and server connections
						if (socketToServer != null) {
							socketToServer.close();
						}
						if (client != null) {
				            client.close();
						}
					} catch (IOException e) { }
					 
				} // End of Try Catch //		
				
			} // End of While //
		
		} // End of the Method //
		
		public void run() {			
			this.runServerAcceptClients();			
		} // End of the method - run
				
	  /////////////	
	} // End of the Inner Class.	
	  //////////////
		
	private class ProxyServerClientThread implements Runnable {
		
	    private final InputStream  clntStreamFromClient;
        private final OutputStream clntStreamToServer;
		        
		public ProxyServerClientThread(final InputStream streamFromClient, final OutputStream streamToServer) {
			this.clntStreamFromClient = streamFromClient;
			this.clntStreamToServer = streamToServer;
		}
		
		/**
		 * Read data from the client (the browser) and stream to the server (the application server).		 
		 */
		private void runClientWriteServer() {
			// Keep a record of all transmissions.
			final ByteArrayOutputStream baosLogger = new ByteArrayOutputStream();			
			int bytesRead;
			try {
			    final byte[] request = new byte[1024];			    
				while ((bytesRead = this.clntStreamFromClient.read(request)) != -1) {
					this.clntStreamToServer.write(request, 0, bytesRead);
					// Also log to memory
					baosLogger.write(request, 0, bytesRead);
					this.clntStreamToServer.flush();
				}
			} catch (IOException e) { }

			// the client closed the connection to us, so close our
	        // connection to the server.
			try {
				this.clntStreamToServer.close();
				baosLogger.flush();
			} catch (IOException e) { }
			
			// Print the data stream (client to server communcation)			
			final String printMsgContent = new String(baosLogger.toByteArray());	
			System.out.println("<INFO> [From Client to Server] " + printMsgContent);
			
		} // End of the method
		
		public void run() {
			
			// Enter thread process loop.
			runClientWriteServer();
		}		
	} // End of the Class //
	
	private static final void main(final String [] args) { }
	
  ////////////////							
} // End of Class
  ////////////////
