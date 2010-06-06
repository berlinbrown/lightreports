/*---------------------------
 * Berlin Brown
 * Created on Jul 18, 2007
 */
package com.light.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

/**
 * Simple Server Client Thread Handler 
 */
public class LightSimpleServerThread implements Runnable {	
	private Socket client;
	private boolean running = false;
	private DataInputStream in;	
	private PrintStream out;
	
	public LightSimpleServerThread(Socket client) {
		this.client = client;
		try { 
			System.out.println("communicating with server=" + client);
			in = new DataInputStream(client.getInputStream());
			out = new PrintStream(client.getOutputStream());
		} catch (IOException e) {				
			try { 
				client.close(); 
			} catch (IOException e2) { ; }
			System.err.println("Exception while opening socket streams: " + e);
			return;
		}
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {	
		running = true;
		String line;
		try {
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
			while(running) {						
				// read in a line from the client
				line = bufReader.readLine();
				if (line == null)
					break;														 
				// and write out the reversed line				
				System.out.println("[server/" + line.length() + "]" + line);
				if (line.length() == 0)
					break;
			}			
			// Write a html response back
			StringBuffer buf = new StringBuffer();			
			buf.append("HTTP/1.1 200 Ok\r\n");
			buf.append("Server: Apache-Test\r\n");		 
			buf.append("Connection: close\r\n");	 
			buf.append("Content-Type: text/html\r\n");
			buf.append("\r\n");			 
 
			buf.append("<html>");
			buf.append("<body>");
			buf.append("" + new Date() + " / " + this.client);
			buf.append("</body>");
			buf.append("</html>");
			out.println(buf);
		} catch (IOException e) { 
			e.printStackTrace();
		} finally { 
			try {
				if (out != null) out.close();
				if (in != null) in.close();
				client.close();
			} catch (IOException e2) {;}
			System.out.println("[server] closing connection"); 
		} // End of Try - Catch - Finally

	}
}
// End of File